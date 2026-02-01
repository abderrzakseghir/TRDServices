-- 1. THE TICKET (BET) TABLE
CREATE TABLE bets (
    id VARCHAR(36) PRIMARY KEY, -- UUID
    account_id VARCHAR(36) NOT NULL,
    type VARCHAR(20) NOT NULL, -- SIMPLE, COMBINED
    status VARCHAR(20) NOT NULL, -- PENDING, CONFIRMED...

    stake DECIMAL(19, 2) NOT NULL,
    total_odds DECIMAL(19, 2) NOT NULL,
    potential_payout DECIMAL(19, 2) NOT NULL,

    created_at TIMESTAMP NOT NULL
);

-- Index for finding user history quickly
CREATE INDEX idx_bets_account_id ON bets(account_id);

-- 2. THE SELECTION (MATCHES) TABLE
CREATE TABLE bet_selections (
    id BIGSERIAL PRIMARY KEY,
    bet_id VARCHAR(36) NOT NULL,

    match_id VARCHAR(50) NOT NULL,
    market_name VARCHAR(100) NOT NULL,
    selection_name VARCHAR(100) NOT NULL,
    odd DECIMAL(10, 2) NOT NULL,

    CONSTRAINT fk_selection_bet FOREIGN KEY (bet_id) REFERENCES bets(id)
);

-- Index for searching specific matches
CREATE INDEX idx_selections_match_id ON bet_selections(match_id);