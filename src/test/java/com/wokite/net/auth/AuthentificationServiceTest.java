package com.wokite.net.auth;

import com.wokite.net.onboarding.application.service.OnboardingService;
import com.wokite.net.shared.persistence.RecordStatus;
import com.wokite.net.shared.security.AuthenticatedUser;
import com.wokite.net.shared.security.JwtService;
import com.wokite.net.utilisateur.auth.AuthentificationService;
import com.wokite.net.utilisateur.auth.dto.InscriptionResponse;
import com.wokite.net.utilisateur.auth.dto.InscrireUtilisateurRequest;
import com.wokite.net.utilisateur.auth.dto.LoginRequest;
import com.wokite.net.utilisateur.auth.dto.LoginResponse;
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

    @InjectMocks
    private AuthentificationService service;

    @Test
    void doitInscrireUnUtilisateurSansEntrepriseNiRole() {
        InscrireUtilisateurRequest request = new InscrireUtilisateurRequest(
                "  Diallo  ",
                "  Tairou  ",
                " TAIROU@EXAMPLE.COM ",
                "Password123"
        );

        when(users.existsByEmailIgnoreCase("tairou@example.com")).thenReturn(false);
        when(encoder.encode("Password123")).thenReturn("hashed-password");
        Utilisateur saved = new Utilisateur();

        saved.setUuid(UUID.randomUUID());
        saved.setNom("Diallo");
        saved.setPrenom("Tairou");
        saved.setEmail("tairou@example.com");
        saved.setMotDePasse("hashed-password");
        saved.setProviderAuthentification(ProviderAuthentification.LOCAL);
        saved.setRecordStatus(RecordStatus.ACTIVE);

        when(users.save(any(Utilisateur.class))).thenReturn(saved);

        InscriptionResponse response = service.inscrire(request);

        assertThat(response.utilisateurUuid()).isEqualTo(saved.getUuid());
        assertThat(response.email()).isEqualTo("tairou@example.com");
        assertThat(response.providerAuthentification()).isEqualTo(ProviderAuthentification.LOCAL);
        assertThat(response.onboardingRequis()).isTrue();

        ArgumentCaptor<Utilisateur> captor = ArgumentCaptor.forClass(Utilisateur.class);

        verify(users).save(captor.capture());

        Utilisateur utilisateur = captor.getValue();
        assertThat(utilisateur.getNom()).isEqualTo("Diallo");
        assertThat(utilisateur.getPrenom()).isEqualTo("Tairou");
        assertThat(utilisateur.getEmail()).isEqualTo("tairou@example.com");
        assertThat(utilisateur.getEntrepriseUuid()).isNull();
        assertThat(utilisateur.getRole()).isNull();
        assertThat(utilisateur.getMotDePasse()).isEqualTo("hashed-password");
        verify(onboardingService).creer(saved.getUuid());
    }

    @Test
    void doitRefuserUneAdresseEmailDejaUtilisee() {
        InscrireUtilisateurRequest request = new InscrireUtilisateurRequest(
                "Diallo",
                "Tairou",
                "tairou@example.com",
                "Password123"
        );

        when(users.existsByEmailIgnoreCase("tairou@example.com")).thenReturn(true);
        assertThatThrownBy(() -> service.inscrire(request)
        )
                .isInstanceOf(UtilisateurEmailAlreadyExistsException.class);

        verify(users, never()).save(any());
        verify(onboardingService, never()).creer(any());
    }

    @Test
    void doitConnecterUnUtilisateurAvecUnRoleEtUneEntreprise() {
        UUID utilisateurUuid = UUID.randomUUID();
        UUID entrepriseUuid = UUID.randomUUID();
        Role role = new Role();
        role.setCode("MEDECIN");
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUuid(utilisateurUuid);
        utilisateur.setEntrepriseUuid(entrepriseUuid);
        utilisateur.setRole(role);
        utilisateur.setEmail("tairou@example.com");
        utilisateur.setMotDePasse("hashed-password");
        utilisateur.setRecordStatus(RecordStatus.ACTIVE);

        when(users.findByEmailIgnoreCase("tairou@example.com")).thenReturn(
                Optional.of(utilisateur)
        );

        when(encoder.matches(
                "Password123",
                "hashed-password"
        )).thenReturn(true);

        String token = "jwt-token";

        when(jwtService.generateToken(
                any(AuthenticatedUser.class)
        )).thenReturn(token);

        LoginResponse response = service.login(new LoginRequest(
                        " TAIROU@EXAMPLE.COM ",
                        "Password123"
                )
        );

        assertThat(response.token()).isEqualTo(token);
        assertThat(response.type()).isEqualTo("Bearer");
        assertThat(response.utilisateurUuid()).isEqualTo(utilisateurUuid);
        assertThat(response.entrepriseUuid()).isEqualTo(entrepriseUuid);
        assertThat(response.role()).isEqualTo("MEDECIN");
        assertThat(response.permissions()).isEmpty();
        ArgumentCaptor<AuthenticatedUser> captor = ArgumentCaptor.forClass(AuthenticatedUser.class);
        verify(jwtService).generateToken(captor.capture());
        AuthenticatedUser authenticatedUser = captor.getValue();
        assertThat(authenticatedUser.utilisateurUuid()).isEqualTo(utilisateurUuid);
        assertThat(authenticatedUser.entrepriseUuid()).isEqualTo(entrepriseUuid);
        assertThat(authenticatedUser.role()).isEqualTo("MEDECIN");
        assertThat(authenticatedUser.permissions()).isEmpty();
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
    }

    @Test
    void doitRefuserUnEmailInexistant() {

        when(users.findByEmailIgnoreCase(
                "inexistant@example.com"
        )).thenReturn(Optional.empty());

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
                .matches(anyString(), anyString());

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

        utilisateur.setMotDePasse(
                "hashed-password"
        );

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
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateToken(any());
    }
}