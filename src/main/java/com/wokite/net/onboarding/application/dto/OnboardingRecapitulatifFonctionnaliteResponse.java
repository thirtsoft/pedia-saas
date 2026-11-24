package com.wokite.net.onboarding.application.dto;

import java.util.UUID;

public record OnboardingRecapitulatifFonctionnaliteResponse(

        UUID uuid,
        String code,
        String nom,
        String description

) {
}