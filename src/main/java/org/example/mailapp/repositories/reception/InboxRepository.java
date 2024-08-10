package org.example.mailapp.repositories.reception;

import org.example.mailapp.entities.reception.InboxMessage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InboxRepository extends CrudRepository<InboxMessage, String> {
    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE inbox_message AUTO_INCREMENT = 1", nativeQuery = true)
    void executeUpdate(String sql);

    List<InboxMessage> findByType(String type);
}

