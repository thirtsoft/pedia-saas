CREATE TABLE plan_abonnement
(
    id                      BIGINT                   NOT NULL,
    uuid                    UUID                     NOT NULL,
    code                    VARCHAR(50)              NOT NULL,
    nom                     VARCHAR(120)             NOT NULL,
    description             VARCHAR(500),
    prix_mensuel            NUMERIC(15, 2)           NOT NULL,
    nombre_utilisateurs_max INTEGER                  NOT NULL,
    actif                   BOOLEAN                  NOT NULL DEFAULT TRUE,

    created_at              TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by              UUID,
    record_status           VARCHAR(20)              NOT NULL,
    version                 BIGINT                   NOT NULL,

    CONSTRAINT pk_plan_abonnement
        PRIMARY KEY (id),

    CONSTRAINT uk_plan_abonnement_uuid
        UNIQUE (uuid),

    CONSTRAINT uk_plan_abonnement_code
        UNIQUE (code),

    CONSTRAINT ck_plan_abonnement_prix
        CHECK (prix_mensuel >= 0),

    CONSTRAINT ck_plan_abonnement_users
        CHECK (nombre_utilisateurs_max > 0),

    CONSTRAINT ck_plan_abonnement_record_status
        CHECK (
            record_status IN (
                              'ACTIVE',
                              'INACTIVE',
                              'ARCHIVED',
                              'DELETED'
                )
            )
);


CREATE TABLE fonctionnalite_plan_abonnement
(
    id                  BIGINT                   NOT NULL,
    uuid                UUID                     NOT NULL,
    plan_abonnement_id  BIGINT                   NOT NULL,
    code                VARCHAR(80)              NOT NULL,
    nom                 VARCHAR(150)             NOT NULL,
    description         VARCHAR(500),
    actif               BOOLEAN                  NOT NULL DEFAULT TRUE,

    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by          UUID,
    record_status       VARCHAR(20)              NOT NULL,
    version             BIGINT                   NOT NULL,

    CONSTRAINT pk_fonctionnalite_plan_abonnement
        PRIMARY KEY (id),

    CONSTRAINT uk_fonctionnalite_plan_abonnement_uuid
        UNIQUE (uuid),

    CONSTRAINT uk_fonctionnalite_plan_abonnement_plan_code
        UNIQUE (plan_abonnement_id, code),

    CONSTRAINT fk_fonctionnalite_plan_abonnement_plan
        FOREIGN KEY (plan_abonnement_id)
            REFERENCES plan_abonnement (id),

    CONSTRAINT ck_fonctionnalite_plan_abonnement_record_status
        CHECK (
            record_status IN (
                              'ACTIVE',
                              'INACTIVE',
                              'ARCHIVED',
                              'DELETED'
                )
            )
);


CREATE INDEX idx_fonctionnalite_plan_abonnement_plan
    ON fonctionnalite_plan_abonnement (plan_abonnement_id);


CREATE TABLE abonnement
(
    id                  BIGINT                   NOT NULL,
    uuid                UUID                     NOT NULL,
    entreprise_uuid     UUID                     NOT NULL,
    plan_abonnement_id  BIGINT                   NOT NULL,
    date_debut          DATE                     NOT NULL,
    date_fin            DATE                     NOT NULL,
    statut_abonnement   VARCHAR(30)              NOT NULL,

    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by          UUID,
    record_status       VARCHAR(20)              NOT NULL,
    version             BIGINT                   NOT NULL,

    CONSTRAINT pk_abonnement
        PRIMARY KEY (id),

    CONSTRAINT uk_abonnement_uuid
        UNIQUE (uuid),

    CONSTRAINT fk_abonnement_plan
        FOREIGN KEY (plan_abonnement_id)
            REFERENCES plan_abonnement (id),

    CONSTRAINT ck_abonnement_statut
        CHECK (
            statut_abonnement IN (
                                  'EN_PERIODE_ESSAI',
                                  'ACTIF',
                                  'EXPIRE',
                                  'SUSPENDU',
                                  'ANNULE'
                )
            ),

    CONSTRAINT ck_abonnement_record_status
        CHECK (
            record_status IN (
                              'ACTIVE',
                              'INACTIVE',
                              'ARCHIVED',
                              'DELETED'
                )
            ),

    CONSTRAINT ck_abonnement_dates
        CHECK (date_fin >= date_debut)
);


CREATE INDEX idx_abonnement_entreprise
    ON abonnement (entreprise_uuid);

CREATE INDEX idx_abonnement_entreprise_statut
    ON abonnement (entreprise_uuid, statut_abonnement);

CREATE INDEX idx_abonnement_plan
    ON abonnement (plan_abonnement_id);


CREATE TABLE facture_abonnement
(
    id                          BIGINT                   NOT NULL,
    uuid                        UUID                     NOT NULL,
    entreprise_uuid             UUID                     NOT NULL,
    abonnement_id               BIGINT                   NOT NULL,
    numero                      VARCHAR(50)              NOT NULL,
    date_emission               DATE                     NOT NULL,
    date_echeance               DATE                     NOT NULL,
    montant                     NUMERIC(15, 2)           NOT NULL,
    statut_facture_abonnement   VARCHAR(20)              NOT NULL,

    created_at                  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at                  TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by                  UUID,
    record_status               VARCHAR(20)              NOT NULL,
    version                     BIGINT                   NOT NULL,

    CONSTRAINT pk_facture_abonnement PRIMARY KEY (id),
    CONSTRAINT uk_facture_abonnement_uuid UNIQUE (uuid),
    CONSTRAINT uk_facture_abonnement_numero UNIQUE (numero),
    CONSTRAINT fk_facture_abonnement_abonnement
        FOREIGN KEY (abonnement_id)
            REFERENCES abonnement (id),

    CONSTRAINT ck_facture_abonnement_montant CHECK (montant >= 0),

    CONSTRAINT ck_facture_abonnement_statut
        CHECK (
            statut_facture_abonnement IN (
                                          'EN_ATTENTE',
                                          'PAYEE',
                                          'EN_RETARD',
                                          'ANNULEE'
                )
            ),

    CONSTRAINT ck_facture_abonnement_record_status
        CHECK (
            record_status IN (
                              'ACTIVE',
                              'INACTIVE',
                              'ARCHIVED',
                              'DELETED'
                )
            ),

    CONSTRAINT ck_facture_abonnement_dates CHECK (date_echeance >= date_emission)
);

CREATE INDEX idx_facture_abonnement_entreprise ON facture_abonnement (entreprise_uuid);
CREATE INDEX idx_facture_abonnement_abonnement ON facture_abonnement (abonnement_id);
CREATE INDEX idx_facture_abonnement_statut ON facture_abonnement (statut_facture_abonnement);