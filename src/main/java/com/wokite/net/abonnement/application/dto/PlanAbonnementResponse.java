package com.wokite.net.abonnement.application.dto;

import com.wokite.net.shared.persistence.RecordStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PlanAbonnementResponse(
        UUID uuid, String code, String nom, String description, BigDecimal prixMensuel,
        Integer nombreUtilisateursMax, boolean actif, RecordStatus recordStatus,
        Instant createdAt, Instant updatedAt
) {
}