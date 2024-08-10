package org.example.mailapp.repositories.reception;

import org.example.mailapp.entities.reception.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, String> {}

