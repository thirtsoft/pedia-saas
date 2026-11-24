package com.wokite.net.onboarding.application.external;

import java.time.LocalDate;
import java.util.UUID;

public interface AbonnementExternalService {

    AbonnementOnboardingInfo creerEssai(UUID entrepriseUuid, UUID planUuid, LocalDate dateDebut);

    record AbonnementOnboardingInfo(
            UUID uuid,
            UUID entrepriseUuid,
            UUID planUuid,
            LocalDate dateDebut,
            LocalDate dateFin,
            String statut
    ) {
    }
}