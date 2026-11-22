package com.wokite.net.abonnement.domain.exception;
import com.wokite.net.shared.web.error.ConflictException;
import java.util.UUID;
public class FonctionnaliteAlreadyExistsException extends ConflictException {
    public FonctionnaliteAlreadyExistsException(UUID planUuid, String code) {
        super("La fonctionnalité '" + code + "' existe déjà pour le plan " + planUuid + ".");
    }
}
