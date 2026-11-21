package com.wokite.net.utilisateur.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wokite.net.utilisateur.domain.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByUuid(UUID uuid);
    Optional<Role> findByCode(String code);
    boolean existsByCode(String code);
}
