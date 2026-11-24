package com.wokite.net.abonnement.domain.repository;

import com.wokite.net.abonnement.domain.model.FonctionnalitePlanAbonnement;
import com.wokite.net.shared.persistence.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FonctionnalitePlanAbonnementRepository extends JpaRepository<FonctionnalitePlanAbonnement, Long> {

    Optional<FonctionnalitePlanAbonnement> findByUuid(UUID uuid);

    List<FonctionnalitePlanAbonnement> findAllByPlanAbonnementUuidAndActifTrueOrderByNomAsc(UUID planUuid);

    boolean existsByPlanAbonnementUuidAndCodeIgnoreCase(UUID planUuid, String code);

    List<FonctionnalitePlanAbonnement> findByPlanAbonnementIdInAndActifTrueAndRecordStatusOrderByIdAsc(
            Collection<Long> planAbonnementIds, RecordStatus recordStatus
    );
}