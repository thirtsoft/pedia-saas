package com.wokite.net.utilisateur.application.dto;

import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.utilisateur.domain.model.ProviderAuthentification;

import java.time.Instant;
import java.util.UUID;

public record UtilisateurResponse(
        UUID uuid,
        UUID entrepriseUuid,
        UUID roleUuid,
        String roleCode,
        String nom,
        String prenom,
        String email,
        ProviderAuthentification providerAuthentification,
        RecordStatus recordStatus,
        Instant createdAt,
        Instant updatedAt) {
}