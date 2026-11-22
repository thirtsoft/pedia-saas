package com.wokite.net.abonnement.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModifierFonctionnalitePlanAbonnementRequest(
        @NotBlank @Size(min = 2, max = 150) String nom,
        @Size(max = 500) String description,
        @NotNull Boolean actif
) {
}