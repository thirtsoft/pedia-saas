package com.wokite.net.utilisateur.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wokite.net.utilisateur.domain.model.RolePermission;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findAllByRoleUuid(UUID roleUuid);
    Optional<RolePermission> findByRoleUuidAndPermissionUuid(UUID roleUuid, UUID permissionUuid);
    boolean existsByRoleUuidAndPermissionUuid(UUID roleUuid, UUID permissionUuid);
}
