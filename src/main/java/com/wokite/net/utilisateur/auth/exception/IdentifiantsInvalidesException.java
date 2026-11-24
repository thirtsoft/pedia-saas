package com.wokite.net.utilisateur.auth.exception;

import com.wokite.net.shared.web.error.BusinessException;
import com.wokite.net.shared.web.error.ErrorCode;

public class IdentifiantsInvalidesException
        extends BusinessException {

    public IdentifiantsInvalidesException() {
        super(
                ErrorCode.VALIDATION_ERROR,
                "Adresse email ou mot de passe incorrect."
        );
    }
}