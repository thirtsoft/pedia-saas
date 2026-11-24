package com.wokite.net.abonnement.application.external;

import com.wokite.net.abonnement.application.dto.AbonnementResponse;
import com.wokite.net.abonnement.application.dto.CreerAbonnementRequest;
import com.wokite.net.abonnement.application.service.AbonnementService;
import com.wokite.net.onboarding.application.external.AbonnementExternalService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class AbonnementExternalServiceImpl implements AbonnementExternalService {

    private final AbonnementService abonnementService;

    public AbonnementExternalServiceImpl(AbonnementService abonnementService) {
        this.abonnementService = abonnementService;
    }

    @Override
    public AbonnementOnboardingInfo creerEssai(UUID entrepriseUuid, UUID planUuid, LocalDate dateDebut) {

        AbonnementResponse abonnement = abonnementService.creer(
                new CreerAbonnementRequest(entrepriseUuid, planUuid, dateDebut)
        );

        return new AbonnementOnboardingInfo(
                abonnement.uuid(),
                abonnement.entrepriseUuid(),
                abonnement.planUuid(),
                abonnement.dateDebut(),
                abonnement.dateFin(),
                abonnement.statut().name()
        );
    }
}