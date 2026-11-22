package com.wokite.net.abonnement.application.dto;

import com.wokite.net.abonnement.domain.model.StatutAbonnement;
import jakarta.validation.constraints.NotNull;

public record ModifierStatutAbonnementRequest(@NotNull StatutAbonnement statut) {
}