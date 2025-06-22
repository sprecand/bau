-- Initial database schema for Bau platform (UUID version)

-- Enable uuid-ossp extension if not already enabled
-- CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Users table (for AWS Cognito integration)
CREATE TABLE users (
    id UUID PRIMARY KEY,
    cognito_id VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    status VARCHAR(20) DEFAULT 'AKTIV' CHECK (status IN ('AKTIV', 'INACTIV')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Companies table
CREATE TABLE betrieb (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    adresse TEXT NOT NULL,
    email VARCHAR(255) NOT NULL,
    telefon VARCHAR(50),
    status VARCHAR(20) DEFAULT 'AKTIV' CHECK (status IN ('AKTIV', 'INACTIV')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Demands table
CREATE TABLE bedarf (
    id UUID PRIMARY KEY,
    betrieb_id UUID NOT NULL,
    holzbau_anzahl INTEGER NOT NULL DEFAULT 0 CHECK (holzbau_anzahl >= 0),
    zimmermann_anzahl INTEGER NOT NULL DEFAULT 0 CHECK (zimmermann_anzahl >= 0),
    datum_von DATE NOT NULL,
    datum_bis DATE NOT NULL,
    adresse TEXT NOT NULL,
    mit_werkzeug BOOLEAN DEFAULT FALSE,
    mit_fahrzeug BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'AKTIV' CHECK (status IN ('AKTIV', 'INACTIV', 'ABGESCHLOSSEN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT bedarf_dates_check CHECK (datum_bis >= datum_von)
);

-- Indexes for performance
CREATE INDEX idx_users_cognito_id ON users(cognito_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_betrieb_email ON betrieb(email);
CREATE INDEX idx_bedarf_betrieb_id ON bedarf(betrieb_id);
CREATE INDEX idx_bedarf_status ON bedarf(status);
CREATE INDEX idx_bedarf_dates ON bedarf(datum_von, datum_bis);

-- Update triggers for updated_at timestamps (PostgreSQL only)
-- CREATE OR REPLACE FUNCTION update_updated_at_column()
-- RETURNS TRIGGER AS $$
-- BEGIN
--     NEW.updated_at = CURRENT_TIMESTAMP;
--     RETURN NEW;
-- END;
-- $$ language 'plpgsql';

-- CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
--     FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- CREATE TRIGGER update_betrieb_updated_at BEFORE UPDATE ON betrieb
--     FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- CREATE TRIGGER update_bedarf_updated_at BEFORE UPDATE ON bedarf
--     FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
