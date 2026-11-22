package com.wokite.net.abonnement.domain.repository;

import com.wokite.net.abonnement.domain.model.FactureAbonnement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FactureAbonnementRepository extends JpaRepository<FactureAbonnement, Long> {

    Optional<FactureAbonnement> findByUuid(UUID uuid);

    boolean existsByNumero(String numero);

    List<FactureAbonnement> findAllByEntrepriseUuidOrderByDateEmissionDesc(UUID entrepriseUuid);
}