package com.wokite.net.onboarding.application.external;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PlanAbonnementExternalService {

    PlanOnboardingInfo rechercherPlanDisponible(UUID planUuid);

    List<PlanFonctionnaliteInfo> rechercherFonctionnalites(UUID planUuid);

    record PlanOnboardingInfo(
            UUID uuid,
            String code,
            String nom,
            BigDecimal prixMensuel,
            Integer nombreUtilisateursMax
    ) {
    }

    record PlanFonctionnaliteInfo(
            UUID uuid,
            String code,
            String nom,
            String description
    ) {
    }
}