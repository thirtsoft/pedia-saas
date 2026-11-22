package com.wokite.net.abonnement.application.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreerAbonnementRequest(
        @NotNull UUID entrepriseUuid,
        @NotNull UUID planUuid,
        @NotNull LocalDate dateDebut
) {
}