package com.wokite.net.abonnement.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ModifierPlanAbonnementRequest(
        @NotBlank @Size(min = 2, max = 120) String nom,
        @Size(max = 500) String description,
        @NotNull @DecimalMin("0.00") BigDecimal prixMensuel,
        @NotNull @Min(1) @Max(100000) Integer nombreUtilisateursMax,
        @NotNull Boolean actif
) {
}