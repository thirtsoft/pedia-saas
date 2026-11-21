-- ============================================================
-- pediaSaaS
-- Phase 1
-- Shared persistence + Entreprise
-- ============================================================

-- ------------------------------------------------------------
-- Sequence commune aux identifiants internes
-- ------------------------------------------------------------

CREATE SEQUENCE wokite_sequence
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 50;


-- ------------------------------------------------------------
-- Table entreprise
-- ------------------------------------------------------------

CREATE TABLE entreprise (
                            id BIGINT NOT NULL,
                            uuid UUID NOT NULL,
                            code VARCHAR(50) NOT NULL,
                            nom VARCHAR(150) NOT NULL,
                            type VARCHAR(30) NOT NULL,
                            email VARCHAR(180),
                            telephone VARCHAR(30),
                            adresse VARCHAR(255),

                            created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                            updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                            created_by UUID,

                            record_status VARCHAR(20) NOT NULL,
                            version BIGINT NOT NULL,

                            CONSTRAINT pk_entreprise PRIMARY KEY (id),

                            CONSTRAINT uk_entreprise_uuid UNIQUE (uuid),

                            CONSTRAINT uk_entreprise_code UNIQUE (code),

                            CONSTRAINT ck_entreprise_record_status
                                CHECK (
                                    record_status IN (
                                                      'ACTIVE',
                                                      'INACTIVE',
                                                      'ARCHIVED',
                                                      'DELETED'
                                        )
                                    ),

                            CONSTRAINT ck_entreprise_type
                                CHECK (
                                    type IN (
                                             'MEDECIN_INDEPENDANT',
                                             'CABINET',
                                            'WOKITE'
                                        )
                                    )
);


-- ------------------------------------------------------------
-- Index
-- ------------------------------------------------------------

CREATE INDEX idx_entreprise_record_status ON entreprise (record_status);
CREATE INDEX idx_entreprise_nom ON entreprise (nom);