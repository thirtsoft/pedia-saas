package com.wokite.net.abonnement.web.controller;

import com.wokite.net.abonnement.application.dto.CreerFactureAbonnementRequest;
import com.wokite.net.abonnement.application.dto.FactureAbonnementResponse;
import com.wokite.net.abonnement.application.dto.ModifierStatutFactureAbonnementRequest;
import com.wokite.net.abonnement.application.service.FactureAbonnementService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/abonnement/factures")
public class FactureAbonnementController {

    private final FactureAbonnementService service;

    public FactureAbonnementController(FactureAbonnementService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FactureAbonnementResponse creer(@Valid @RequestBody CreerFactureAbonnementRequest request) {
        return service.creer(request);
    }

    @GetMapping("/{uuid}")
    public FactureAbonnementResponse rechercher(@PathVariable UUID uuid) {
        return service.rechercher(uuid);
    }

    @GetMapping("/entreprise/{entrepriseUuid}")
    public List<FactureAbonnementResponse> lister(@PathVariable UUID entrepriseUuid) {
        return service.listerParEntreprise(entrepriseUuid);
    }

    @PatchMapping("/{uuid}/statut")
    public FactureAbonnementResponse modifierStatut(
            @PathVariable UUID uuid,
            @Valid @RequestBody ModifierStatutFactureAbonnementRequest request) {
        return service.modifierStatut(uuid, request);
    }
}