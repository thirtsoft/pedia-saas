package com.wokite.net.onboarding.web;

import com.wokite.net.onboarding.application.dto.OnboardingEntrepriseRequest;
import com.wokite.net.onboarding.application.dto.OnboardingPlanRequest;
import com.wokite.net.onboarding.application.dto.OnboardingRecapitulatifResponse;
import com.wokite.net.onboarding.application.dto.OnboardingResponse;
import com.wokite.net.onboarding.application.dto.OnboardingRoleRequest;
import com.wokite.net.onboarding.application.service.OnboardingService;
import com.wokite.net.onboarding.domain.model.OnboardingStepCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/onboarding")
@Validated
public class OnboardingController {

    private final OnboardingService service;

    public OnboardingController(OnboardingService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<OnboardingResponse> rechercherPourUtilisateurConnecte() {
        return ResponseEntity.ok(service.rechercherPourUtilisateurConnecte());
    }

    @PutMapping("/role")
    public ResponseEntity<OnboardingResponse> choisirRole(@Valid @RequestBody OnboardingRoleRequest request) {
        return ResponseEntity.ok(service.choisirRole(request));
    }

    @PutMapping("/entreprise")
    public ResponseEntity<OnboardingResponse> renseignerEntreprise(@Valid @RequestBody OnboardingEntrepriseRequest request) {
        return ResponseEntity.ok(service.renseignerEntreprise(request));
    }

    @PutMapping("/plan")
    public ResponseEntity<OnboardingResponse> choisirPlan(@Valid @RequestBody OnboardingPlanRequest request) {
        return ResponseEntity.ok(service.choisirPlan(request));
    }

    @GetMapping("/recapitulatif")
    public ResponseEntity<OnboardingRecapitulatifResponse> rechercherRecapitulatif() {
        return ResponseEntity.ok(service.rechercherRecapitulatif());
    }

    @PostMapping("/terminer")
    public ResponseEntity<OnboardingResponse> finaliserOnboarding() {
        return ResponseEntity.ok(service.finaliserOnboarding());
    }

    /**
     * @deprecated Le nouveau workflow utilise /me.
     */
    @Deprecated
    @GetMapping("/{utilisateurUuid}")
    public ResponseEntity<OnboardingResponse> rechercher(@PathVariable UUID utilisateurUuid) {
        return ResponseEntity.ok(service.rechercherParUtilisateur(utilisateurUuid));
    }

    /**
     * @deprecated Le workflow V2 contrôle désormais les transitions
     * automatiquement.
     */
    @Deprecated
    @PostMapping("/{utilisateurUuid}/step")
    public ResponseEntity<OnboardingResponse> mettreAJourEtape(@PathVariable UUID utilisateurUuid, @RequestParam OnboardingStepCode stepCode) {
        return ResponseEntity.ok(service.mettreAJourEtape(
                        utilisateurUuid, stepCode
                )
        );
    }

    /**
     * @deprecated Le workflow V2 utilise /terminer.
     */
    @Deprecated
    @PostMapping("/{utilisateurUuid}/complete")
    public ResponseEntity<OnboardingResponse> terminer(@PathVariable UUID utilisateurUuid, @RequestParam UUID entrepriseUuid) {
        return ResponseEntity.ok(
                service.terminer(utilisateurUuid, entrepriseUuid)
        );
    }

    /**
     * Endpoint technique V1 conservé pour le moment.
     */
    @Deprecated
    @PostMapping("/{utilisateurUuid}/error")
    public ResponseEntity<OnboardingResponse> enregistrerErreur(
            @PathVariable UUID utilisateurUuid,
            @RequestParam OnboardingStepCode stepCode,
            @RequestParam
            @NotBlank
            @Size(max = 100)
            String errorCode,
            @RequestParam
            @NotBlank
            @Size(max = 500)
            String errorMessage) {

        return ResponseEntity.ok(
                service.enregistrerErreur(
                        utilisateurUuid,
                        stepCode,
                        errorCode,
                        errorMessage
                )
        );
    }

    /**
     * Endpoint technique V1 conservé pour le moment.
     */
    @Deprecated
    @PostMapping("/{utilisateurUuid}/abandon")
    public ResponseEntity<OnboardingResponse> abandonner(@PathVariable UUID utilisateurUuid) {
        return ResponseEntity.ok(service.abandonner(utilisateurUuid));
    }
}