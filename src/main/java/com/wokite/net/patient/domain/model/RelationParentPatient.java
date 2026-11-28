package com.wokite.net.patient.domain.model;

import com.wokite.net.shared.persistence.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "relation_parent_patient",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_relation_parent_patient",
                        columnNames = {"parent_uuid", "patient_uuid"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelationParentPatient extends TenantAwareEntity {

    @Column(name = "parent_uuid", nullable = false, updatable = false)
    private UUID parentUuid;

    @Column(name = "patient_uuid", nullable = false, updatable = false)
    private UUID patientUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_relation", nullable = false, length = 30)
    private TypeRelation typeRelation;

}