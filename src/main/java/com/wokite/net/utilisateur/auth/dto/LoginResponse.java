package com.wokite.net.utilisateur.auth.dto;

import com.wokite.net.onboarding.domain.model.OnboardingStatus;
import com.wokite.net.onboarding.domain.model.OnboardingStepCode;

import java.util.Set;
import java.util.UUID;

public record LoginResponse(
        String token,
        String type,
        UUID utilisateurUuid,
        UUID entrepriseUuid,
        String role,
        Set<String> permissions,
        OnboardingStatus onboardingStatus,
        OnboardingStepCode onboardingStep
) {
    public LoginResponse {
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }

    @Override
    public Set<String> permissions() {
        return Set.copyOf(permissions);
    }
}