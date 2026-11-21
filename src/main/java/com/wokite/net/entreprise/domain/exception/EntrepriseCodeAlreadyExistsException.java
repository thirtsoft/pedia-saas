package com.wokite.net.entreprise.domain.exception;

import com.wokite.net.shared.web.error.ConflictException;

public class EntrepriseCodeAlreadyExistsException extends ConflictException {

    public EntrepriseCodeAlreadyExistsException(String code) {
        super("Le code entreprise '" + code + "' existe déjà.");
    }
}