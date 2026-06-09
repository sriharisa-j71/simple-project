#!/usr/bin/env bash
set -euo pipefail

FLOCI_HEALTH="http://localhost:4566/_localstack/health"
POSTGRES_HOST="${DB_HOST:-localhost}"
POSTGRES_PORT="${DB_PORT:-5432}"
POSTGRES_USER="${DB_USER:-testuser}"
POSTGRES_DB="${DB_NAME:-eventstore}"
RETRY_INTERVAL=5
MAX_RETRIES=30

echo "=== Waiting for services to be ready ==="

echo ""
echo "--- Waiting for Floci (LocalStack-compatible) ---"
floci_ready=false
for i in $(seq 1 $MAX_RETRIES); do
    if curl -sf "$FLOCI_HEALTH" > /dev/null 2>&1; then
        echo "Floci is ready (attempt $i)"
        floci_ready=true
        break
    fi
    echo "Waiting for Floci... (attempt $i/$MAX_RETRIES)"
    sleep "$RETRY_INTERVAL"
done

if [ "$floci_ready" = false ]; then
    echo "ERROR: Floci did not become ready in time."
    exit 1
fi

echo ""
echo "--- Waiting for PostgreSQL ---"
pg_ready=false
for i in $(seq 1 $MAX_RETRIES); do
    if command -v pg_isready &>/dev/null; then
        if pg_isready -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$POSTGRES_DB" > /dev/null 2>&1; then
            echo "PostgreSQL is ready (attempt $i)"
            pg_ready=true
            break
        fi
    else
        # Fallback: check via docker
        if docker compose exec -T postgres pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB" > /dev/null 2>&1; then
            echo "PostgreSQL is ready (attempt $i)"
            pg_ready=true
            break
        fi
    fi
    echo "Waiting for PostgreSQL... (attempt $i/$MAX_RETRIES)"
    sleep "$RETRY_INTERVAL"
done

if [ "$pg_ready" = false ]; then
    echo "ERROR: PostgreSQL did not become ready in time."
    exit 1
fi

echo ""
echo -e "\033[32mAll services are ready!\033[0m"
