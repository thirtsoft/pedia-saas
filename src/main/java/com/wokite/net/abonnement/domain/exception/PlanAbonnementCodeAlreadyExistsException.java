package com.wokite.net.abonnement.domain.exception;
import com.wokite.net.shared.web.error.ConflictException;
public class PlanAbonnementCodeAlreadyExistsException extends ConflictException {
    public PlanAbonnementCodeAlreadyExistsException(String code) {
        super("Le code du plan d'abonnement '" + code + "' existe déjà.");
    }
}
