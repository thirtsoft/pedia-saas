ALTER TABLE utilisateur ALTER COLUMN entreprise_uuid DROP NOT NULL;
ALTER TABLE utilisateur ALTER COLUMN role_id DROP NOT NULL;
CREATE UNIQUE INDEX uk_utilisateur_mobile ON utilisateur (mobile) WHERE mobile IS NOT NULL;