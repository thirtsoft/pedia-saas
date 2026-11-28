package com.wokite.net.abonnement.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PlanAbonnementOnbordingResponse(
        UUID uuid,
        String code,
        String nom,
        String description,
        BigDecimal prixMensuel,
        Integer nombreUtilisateursMax,
        List<FonctionnalitePlanResponse> fonctionnalites
) {

    public PlanAbonnementOnbordingResponse {
        fonctionnalites = fonctionnalites == null
                ? List.of() : List.copyOf(fonctionnalites);
    }
}