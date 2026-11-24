package com.wokite.net.utilisateur.auth.exception;

public class GoogleTokenInvalideException extends RuntimeException {

    public GoogleTokenInvalideException() {
        super("Le token Google est invalide.");
    }
}