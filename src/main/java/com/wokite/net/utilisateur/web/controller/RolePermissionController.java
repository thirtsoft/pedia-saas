package com.wokite.net.utilisateur.web.controller;

import com.wokite.net.utilisateur.application.dto.AjouterPermissionRoleRequest;
import com.wokite.net.utilisateur.application.dto.RolePermissionResponse;
import com.wokite.net.utilisateur.application.service.RolePermissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roles/{roleUuid}/permissions")
@Validated
public class RolePermissionController {
    private final RolePermissionService service;

    public RolePermissionController(RolePermissionService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RolePermissionResponse> ajouter(@PathVariable UUID roleUuid, @Valid @RequestBody AjouterPermissionRoleRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.ajouter(roleUuid, r));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<RolePermissionResponse> lister(@PathVariable UUID roleUuid) {
        return service.rechercherPermissions(roleUuid);
    }

    @DeleteMapping("/{permissionUuid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> retirer(@PathVariable UUID roleUuid, @PathVariable UUID permissionUuid) {
        service.retirer(roleUuid, permissionUuid);
        return ResponseEntity.noContent().build();
    }
}