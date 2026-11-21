package com.wokite.net.utilisateur.domain.exception;

import com.wokite.net.shared.web.error.ConflictException;

public class RolePermissionAlreadyExistsException extends ConflictException {
    public RolePermissionAlreadyExistsException() { super("Cette permission est déjà associée à ce rôle."); }
    public RolePermissionAlreadyExistsException(String value) { super("Cette permission est déjà associée à ce rôle. " + value); }
}
