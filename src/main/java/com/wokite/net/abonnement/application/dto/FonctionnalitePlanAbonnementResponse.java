package com.wokite.net.abonnement.application.dto;

import com.wokite.net.shared.persistence.RecordStatus;

import java.time.Instant;
import java.util.UUID;

public record FonctionnalitePlanAbonnementResponse(
        UUID uuid, UUID planUuid, String code, String nom, String description,
        boolean actif, RecordStatus recordStatus, Instant createdAt, Instant updatedAt
) {
}