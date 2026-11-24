package com.wokite.net.utilisateur.auth.exception;

public class GoogleAccountConflictException extends RuntimeException {

    public GoogleAccountConflictException() {
        super(
                "Un compte existe déjà avec cette adresse email. "
                        + "Connectez-vous avec votre méthode "
                        + "d'authentification actuelle."
        );
    }
}