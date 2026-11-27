package com.wokite.net.utilisateur.application.dto;

import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.utilisateur.domain.model.TypeRole;

import java.time.Instant;
import java.util.UUID;

public record RoleResponse(
        UUID uuid,
        String code,
        String libelle,
        TypeRole typeRole,
        RecordStatus recordStatus,
        Instant createdAt,
        Instant updatedAt) {
}