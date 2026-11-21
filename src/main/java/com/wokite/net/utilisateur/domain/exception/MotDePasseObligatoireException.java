package com.wokite.net.utilisateur.domain.exception;

import com.wokite.net.shared.web.error.BusinessException;
import com.wokite.net.shared.web.error.ErrorCode;

public class MotDePasseObligatoireException extends BusinessException {
    public MotDePasseObligatoireException() { super(ErrorCode.VALIDATION_ERROR, "Le mot de passe est obligatoire pour une authentification locale."); }
    public MotDePasseObligatoireException(String value) { super(ErrorCode.VALIDATION_ERROR, "Le mot de passe est obligatoire pour une authentification locale. " + value); }
}