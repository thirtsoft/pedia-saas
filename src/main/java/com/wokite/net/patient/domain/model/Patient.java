package com.wokite.net.patient.domain.model;

import com.wokite.net.shared.persistence.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "patient",
        indexes = {
                @Index(name = "idx_patient_entreprise", columnList = "entreprise_uuid"),
                @Index(name = "idx_patient_entreprise_nom", columnList = "entreprise_uuid,nom"),
                @Index(name = "idx_patient_entreprise_prenom", columnList = "entreprise_uuid,prenom")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends TenantAwareEntity {

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Sexe sexe;

}