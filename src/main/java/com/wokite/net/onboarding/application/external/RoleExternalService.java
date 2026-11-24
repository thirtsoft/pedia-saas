package com.wokite.net.onboarding.application.external;

import java.util.UUID;

public interface RoleExternalService {

    RoleOnboardingInfo rechercherRoleDisponible(UUID roleUuid);

    record RoleOnboardingInfo(UUID uuid, String code, String libelle) {
    }
}