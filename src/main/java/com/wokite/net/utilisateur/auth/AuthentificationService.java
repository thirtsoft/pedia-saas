package com.wokite.net.utilisateur.auth;

import com.wokite.net.onboarding.application.dto.OnboardingResponse;
import com.wokite.net.onboarding.application.service.OnboardingService;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.security.AuthenticatedUser;
import com.wokite.net.shared.security.JwtService;
import com.wokite.net.utilisateur.auth.dto.InscriptionResponse;
import com.wokite.net.utilisateur.auth.dto.InscrireUtilisateurRequest;
import com.wokite.net.utilisateur.auth.dto.LoginRequest;
import com.wokite.net.utilisateur.auth.dto.LoginResponse;
import com.wokite.net.utilisateur.auth.exception.IdentifiantsInvalidesException;
import com.wokite.net.utilisateur.domain.exception.UtilisateurEmailAlreadyExistsException;
import com.wokite.net.utilisateur.domain.model.ProviderAuthentification;
import com.wokite.net.utilisateur.domain.model.Utilisateur;
import com.wokite.net.utilisateur.domain.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
public class AuthentificationService {

    private final UtilisateurRepository users;
    private final PasswordEncoder encoder;
    private final OnboardingService onboardingService;
    private final JwtService jwtService;

    public AuthentificationService(
            UtilisateurRepository users,
            PasswordEncoder encoder,
            OnboardingService onboardingService,
            JwtService jwtService) {

        this.users = users;
        this.encoder = encoder;
        this.onboardingService = onboardingService;
        this.jwtService = jwtService;
    }

    @Transactional
    public InscriptionResponse inscrire(InscrireUtilisateurRequest request) {

        String email = normaliserEmail(request.email());

        if (users.existsByEmailIgnoreCase(email)) {
            throw new UtilisateurEmailAlreadyExistsException(email);
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.nom().trim());
        utilisateur.setPrenom(request.prenom().trim());
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(encoder.encode(request.motDePasse()));
        utilisateur.setProviderAuthentification(ProviderAuthentification.LOCAL);
        utilisateur.setEntrepriseUuid(null);
        utilisateur.setRole(null);
        utilisateur.setRecordStatus(RecordStatus.ACTIVE);

        Utilisateur saved = users.saveAndFlush(utilisateur);

        onboardingService.creer(saved.getUuid());

        return toInscriptionResponse(saved);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        String email = normaliserEmail(request.email());

        Utilisateur utilisateur = users.findByEmailIgnoreCase(email).orElseThrow(IdentifiantsInvalidesException::new);

        if (utilisateur.getRecordStatus() != RecordStatus.ACTIVE) {
            throw new IdentifiantsInvalidesException();
        }

        if (utilisateur.getMotDePasse() == null || !encoder.matches(request.motDePasse(), utilisateur.getMotDePasse())) {

            throw new IdentifiantsInvalidesException();
        }

        String role = utilisateur.getRole() == null ? null : utilisateur.getRole().getCode();

        Set<String> permissions = Set.of();

        OnboardingResponse onboarding = onboardingService.rechercherParUtilisateur(utilisateur.getUuid());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                utilisateur.getUuid(),
                utilisateur.getEntrepriseUuid(),
                role, permissions,
                onboarding.onboardingStatus(),
                onboarding.currentStep()
        );

        String token = jwtService.generateToken(authenticatedUser);

        return new LoginResponse(
                token, "Bearer",
                authenticatedUser.utilisateurUuid(),
                authenticatedUser.entrepriseUuid(),
                authenticatedUser.role(),
                authenticatedUser.permissions(),
                authenticatedUser.onboardingStatus(),
                authenticatedUser.onboardingStep()
        );
    }

    private String normaliserEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private InscriptionResponse toInscriptionResponse(Utilisateur utilisateur) {
        return new InscriptionResponse(
                utilisateur.getUuid(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getProviderAuthentification(),
                true,
                utilisateur.getRecordStatus(),
                utilisateur.getCreatedAt()
        );
    }
}