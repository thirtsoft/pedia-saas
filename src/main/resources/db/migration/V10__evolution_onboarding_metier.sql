ALTER TABLE onboarding
    ADD COLUMN role_uuid UUID;

ALTER TABLE onboarding
    ADD COLUMN nom_entreprise VARCHAR(150);

ALTER TABLE onboarding
    ADD COLUMN telephone_entreprise VARCHAR(30);

ALTER TABLE onboarding
    ADD COLUMN email_entreprise VARCHAR(180);

ALTER TABLE onboarding
    ADD COLUMN adresse_entreprise VARCHAR(255);

ALTER TABLE onboarding
    ADD COLUMN plan_uuid UUID;

CREATE INDEX idx_onboarding_role_uuid
    ON onboarding (role_uuid);

CREATE INDEX idx_onboarding_plan_uuid
    ON onboarding (plan_uuid);