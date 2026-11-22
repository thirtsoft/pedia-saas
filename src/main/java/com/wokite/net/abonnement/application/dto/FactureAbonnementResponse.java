package com.wokite.net.abonnement.application.dto;

import com.wokite.net.abonnement.domain.model.StatutFactureAbonnement;
import com.wokite.net.shared.persistence.RecordStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FactureAbonnementResponse(
        UUID uuid, UUID entrepriseUuid, UUID abonnementUuid, String numero,
        LocalDate dateEmission, LocalDate dateEcheance, BigDecimal montant,
        StatutFactureAbonnement statut, RecordStatus recordStatus,
        Instant createdAt, Instant updatedAt
) {
}