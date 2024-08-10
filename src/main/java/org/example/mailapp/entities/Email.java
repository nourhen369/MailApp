package org.example.mailapp.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String toEmail;
    private String subject;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String body;
    private String pathToAttachment;

    public Email() {
    }

    public Email(String toEmail, String subject, String body, String pathToAttachment) {
        this.toEmail = toEmail;
        this.subject = subject;
        this.body = body;
        this.pathToAttachment = pathToAttachment;
    }

}
