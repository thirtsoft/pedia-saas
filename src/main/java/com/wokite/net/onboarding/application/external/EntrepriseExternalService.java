package com.wokite.net.onboarding.application.external;

import java.util.UUID;

public interface EntrepriseExternalService {

    EntrepriseOnboardingInfo creer(
            String nom,
            String telephone,
            String email,
            String adresse,
            String type
    );

    record EntrepriseOnboardingInfo(
            UUID uuid,
            String code,
            String nom,
            String type
    ) {
    }
}