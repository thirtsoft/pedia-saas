package com.wokite.net.patient.domain.repository;


import com.wokite.net.patient.domain.model.RelationParentPatient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RelationParentPatientRepository extends JpaRepository<RelationParentPatient, Long> {

    Optional<RelationParentPatient> findByUuid(UUID uuid);

    List<RelationParentPatient> findByPatientUuid(UUID patientUuid);

    List<RelationParentPatient> findByParentUuid(UUID parentUuid);

    Optional<RelationParentPatient> findByPatientUuidAndParentUuid(UUID patientUuid, UUID parentUuid);

    boolean existsByPatientUuidAndParentUuid(UUID patientUuid, UUID parentUuid);
}