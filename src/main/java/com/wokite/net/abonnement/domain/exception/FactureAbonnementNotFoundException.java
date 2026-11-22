package com.wokite.net.abonnement.domain.exception;
import com.wokite.net.shared.web.error.ResourceNotFoundException;
import java.util.UUID;
public class FactureAbonnementNotFoundException extends ResourceNotFoundException {
    public FactureAbonnementNotFoundException(UUID uuid) {
        super("Facture d'abonnement introuvable : " + uuid);
    }
}
