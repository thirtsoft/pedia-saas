package com.wokite.net.abonnement.domain.model;

import com.wokite.net.shared.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "plan_abonnement")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanAbonnement extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, updatable = false, length = 50)
    private String code;

    @Column(name = "nom", nullable = false, length = 120)
    private String nom;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "prix_mensuel", nullable = false, precision = 15, scale = 2)
    private BigDecimal prixMensuel;

    @Column(name = "nombre_utilisateurs_max", nullable = false)
    private Integer nombreUtilisateursMax;

    @Column(name = "actif", nullable = false)
    private boolean actif = true;


}