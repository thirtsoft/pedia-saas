package com.wokite.net.abonnement.web.controller;

import com.wokite.net.abonnement.application.dto.CreerFonctionnalitePlanAbonnementRequest;
import com.wokite.net.abonnement.application.dto.FonctionnalitePlanAbonnementResponse;
import com.wokite.net.abonnement.application.dto.ModifierFonctionnalitePlanAbonnementRequest;
import com.wokite.net.abonnement.application.service.FonctionnalitePlanAbonnementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/abonnement/plans/{planUuid}/fonctionnalites")
public class FonctionnalitePlanAbonnementController {

    private final FonctionnalitePlanAbonnementService service;

    public FonctionnalitePlanAbonnementController(FonctionnalitePlanAbonnementService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FonctionnalitePlanAbonnementResponse creer(
            @PathVariable UUID planUuid,
            @Valid @RequestBody CreerFonctionnalitePlanAbonnementRequest request) {
        return service.creer(planUuid, request);
    }

    @GetMapping
    public List<FonctionnalitePlanAbonnementResponse> lister(@PathVariable UUID planUuid) {
        return service.listerActives(planUuid);
    }

    @PutMapping("/{fonctionnaliteUuid}")
    public FonctionnalitePlanAbonnementResponse modifier(
            @PathVariable UUID fonctionnaliteUuid,
            @Valid @RequestBody ModifierFonctionnalitePlanAbonnementRequest request) {
        return service.modifier(fonctionnaliteUuid, request);
    }
}