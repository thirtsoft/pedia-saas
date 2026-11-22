package com.wokite.net.abonnement.domain.repository;

import com.wokite.net.abonnement.domain.model.PlanAbonnement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanAbonnementRepository extends JpaRepository<PlanAbonnement, Long> {

    Optional<PlanAbonnement> findByUuid(UUID uuid);

    boolean existsByCodeIgnoreCase(String code);

    List<PlanAbonnement> findAllByActifTrueOrderByPrixMensuelAsc();
}