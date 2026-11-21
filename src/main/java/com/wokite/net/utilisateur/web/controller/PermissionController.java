package com.wokite.net.utilisateur.web.controller;

import com.wokite.net.utilisateur.application.dto.CreerPermissionRequest;
import com.wokite.net.utilisateur.application.dto.ModifierPermissionRequest;
import com.wokite.net.utilisateur.application.dto.PermissionResponse;
import com.wokite.net.utilisateur.application.service.PermissionService;
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

import java.util.UUID;

@RestController
@RequestMapping("/permissions")
@Validated
public class PermissionController {
    private final PermissionService service;

    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PermissionResponse> creer(@Valid @RequestBody CreerPermissionRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.creer(r));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("isAuthenticated()")
    public PermissionResponse rechercher(@PathVariable UUID uuid) {
        return service.rechercherParUuid(uuid);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("isAuthenticated()")
    public PermissionResponse modifier(@PathVariable UUID uuid, @Valid @RequestBody ModifierPermissionRequest r) {
        return service.modifier(uuid, r);
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> desactiver(@PathVariable UUID uuid) {
        service.desactiver(uuid);
        return ResponseEntity.noContent().build();
    }
}