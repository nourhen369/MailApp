package org.example.mailapp.services;

import java.io.*;
import java.util.*;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;

import jakarta.mail.internet.MimeMultipart;
import org.example.mailapp.entities.*;
import org.example.mailapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String username;

    @Autowired private JavaMailSender emailSender;
    @Autowired private EmailRepository emailRepository;
    @Autowired private SentAttachmentRepository sentAttachmentRepository;

    public void sendSimpleMessage(Email email) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(username);
            message.setTo(email.getToEmail());
            message.setSubject(email.getSubject());
            message.setText(email.getBody());
            emailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendValidationEmail(String address) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(address);
            message.setTo(username);
            message.setSubject("Thank You!");
            message.setText(
                    "Hello,\nThank you for sending us an email. We have received your message and will get back to you shortly.\n"+
                    "If you have any urgent queries, please feel free to contact us or call us.\n" +"Best regards,"
            );
            emailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageWithAttachment(Email email) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(username);
            helper.setTo(email.getToEmail());
            helper.setSubject(email.getSubject());

            Multipart mp = new MimeMultipart();

            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(email.getBody(), "text/html");
            mp.addBodyPart(bodyPart);

            File file = new File(email.getPathToAttachment());
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            FileDataSource fileDataSource = new FileDataSource(file);
            mimeBodyPart.setDataHandler(new DataHandler(fileDataSource));
            mimeBodyPart.setFileName(file.getName());
            mp.addBodyPart(mimeBodyPart);

            SentAttachment attachment = new SentAttachment(email.getToEmail(), file);
            message.setContent(mp);

            emailSender.send(message);
            sentAttachmentRepository.save(attachment);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteEmailById(Long id) {
        emailRepository.deleteById(id);
    }
}

