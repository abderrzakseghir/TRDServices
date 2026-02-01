-- 1. ACCOUNTS TABLE
CREATE TABLE accounts (
    id VARCHAR(36) PRIMARY KEY,
    keycloak_id VARCHAR(36) NOT NULL UNIQUE, -- Link to Auth
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_accounts_keycloak ON accounts(keycloak_id);
CREATE INDEX idx_accounts_email ON accounts(email);

-- 2. BET HISTORY TABLE
CREATE TABLE bet_history (
    id BIGSERIAL PRIMARY KEY, -- Database ID
    account_id VARCHAR(36) NOT NULL, -- Link to Parent
    bet_id VARCHAR(36) NOT NULL, -- Business Key from Betting Service

    game_name VARCHAR(255) NOT NULL,
    market_name VARCHAR(100) NOT NULL,
    selection VARCHAR(100) NOT NULL,

    odds DECIMAL(10, 2) NOT NULL,
    amount_wagered DECIMAL(19, 2) NOT NULL,
    payout DECIMAL(19, 2) NOT NULL,

    status VARCHAR(20) NOT NULL, -- PENDING, WON, LOST
    placed_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_history_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE INDEX idx_history_account ON bet_history(account_id);
CREATE INDEX idx_history_bet_id ON bet_history(bet_id);