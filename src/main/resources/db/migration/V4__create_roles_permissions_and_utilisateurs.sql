CREATE TABLE role
(
    id            BIGINT                   NOT NULL,
    uuid          UUID                     NOT NULL,
    code          VARCHAR(50)              NOT NULL,
    libelle       VARCHAR(100)             NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by    UUID,
    record_status VARCHAR(20)              NOT NULL,
    version       BIGINT                   NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id),
    CONSTRAINT uk_role_uuid UNIQUE (uuid),
    CONSTRAINT uk_role_code UNIQUE (code),
    CONSTRAINT ck_role_record_status CHECK (record_status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED', 'DELETED'))
);
CREATE TABLE permission
(
    id            BIGINT                   NOT NULL,
    uuid          UUID                     NOT NULL,
    code          VARCHAR(100)             NOT NULL,
    libelle       VARCHAR(150)             NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by    UUID,
    record_status VARCHAR(20)              NOT NULL,
    version       BIGINT                   NOT NULL,
    CONSTRAINT pk_permission PRIMARY KEY (id),
    CONSTRAINT uk_permission_uuid UNIQUE (uuid),
    CONSTRAINT uk_permission_code UNIQUE (code),
    CONSTRAINT ck_permission_record_status CHECK (record_status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED', 'DELETED'))
);
CREATE TABLE role_permission
(
    id            BIGINT                   NOT NULL,
    uuid          UUID                     NOT NULL,
    role_id       BIGINT                   NOT NULL,
    permission_id BIGINT                   NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by    UUID,
    record_status VARCHAR(20)              NOT NULL,
    version       BIGINT                   NOT NULL,
    CONSTRAINT pk_role_permission PRIMARY KEY (id),
    CONSTRAINT uk_role_permission_uuid UNIQUE (uuid),
    CONSTRAINT uk_role_permission_pair UNIQUE (role_id, permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES role (id),
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES permission (id),
    CONSTRAINT ck_role_permission_record_status CHECK (record_status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED', 'DELETED'))
);
CREATE INDEX idx_role_permission_role_id ON role_permission (role_id);
CREATE INDEX idx_role_permission_permission_id ON role_permission (permission_id);

CREATE TABLE utilisateur
(
    id                        BIGINT                   NOT NULL,
    uuid                      UUID                     NOT NULL,
    entreprise_uuid           UUID                     NOT NULL,
    role_id                   BIGINT                   NOT NULL,
    nom                       VARCHAR(100)             NOT NULL,
    prenom                    VARCHAR(100)             NOT NULL,
    email                     VARCHAR(180)             NOT NULL,
    mobile                    VARCHAR(30),
    mot_de_passe              VARCHAR(255),
    google_subject            VARCHAR(255),
    provider_authentification VARCHAR(20)              NOT NULL,
    created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by                UUID,
    record_status             VARCHAR(20)              NOT NULL,
    version                   BIGINT                   NOT NULL,
    CONSTRAINT pk_utilisateur PRIMARY KEY (id),
    CONSTRAINT uk_utilisateur_uuid UNIQUE (uuid),
    CONSTRAINT uk_utilisateur_email UNIQUE (email),
    CONSTRAINT uk_utilisateur_google_subject UNIQUE (google_subject),
    CONSTRAINT fk_utilisateur_role FOREIGN KEY (role_id) REFERENCES role (id),
    CONSTRAINT ck_utilisateur_provider CHECK (provider_authentification IN ('LOCAL', 'GOOGLE')),
    CONSTRAINT ck_utilisateur_record_status CHECK (record_status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED', 'DELETED'))
);
CREATE INDEX idx_utilisateur_entreprise_uuid ON utilisateur (entreprise_uuid);
CREATE INDEX idx_utilisateur_role_id ON utilisateur (role_id);
CREATE INDEX idx_utilisateur_nom_prenom ON utilisateur (nom, prenom);
INSERT INTO role(id, uuid, code, libelle, created_at, updated_at, record_status, version)
VALUES (nextval('wokite_sequence'), gen_random_uuid(), 'ADMIN_CABINET', 'Administrateur du cabinet', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'ACTIVE', 0),
       (nextval('wokite_sequence'), gen_random_uuid(), 'MEDECIN', 'Médecin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        'ACTIVE', 0),
       (nextval('wokite_sequence'), gen_random_uuid(), 'SECRETAIRE', 'Secrétaire', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        'ACTIVE', 0),
       (nextval('wokite_sequence'), gen_random_uuid(), 'COMPTABLE', 'Comptable', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
        'ACTIVE', 0),
       (nextval('wokite_sequence'), gen_random_uuid(), 'MEDECIN_INDEPENDANT', 'Médecin indépendant', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'ACTIVE', 0);