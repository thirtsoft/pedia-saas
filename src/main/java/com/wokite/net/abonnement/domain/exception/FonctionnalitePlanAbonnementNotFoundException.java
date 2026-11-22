package com.wokite.net.abonnement.domain.exception;
import com.wokite.net.shared.web.error.ResourceNotFoundException;
import java.util.UUID;
public class FonctionnalitePlanAbonnementNotFoundException extends ResourceNotFoundException {
    public FonctionnalitePlanAbonnementNotFoundException(UUID uuid) {
        super("Fonctionnalité du plan d'abonnement introuvable : " + uuid);
    }
}
