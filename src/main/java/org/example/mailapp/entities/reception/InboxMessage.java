package org.example.mailapp.entities.reception;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
public class InboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromEmail;
    private String subject;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String body;
    private String filename;
    private String type;

    public InboxMessage(String fromEmail, String subject, String body, String filename, String type) {
        this.fromEmail = fromEmail;
        this.subject = subject;
        this.body = body;
        this.filename = filename;
        this.type = type;
    }

    public InboxMessage() {
    }
}
