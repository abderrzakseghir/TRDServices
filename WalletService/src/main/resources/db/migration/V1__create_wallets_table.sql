-- Create wallets table
CREATE TABLE wallets (
    id VARCHAR(255) PRIMARY KEY,
    account_id VARCHAR(255) NOT NULL UNIQUE,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add index on account_id for faster lookups
CREATE INDEX idx_wallets_account_id ON wallets(account_id);

