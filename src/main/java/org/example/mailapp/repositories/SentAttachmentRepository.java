package org.example.mailapp.repositories;

import org.example.mailapp.entities.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentAttachmentRepository extends CrudRepository<SentAttachment, String> {
}
