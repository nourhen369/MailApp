package org.example.mailapp.services;

import org.example.mailapp.entities.*;
import org.example.mailapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class StorageService {
    @Autowired private ReceivedAttachmentRepository receivedAttachmentRepository;
    @Autowired private SentAttachmentRepository sentAttachmentRepository;

    // from db
    public String uploadFile(MultipartFile file) throws IOException {
        ReceivedAttachment receivedAttachment = receivedAttachmentRepository.save(ReceivedAttachment.builder()
                .filename(file.getOriginalFilename())
                .content(this.compressImage(file.getBytes())).build());
        return "file uploaded successfully : " + file.getOriginalFilename();
    }

    // from db
    public byte[] downloadReceivedFile(String filename){
        Optional<ReceivedAttachment> dbReceivedAttachment = receivedAttachmentRepository.findByFilename(filename);
        return this.decompressImage(dbReceivedAttachment.get().getContent());
    }

    /*public byte[] downloadSentFile(String filename){
        Optional<SentAttachment> dbReceivedAttachment = sentAttachmentRepository.findByFilename(filename);
        return Utils.decompressImage(dbReceivedAttachment.get().getContent());
    }*/

    public byte[] compressImage(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }



    public byte[] decompressImage(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();
    }


}
