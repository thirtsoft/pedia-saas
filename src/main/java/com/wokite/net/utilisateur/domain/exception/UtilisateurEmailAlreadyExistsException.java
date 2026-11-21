package com.wokite.net.utilisateur.domain.exception;

import com.wokite.net.shared.web.error.ConflictException;

public class UtilisateurEmailAlreadyExistsException extends ConflictException {
    public UtilisateurEmailAlreadyExistsException() { super("L'adresse email existe déjà."); }
    public UtilisateurEmailAlreadyExistsException(String value) { super("L'adresse email existe déjà. " + value); }
}
