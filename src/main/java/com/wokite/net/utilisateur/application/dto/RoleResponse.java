package com.wokite.net.utilisateur.application.dto;

import com.wokite.net.shared.persistence.RecordStatus;

import java.time.Instant;
import java.util.UUID;

public record RoleResponse(
        UUID uuid,
        String code,
        String libelle,
        RecordStatus recordStatus,
        Instant createdAt,
        Instant updatedAt) {
}