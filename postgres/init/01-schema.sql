CREATE TABLE IF NOT EXISTS transactions (
    id TEXT PRIMARY KEY,
    amount NUMERIC(12,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions (created_at);
