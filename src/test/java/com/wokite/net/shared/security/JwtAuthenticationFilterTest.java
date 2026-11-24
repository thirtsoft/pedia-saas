package com.wokite.net.shared.security;

import com.wokite.net.onboarding.domain.model.OnboardingStatus;
import com.wokite.net.onboarding.domain.model.OnboardingStepCode;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doitAuthentifierUneRequeteAvecUnTokenValide()
            throws Exception {

        UUID utilisateurUuid = UUID.randomUUID();

        UUID entrepriseUuid = UUID.randomUUID();

        AuthenticatedUser user = new AuthenticatedUser(
                utilisateurUuid,
                entrepriseUuid,
                "MEDECIN",
                Set.of("PATIENT_READ"),
                OnboardingStatus.EN_COURS,
                OnboardingStepCode.CHOIX_ROLE
        );

        when(jwtService.parseUser("valid-token"))
                .thenReturn(user);

        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtService);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer valid-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertThat(authentication)
                .isNotNull();

        assertThat(authentication.isAuthenticated())
                .isTrue();

        assertThat(authentication.getPrincipal())
                .isInstanceOf(
                        AuthenticatedPrincipal.class
                );

        AuthenticatedPrincipal principal =
                (AuthenticatedPrincipal)
                        authentication.getPrincipal();

        assertThat(principal.getUtilisateurUuid())
                .isEqualTo(utilisateurUuid);

        assertThat(principal.getEntrepriseUuid())
                .isEqualTo(entrepriseUuid);

        assertThat(principal.getRole())
                .isEqualTo("MEDECIN");

        assertThat(principal.getPermissions())
                .containsExactly("PATIENT_READ");

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void neDoitPasAuthentifierSansToken()
            throws Exception {

        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtService);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        ).isNull();

        verifyNoInteractions(jwtService);

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void neDoitPasAuthentifierAvecUnTokenInvalide()
            throws Exception {

        when(jwtService.parseUser("invalid-token"))
                .thenThrow(
                        new IllegalArgumentException(
                                "Token invalide"
                        )
                );

        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtService);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer invalid-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        ).isNull();

        verify(jwtService)
                .parseUser("invalid-token");

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void doitAccepterUnTokenSansEntrepriseNiRole()
            throws Exception {

        UUID utilisateurUuid = UUID.randomUUID();

        AuthenticatedUser user = new AuthenticatedUser(
                utilisateurUuid,
                null,
                null,
                Set.of(),
                OnboardingStatus.EN_COURS,
                OnboardingStepCode.CHOIX_ROLE
        );

        when(jwtService.parseUser("valid-token"))
                .thenReturn(user);

        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtService);

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer valid-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();

        AuthenticatedPrincipal principal = (AuthenticatedPrincipal) authentication.getPrincipal();

        assertThat(principal.getUtilisateurUuid()).isEqualTo(utilisateurUuid);

        assertThat(principal.getEntrepriseUuid()).isNull();

        assertThat(principal.getRole()).isNull();
    }
}