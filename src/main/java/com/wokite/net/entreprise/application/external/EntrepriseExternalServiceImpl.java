package com.wokite.net.entreprise.application.external;


import com.wokite.net.entreprise.application.dto.EntrepriseResponse;
import com.wokite.net.entreprise.application.service.EntrepriseService;
import com.wokite.net.onboarding.application.external.EntrepriseExternalService;
import org.springframework.stereotype.Service;

@Service
public class EntrepriseExternalServiceImpl implements EntrepriseExternalService {

    private final EntrepriseService entrepriseService;

    public EntrepriseExternalServiceImpl(EntrepriseService entrepriseService) {
        this.entrepriseService = entrepriseService;
    }

    @Override
    public EntrepriseOnboardingInfo creer(
            String nom, String telephone, String email, String adresse, String type) {

        EntrepriseResponse entreprise = entrepriseService.creerDepuisOnboarding(
                nom, telephone, email, adresse, type
        );

        return new EntrepriseOnboardingInfo(entreprise.uuid(), entreprise.code(), entreprise.nom(), entreprise.type().name());
    }
}