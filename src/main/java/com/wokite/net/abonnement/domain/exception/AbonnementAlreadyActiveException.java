package com.wokite.net.abonnement.domain.exception;
import com.wokite.net.shared.web.error.ConflictException;
import java.util.UUID;
public class AbonnementAlreadyActiveException extends ConflictException {
    public AbonnementAlreadyActiveException(UUID entrepriseUuid) {
        super("L'entreprise " + entrepriseUuid + " possède déjà un abonnement actif ou en période d'essai.");
    }
}
