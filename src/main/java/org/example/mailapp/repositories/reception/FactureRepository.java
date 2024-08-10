package org.example.mailapp.repositories.reception;

import org.example.mailapp.entities.reception.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactureRepository extends JpaRepository<Facture, String> {}

