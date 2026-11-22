package com.wokite.net.abonnement.application.service;

import com.wokite.net.abonnement.application.dto.CreerFonctionnalitePlanAbonnementRequest;
import com.wokite.net.abonnement.application.dto.FonctionnalitePlanAbonnementResponse;
import com.wokite.net.abonnement.application.dto.ModifierFonctionnalitePlanAbonnementRequest;
import com.wokite.net.abonnement.domain.exception.FonctionnaliteAlreadyExistsException;
import com.wokite.net.abonnement.domain.exception.FonctionnalitePlanAbonnementNotFoundException;
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
import java.util.UUID;

@Service
public class FonctionnalitePlanAbonnementService {

    private final FonctionnalitePlanAbonnementRepository fonctionnaliteRepository;
    private final PlanAbonnementRepository planRepository;

    public FonctionnalitePlanAbonnementService(FonctionnalitePlanAbonnementRepository fonctionnaliteRepository, PlanAbonnementRepository planRepository) {
        this.fonctionnaliteRepository = fonctionnaliteRepository;
        this.planRepository = planRepository;
    }

    @Transactional
    public FonctionnalitePlanAbonnementResponse creer(UUID planUuid, CreerFonctionnalitePlanAbonnementRequest request) {
        PlanAbonnement plan = verifierPlan(planUuid);
        String code = normalizeCode(request.code());
        if (fonctionnaliteRepository.existsByPlanAbonnementUuidAndCodeIgnoreCase(planUuid, code)) {
            throw new FonctionnaliteAlreadyExistsException(planUuid, code);
        }
        FonctionnalitePlanAbonnement fonctionnalite = new FonctionnalitePlanAbonnement();
        fonctionnalite.setPlanAbonnement(plan);
        fonctionnalite.setCode(code);
        fonctionnalite.setNom(request.nom().trim());
        fonctionnalite.setDescription(normalizeNullable(request.description()));
        fonctionnalite.setActif(true);
        fonctionnalite.setRecordStatus(RecordStatus.ACTIVE);
        FonctionnalitePlanAbonnement saved = fonctionnaliteRepository.save(fonctionnalite);
        return toResponse(saved);
    }

    public List<FonctionnalitePlanAbonnementResponse> listerActives(UUID planUuid) {
        verifierPlan(planUuid);
        return fonctionnaliteRepository.findAllByPlanAbonnementUuidAndActifTrueOrderByNomAsc(planUuid)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FonctionnalitePlanAbonnementResponse modifier(UUID uuid, ModifierFonctionnalitePlanAbonnementRequest request) {
        FonctionnalitePlanAbonnement fonctionnalite = fonctionnaliteRepository.findByUuid(uuid)
                .orElseThrow(() -> new FonctionnalitePlanAbonnementNotFoundException(uuid));
        fonctionnalite.setNom(request.nom().trim());
        fonctionnalite.setDescription(normalizeNullable(request.description()));
        fonctionnalite.setActif(request.actif());
        return toResponse(fonctionnaliteRepository.save(fonctionnalite));
    }

    private PlanAbonnement verifierPlan(UUID uuid) {
        return planRepository.findByUuid(uuid).orElseThrow(() ->
                new PlanAbonnementNotFoundException(uuid));
    }

    private FonctionnalitePlanAbonnementResponse toResponse(FonctionnalitePlanAbonnement fonctionnalite) {
        return new FonctionnalitePlanAbonnementResponse(
                fonctionnalite.getUuid(),
                fonctionnalite.getPlanAbonnement().getUuid(),
                fonctionnalite.getCode(),
                fonctionnalite.getNom(),
                fonctionnalite.getDescription(),
                fonctionnalite.isActif(),
                fonctionnalite.getRecordStatus(),
                fonctionnalite.getCreatedAt(),
                fonctionnalite.getUpdatedAt()
        );
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