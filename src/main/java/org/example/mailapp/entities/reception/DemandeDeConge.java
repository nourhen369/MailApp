package org.example.mailapp.entities.reception;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("DEMANDECONGE")
public class DemandeDeConge extends InboxMessage {
}

