-- ============================================================
-- pediaSaaS
-- Foundation migration
-- ============================================================

CREATE SCHEMA IF NOT EXISTS pediasaas;

CREATE EXTENSION IF NOT EXISTS pgcrypto
    WITH SCHEMA pediasaas;