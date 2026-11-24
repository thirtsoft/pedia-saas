-- ============================================================
-- DONNEES DE REFERENCE - MODULE ABONNEMENT
-- pediaSaaS
-- ============================================================

-- ============================================================
-- 1. PLANS D'ABONNEMENT
-- ============================================================

INSERT INTO plan_abonnement (
    id,
    uuid,
    code,
    nom,
    description,
    prix_mensuel,
    nombre_utilisateurs_max,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '22222222-2222-2222-2222-000000000001',
    'ESSENTIEL',
    'Essentiel',
    'La formule essentielle pour un médecin indépendant.',
    10000.00,
    1,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
    WHERE NOT EXISTS (
    SELECT 1
    FROM plan_abonnement
    WHERE code = 'ESSENTIEL'
);


INSERT INTO plan_abonnement (
    id,
    uuid,
    code,
    nom,
    description,
    prix_mensuel,
    nombre_utilisateurs_max,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '22222222-2222-2222-2222-000000000002',
    'CABINET',
    'Cabinet',
    'La formule adaptée aux cabinets pédiatriques et à leurs équipes.',
    25000.00,
    5,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
    WHERE NOT EXISTS (
    SELECT 1
    FROM plan_abonnement
    WHERE code = 'CABINET'
);


INSERT INTO plan_abonnement (
    id,
    uuid,
    code,
    nom,
    description,
    prix_mensuel,
    nombre_utilisateurs_max,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '22222222-2222-2222-2222-000000000003',
    'CABINET_PRO',
    'Cabinet Pro',
    'La formule complète pour les cabinets ayant une équipe plus importante.',
    40000.00,
    15,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
    WHERE NOT EXISTS (
    SELECT 1
    FROM plan_abonnement
    WHERE code = 'CABINET_PRO'
);


-- ============================================================
-- 2. FONCTIONNALITES - PLAN ESSENTIEL
-- ============================================================

INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000001',
    p.id,
    'PATIENTS',
    'Gestion des patients',
    'Création, modification, recherche et consultation des patients.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'ESSENTIEL'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'PATIENTS'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000002',
    p.id,
    'DOSSIER_MEDICAL',
    'Dossier médical',
    'Gestion du dossier médical pédiatrique.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'ESSENTIEL'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'DOSSIER_MEDICAL'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000003',
    p.id,
    'CONSULTATIONS',
    'Consultations',
    'Enregistrement et suivi des consultations médicales.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'ESSENTIEL'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'CONSULTATIONS'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000004',
    p.id,
    'AGENDA',
    'Agenda',
    'Gestion des rendez-vous et de l’agenda médical.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'ESSENTIEL'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'AGENDA'
);


-- ============================================================
-- 3. FONCTIONNALITES - PLAN CABINET
-- ============================================================

INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000011',
    p.id,
    'PATIENTS',
    'Gestion des patients',
    'Création, modification, recherche et consultation des patients.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'PATIENTS'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000012',
    p.id,
    'DOSSIER_MEDICAL',
    'Dossier médical',
    'Gestion complète du dossier médical pédiatrique.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'DOSSIER_MEDICAL'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000013',
    p.id,
    'CONSULTATIONS',
    'Consultations',
    'Enregistrement et suivi des consultations médicales.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'CONSULTATIONS'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000014',
    p.id,
    'AGENDA',
    'Agenda',
    'Gestion des rendez-vous et de l’agenda médical.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'AGENDA'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000015',
    p.id,
    'UTILISATEURS',
    'Gestion des utilisateurs',
    'Gestion des membres et des rôles du cabinet.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'UTILISATEURS'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000016',
    p.id,
    'FACTURATION',
    'Facturation',
    'Gestion de la facturation et du suivi financier.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'FACTURATION'
);


-- ============================================================
-- 4. FONCTIONNALITES - PLAN CABINET PRO
-- ============================================================

INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000021',
    p.id,
    'PATIENTS',
    'Gestion des patients',
    'Gestion complète des patients.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET_PRO'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'PATIENTS'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000022',
    p.id,
    'DOSSIER_MEDICAL',
    'Dossier médical',
    'Gestion complète du dossier médical pédiatrique.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET_PRO'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'DOSSIER_MEDICAL'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000023',
    p.id,
    'CONSULTATIONS',
    'Consultations',
    'Enregistrement et suivi des consultations.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET_PRO'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'CONSULTATIONS'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000024',
    p.id,
    'AGENDA',
    'Agenda',
    'Gestion avancée des rendez-vous et de l’agenda.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET_PRO'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'AGENDA'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000025',
    p.id,
    'UTILISATEURS',
    'Gestion des utilisateurs',
    'Gestion avancée des utilisateurs et des rôles.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET_PRO'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'UTILISATEURS'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000026',
    p.id,
    'FACTURATION',
    'Facturation',
    'Gestion complète de la facturation et du suivi financier.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET_PRO'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'FACTURATION'
);


INSERT INTO fonctionnalite_plan_abonnement (
    id,
    uuid,
    plan_abonnement_id,
    code,
    nom,
    description,
    actif,
    created_at,
    updated_at,
    record_status,
    version
)
SELECT
    nextval('wokite_sequence'),
    '33333333-3333-3333-3333-000000000027',
    p.id,
    'REPORTING',
    'Reporting',
    'Tableaux de bord et indicateurs d’activité.',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ACTIVE',
    0
FROM plan_abonnement p
WHERE p.code = 'CABINET_PRO'
  AND NOT EXISTS (
    SELECT 1
    FROM fonctionnalite_plan_abonnement f
    WHERE f.plan_abonnement_id = p.id
      AND f.code = 'REPORTING'
);


-- ============================================================
-- 5. VERIFICATION
-- ============================================================

SELECT
    p.code,
    p.nom,
    p.prix_mensuel,
    p.nombre_utilisateurs_max,
    p.actif,
    COUNT(f.id) AS nombre_fonctionnalites
FROM plan_abonnement p
         LEFT JOIN fonctionnalite_plan_abonnement f
                   ON f.plan_abonnement_id = p.id
WHERE p.record_status = 'ACTIVE'
GROUP BY
    p.id,
    p.code,
    p.nom,
    p.prix_mensuel,
    p.nombre_utilisateurs_max,
    p.actif
ORDER BY p.id;