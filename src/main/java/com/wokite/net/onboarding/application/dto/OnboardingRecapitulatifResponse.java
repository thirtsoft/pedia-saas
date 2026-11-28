package com.wokite.net.onboarding.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OnboardingRecapitulatifResponse(

        UUID roleUuid,
        String roleCode,
        String roleLibelle,

        String nomEntreprise,
        String telephone,
        String email,
        String adresse,

        UUID planUuid,
        String planCode,
        String planNom,
        BigDecimal prixMensuel,
        Integer nombreUtilisateursMax,

        List<OnboardingRecapitulatifFonctionnaliteResponse> fonctionnalites

) {
    public OnboardingRecapitulatifResponse {
        fonctionnalites = fonctionnalites == null
                ? List.of() : List.copyOf(fonctionnalites);
    }
}