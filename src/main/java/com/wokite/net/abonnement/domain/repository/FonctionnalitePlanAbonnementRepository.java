package com.wokite.net.abonnement.domain.repository;

import com.wokite.net.abonnement.domain.model.FonctionnalitePlanAbonnement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FonctionnalitePlanAbonnementRepository
        extends JpaRepository<FonctionnalitePlanAbonnement, Long> {

    Optional<FonctionnalitePlanAbonnement> findByUuid(UUID uuid);

    List<FonctionnalitePlanAbonnement> findAllByPlanAbonnementUuidAndActifTrueOrderByNomAsc(
            UUID planUuid
    );

    boolean existsByPlanAbonnementUuidAndCodeIgnoreCase(
            UUID planUuid,
            String code
    );
}