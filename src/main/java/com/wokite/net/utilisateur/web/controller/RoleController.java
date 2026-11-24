package com.wokite.net.utilisateur.web.controller;

import com.wokite.net.utilisateur.application.dto.CreerRoleRequest;
import com.wokite.net.utilisateur.application.dto.ModifierRoleRequest;
import com.wokite.net.utilisateur.application.dto.RoleOnboardingResponse;
import com.wokite.net.utilisateur.application.dto.RoleResponse;
import com.wokite.net.utilisateur.application.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
@Validated
public class RoleController {
    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoleResponse> creer(@Valid @RequestBody CreerRoleRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.creer(r));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("isAuthenticated()")
    public RoleResponse rechercher(@PathVariable UUID uuid) {
        return service.rechercherParUuid(uuid);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("isAuthenticated()")
    public RoleResponse modifier(@PathVariable UUID uuid, @Valid @RequestBody ModifierRoleRequest r) {
        return service.modifier(uuid, r);
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> desactiver(@PathVariable UUID uuid) {
        service.desactiver(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/onboarding")
    public ResponseEntity<List<RoleOnboardingResponse>> rechercherRolesDisponiblesPourOnboarding() {
        return ResponseEntity.ok(service.rechercherRolesDisponiblesPourOnboarding());
    }
}