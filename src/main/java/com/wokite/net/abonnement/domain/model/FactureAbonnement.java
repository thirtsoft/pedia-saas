package com.wokite.net.abonnement.domain.model;

import com.wokite.net.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "facture_abonnement")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FactureAbonnement extends BaseEntity {

    @Column(name = "entreprise_uuid", nullable = false)
    private UUID entrepriseUuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "abonnement_id", nullable = false)
    private Abonnement abonnement;

    @Column(name = "numero", nullable = false, unique = true, updatable = false, length = 50)
    private String numero;

    @Column(name = "date_emission", nullable = false)
    private LocalDate dateEmission;

    @Column(name = "date_echeance", nullable = false)
    private LocalDate dateEcheance;

    @Column(name = "montant", nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_facture_abonnement", nullable = false, length = 20)
    private StatutFactureAbonnement statutFactureAbonnement;

}