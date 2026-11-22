package com.wokite.net.utilisateur.application.dto;

import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.utilisateur.domain.model.ProviderAuthentification;

import java.time.Instant;
import java.util.UUID;

public record InscriptionResponse(
        UUID utilisateurUuid,
        String nom,
        String prenom,
        String email,
        ProviderAuthentification providerAuthentification,
        boolean onboardingRequis,
        RecordStatus recordStatus,
        Instant createdAt
) {
}