package com.wokite.net.auth;

import com.wokite.net.utilisateur.application.dto.InscriptionResponse;
import com.wokite.net.utilisateur.application.dto.InscrireUtilisateurRequest;
import com.wokite.net.utilisateur.auth.AuthentificationService;
import com.wokite.net.utilisateur.domain.exception.UtilisateurEmailAlreadyExistsException;
import com.wokite.net.utilisateur.domain.model.ProviderAuthentification;
import com.wokite.net.utilisateur.domain.model.Utilisateur;
import com.wokite.net.utilisateur.domain.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthentificationServiceTest {

    @Mock
    private UtilisateurRepository users;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthentificationService service;

    @Test
    void doitInscrireUnUtilisateurSansEntrepriseNiRole() {
        InscrireUtilisateurRequest request = new InscrireUtilisateurRequest(
                "  Diallo  ",
                "  Tairou  ",
                " TAiROU@Example.COM ",
                "Password123"
        );

        when(users.existsByEmailIgnoreCase("tairou@example.com")).thenReturn(false);
        when(encoder.encode("Password123")).thenReturn("HASHED_PASSWORD");
        Utilisateur saved = new Utilisateur();
        saved.setNom("Diallo");
        saved.setPrenom("Tairou");
        saved.setEmail("tairou@example.com");
        saved.setMotDePasse("HASHED_PASSWORD");
        saved.setProviderAuthentification(ProviderAuthentification.LOCAL);

        when(users.save(any(Utilisateur.class))).thenReturn(saved);

        InscriptionResponse response = service.inscrire(request);
        ArgumentCaptor<Utilisateur> captor = ArgumentCaptor.forClass(Utilisateur.class);
        verify(users).save(captor.capture());

        Utilisateur utilisateur = captor.getValue();

        assertThat(utilisateur.getNom()).isEqualTo("Diallo");
        assertThat(utilisateur.getPrenom()).isEqualTo("Tairou");
        assertThat(utilisateur.getEmail()).isEqualTo("tairou@example.com");
        assertThat(utilisateur.getMotDePasse()).isEqualTo("HASHED_PASSWORD");
        assertThat(utilisateur.getProviderAuthentification()).isEqualTo(ProviderAuthentification.LOCAL);
        assertThat(utilisateur.getEntrepriseUuid()).isNull();
        assertThat(utilisateur.getRole()).isNull();
        assertThat(response.nom()).isEqualTo("Diallo");
        assertThat(response.prenom()).isEqualTo("Tairou");
        assertThat(response.email()).isEqualTo("tairou@example.com");
        assertThat(response.providerAuthentification()).isEqualTo(ProviderAuthentification.LOCAL);
        assertThat(response.onboardingRequis()).isTrue();
        verify(encoder).encode("Password123");
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
        assertThatThrownBy(() -> service.inscrire(request)).isInstanceOf(UtilisateurEmailAlreadyExistsException.class);
        verify(users, never()).save(any());
        verifyNoInteractions(encoder);
    }
}