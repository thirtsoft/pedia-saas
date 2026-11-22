package com.wokite.net.abonnement.domain.exception;
import com.wokite.net.shared.web.error.ConflictException;
public class FactureAbonnementNumeroAlreadyExistsException extends ConflictException {
    public FactureAbonnementNumeroAlreadyExistsException(String numero) {
        super("Le numéro de facture '" + numero + "' existe déjà.");
    }
}
