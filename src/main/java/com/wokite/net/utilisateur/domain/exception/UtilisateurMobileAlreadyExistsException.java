package com.wokite.net.utilisateur.domain.exception;

import com.wokite.net.shared.web.error.ConflictException;

public class UtilisateurMobileAlreadyExistsException extends ConflictException {

    public UtilisateurMobileAlreadyExistsException(String mobile) {
        super("Le numéro de mobile '" + mobile + "' est déjà utilisé.");
    }
}