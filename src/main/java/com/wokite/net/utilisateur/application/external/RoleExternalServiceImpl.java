package com.wokite.net.utilisateur.application.external;

import com.wokite.net.onboarding.application.external.RoleExternalService;
import com.wokite.net.utilisateur.application.dto.RoleOnboardingResponse;
import com.wokite.net.utilisateur.application.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoleExternalServiceImpl implements RoleExternalService {

    private final RoleService roleService;

    public RoleExternalServiceImpl(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public RoleOnboardingInfo rechercherRoleDisponible(UUID roleUuid) {
        RoleOnboardingResponse role = roleService.rechercherRolesDisponiblesPourOnboarding()
                .stream()
                .filter(item -> item.uuid().equals(roleUuid))
                .findFirst()
                .orElse(null);

        if (role == null) {
            return null;
        }

        return new RoleOnboardingInfo(role.uuid(), role.code(), role.libelle());
    }
}