package com.wokite.net.abonnement.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreerFactureAbonnementRequest(
        @NotNull UUID entrepriseUuid,
        @NotNull UUID abonnementUuid,
        @Size(max = 50) String numero,
        @NotNull LocalDate dateEmission,
        @NotNull LocalDate dateEcheance,
        @NotNull @DecimalMin("0.00") BigDecimal montant
) {
}