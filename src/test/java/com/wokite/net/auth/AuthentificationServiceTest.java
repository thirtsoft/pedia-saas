package com.wokite.net.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.wokite.net.onboarding.application.dto.OnboardingResponse;
import com.wokite.net.onboarding.application.service.OnboardingService;
import com.wokite.net.onboarding.domain.model.OnboardingStatus;
import com.wokite.net.onboarding.domain.model.OnboardingStepCode;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.security.AuthenticatedUser;
import com.wokite.net.shared.security.JwtService;
import com.wokite.net.utilisateur.auth.AuthentificationService;
import com.wokite.net.utilisateur.auth.GoogleTokenVerifierService;
import com.wokite.net.utilisateur.auth.dto.GoogleLoginRequest;
import com.wokite.net.utilisateur.auth.dto.InscriptionResponse;
import com.wokite.net.utilisateur.auth.dto.InscrireUtilisateurRequest;
import com.wokite.net.utilisateur.auth.dto.LoginRequest;
import com.wokite.net.utilisateur.auth.dto.LoginResponse;
import com.wokite.net.utilisateur.auth.exception.GoogleAccountConflictException;
import com.wokite.net.utilisateur.auth.exception.IdentifiantsInvalidesException;
import com.wokite.net.utilisateur.domain.exception.UtilisateurEmailAlreadyExistsException;
import com.wokite.net.utilisateur.domain.model.ProviderAuthentification;
import com.wokite.net.utilisateur.domain.model.Role;
import com.wokite.net.utilisateur.domain.model.Utilisateur;
import com.wokite.net.utilisateur.domain.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthentificationServiceTest {

    @Mock
    private UtilisateurRepository users;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private OnboardingService onboardingService;

    @Mock
    private JwtService jwtService;

    @Mock
    private GoogleTokenVerifierService googleTokenVerifierService;

    @InjectMocks
    private AuthentificationService service;

    @Test
    void doitInscrireUnUtilisateurSansEntrepriseNiRole() {

        InscrireUtilisateurRequest request =
                new InscrireUtilisateurRequest(
                        "  Diallo  ",
                        "  Tairou  ",
                        " TAIROU@EXAMPLE.COM ",
                        "Password123"
                );

        when(users.existsByEmailIgnoreCase(
                "tairou@example.com"
        )).thenReturn(false);

        when(encoder.encode("Password123"))
                .thenReturn("hashed-password");

        Utilisateur saved = new Utilisateur();

        UUID utilisateurUuid = UUID.randomUUID();

        saved.setUuid(utilisateurUuid);
        saved.setNom("Diallo");
        saved.setPrenom("Tairou");
        saved.setEmail("tairou@example.com");
        saved.setMotDePasse("hashed-password");
        saved.setProviderAuthentification(
                ProviderAuthentification.LOCAL
        );
        saved.setRecordStatus(
                RecordStatus.ACTIVE
        );

        when(users.saveAndFlush(
                any(Utilisateur.class)
        )).thenReturn(saved);

        InscriptionResponse response =
                service.inscrire(request);

        assertThat(response.utilisateurUuid())
                .isEqualTo(saved.getUuid());

        assertThat(response.email())
                .isEqualTo("tairou@example.com");

        assertThat(response.providerAuthentification())
                .isEqualTo(ProviderAuthentification.LOCAL);

        assertThat(response.onboardingRequis())
                .isTrue();

        ArgumentCaptor<Utilisateur> captor =
                ArgumentCaptor.forClass(
                        Utilisateur.class
                );

        verify(users).saveAndFlush(
                captor.capture()
        );

        Utilisateur utilisateur =
                captor.getValue();

        assertThat(utilisateur.getNom())
                .isEqualTo("Diallo");

        assertThat(utilisateur.getPrenom())
                .isEqualTo("Tairou");

        assertThat(utilisateur.getEmail())
                .isEqualTo("tairou@example.com");

        assertThat(utilisateur.getEntrepriseUuid())
                .isNull();

        assertThat(utilisateur.getRole())
                .isNull();

        assertThat(utilisateur.getMotDePasse())
                .isEqualTo("hashed-password");

        assertThat(utilisateur.getProviderAuthentification())
                .isEqualTo(ProviderAuthentification.LOCAL);

        assertThat(utilisateur.getRecordStatus())
                .isEqualTo(RecordStatus.ACTIVE);

        verify(onboardingService)
                .creer(saved.getUuid());
    }

    @Test
    void doitRefuserUneAdresseEmailDejaUtilisee() {

        InscrireUtilisateurRequest request =
                new InscrireUtilisateurRequest(
                        "Diallo",
                        "Tairou",
                        "tairou@example.com",
                        "Password123"
                );

        when(users.existsByEmailIgnoreCase(
                "tairou@example.com"
        )).thenReturn(true);

        assertThatThrownBy(
                () -> service.inscrire(request)
        )
                .isInstanceOf(
                        UtilisateurEmailAlreadyExistsException.class
                );

        verify(users, never())
                .saveAndFlush(any(Utilisateur.class));

        verify(onboardingService, never())
                .creer(any());
    }

    @Test
    void doitConnecterUnUtilisateurAvecUnRoleEtUneEntreprise() {

        UUID utilisateurUuid =
                UUID.randomUUID();

        UUID entrepriseUuid =
                UUID.randomUUID();

        Role role = new Role();

        role.setCode("MEDECIN");

        Utilisateur utilisateur =
                new Utilisateur();

        utilisateur.setUuid(utilisateurUuid);
        utilisateur.setEntrepriseUuid(entrepriseUuid);
        utilisateur.setRole(role);
        utilisateur.setEmail("tairou@example.com");
        utilisateur.setMotDePasse("hashed-password");
        utilisateur.setRecordStatus(
                RecordStatus.ACTIVE
        );

        when(users.findByEmailIgnoreCase(
                "tairou@example.com"
        )).thenReturn(
                Optional.of(utilisateur)
        );

        when(encoder.matches(
                "Password123",
                "hashed-password"
        )).thenReturn(true);

        OnboardingResponse onboarding =
                onboardingResponse(
                        OnboardingStatus.EN_COURS,
                        OnboardingStepCode.CHOIX_ROLE
                );

        when(onboardingService.rechercherParUtilisateur(
                utilisateurUuid
        )).thenReturn(onboarding);

        String token = "jwt-token";

        when(jwtService.generateToken(
                any(AuthenticatedUser.class)
        )).thenReturn(token);

        LoginResponse response =
                service.login(
                        new LoginRequest(
                                " TAIROU@EXAMPLE.COM ",
                                "Password123"
                        )
                );

        assertThat(response.token())
                .isEqualTo(token);

        assertThat(response.type())
                .isEqualTo("Bearer");

        assertThat(response.utilisateurUuid())
                .isEqualTo(utilisateurUuid);

        assertThat(response.entrepriseUuid())
                .isEqualTo(entrepriseUuid);

        assertThat(response.role())
                .isEqualTo("MEDECIN");

        assertThat(response.permissions())
                .isEmpty();

        assertThat(response.onboardingStatus())
                .isEqualTo(OnboardingStatus.EN_COURS);

        assertThat(response.onboardingStep())
                .isEqualTo(OnboardingStepCode.CHOIX_ROLE);

        ArgumentCaptor<AuthenticatedUser> captor =
                ArgumentCaptor.forClass(
                        AuthenticatedUser.class
                );

        verify(jwtService)
                .generateToken(captor.capture());

        AuthenticatedUser authenticatedUser =
                captor.getValue();

        assertThat(authenticatedUser.utilisateurUuid())
                .isEqualTo(utilisateurUuid);

        assertThat(authenticatedUser.entrepriseUuid())
                .isEqualTo(entrepriseUuid);

        assertThat(authenticatedUser.role())
                .isEqualTo("MEDECIN");

        assertThat(authenticatedUser.permissions())
                .isEmpty();

        assertThat(authenticatedUser.onboardingStatus())
                .isEqualTo(OnboardingStatus.EN_COURS);

        assertThat(authenticatedUser.onboardingStep())
                .isEqualTo(OnboardingStepCode.CHOIX_ROLE);
    }

    @Test
    void doitConnecterUnUtilisateurSansEntrepriseNiRole() {

        UUID utilisateurUuid =
                UUID.randomUUID();

        Utilisateur utilisateur =
                new Utilisateur();

        utilisateur.setUuid(utilisateurUuid);
        utilisateur.setEntrepriseUuid(null);
        utilisateur.setRole(null);
        utilisateur.setEmail("tairou@example.com");
        utilisateur.setMotDePasse("hashed-password");
        utilisateur.setRecordStatus(
                RecordStatus.ACTIVE
        );

        when(users.findByEmailIgnoreCase(
                "tairou@example.com"
        )).thenReturn(
                Optional.of(utilisateur)
        );

        when(encoder.matches(
                "Password123",
                "hashed-password"
        )).thenReturn(true);

        OnboardingResponse onboarding =
                onboardingResponse(
                        OnboardingStatus.EN_COURS,
                        OnboardingStepCode.CHOIX_ROLE
                );

        when(onboardingService.rechercherParUtilisateur(
                utilisateurUuid
        )).thenReturn(onboarding);

        when(jwtService.generateToken(
                any(AuthenticatedUser.class)
        )).thenReturn("jwt-token");

        LoginResponse response =
                service.login(
                        new LoginRequest(
                                "tairou@example.com",
                                "Password123"
                        )
                );

        assertThat(response.token())
                .isEqualTo("jwt-token");

        assertThat(response.utilisateurUuid())
                .isEqualTo(utilisateurUuid);

        assertThat(response.entrepriseUuid())
                .isNull();

        assertThat(response.role())
                .isNull();

        assertThat(response.permissions())
                .isEmpty();

        assertThat(response.onboardingStatus())
                .isEqualTo(OnboardingStatus.EN_COURS);

        assertThat(response.onboardingStep())
                .isEqualTo(OnboardingStepCode.CHOIX_ROLE);
    }

    @Test
    void doitRefuserUnEmailInexistant() {

        when(users.findByEmailIgnoreCase(
                "inexistant@example.com"
        )).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(
                () -> service.login(
                        new LoginRequest(
                                "inexistant@example.com",
                                "Password123"
                        )
                )
        )
                .isInstanceOf(
                        IdentifiantsInvalidesException.class
                );

        verify(encoder, never())
                .matches(
                        anyString(),
                        anyString()
                );

        verify(jwtService, never())
                .generateToken(any());
    }

    @Test
    void doitRefuserUnMotDePasseIncorrect() {

        Utilisateur utilisateur =
                new Utilisateur();

        utilisateur.setUuid(
                UUID.randomUUID()
        );

        utilisateur.setEmail(
                "tairou@example.com"
        );

        utilisateur.setMotDePasse(
                "hashed-password"
        );

        utilisateur.setRecordStatus(
                RecordStatus.ACTIVE
        );

        when(users.findByEmailIgnoreCase(
                "tairou@example.com"
        )).thenReturn(
                Optional.of(utilisateur)
        );

        when(encoder.matches(
                "WrongPassword",
                "hashed-password"
        )).thenReturn(false);

        assertThatThrownBy(
                () -> service.login(
                        new LoginRequest(
                                "tairou@example.com",
                                "WrongPassword"
                        )
                )
        )
                .isInstanceOf(
                        IdentifiantsInvalidesException.class
                );

        verify(jwtService, never())
                .generateToken(any());
    }

    @Test
    void doitRefuserUnUtilisateurInactif() {

        Utilisateur utilisateur =
                new Utilisateur();

        utilisateur.setUuid(
                UUID.randomUUID()
        );

        utilisateur.setEmail(
                "tairou@example.com"
        );

        utilisateur.setMotDePasse("hashed-password");

        utilisateur.setRecordStatus(
                RecordStatus.INACTIVE
        );

        when(users.findByEmailIgnoreCase(
                "tairou@example.com"
        )).thenReturn(
                Optional.of(utilisateur)
        );

        assertThatThrownBy(
                () -> service.login(
                        new LoginRequest(
                                "tairou@example.com",
                                "Password123"
                        )
                )
        )
                .isInstanceOf(
                        IdentifiantsInvalidesException.class
                );

        verify(encoder, never())
                .matches(
                        anyString(),
                        anyString()
                );

        verify(jwtService, never())
                .generateToken(any());
    }

    @Test
    void doitCreerUnUtilisateurLorsDunePremiereConnexionGoogle() {

        String googleSubject =
                "google-subject-123";

        String idToken =
                "google-id-token";

        UUID utilisateurUuid =
                UUID.randomUUID();

        GoogleIdToken.Payload payload =
                org.mockito.Mockito.mock(
                        GoogleIdToken.Payload.class
                );

        when(googleTokenVerifierService.verifier(idToken))
                .thenReturn(payload);

        when(payload.getSubject())
                .thenReturn(googleSubject);

        when(payload.getEmail())
                .thenReturn(" TAIROU@EXAMPLE.COM ");

        when(payload.get("family_name"))
                .thenReturn("Diallo");

        when(payload.get("given_name"))
                .thenReturn("Tairou");

        when(users.findByGoogleSubject(
                googleSubject
        )).thenReturn(
                Optional.empty()
        );

        when(users.findByEmailIgnoreCase(
                "tairou@example.com"
        )).thenReturn(
                Optional.empty()
        );

        Utilisateur saved =
                new Utilisateur();

        saved.setUuid(utilisateurUuid);
        saved.setNom("Diallo");
        saved.setPrenom("Tairou");
        saved.setEmail("tairou@example.com");
        saved.setGoogleSubject(googleSubject);
        saved.setMotDePasse(null);
        saved.setProviderAuthentification(
                ProviderAuthentification.GOOGLE
        );
        saved.setEntrepriseUuid(null);
        saved.setRole(null);
        saved.setRecordStatus(
                RecordStatus.ACTIVE
        );

        when(users.saveAndFlush(
                any(Utilisateur.class)
        )).thenReturn(saved);

        OnboardingResponse onboarding =
                onboardingResponse(
                        OnboardingStatus.EN_COURS,
                        OnboardingStepCode.CHOIX_ROLE
                );

        when(onboardingService.rechercherParUtilisateur(
                utilisateurUuid
        )).thenReturn(onboarding);

        when(jwtService.generateToken(
                any(AuthenticatedUser.class)
        )).thenReturn("google-jwt-token");

        LoginResponse response =
                service.loginAvecGoogle(
                        new GoogleLoginRequest(idToken)
                );

        assertThat(response.token())
                .isEqualTo("google-jwt-token");

        assertThat(response.type())
                .isEqualTo("Bearer");

        assertThat(response.utilisateurUuid())
                .isEqualTo(utilisateurUuid);

        assertThat(response.entrepriseUuid())
                .isNull();

        assertThat(response.role())
                .isNull();

        assertThat(response.permissions())
                .isEmpty();

        assertThat(response.onboardingStatus())
                .isEqualTo(OnboardingStatus.EN_COURS);

        assertThat(response.onboardingStep())
                .isEqualTo(OnboardingStepCode.CHOIX_ROLE);

        ArgumentCaptor<Utilisateur> captor =
                ArgumentCaptor.forClass(
                        Utilisateur.class
                );

        verify(users).saveAndFlush(
                captor.capture()
        );

        Utilisateur utilisateur =
                captor.getValue();

        assertThat(utilisateur.getNom())
                .isEqualTo("Diallo");

        assertThat(utilisateur.getPrenom())
                .isEqualTo("Tairou");

        assertThat(utilisateur.getEmail())
                .isEqualTo("tairou@example.com");

        assertThat(utilisateur.getGoogleSubject())
                .isEqualTo(googleSubject);

        assertThat(utilisateur.getMotDePasse())
                .isNull();

        assertThat(utilisateur.getProviderAuthentification())
                .isEqualTo(ProviderAuthentification.GOOGLE);

        assertThat(utilisateur.getEntrepriseUuid())
                .isNull();

        assertThat(utilisateur.getRole())
                .isNull();

        assertThat(utilisateur.getRecordStatus())
                .isEqualTo(RecordStatus.ACTIVE);

        verify(onboardingService)
                .creer(utilisateurUuid);

        verify(googleTokenVerifierService)
                .verifier(idToken);
    }

    @Test
    void doitConnecterUnUtilisateurGoogleExistant() {

        String googleSubject =
                "google-subject-456";

        String idToken =
                "google-id-token";

        UUID utilisateurUuid =
                UUID.randomUUID();

        UUID entrepriseUuid =
                UUID.randomUUID();

        Role role = new Role();
        role.setCode("MEDECIN");

        Utilisateur utilisateur =
                new Utilisateur();

        utilisateur.setUuid(utilisateurUuid);
        utilisateur.setEntrepriseUuid(entrepriseUuid);
        utilisateur.setRole(role);
        utilisateur.setEmail("tairou@example.com");
        utilisateur.setGoogleSubject(googleSubject);
        utilisateur.setMotDePasse(null);
        utilisateur.setProviderAuthentification(
                ProviderAuthentification.GOOGLE
        );
        utilisateur.setRecordStatus(
                RecordStatus.ACTIVE
        );

        GoogleIdToken.Payload payload =
                org.mockito.Mockito.mock(
                        GoogleIdToken.Payload.class
                );

        when(googleTokenVerifierService.verifier(idToken))
                .thenReturn(payload);

        when(payload.getSubject())
                .thenReturn(googleSubject);

        when(payload.getEmail())
                .thenReturn("tairou@example.com");

        when(users.findByGoogleSubject(
                googleSubject
        )).thenReturn(
                Optional.of(utilisateur)
        );

        OnboardingResponse onboarding =
                onboardingResponse(
                        OnboardingStatus.TERMINE,
                        OnboardingStepCode.VALIDATION
                );

        when(onboardingService.rechercherParUtilisateur(
                utilisateurUuid
        )).thenReturn(onboarding);

        when(jwtService.generateToken(
                any(AuthenticatedUser.class)
        )).thenReturn("existing-google-jwt");

        LoginResponse response =
                service.loginAvecGoogle(
                        new GoogleLoginRequest(idToken)
                );

        assertThat(response.token())
                .isEqualTo("existing-google-jwt");

        assertThat(response.type())
                .isEqualTo("Bearer");

        assertThat(response.utilisateurUuid())
                .isEqualTo(utilisateurUuid);

        assertThat(response.entrepriseUuid())
                .isEqualTo(entrepriseUuid);

        assertThat(response.role())
                .isEqualTo("MEDECIN");

        assertThat(response.onboardingStatus())
                .isEqualTo(OnboardingStatus.TERMINE);

        assertThat(response.onboardingStep())
                .isEqualTo(OnboardingStepCode.VALIDATION);

        verify(users, never())
                .findByEmailIgnoreCase(anyString());

        verify(users, never())
                .saveAndFlush(any(Utilisateur.class));

        verify(onboardingService, never())
                .creer(any());

        verify(jwtService)
                .generateToken(any(AuthenticatedUser.class));
    }

    @Test
    void doitRefuserUneConnexionGoogleSiEmailExisteDeja() {

        String googleSubject =
                "google-subject-conflict";

        String idToken =
                "google-id-token";

        GoogleIdToken.Payload payload =
                org.mockito.Mockito.mock(
                        GoogleIdToken.Payload.class
                );

        when(googleTokenVerifierService.verifier(idToken))
                .thenReturn(payload);

        when(payload.getSubject())
                .thenReturn(googleSubject);

        when(payload.getEmail())
                .thenReturn("tairou@example.com");

        Utilisateur utilisateurLocal =
                new Utilisateur();

        utilisateurLocal.setUuid(
                UUID.randomUUID()
        );

        utilisateurLocal.setEmail(
                "tairou@example.com"
        );

        utilisateurLocal.setProviderAuthentification(
                ProviderAuthentification.LOCAL
        );

        when(users.findByGoogleSubject(
                googleSubject
        )).thenReturn(
                Optional.empty()
        );

        when(users.findByEmailIgnoreCase(
                "tairou@example.com"
        )).thenReturn(
                Optional.of(utilisateurLocal)
        );

        assertThatThrownBy(
                () -> service.loginAvecGoogle(
                        new GoogleLoginRequest(idToken)
                )
        )
                .isInstanceOf(
                        GoogleAccountConflictException.class
                );

        verify(users, never())
                .saveAndFlush(any(Utilisateur.class));

        verify(onboardingService, never())
                .creer(any());

        verify(jwtService, never())
                .generateToken(any());
    }

    @Test
    void doitRefuserUnUtilisateurGoogleInactif() {

        String googleSubject =
                "google-subject-inactive";

        String idToken =
                "google-id-token";

        UUID utilisateurUuid =
                UUID.randomUUID();

        GoogleIdToken.Payload payload =
                org.mockito.Mockito.mock(
                        GoogleIdToken.Payload.class
                );

        when(googleTokenVerifierService.verifier(idToken))
                .thenReturn(payload);

        when(payload.getSubject())
                .thenReturn(googleSubject);

        when(payload.getEmail())
                .thenReturn("tairou@example.com");

        Utilisateur utilisateur =
                new Utilisateur();

        utilisateur.setUuid(utilisateurUuid);
        utilisateur.setEmail("tairou@example.com");
        utilisateur.setGoogleSubject(googleSubject);
        utilisateur.setProviderAuthentification(
                ProviderAuthentification.GOOGLE
        );
        utilisateur.setRecordStatus(
                RecordStatus.INACTIVE
        );

        when(users.findByGoogleSubject(
                googleSubject
        )).thenReturn(
                Optional.of(utilisateur)
        );

        assertThatThrownBy(
                () -> service.loginAvecGoogle(
                        new GoogleLoginRequest(idToken)
                )
        )
                .isInstanceOf(
                        IdentifiantsInvalidesException.class
                );

        verify(users, never())
                .findByEmailIgnoreCase(anyString());

        verify(users, never())
                .saveAndFlush(any(Utilisateur.class));

        verify(onboardingService, never())
                .rechercherParUtilisateur(any());

        verify(jwtService, never())
                .generateToken(any());
    }

    private OnboardingResponse onboardingResponse(OnboardingStatus status, OnboardingStepCode step) {

        OnboardingResponse response = org.mockito.Mockito.mock(OnboardingResponse.class);
        when(response.onboardingStatus()).thenReturn(status);
        when(response.currentStep()).thenReturn(step);

        return response;
    }
}