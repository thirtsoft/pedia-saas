package com.wokite.net.abonnement.application.external;

import com.wokite.net.abonnement.application.dto.FonctionnalitePlanResponse;
import com.wokite.net.abonnement.application.dto.PlanAbonnementOnbordingResponse;
import com.wokite.net.abonnement.application.service.PlanAbonnementService;
import com.wokite.net.onboarding.application.external.PlanAbonnementExternalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlanAbonnementExternalServiceImpl implements PlanAbonnementExternalService {

    private final PlanAbonnementService planAbonnementService;

    public PlanAbonnementExternalServiceImpl(PlanAbonnementService planAbonnementService) {
        this.planAbonnementService = planAbonnementService;
    }

    @Override
    public PlanOnboardingInfo rechercherPlanDisponible(UUID planUuid) {
        return planAbonnementService.rechercherPlansDisponibles()
                .stream()
                .filter(plan -> plan.uuid().equals(planUuid))
                .findFirst()
                .map(plan -> new PlanOnboardingInfo(
                        plan.uuid(),
                        plan.code(),
                        plan.nom(),
                        plan.prixMensuel(),
                        plan.nombreUtilisateursMax()
                ))
                .orElse(null);
    }

    @Override
    public List<PlanFonctionnaliteInfo> rechercherFonctionnalites(UUID planUuid) {
        return planAbonnementService.rechercherPlansDisponibles()
                .stream()
                .filter(plan -> plan.uuid().equals(planUuid))
                .findFirst()
                .map(PlanAbonnementOnbordingResponse::fonctionnalites)
                .orElse(List.of())
                .stream()
                .map(this::toFonctionnaliteInfo)
                .toList();
    }

    private PlanFonctionnaliteInfo toFonctionnaliteInfo(FonctionnalitePlanResponse fonctionnalite) {
        return new PlanFonctionnaliteInfo(
                fonctionnalite.uuid(),
                fonctionnalite.code(),
                fonctionnalite.nom(),
                fonctionnalite.description()
        );
    }
}