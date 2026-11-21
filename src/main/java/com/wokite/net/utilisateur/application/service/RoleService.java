package com.wokite.net.utilisateur.application.service;

import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.web.error.ResourceNotFoundException;
import com.wokite.net.utilisateur.application.dto.CreerRoleRequest;
import com.wokite.net.utilisateur.application.dto.ModifierRoleRequest;
import com.wokite.net.utilisateur.application.dto.RoleResponse;
import com.wokite.net.utilisateur.domain.exception.RoleCodeAlreadyExistsException;
import com.wokite.net.utilisateur.domain.model.Role;
import com.wokite.net.utilisateur.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class RoleService {

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

    private RoleResponse toResponse(Role e) {
        return new RoleResponse(e.getUuid(), e.getCode(), e.getLibelle(), e.getRecordStatus(), e.getCreatedAt(), e.getUpdatedAt());
    }
}