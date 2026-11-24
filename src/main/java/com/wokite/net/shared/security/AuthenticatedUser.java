package com.wokite.net.shared.security;

import com.wokite.net.onboarding.domain.model.OnboardingStatus;
import com.wokite.net.onboarding.domain.model.OnboardingStepCode;

import java.util.Set;
import java.util.UUID;

public record AuthenticatedUser(
        UUID utilisateurUuid,
        UUID entrepriseUuid,
        String role,
        Set<String> permissions,
        OnboardingStatus onboardingStatus,
        OnboardingStepCode onboardingStep
) {
}