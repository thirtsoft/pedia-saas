CREATE UNIQUE INDEX uk_abonnement_entreprise_plan
    ON abonnement (entreprise_uuid, plan_abonnement_id);

CREATE UNIQUE INDEX uk_abonnement_entreprise_encours
    ON abonnement (entreprise_uuid)
    WHERE statut_abonnement IN (
        'EN_PERIODE_ESSAI',
        'ACTIF',
        'SUSPENDU'
    );

ALTER TABLE role
    ADD COLUMN type_role VARCHAR(30);

UPDATE role
SET type_role = 'CABINET'
WHERE code IN (
               'ADMIN_CABINET',
               'COMPTABLE',
               'SECRETAIRE',
               'MEDECIN'
    );

UPDATE role
SET type_role = 'MEDECIN_INDEPENDANT'
WHERE code = 'MEDECIN_INDEPENDANT';