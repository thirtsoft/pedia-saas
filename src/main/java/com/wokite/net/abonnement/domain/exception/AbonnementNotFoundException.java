package com.wokite.net.abonnement.domain.exception;
import com.wokite.net.shared.web.error.ResourceNotFoundException;
import java.util.UUID;
public class AbonnementNotFoundException extends ResourceNotFoundException {
    public AbonnementNotFoundException(UUID uuid) {
        super("Abonnement introuvable : " + uuid);
    }
}
