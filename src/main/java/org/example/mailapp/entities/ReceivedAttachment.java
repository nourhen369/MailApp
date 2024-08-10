package org.example.mailapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fromEmail;
    private String filename;
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] content;

    public ReceivedAttachment(String fromEmail, String filename, byte[] content) {
        this.fromEmail = fromEmail;
        this.filename = filename;
        this.content = content;
    }
    public ReceivedAttachment(String filename, byte[] content) {
        this.filename = filename;
        this.content = content;
    }
}
