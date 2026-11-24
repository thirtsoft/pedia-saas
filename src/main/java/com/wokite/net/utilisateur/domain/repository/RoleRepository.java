package com.wokite.net.utilisateur.domain.repository;

import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.utilisateur.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByUuid(UUID uuid);

    Optional<Role> findByCode(String code);

    boolean existsByCode(String code);

    @Query("""
            SELECT r
            FROM Role r
            WHERE r.code IN :codesRole
              AND r.recordStatus = :recordStatus
            ORDER BY r.id
            """)
    List<Role> findRolesDisponiblesPourOnboarding(@Param("codesRole") Collection<String> codesRole, @Param("recordStatus") RecordStatus recordStatus);
}