package com.wokite.net.abonnement.domain.exception;
import com.wokite.net.shared.web.error.ResourceNotFoundException;
import java.util.UUID;
public class PlanAbonnementNotFoundException extends ResourceNotFoundException {
    public PlanAbonnementNotFoundException(UUID uuid) {
        super("Plan d'abonnement introuvable : " + uuid);
    }
}
