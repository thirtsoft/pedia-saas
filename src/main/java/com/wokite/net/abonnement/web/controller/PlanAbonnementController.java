package com.wokite.net.abonnement.web.controller;

import com.wokite.net.abonnement.application.dto.CreerPlanAbonnementRequest;
import com.wokite.net.abonnement.application.dto.ModifierPlanAbonnementRequest;
import com.wokite.net.abonnement.application.dto.PlanAbonnementResponse;
import com.wokite.net.abonnement.application.service.PlanAbonnementService;
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
@RequestMapping("/abonnement/plans")
public class PlanAbonnementController {

    private final PlanAbonnementService service;

    public PlanAbonnementController(PlanAbonnementService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanAbonnementResponse creer(@Valid @RequestBody CreerPlanAbonnementRequest request) {
        return service.creer(request);
    }

    @GetMapping("/{uuid}")
    public PlanAbonnementResponse rechercher(@PathVariable UUID uuid) {
        return service.rechercher(uuid);
    }

    @GetMapping("/actifs")
    public List<PlanAbonnementResponse> listerActifs() {
        return service.listerActifs();
    }

    @PutMapping("/{uuid}")
    public PlanAbonnementResponse modifier(@PathVariable UUID uuid,
                                           @Valid @RequestBody ModifierPlanAbonnementRequest request) {
        return service.modifier(uuid, request);
    }
}