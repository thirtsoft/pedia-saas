CREATE TABLE patient (
                         id BIGINT NOT NULL,
                         uuid UUID NOT NULL,

                         entreprise_uuid UUID NOT NULL,

                         nom VARCHAR(100) NOT NULL,
                         prenom VARCHAR(100) NOT NULL,
                         date_naissance DATE NOT NULL,
                         sexe VARCHAR(20) NOT NULL,

                         created_by UUID,
                         record_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                         created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                         updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                         version BIGINT NOT NULL,

                         CONSTRAINT pk_patient
                             PRIMARY KEY (id),

                         CONSTRAINT uk_patient_uuid
                             UNIQUE (uuid),

                         CONSTRAINT uk_patient_entreprise_nom_prenom_date_naissance
                             UNIQUE (
                                     entreprise_uuid,
                                     nom,
                                     prenom,
                                     date_naissance
                                 )
);

CREATE INDEX idx_patient_entreprise
    ON patient (entreprise_uuid);

CREATE INDEX idx_patient_entreprise_nom
    ON patient (entreprise_uuid, nom);

CREATE INDEX idx_patient_entreprise_prenom
    ON patient (entreprise_uuid, prenom);


CREATE TABLE parent (
                        id BIGINT NOT NULL,
                        uuid UUID NOT NULL,

                        entreprise_uuid UUID NOT NULL,

                        nom VARCHAR(100) NOT NULL,
                        prenom VARCHAR(100) NOT NULL,
                        telephone VARCHAR(30) NOT NULL,
                        email VARCHAR(180),
                        adresse VARCHAR(180),

                        created_by UUID,
                        record_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                        created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                        updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                        version BIGINT NOT NULL,

                        CONSTRAINT pk_parent
                            PRIMARY KEY (id),

                        CONSTRAINT uk_parent_uuid
                            UNIQUE (uuid)
);

CREATE INDEX idx_parent_entreprise
    ON parent (entreprise_uuid);

CREATE INDEX idx_parent_entreprise_nom
    ON parent (entreprise_uuid, nom);

CREATE INDEX idx_parent_telephone
    ON parent (entreprise_uuid, telephone);


CREATE TABLE relation_parent_patient (
                                         id BIGINT NOT NULL,
                                         uuid UUID NOT NULL,

                                         entreprise_uuid UUID NOT NULL,

                                         parent_uuid UUID NOT NULL,
                                         patient_uuid UUID NOT NULL,

                                         type_relation VARCHAR(30) NOT NULL,

                                         created_by UUID,
                                         record_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                                         created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                         updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                         version BIGINT NOT NULL,

                                         CONSTRAINT pk_relation_parent_patient
                                             PRIMARY KEY (id),

                                         CONSTRAINT uk_relation_parent_patient_uuid
                                             UNIQUE (uuid),

                                         CONSTRAINT fk_relation_parent
                                             FOREIGN KEY (parent_uuid)
                                                 REFERENCES parent(uuid),

                                         CONSTRAINT fk_relation_patient
                                             FOREIGN KEY (patient_uuid)
                                                 REFERENCES patient(uuid),

                                         CONSTRAINT uk_relation_parent_patient
                                             UNIQUE (
                                                     parent_uuid,
                                                     patient_uuid
                                                 )
);

CREATE INDEX idx_relation_parent
    ON relation_parent_patient (parent_uuid);

CREATE INDEX idx_relation_patient
    ON relation_parent_patient (patient_uuid);

CREATE INDEX idx_relation_entreprise
    ON relation_parent_patient (entreprise_uuid);