package org.example.mailapp.repositories;

import org.example.mailapp.entities.ReceivedAttachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ReceivedAttachmentRepository extends CrudRepository<ReceivedAttachment, String> {
    Optional<ReceivedAttachment> findByFilename(String filename);
}
