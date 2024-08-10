package org.example.mailapp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String toEmail;
    private File file;
     private String filename;
     private String pathToAttachment;

    public SentAttachment(String toEmail, File file) {
        this.toEmail = toEmail;
        this.file = file;
        this.filename = file.getName();
        this.pathToAttachment = file.getAbsolutePath();
    }
}
