package com.wokite.net.onboarding.application.dto;

import com.wokite.net.onboarding.domain.model.OnboardingStatus;
import com.wokite.net.onboarding.domain.model.OnboardingStepCode;

import java.time.Instant;
import java.util.UUID;

public record OnboardingResponse(
        UUID uuid,
        UUID utilisateurUuid,
        UUID entrepriseUuid,
        OnboardingStatus onboardingStatus,
        OnboardingStepCode currentStep,
        Instant startedAt,
        Instant completedAt,
        Instant lastActivityAt,
        String errorCode,
        String errorMessage
) {
}