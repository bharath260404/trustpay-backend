CREATE TABLE IF NOT EXISTS threat_entities (
    id BIGSERIAL PRIMARY KEY,
    entity_value VARCHAR(255) NOT NULL UNIQUE,
    entity_type VARCHAR(50) NOT NULL,
    reputation_score INT NOT NULL DEFAULT 100,
    total_reports INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_reported_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS scam_reports (
    id BIGSERIAL PRIMARY KEY,
    entity_id BIGINT REFERENCES threat_entities(id) ON DELETE CASCADE,
    reporter_hash VARCHAR(128) NOT NULL,
    fraud_category VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS verification_logs (
    id BIGSERIAL PRIMARY KEY,
    input_type VARCHAR(50) NOT NULL,
    risk_score DOUBLE PRECISION NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_threat_entity_value ON threat_entities(entity_value);