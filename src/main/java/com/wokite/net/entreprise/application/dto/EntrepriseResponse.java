package com.wokite.net.entreprise.application.dto;

import com.wokite.net.entreprise.domain.model.TypeEntreprise;
import com.wokite.net.shared.persistence.RecordStatus;

import java.time.Instant;
import java.util.UUID;

public record EntrepriseResponse(
        UUID uuid,
        String code,
        String nom,
        TypeEntreprise type,
        String email,
        String telephone,
        String adresse,
        RecordStatus recordStatus,
        Instant createdAt,
        Instant updatedAt
) {
}