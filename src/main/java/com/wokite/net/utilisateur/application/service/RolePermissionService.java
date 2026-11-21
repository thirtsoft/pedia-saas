package com.wokite.net.utilisateur.application.service;

import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.web.error.ResourceNotFoundException;
import com.wokite.net.utilisateur.application.dto.AjouterPermissionRoleRequest;
import com.wokite.net.utilisateur.application.dto.RolePermissionResponse;
import com.wokite.net.utilisateur.domain.exception.RolePermissionAlreadyExistsException;
import com.wokite.net.utilisateur.domain.model.Permission;
import com.wokite.net.utilisateur.domain.model.Role;
import com.wokite.net.utilisateur.domain.model.RolePermission;
import com.wokite.net.utilisateur.domain.repository.PermissionRepository;
import com.wokite.net.utilisateur.domain.repository.RolePermissionRepository;
import com.wokite.net.utilisateur.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RolePermissionService {

    private final RoleRepository roles;
    private final PermissionRepository permissions;
    private final RolePermissionRepository rolePermissions;

    public RolePermissionService(RoleRepository roles, PermissionRepository permissions, RolePermissionRepository rolePermissions) {
        this.roles = roles;
        this.permissions = permissions;
        this.rolePermissions = rolePermissions;
    }

    @Transactional
    public RolePermissionResponse ajouter(UUID ru, AjouterPermissionRoleRequest r) {
        Role role = findRole(ru);
        Permission p = permissions.findByUuid(r.permissionUuid()).orElseThrow(() -> new ResourceNotFoundException("Permission introuvable."));
        if (rolePermissions.existsByRoleUuidAndPermissionUuid(ru, r.permissionUuid())) {
            throw new RolePermissionAlreadyExistsException();
        }
        RolePermission x = new RolePermission();
        x.setRole(role);
        x.setPermission(p);
        x.setRecordStatus(RecordStatus.ACTIVE);
        return toResponse(rolePermissions.save(x));
    }

    public List<RolePermissionResponse> rechercherPermissions(UUID ru) {
        findRole(ru);
        return rolePermissions.findAllByRoleUuid(ru).stream().filter(x -> x.getRecordStatus() == RecordStatus.ACTIVE).map(this::toResponse).toList();
    }

    public void retirer(UUID ru, UUID pu) {
        RolePermission x = rolePermissions.findByRoleUuidAndPermissionUuid(ru, pu).orElseThrow(() -> new ResourceNotFoundException("Association rôle-permission introuvable."));
        x.setRecordStatus(RecordStatus.INACTIVE);
    }

    private Role findRole(UUID u) {
        return roles.findByUuid(u).orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable."));
    }

    private RolePermissionResponse toResponse(RolePermission x) {
        return new RolePermissionResponse(x.getRole().getUuid(), x.getRole().getCode(), x.getPermission().getUuid(), x.getPermission().getCode(), x.getPermission().getLibelle());
    }
}