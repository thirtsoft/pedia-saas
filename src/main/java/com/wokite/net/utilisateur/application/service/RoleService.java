package com.wokite.net.utilisateur.application.service;

import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.web.error.ResourceNotFoundException;
import com.wokite.net.utilisateur.application.dto.CreerRoleRequest;
import com.wokite.net.utilisateur.application.dto.ModifierRoleRequest;
import com.wokite.net.utilisateur.application.dto.RoleOnboardingResponse;
import com.wokite.net.utilisateur.application.dto.RoleResponse;
import com.wokite.net.utilisateur.domain.exception.RoleCodeAlreadyExistsException;
import com.wokite.net.utilisateur.domain.model.Role;
import com.wokite.net.utilisateur.domain.model.RoleCode;
import com.wokite.net.utilisateur.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class RoleService {

    private static final List<String> CODES_ROLES_ONBOARDING = List.of(
            RoleCode.MEDECIN_INDEPENDANT.name(),
            RoleCode.ADMIN_CABINET.name()
    );

    private final RoleRepository repository;

    public RoleService(RoleRepository repository) {
        this.repository = repository;
    }

    public RoleResponse creer(CreerRoleRequest r) {
        String c = r.code().trim().toUpperCase(Locale.ROOT);
        if (repository.existsByCode(c)) {
            throw new RoleCodeAlreadyExistsException(c);
        }
        Role e = new Role();
        e.setCode(c);
        e.setLibelle(r.libelle().trim());
        e.setTypeRole(r.typeRole());
        e.setRecordStatus(RecordStatus.ACTIVE);
        return toResponse(repository.save(e));
    }

    public RoleResponse modifier(UUID u, ModifierRoleRequest r) {
        Role e = find(u);
        e.setLibelle(r.libelle().trim());
        return toResponse(e);
    }

    public RoleResponse rechercherParUuid(UUID u) {
        return toResponse(find(u));
    }

    public void desactiver(UUID u) {
        find(u).setRecordStatus(RecordStatus.INACTIVE);
    }

    private Role find(UUID u) {
        return repository.findByUuid(u).orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable."));
    }

    public List<RoleOnboardingResponse> rechercherRolesDisponiblesPourOnboarding() {
        return repository
                .findRolesDisponiblesPourOnboarding(CODES_ROLES_ONBOARDING, RecordStatus.ACTIVE)
                .stream()
                .map(this::toOnboardingResponse)
                .toList();
    }

    private RoleOnboardingResponse toOnboardingResponse(Role role) {
        return new RoleOnboardingResponse(role.getUuid(), role.getCode(), role.getLibelle());
    }

    private RoleResponse toResponse(Role e) {
        return new RoleResponse(e.getUuid(), e.getCode(), e.getLibelle(), e.getTypeRole(), e.getRecordStatus(), e.getCreatedAt(), e.getUpdatedAt());
    }
}