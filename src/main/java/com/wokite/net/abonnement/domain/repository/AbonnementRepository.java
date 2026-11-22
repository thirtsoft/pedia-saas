package com.wokite.net.abonnement.domain.repository;

import com.wokite.net.abonnement.domain.model.Abonnement;
import com.wokite.net.abonnement.domain.model.StatutAbonnement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {

    Optional<Abonnement> findByUuid(UUID uuid);

    boolean existsByEntrepriseUuidAndStatutAbonnementIn(UUID entrepriseUuid, Collection<StatutAbonnement> statuts);

    Optional<Abonnement> findFirstByEntrepriseUuidAndStatutAbonnementInOrderByDateDebutDesc(UUID entrepriseUuid, Collection<StatutAbonnement> statuts);
}