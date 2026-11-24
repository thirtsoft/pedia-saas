package com.wokite.net.abonnement.application.service;

import com.wokite.net.abonnement.application.dto.CreerPlanAbonnementRequest;
import com.wokite.net.abonnement.application.dto.FonctionnalitePlanResponse;
import com.wokite.net.abonnement.application.dto.ModifierPlanAbonnementRequest;
import com.wokite.net.abonnement.application.dto.PlanAbonnementOnbordingResponse;
import com.wokite.net.abonnement.application.dto.PlanAbonnementResponse;
import com.wokite.net.abonnement.domain.exception.PlanAbonnementCodeAlreadyExistsException;
import com.wokite.net.abonnement.domain.exception.PlanAbonnementNotFoundException;
import com.wokite.net.abonnement.domain.model.FonctionnalitePlanAbonnement;
import com.wokite.net.abonnement.domain.model.PlanAbonnement;
import com.wokite.net.abonnement.domain.repository.FonctionnalitePlanAbonnementRepository;
import com.wokite.net.abonnement.domain.repository.PlanAbonnementRepository;
import com.wokite.net.shared.persistence.RecordStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlanAbonnementService {

    private final PlanAbonnementRepository repository;
    private final FonctionnalitePlanAbonnementRepository fonctionnalitePlanAbonnementRepository;

    public PlanAbonnementService(PlanAbonnementRepository repository, FonctionnalitePlanAbonnementRepository fonctionnalitePlanAbonnementRepository) {
        this.repository = repository;
        this.fonctionnalitePlanAbonnementRepository = fonctionnalitePlanAbonnementRepository;
    }

    @Transactional
    public PlanAbonnementResponse creer(CreerPlanAbonnementRequest request) {
        String code = normalizeCode(request.code());
        if (repository.existsByCodeIgnoreCase(code)) {
            throw new PlanAbonnementCodeAlreadyExistsException(code);
        }
        PlanAbonnement plan = new PlanAbonnement();
        plan.setCode(code);
        plan.setNom(request.nom().trim());
        plan.setDescription(normalizeNullable(request.description()));
        plan.setPrixMensuel(request.prixMensuel());
        plan.setNombreUtilisateursMax(request.nombreUtilisateursMax());
        plan.setActif(true);
        plan.setRecordStatus(RecordStatus.ACTIVE);
        return toResponse(repository.save(plan));
    }

    public PlanAbonnementResponse rechercher(UUID uuid) {
        return toResponse(getEntity(uuid));
    }

    public List<PlanAbonnementResponse> listerActifs() {
        return repository.findAllByActifTrueOrderByPrixMensuelAsc().stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public PlanAbonnementResponse modifier(UUID uuid, ModifierPlanAbonnementRequest request) {
        PlanAbonnement plan = getEntity(uuid);
        plan.setNom(request.nom().trim());
        plan.setDescription(normalizeNullable(request.description()));
        plan.setPrixMensuel(request.prixMensuel());
        plan.setNombreUtilisateursMax(request.nombreUtilisateursMax());
        plan.setActif(request.actif());
        return toResponse(repository.save(plan));
    }

    private PlanAbonnement getEntity(UUID uuid) {
        return repository.findByUuid(uuid)
                .orElseThrow(() -> new PlanAbonnementNotFoundException(uuid));
    }

    public List<PlanAbonnementOnbordingResponse> rechercherPlansDisponibles() {

        List<PlanAbonnement> plans = repository.findByActifTrueAndRecordStatusOrderByIdAsc(RecordStatus.ACTIVE);

        if (plans.isEmpty()) {
            return List.of();
        }

        List<Long> planIds = plans.stream()
                .map(PlanAbonnement::getId)
                .toList();

        List<FonctionnalitePlanAbonnement> fonctionnalites = fonctionnalitePlanAbonnementRepository
                .findByPlanAbonnementIdInAndActifTrueAndRecordStatusOrderByIdAsc(planIds, RecordStatus.ACTIVE);

        Map<Long, List<FonctionnalitePlanAbonnement>> fonctionnalitesParPlan =
                fonctionnalites.stream()
                        .collect(Collectors.groupingBy(
                                fonctionnalite ->
                                        fonctionnalite
                                                .getPlanAbonnement()
                                                .getId()
                        ));

        return plans.stream()
                .map(plan -> toOnboardingResponse(
                        plan,
                        fonctionnalitesParPlan.getOrDefault(plan.getId(), List.of())
                ))
                .toList();
    }

    private PlanAbonnementOnbordingResponse toOnboardingResponse(PlanAbonnement plan, List<FonctionnalitePlanAbonnement> fonctionnalites) {

        List<FonctionnalitePlanResponse> fonctionnaliteResponses = fonctionnalites
                .stream()
                .map(this::toFonctionnaliteResponse)
                .toList();

        return new PlanAbonnementOnbordingResponse(
                plan.getUuid(),
                plan.getCode(),
                plan.getNom(),
                plan.getDescription(),
                plan.getPrixMensuel(),
                plan.getNombreUtilisateursMax(),
                fonctionnaliteResponses
        );
    }

    private FonctionnalitePlanResponse toFonctionnaliteResponse(FonctionnalitePlanAbonnement fonctionnalite) {
        return new FonctionnalitePlanResponse(
                fonctionnalite.getUuid(),
                fonctionnalite.getCode(),
                fonctionnalite.getNom(),
                fonctionnalite.getDescription()
        );
    }

    private PlanAbonnementResponse toResponse(PlanAbonnement p) {
        return new PlanAbonnementResponse(p.getUuid(), p.getCode(), p.getNom(),
                p.getDescription(), p.getPrixMensuel(), p.getNombreUtilisateursMax(),
                p.isActif(), p.getRecordStatus(), p.getCreatedAt(), p.getUpdatedAt());
    }

    private String normalizeCode(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isBlank() ? null : normalized;
    }
}