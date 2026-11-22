package com.wokite.net.utilisateur.domain.repository;

import com.wokite.net.utilisateur.domain.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByUuid(UUID uuid);

    Optional<Utilisateur> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndUuidNot(String email, UUID uuid);

    Optional<Utilisateur> findByGoogleSubject(String googleSubject);

    boolean existsByGoogleSubject(String googleSubject);

    Optional<Utilisateur> findByMobile(String mobile);

    boolean existsByMobile(String mobile);

}