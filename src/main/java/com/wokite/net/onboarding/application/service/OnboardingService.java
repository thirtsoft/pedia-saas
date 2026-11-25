package com.wokite.net.onboarding.application.service;

import com.wokite.net.onboarding.application.dto.OnboardingEntrepriseRequest;
import com.wokite.net.onboarding.application.dto.OnboardingPlanRequest;
import com.wokite.net.onboarding.application.dto.OnboardingRecapitulatifFonctionnaliteResponse;
import com.wokite.net.onboarding.application.dto.OnboardingRecapitulatifResponse;
import com.wokite.net.onboarding.application.dto.OnboardingResponse;
import com.wokite.net.onboarding.application.dto.OnboardingRoleRequest;
import com.wokite.net.onboarding.application.external.AbonnementExternalService;
import com.wokite.net.onboarding.application.external.EntrepriseExternalService;
import com.wokite.net.onboarding.application.external.PlanAbonnementExternalService;
import com.wokite.net.onboarding.application.external.RoleExternalService;
import com.wokite.net.onboarding.application.external.UtilisateurExternalService;
import com.wokite.net.onboarding.domain.exception.OnboardingNotFoundException;
import com.wokite.net.onboarding.domain.model.Onboarding;
import com.wokite.net.onboarding.domain.model.OnboardingStatus;
import com.wokite.net.onboarding.domain.model.OnboardingStepCode;
import com.wokite.net.onboarding.domain.model.RoleOnboardingCode;
import com.wokite.net.onboarding.domain.repository.OnboardingRepository;
import com.wokite.net.shared.security.service.CommonService;
import com.wokite.net.shared.tenant.TenantContext;
import com.wokite.net.shared.web.error.BusinessException;
import com.wokite.net.shared.web.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OnboardingService {

    private static final String ROLE_MEDECIN_INDEPENDANT =
            RoleOnboardingCode.MEDECIN_INDEPENDANT.name();

    private static final String ROLE_ADMIN_CABINET =
            RoleOnboardingCode.ADMIN_CABINET.name();

    private static final String ROLE_CABINET = "CABINET";

    private final OnboardingRepository onboardings;
    private final CommonService commonService;
    private final RoleExternalService roleExternalService;
    private final EntrepriseExternalService entrepriseExternalService;
    private final UtilisateurExternalService utilisateurExternalService;
    private final PlanAbonnementExternalService planAbonnementExternalService;
    private final AbonnementExternalService abonnementExternalService;

    public OnboardingService(
            OnboardingRepository onboardings,
            CommonService commonService,
            RoleExternalService roleExternalService,
            EntrepriseExternalService entrepriseExternalService,
            UtilisateurExternalService utilisateurExternalService,
            PlanAbonnementExternalService planAbonnementExternalService,
            AbonnementExternalService abonnementExternalService
    ) {
        this.onboardings = onboardings;
        this.commonService = commonService;
        this.roleExternalService = roleExternalService;
        this.entrepriseExternalService = entrepriseExternalService;
        this.utilisateurExternalService = utilisateurExternalService;
        this.planAbonnementExternalService = planAbonnementExternalService;
        this.abonnementExternalService = abonnementExternalService;
    }

    @Transactional
    public OnboardingResponse creer(UUID utilisateurUuid) {
        return onboardings.findByUtilisateurUuid(utilisateurUuid)
                .map(this::toResponse)
                .orElseGet(() -> {
                    Instant now = Instant.now();

                    Onboarding onboarding = new Onboarding();
                    onboarding.setUtilisateurUuid(utilisateurUuid);
                    onboarding.setOnboardingStatus(OnboardingStatus.EN_COURS);
                    onboarding.setCurrentStep(OnboardingStepCode.CHOIX_ROLE);
                    onboarding.setStartedAt(now);
                    onboarding.setLastActivityAt(now);

                    return toResponse(onboardings.save(onboarding));
                });
    }

    public OnboardingResponse rechercherParUtilisateur(UUID utilisateurUuid) {
        return toResponse(trouverParUtilisateur(utilisateurUuid));
    }

    @Transactional
    public OnboardingResponse mettreAJourEtape(
            UUID utilisateurUuid,
            OnboardingStepCode stepCode
    ) {
        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);

        if (onboarding.getOnboardingStatus() == OnboardingStatus.TERMINE) {
            return toResponse(onboarding);
        }

        onboarding.setCurrentStep(stepCode);
        onboarding.setOnboardingStatus(OnboardingStatus.EN_COURS);
        onboarding.setLastActivityAt(Instant.now());
        onboarding.setErrorCode(null);
        onboarding.setErrorMessage(null);

        return toResponse(onboarding);
    }

    @Deprecated
    @Transactional
    public OnboardingResponse terminer(
            UUID utilisateurUuid,
            UUID entrepriseUuid
    ) {
        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);

        Instant now = Instant.now();

        onboarding.setEntrepriseUuid(entrepriseUuid);
        onboarding.setOnboardingStatus(OnboardingStatus.TERMINE);
        onboarding.setCurrentStep(OnboardingStepCode.VALIDATION);
        onboarding.setCompletedAt(now);
        onboarding.setLastActivityAt(now);
        onboarding.setErrorCode(null);
        onboarding.setErrorMessage(null);

        return toResponse(onboarding);
    }

    @Transactional
    public OnboardingResponse enregistrerErreur(
            UUID utilisateurUuid,
            OnboardingStepCode stepCode,
            String errorCode,
            String errorMessage
    ) {
        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);

        onboarding.setCurrentStep(stepCode);
        onboarding.setOnboardingStatus(OnboardingStatus.ERREUR);
        onboarding.setLastActivityAt(Instant.now());
        onboarding.setErrorCode(errorCode);
        onboarding.setErrorMessage(errorMessage);

        return toResponse(onboarding);
    }

    @Transactional
    public OnboardingResponse abandonner(UUID utilisateurUuid) {
        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);

        onboarding.setOnboardingStatus(OnboardingStatus.ABANDONNE);
        onboarding.setLastActivityAt(Instant.now());

        return toResponse(onboarding);
    }

    public OnboardingResponse rechercherPourUtilisateurConnecte() {
        UUID utilisateurUuid = commonService.getCurrentUserUuid();
        return rechercherParUtilisateur(utilisateurUuid);
    }

    @Transactional
    public OnboardingResponse choisirRole(OnboardingRoleRequest request) {

        UUID utilisateurUuid = commonService.getCurrentUserUuid();

        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);

        verifierOnboardingModifiable(onboarding);
        verifierEtapeCourante(onboarding, OnboardingStepCode.CHOIX_ROLE);

        RoleExternalService.RoleOnboardingInfo role =
                roleExternalService.rechercherRoleDisponible(request.roleUuid());

        if (role == null) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    "Le rôle sélectionné n'est pas disponible pour l'onboarding."
            );
        }

        if (!isRoleOnboardingAutorise(role.code())) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_ERROR,
                    "Le rôle sélectionné n'est pas autorisé pour l'onboarding."
            );
        }

        onboarding.setRoleUuid(role.uuid());
        onboarding.setCurrentStep(OnboardingStepCode.INFORMATIONS_ENTREPRISE);
        onboarding.setOnboardingStatus(OnboardingStatus.EN_COURS);
        onboarding.setLastActivityAt(Instant.now());
        onboarding.setErrorCode(null);
        onboarding.setErrorMessage(null);

        return toResponse(onboarding);
    }

    @Transactional
    public OnboardingResponse renseignerEntreprise(
            OnboardingEntrepriseRequest request
    ) {

        UUID utilisateurUuid = commonService.getCurrentUserUuid();

        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);

        verifierOnboardingModifiable(onboarding);
        verifierEtapeCourante(
                onboarding,
                OnboardingStepCode.INFORMATIONS_ENTREPRISE
        );

        if (onboarding.getRoleUuid() == null) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_ERROR,
                    "Le profil professionnel doit être sélectionné avant les informations du cabinet."
            );
        }

        RoleExternalService.RoleOnboardingInfo role =
                roleExternalService.rechercherRoleDisponible(
                        onboarding.getRoleUuid()
                );

        if (role == null) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    "Le rôle sélectionné est introuvable."
            );
        }

        onboarding.setNomEntreprise(request.nom().trim());
        onboarding.setTelephoneEntreprise(request.telephone().trim());
        onboarding.setEmailEntreprise(
                normaliserNullable(request.email())
        );
        onboarding.setAdresseEntreprise(
                normaliserNullable(request.adresse())
        );

        onboarding.setCurrentStep(OnboardingStepCode.CHOIX_PLAN);
        onboarding.setOnboardingStatus(OnboardingStatus.EN_COURS);
        onboarding.setLastActivityAt(Instant.now());
        onboarding.setErrorCode(null);
        onboarding.setErrorMessage(null);

        return toResponse(onboarding);
    }

    @Transactional
    public OnboardingResponse choisirPlan(
            OnboardingPlanRequest request
    ) {

        UUID utilisateurUuid = commonService.getCurrentUserUuid();

        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);

        verifierOnboardingModifiable(onboarding);
        verifierEtapeCourante(
                onboarding,
                OnboardingStepCode.CHOIX_PLAN
        );

        PlanAbonnementExternalService.PlanOnboardingInfo plan =
                planAbonnementExternalService.rechercherPlanDisponible(
                        request.planUuid()
                );

        if (plan == null) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    "La formule sélectionnée n'est pas disponible."
            );
        }

        onboarding.setPlanUuid(plan.uuid());
        onboarding.setCurrentStep(OnboardingStepCode.VALIDATION);
        onboarding.setOnboardingStatus(OnboardingStatus.EN_COURS);
        onboarding.setLastActivityAt(Instant.now());
        onboarding.setErrorCode(null);
        onboarding.setErrorMessage(null);

        return toResponse(onboarding);
    }

    public OnboardingRecapitulatifResponse rechercherRecapitulatif() {

        UUID utilisateurUuid = commonService.getCurrentUserUuid();

        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);

        verifierOnboardingModifiable(onboarding);
        verifierDonneesCompletesPourRecapitulatif(onboarding);

        RoleExternalService.RoleOnboardingInfo role =
                roleExternalService.rechercherRoleDisponible(
                        onboarding.getRoleUuid()
                );

        if (role == null) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    "Le rôle sélectionné est introuvable."
            );
        }

        PlanAbonnementExternalService.PlanOnboardingInfo plan =
                planAbonnementExternalService.rechercherPlanDisponible(
                        onboarding.getPlanUuid()
                );

        if (plan == null) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    "La formule sélectionnée est introuvable."
            );
        }

        List<OnboardingRecapitulatifFonctionnaliteResponse> fonctionnalites =
                planAbonnementExternalService
                        .rechercherFonctionnalites(plan.uuid())
                        .stream()
                        .map(fonctionnalite ->
                                new OnboardingRecapitulatifFonctionnaliteResponse(
                                        fonctionnalite.uuid(),
                                        fonctionnalite.code(),
                                        fonctionnalite.nom(),
                                        fonctionnalite.description()
                                )
                        )
                        .toList();

        return new OnboardingRecapitulatifResponse(
                role.uuid(),
                role.code(),
                role.libelle(),
                onboarding.getNomEntreprise(),
                onboarding.getTelephoneEntreprise(),
                onboarding.getEmailEntreprise(),
                onboarding.getAdresseEntreprise(),
                plan.uuid(),
                plan.code(),
                plan.nom(),
                plan.prixMensuel(),
                plan.nombreUtilisateursMax(),
                fonctionnalites
        );
    }

    @Transactional
    public OnboardingResponse finaliserOnboarding() {

        UUID utilisateurUuid = commonService.getCurrentUserUuid();

        Onboarding onboarding = trouverParUtilisateur(utilisateurUuid);

        verifierOnboardingModifiable(onboarding);
        verifierDonneesCompletesPourFinalisation(onboarding);

        RoleExternalService.RoleOnboardingInfo role =
                roleExternalService.rechercherRoleDisponible(
                        onboarding.getRoleUuid()
                );

        if (role == null) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    "Le rôle sélectionné est introuvable."
            );
        }

        PlanAbonnementExternalService.PlanOnboardingInfo plan =
                planAbonnementExternalService.rechercherPlanDisponible(
                        onboarding.getPlanUuid()
                );

        if (plan == null) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    "La formule sélectionnée est introuvable."
            );
        }

        String typeEntreprise = determinerTypeEntreprise(role.code());

        EntrepriseExternalService.EntrepriseOnboardingInfo entreprise =
                entrepriseExternalService.creer(
                        onboarding.getNomEntreprise(),
                        onboarding.getTelephoneEntreprise(),
                        onboarding.getEmailEntreprise(),
                        onboarding.getAdresseEntreprise(),
                        typeEntreprise
                );

        try {
            TenantContext.setTenantUuid(entreprise.uuid());

            utilisateurExternalService.rattacherEntreprise(utilisateurUuid, entreprise.uuid());

            utilisateurExternalService.affecterRole(utilisateurUuid, role.uuid());

            abonnementExternalService.creerEssai(entreprise.uuid(), plan.uuid(), LocalDate.now());

        } finally {
            TenantContext.clear();
        }

        Instant now = Instant.now();

        onboarding.setEntrepriseUuid(entreprise.uuid());
        onboarding.setOnboardingStatus(OnboardingStatus.TERMINE);
        onboarding.setCurrentStep(OnboardingStepCode.VALIDATION);
        onboarding.setCompletedAt(now);
        onboarding.setLastActivityAt(now);
        onboarding.setErrorCode(null);
        onboarding.setErrorMessage(null);

        return toResponse(onboarding);
    }

    private void verifierOnboardingModifiable(Onboarding onboarding) {

        if (onboarding.getOnboardingStatus() == OnboardingStatus.TERMINE) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_ERROR,
                    "L'onboarding est déjà terminé."
            );
        }

        if (onboarding.getOnboardingStatus() == OnboardingStatus.ABANDONNE) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_ERROR,
                    "Cet onboarding a été abandonné."
            );
        }
    }

    private void verifierEtapeCourante(
            Onboarding onboarding,
            OnboardingStepCode expectedStep
    ) {

        if (onboarding.getCurrentStep() != expectedStep) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_ERROR,
                    "L'étape actuelle de l'onboarding ne permet pas cette opération."
            );
        }
    }

    private void verifierDonneesCompletesPourRecapitulatif(
            Onboarding onboarding
    ) {

        if (onboarding.getRoleUuid() == null) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_ERROR,
                    "Le profil professionnel n'a pas été sélectionné."
            );
        }

        if (isBlank(onboarding.getNomEntreprise())) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_ERROR,
                    "Le nom du cabinet est obligatoire."
            );
        }

        if (isBlank(onboarding.getTelephoneEntreprise())) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_ERROR,
                    "Le téléphone professionnel est obligatoire."
            );
        }

        if (onboarding.getPlanUuid() == null) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_ERROR,
                    "La formule n'a pas été sélectionnée."
            );
        }

        verifierEtapeCourante(
                onboarding,
                OnboardingStepCode.VALIDATION
        );
    }

    private void verifierDonneesCompletesPourFinalisation(
            Onboarding onboarding
    ) {
        verifierDonneesCompletesPourRecapitulatif(onboarding);
    }

    private boolean isRoleOnboardingAutorise(String roleCode) {
        return ROLE_MEDECIN_INDEPENDANT.equals(roleCode)
                || ROLE_ADMIN_CABINET.equals(roleCode);
    }

    private String determinerTypeEntreprise(String roleCode) {

        if (ROLE_MEDECIN_INDEPENDANT.equals(roleCode)) {
            return ROLE_MEDECIN_INDEPENDANT;
        }

        if (ROLE_ADMIN_CABINET.equals(roleCode)) {
            return ROLE_CABINET;
        }

        throw new BusinessException(
                ErrorCode.BUSINESS_ERROR,
                "Le profil professionnel sélectionné ne permet pas de créer une entreprise."
        );
    }

    private Onboarding trouverParUtilisateur(UUID utilisateurUuid) {
        return onboardings.findByUtilisateurUuid(utilisateurUuid)
                .orElseThrow(() ->
                        new OnboardingNotFoundException(utilisateurUuid)
                );
    }

    private OnboardingResponse toResponse(Onboarding onboarding) {
        return new OnboardingResponse(
                onboarding.getUuid(),
                onboarding.getUtilisateurUuid(),
                onboarding.getEntrepriseUuid(),
                onboarding.getOnboardingStatus(),
                onboarding.getCurrentStep(),
                onboarding.getStartedAt(),
                onboarding.getCompletedAt(),
                onboarding.getLastActivityAt(),
                onboarding.getErrorCode(),
                onboarding.getErrorMessage()
        );
    }

    private String normaliserNullable(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        return normalized.isBlank() ? null : normalized;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}