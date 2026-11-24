package com.wokite.net.abonnement.application.dto;

import java.util.UUID;

public record FonctionnalitePlanResponse(
        UUID uuid,
        String code,
        String nom,
        String description
) {
}