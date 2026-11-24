package com.wokite.net.onboarding.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OnboardingRoleRequest(

        @NotNull(message = "Le rôle est obligatoire.")
        UUID roleUuid

) {
}