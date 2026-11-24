package com.wokite.net.shared.security.service;

import com.wokite.net.shared.security.AuthenticatedPrincipal;
import com.wokite.net.shared.web.error.BusinessException;
import com.wokite.net.shared.web.error.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CommonService {

    public UUID getCurrentUserUuid() {
        return getAuthenticatedPrincipal().getUtilisateurUuid();
    }

    public UUID getCurrentEntrepriseUuid() {
        return getAuthenticatedPrincipal().getEntrepriseUuid();
    }

    public String getCurrentRole() {
        return getAuthenticatedPrincipal().getRole();
    }

    private AuthenticatedPrincipal getAuthenticatedPrincipal() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof AuthenticatedPrincipal principal)) {

            throw new BusinessException(
                    ErrorCode.UNAUTHORIZED,
                    "Utilisateur non authentifié."
            );
        }

        return principal;
    }
}