CREATE TABLE onboarding (
                            id BIGINT NOT NULL,
                            uuid UUID NOT NULL,

                            entreprise_uuid UUID,

                            utilisateur_uuid UUID NOT NULL,

                            onboarding_status VARCHAR(20) NOT NULL,
                            current_step VARCHAR(50) NOT NULL,

                            started_at TIMESTAMP WITH TIME ZONE NOT NULL,
                            completed_at TIMESTAMP WITH TIME ZONE,
                            last_activity_at TIMESTAMP WITH TIME ZONE NOT NULL,

                            error_code VARCHAR(100),
                            error_message VARCHAR(500),

                            created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                            updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
                            created_by UUID,

                            record_status VARCHAR(20) NOT NULL,
                            version BIGINT NOT NULL,

                            CONSTRAINT pk_onboarding
                                PRIMARY KEY (id),

                            CONSTRAINT uk_onboarding_uuid
                                UNIQUE (uuid),

                            CONSTRAINT uk_onboarding_utilisateur_uuid
                                UNIQUE (utilisateur_uuid),

                            CONSTRAINT ck_onboarding_status
                                CHECK (
                                    onboarding_status IN (
                                                          'EN_COURS',
                                                          'TERMINE',
                                                          'ABANDONNE',
                                                          'ERREUR'
                                        )
                                    ),

                            CONSTRAINT ck_onboarding_current_step
                                CHECK (
                                    current_step IN (
                                                     'CHOIX_ROLE',
                                                     'TYPE_ENTREPRISE',
                                                     'INFORMATIONS_ENTREPRISE',
                                                     'CHOIX_PLAN',
                                                     'VALIDATION'
                                        )
                                    ),

                            CONSTRAINT ck_onboarding_record_status
                                CHECK (
                                    record_status IN (
                                                      'ACTIVE',
                                                      'INACTIVE',
                                                      'ARCHIVED',
                                                      'DELETED'
                                        )
                                    )
);

CREATE INDEX idx_onboarding_status
    ON onboarding (onboarding_status);

CREATE INDEX idx_onboarding_current_step
    ON onboarding (current_step);

CREATE INDEX idx_onboarding_last_activity
    ON onboarding (last_activity_at);

CREATE INDEX idx_onboarding_utilisateur
    ON onboarding (utilisateur_uuid);