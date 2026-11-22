package com.wokite.net.abonnement.application.dto;

import com.wokite.net.abonnement.domain.model.StatutAbonnement;
import com.wokite.net.shared.persistence.RecordStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AbonnementResponse(
        UUID uuid, UUID entrepriseUuid, UUID planUuid, LocalDate dateDebut,
        LocalDate dateFin, StatutAbonnement statut, RecordStatus recordStatus,
        Instant createdAt, Instant updatedAt
) {
}