package com.wokite.net.abonnement.web.controller;

import com.wokite.net.abonnement.application.dto.AbonnementResponse;
import com.wokite.net.abonnement.application.dto.CreerAbonnementRequest;
import com.wokite.net.abonnement.application.dto.ModifierStatutAbonnementRequest;
import com.wokite.net.abonnement.application.service.AbonnementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/abonnement")
public class AbonnementController {

    private final AbonnementService service;

    public AbonnementController(AbonnementService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AbonnementResponse creer(@Valid @RequestBody CreerAbonnementRequest request) {
        return service.creer(request);
    }

    @GetMapping("/{uuid}")
    public AbonnementResponse rechercher(@PathVariable UUID uuid) {
        return service.rechercher(uuid);
    }

    @PatchMapping("/{uuid}/statut")
    public AbonnementResponse modifierStatut(@PathVariable UUID uuid, @Valid @RequestBody ModifierStatutAbonnementRequest request) {
        return service.modifierStatut(uuid, request);
    }
}