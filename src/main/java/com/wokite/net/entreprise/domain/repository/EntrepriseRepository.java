package com.wokite.net.entreprise.domain.repository;

import com.wokite.net.entreprise.domain.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {

    Optional<Entreprise> findByUuid(UUID uuid);

    boolean existsByCodeIgnoreCase(String code);
}