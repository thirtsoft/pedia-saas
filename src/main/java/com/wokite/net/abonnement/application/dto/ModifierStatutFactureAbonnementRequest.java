package com.wokite.net.abonnement.application.dto;

import com.wokite.net.abonnement.domain.model.StatutFactureAbonnement;
import jakarta.validation.constraints.NotNull;

public record ModifierStatutFactureAbonnementRequest(@NotNull StatutFactureAbonnement statut) {
}