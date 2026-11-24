package com.wokite.net.onboarding.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OnboardingPlanRequest(

        @NotNull(message = "La formule est obligatoire.")
        UUID planUuid

) {
}