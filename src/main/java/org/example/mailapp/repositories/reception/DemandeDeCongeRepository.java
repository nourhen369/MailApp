package org.example.mailapp.repositories.reception;

import org.example.mailapp.entities.reception.DemandeDeConge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandeDeCongeRepository extends JpaRepository<DemandeDeConge, String> {
}
