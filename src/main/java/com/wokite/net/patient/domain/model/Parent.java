package com.wokite.net.patient.domain.model;


import com.wokite.net.shared.persistence.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "parent",
        indexes = {
                @Index(name = "idx_parent_entreprise", columnList = "entreprise_uuid"),
                @Index(name = "idx_parent_entreprise_nom", columnList = "entreprise_uuid,nom"),
                @Index(name = "idx_parent_telephone", columnList = "entreprise_uuid,telephone")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Parent extends TenantAwareEntity {

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 30)
    private String telephone;

    @Column(length = 180)
    private String email;

    @Column(length = 230)
    private String adresse;
}