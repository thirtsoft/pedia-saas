package com.wokite.net.utilisateur.domain.exception;

import com.wokite.net.shared.web.error.ConflictException;

public class RoleCodeAlreadyExistsException extends ConflictException {
    public RoleCodeAlreadyExistsException() { super("Le code rôle existe déjà."); }
    public RoleCodeAlreadyExistsException(String value) { super("Le code rôle existe déjà. " + value); }
}
