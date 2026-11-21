package com.wokite.net.utilisateur.application.service;

import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.web.error.ResourceNotFoundException;
import com.wokite.net.utilisateur.application.dto.CreerPermissionRequest;
import com.wokite.net.utilisateur.application.dto.ModifierPermissionRequest;
import com.wokite.net.utilisateur.application.dto.PermissionResponse;
import com.wokite.net.utilisateur.domain.exception.PermissionCodeAlreadyExistsException;
import com.wokite.net.utilisateur.domain.model.Permission;
import com.wokite.net.utilisateur.domain.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class PermissionService {

    private final PermissionRepository repository;

    public PermissionService(PermissionRepository repository) {
        this.repository = repository;
    }

    public PermissionResponse creer(CreerPermissionRequest r) {
        String c = r.code().trim().toUpperCase(Locale.ROOT);
        if (repository.existsByCode(c)) {
            throw new PermissionCodeAlreadyExistsException(c);
        }
        Permission e = new Permission();
        e.setCode(c);
        e.setLibelle(r.libelle().trim());
        e.setRecordStatus(RecordStatus.ACTIVE);
        return toResponse(repository.save(e));
    }

    public PermissionResponse modifier(UUID u, ModifierPermissionRequest r) {
        Permission e = find(u);
        e.setLibelle(r.libelle().trim());
        return toResponse(e);
    }

    public PermissionResponse rechercherParUuid(UUID u) {
        return toResponse(find(u));
    }

    public void desactiver(UUID u) {
        find(u).setRecordStatus(RecordStatus.INACTIVE);
    }

    private Permission find(UUID u) {
        return repository.findByUuid(u).orElseThrow(() -> new ResourceNotFoundException("Permission introuvable."));
    }

    private PermissionResponse toResponse(Permission e) {
        return new PermissionResponse(e.getUuid(), e.getCode(), e.getLibelle(), e.getRecordStatus(), e.getCreatedAt(), e.getUpdatedAt());
    }
}