package com.wokite.net.abonnement.domain.exception;

import java.util.UUID;

public class PlanAbonnementAlreadyUsedException extends RuntimeException {

    private final UUID entrepriseUuid;
    private final UUID planUuid;

    public PlanAbonnementAlreadyUsedException(
            UUID entrepriseUuid,
            UUID planUuid
    ) {
        super(
                "L'entreprise " + entrepriseUuid
                        + " a déjà utilisé le plan d'abonnement "
                        + planUuid
                        + "."
        );
        this.entrepriseUuid = entrepriseUuid;
        this.planUuid = planUuid;
    }

    public UUID getEntrepriseUuid() {
        return entrepriseUuid;
    }

    public UUID getPlanUuid() {
        return planUuid;
    }
}