package com.wokite.net.utilisateur.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wokite.net.utilisateur.domain.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByUuid(UUID uuid);
    Optional<Permission> findByCode(String code);
    boolean existsByCode(String code);
}
