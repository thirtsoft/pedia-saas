package com.wokite.net.onboarding.domain.exception;

import com.wokite.net.shared.web.error.ResourceNotFoundException;

import java.util.UUID;

public class OnboardingNotFoundException
        extends ResourceNotFoundException {

    public OnboardingNotFoundException(UUID utilisateurUuid) {
        super(
                "Onboarding introuvable pour l'utilisateur : "
                        + utilisateurUuid
        );
    }
}