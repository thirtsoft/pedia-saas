package com.wokite.net.onboarding.application.service;

import com.wokite.net.onboarding.application.dto.OnboardingResponse;
import com.wokite.net.onboarding.domain.exception.OnboardingNotFoundException;
import com.wokite.net.onboarding.domain.model.Onboarding;
import com.wokite.net.onboarding.domain.model.OnboardingStatus;
import com.wokite.net.onboarding.domain.model.OnboardingStepCode;
import com.wokite.net.onboarding.domain.repository.OnboardingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OnboardingService {

    private final OnboardingRepository onboardings;

    public OnboardingService(OnboardingRepository onboardings) {
        this.onboardings = onboardings;
    }

    @Transactional
    public OnboardingResponse creer(UUID utilisateurUuid) {
        return onboardings.findByUtilisateurUuid(utilisateurUuid)
                .map(this::toResponse)
                .orElseGet(() -> {

                    Instant now = Instant.now();
                    Onboarding onboarding = new Onboarding();
                    onboarding.setUtilisateurUuid(utilisateurUuid);
                    onboarding.setOnboardingStatus(OnboardingStatus.EN_COURS);
                    onboarding.setCurrentStep(OnboardingStepCode.CHOIX_ROLE);
                    onboarding.setStartedAt(now);
                    onboarding.setLastActivityAt(now);
                    return toResponse(onboardings.save(onboarding));
                });
    }

    public OnboardingResponse rechercherParUtilisateur(UUID utilisateurUuid) {
        return toResponse(trouverParUtilisateur(utilisateurUuid));
    }

    @Transactional
    public OnboardingResponse mettreAJourEtape(UUID utilisateurUuid, OnboardingStepCode stepCode) {
        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);
        if (onboarding.getOnboardingStatus() == OnboardingStatus.TERMINE) {
            return toResponse(onboarding);
        }
        onboarding.setCurrentStep(stepCode);
        onboarding.setOnboardingStatus(OnboardingStatus.EN_COURS);
        onboarding.setLastActivityAt(Instant.now());
        onboarding.setErrorCode(null);
        onboarding.setErrorMessage(null);
        return toResponse(onboarding);
    }

    @Transactional
    public OnboardingResponse terminer(UUID utilisateurUuid, UUID entrepriseUuid) {
        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);
        Instant now = Instant.now();
        onboarding.setEntrepriseUuid(entrepriseUuid);
        onboarding.setOnboardingStatus(OnboardingStatus.TERMINE);
        onboarding.setCurrentStep(OnboardingStepCode.VALIDATION);
        onboarding.setCompletedAt(now);
        onboarding.setLastActivityAt(now);
        onboarding.setErrorCode(null);
        onboarding.setErrorMessage(null);
        return toResponse(onboarding);
    }

    @Transactional
    public OnboardingResponse enregistrerErreur(
            UUID utilisateurUuid,
            OnboardingStepCode stepCode,
            String errorCode,
            String errorMessage) {

        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);
        onboarding.setCurrentStep(stepCode);
        onboarding.setOnboardingStatus(OnboardingStatus.ERREUR);
        onboarding.setLastActivityAt(Instant.now());
        onboarding.setErrorCode(errorCode);
        onboarding.setErrorMessage(errorMessage);
        return toResponse(onboarding);
    }

    @Transactional
    public OnboardingResponse abandonner(UUID utilisateurUuid) {
        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);
        onboarding.setOnboardingStatus(OnboardingStatus.ABANDONNE);
        onboarding.setLastActivityAt(Instant.now());
        return toResponse(onboarding);
    }

    private Onboarding trouverParUtilisateur(UUID utilisateurUuid) {
        return onboardings.findByUtilisateurUuid(utilisateurUuid)
                .orElseThrow(() -> new OnboardingNotFoundException(utilisateurUuid));
    }

    private OnboardingResponse toResponse(Onboarding onboarding) {
        return new OnboardingResponse(
                onboarding.getUuid(),
                onboarding.getUtilisateurUuid(),
                onboarding.getEntrepriseUuid(),
                onboarding.getOnboardingStatus(),
                onboarding.getCurrentStep(),
                onboarding.getStartedAt(),
                onboarding.getCompletedAt(),
                onboarding.getLastActivityAt(),
                onboarding.getErrorCode(),
                onboarding.getErrorMessage()
        );
    }
}