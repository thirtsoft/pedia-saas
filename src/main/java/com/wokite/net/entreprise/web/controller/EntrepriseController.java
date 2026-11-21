package com.wokite.net.entreprise.web.controller;

import com.wokite.net.entreprise.application.dto.CreerEntrepriseRequest;
import com.wokite.net.entreprise.application.dto.EntrepriseResponse;
import com.wokite.net.entreprise.application.service.EntrepriseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

/**
 * API de gestion des entreprises.
 */
@RestController
@RequestMapping("/entreprises")
@Validated
public class EntrepriseController {

    private final EntrepriseService entrepriseService;

    public EntrepriseController(EntrepriseService entrepriseService) {
        this.entrepriseService = entrepriseService;
    }

    @PostMapping
    public ResponseEntity<EntrepriseResponse> creer(@Valid @RequestBody CreerEntrepriseRequest request) {
        EntrepriseResponse response = entrepriseService.creer(request);
        URI location = URI.create("/entreprises/" + response.uuid());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<EntrepriseResponse> rechercher(@PathVariable UUID uuid) {
        return ResponseEntity.ok(entrepriseService.rechercherParUuid(uuid));
    }
}