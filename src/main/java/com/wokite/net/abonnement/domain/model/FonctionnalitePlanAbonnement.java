package com.wokite.net.abonnement.domain.model;

import com.wokite.net.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "fonctionnalite_plan_abonnement",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_fonctionnalite_plan_abonnement_plan_code",
                columnNames = {"plan_abonnement_id", "code"}
        )
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FonctionnalitePlanAbonnement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_abonnement_id", nullable = false)
    private PlanAbonnement planAbonnement;

    @Column(name = "code", nullable = false, updatable = false, length = 80)
    private String code;

    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;

}