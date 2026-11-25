CREATE UNIQUE INDEX uk_abonnement_entreprise_plan
    ON abonnement (entreprise_uuid, plan_abonnement_id);

CREATE UNIQUE INDEX uk_abonnement_entreprise_encours
    ON abonnement (entreprise_uuid)
    WHERE statut_abonnement IN (
        'EN_PERIODE_ESSAI',
        'ACTIF',
        'SUSPENDU'
    );