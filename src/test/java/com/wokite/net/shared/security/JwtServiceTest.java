package com.wokite.net.shared.security;

import com.wokite.net.onboarding.domain.model.OnboardingStatus;
import com.wokite.net.onboarding.domain.model.OnboardingStepCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "01234567890123456789012345678901",
                Duration.ofHours(24)
        );

        jwtService = new JwtService(properties);
    }

    @Test
    void doitGenererEtRelireUnToken() {

        UUID utilisateurUuid = UUID.randomUUID();
        UUID entrepriseUuid = UUID.randomUUID();

        AuthenticatedUser user = new AuthenticatedUser(
                utilisateurUuid,
                entrepriseUuid,
                "MEDECIN",
                Set.of(
                        "PATIENT_READ",
                        "PATIENT_WRITE"
                ),
                OnboardingStatus.EN_COURS,
                OnboardingStepCode.CHOIX_ROLE
        );

        String token = jwtService.generateToken(user);

        assertThat(token)
                .isNotBlank();

        AuthenticatedUser result =
                jwtService.parseUser(token);

        assertThat(result.utilisateurUuid())
                .isEqualTo(utilisateurUuid);

        assertThat(result.entrepriseUuid())
                .isEqualTo(entrepriseUuid);

        assertThat(result.role())
                .isEqualTo("MEDECIN");

        assertThat(result.permissions())
                .containsExactlyInAnyOrder(
                        "PATIENT_READ",
                        "PATIENT_WRITE"
                );

        assertThat(result.onboardingStatus())
                .isEqualTo(OnboardingStatus.EN_COURS);

        assertThat(result.onboardingStep())
                .isEqualTo(OnboardingStepCode.CHOIX_ROLE);
    }

    @Test
    void doitAccepterUnUtilisateurSansEntrepriseNiRole() {

        UUID utilisateurUuid = UUID.randomUUID();

        AuthenticatedUser user = new AuthenticatedUser(
                utilisateurUuid,
                null,
                null,
                Set.of(),
                OnboardingStatus.EN_COURS,
                OnboardingStepCode.CHOIX_ROLE
        );

        String token =
                jwtService.generateToken(user);

        AuthenticatedUser result =
                jwtService.parseUser(token);

        assertThat(result.utilisateurUuid())
                .isEqualTo(utilisateurUuid);

        assertThat(result.entrepriseUuid())
                .isNull();

        assertThat(result.role())
                .isNull();

        assertThat(result.permissions())
                .isEmpty();

        assertThat(result.onboardingStatus())
                .isEqualTo(OnboardingStatus.EN_COURS);

        assertThat(result.onboardingStep())
                .isEqualTo(OnboardingStepCode.CHOIX_ROLE);
    }

    @Test
    void doitAccepterUnUtilisateurAvecOnboardingTermine() {

        UUID utilisateurUuid = UUID.randomUUID();
        UUID entrepriseUuid = UUID.randomUUID();

        AuthenticatedUser user = new AuthenticatedUser(
                utilisateurUuid,
                entrepriseUuid,
                "MEDECIN",
                Set.of("PATIENT_READ"),
                OnboardingStatus.TERMINE,
                OnboardingStepCode.VALIDATION
        );

        String token =
                jwtService.generateToken(user);

        AuthenticatedUser result =
                jwtService.parseUser(token);

        assertThat(result.onboardingStatus())
                .isEqualTo(OnboardingStatus.TERMINE);

        assertThat(result.onboardingStep())
                .isEqualTo(OnboardingStepCode.VALIDATION);
    }

    @Test
    void doitRefuserUnTokenInvalide() {

        assertThatThrownBy(
                () -> jwtService.parseUser("token-invalide")
        ).isInstanceOf(Exception.class);
    }

    @Test
    void doitRefuserUneCleTropCourte() {

        JwtProperties properties =
                new JwtProperties(
                        "secret-trop-court",
                        Duration.ofHours(24)
                );

        assertThatThrownBy(
                () -> new JwtService(properties)
        )
                .hasMessageContaining(
                        "au moins 32 octets"
                );
    }
}