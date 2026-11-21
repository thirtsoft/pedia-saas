package com.wokite.net.utilisateur.domain.exception;

import com.wokite.net.shared.web.error.ConflictException;

public class PermissionCodeAlreadyExistsException extends ConflictException {
    public PermissionCodeAlreadyExistsException() { super("Le code permission existe déjà."); }
    public PermissionCodeAlreadyExistsException(String value) { super("Le code permission existe déjà. " + value); }
}
