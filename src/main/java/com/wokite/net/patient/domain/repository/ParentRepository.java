package com.wokite.net.patient.domain.repository;

import com.wokite.net.patient.domain.model.Parent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    Optional<Parent> findByUuid(UUID uuid);

    boolean existsByUuid(UUID uuid);

    @Query("""
            SELECT p
            FROM Parent p
            WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :recherche, '%'))
               OR LOWER(p.prenom) LIKE LOWER(CONCAT('%', :recherche, '%'))
            """)
    Page<Parent> rechercher(
            @Param("recherche") String recherche,
            Pageable pageable
    );
}