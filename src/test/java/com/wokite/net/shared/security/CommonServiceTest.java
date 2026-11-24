package com.wokite.net.shared.security;

import com.wokite.net.shared.security.service.CommonService;
import com.wokite.net.shared.web.error.BusinessException;
import com.wokite.net.shared.web.error.ErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommonServiceTest {

    private final CommonService commonService = new CommonService();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doitRetournerUuidUtilisateurConnecte() {

        UUID utilisateurUuid = UUID.randomUUID();
        UUID entrepriseUuid = UUID.randomUUID();

        AuthenticatedUser user = new AuthenticatedUser(
                utilisateurUuid,
                entrepriseUuid,
                "MEDECIN_INDEPENDANT",
                Set.of("PATIENT_READ"),
                null,
                null
        );

        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities()
                )
        );

        assertEquals(utilisateurUuid, commonService.getCurrentUserUuid());
    }

    @Test
    void doitRetournerUuidEntrepriseConnectee() {
        UUID utilisateurUuid = UUID.randomUUID();
        UUID entrepriseUuid = UUID.randomUUID();
        AuthenticatedUser user = new AuthenticatedUser(
                utilisateurUuid, entrepriseUuid,
                "ADMIN_CABINET",
                Set.of("PATIENT_READ"),
                null,
                null
        );

        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities()
                )
        );

        assertEquals(
                entrepriseUuid,
                commonService.getCurrentEntrepriseUuid()
        );
    }

    @Test
    void doitRetournerRoleUtilisateurConnecte() {

        AuthenticatedUser user = new AuthenticatedUser(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ADMIN_CABINET",
                Set.of("PATIENT_READ"),
                null,
                null
        );

        AuthenticatedPrincipal principal =
                new AuthenticatedPrincipal(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                )
        );

        assertEquals(
                "ADMIN_CABINET",
                commonService.getCurrentRole()
        );
    }

    @Test
    void doitRefuserAccesSiUtilisateurNonAuthentifie() {

        SecurityContextHolder.clearContext();

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> commonService.getCurrentUserUuid()
                );

        assertEquals(
                ErrorCode.UNAUTHORIZED,
                exception.getCode()
        );
    }

    @Test
    void doitRefuserAccesSiPrincipalInvalide() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "anonymous",
                        null
                )
        );

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> commonService.getCurrentUserUuid()
                );

        assertEquals(
                ErrorCode.UNAUTHORIZED,
                exception.getCode()
        );
    }
}