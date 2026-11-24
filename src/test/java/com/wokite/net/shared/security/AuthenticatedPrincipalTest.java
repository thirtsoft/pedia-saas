package com.wokite.net.shared.security;

import com.wokite.net.onboarding.domain.model.OnboardingStatus;
import com.wokite.net.onboarding.domain.model.OnboardingStepCode;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticatedPrincipalTest {

    @Test
    void doitExposerLIdentiteEtLesPermissions() {

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

        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(user);

        assertThat(principal.getUsername()).isEqualTo(utilisateurUuid.toString());

        assertThat(principal.getUtilisateurUuid()).isEqualTo(utilisateurUuid);

        assertThat(principal.getEntrepriseUuid()).isEqualTo(entrepriseUuid);

        assertThat(principal.getRole()).isEqualTo("MEDECIN");

        assertThat(principal.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder(
                        "PATIENT_READ",
                        "PATIENT_WRITE"
                );
    }
}