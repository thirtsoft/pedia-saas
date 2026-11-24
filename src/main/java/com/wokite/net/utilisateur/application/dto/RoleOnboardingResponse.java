package com.wokite.net.utilisateur.application.dto;

import java.util.UUID;

public record RoleOnboardingResponse(
        UUID uuid,
        String code,
        String libelle
) {
}