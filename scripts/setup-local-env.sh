#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

echo "=========================================="
echo " Event-Driven Testing Pipeline - Setup"
echo "=========================================="

echo ""
echo "=== Step 1: Check Dependencies ==="
bash scripts/check-dependencies.sh

echo ""
echo "=== Step 2: Build Go Lambdas ==="
echo "Building Lambda 1 (SQS -> S3)..."
cd "$ROOT_DIR/lambdas/lambda1"
GOOS=linux GOARCH=amd64 go build -o bootstrap main.go
zip -j function.zip bootstrap
rm -f bootstrap

echo "Building Lambda 2 (S3 -> DB)..."
cd "$ROOT_DIR/lambdas/lambda2"
GOOS=linux GOARCH=amd64 go build -o bootstrap main.go
zip -j function.zip bootstrap
rm -f bootstrap

echo ""
echo "Building Lambda 3 (Python SQS -> S3)..."
cd "$ROOT_DIR/lambdas/lambda3"
zip -j function.zip main.py requirements.txt

echo ""
echo "=== Step 3: Build Maven Project ==="
cd "$ROOT_DIR"
mvn clean package -DskipTests

echo ""
echo "=== Step 4: Start Docker Services ==="
cd "$ROOT_DIR"
docker compose up -d --wait

echo ""
echo "=== Step 5: Wait for Services ==="
bash scripts/wait-for-services.sh

echo ""
echo "=== Step 6: Initialize OpenTofu ==="
cd "$ROOT_DIR/infra"
tofu init

echo ""
echo "=== Step 7: Apply OpenTofu ==="
tofu apply -auto-approve

echo ""
echo "=== Step 8: Initialize FitNesse ==="
cd "$ROOT_DIR"
WIKI_DIR="fitnesse-wiki"
if [ ! -d "$WIKI_DIR" ]; then
    mkdir -p "$WIKI_DIR"
fi

# Create FitNesse root page
cat > "$WIKI_DIR/FitNesseRoot/content.txt" << 'WIKI'
!1 Event-Driven Testing Pipeline

!2 Test Suites

* SimpleProjectTest — End-to-end test: SQS publish → Lambda → CloudWatch logs → PostgreSQL

!2 Prerequisites

Ensure the local environment is running (docker compose up) and OpenTofu has been applied.

!contents -R -g -p -f
WIKI

echo ""
echo "=== Setup Complete ==="
echo ""
echo "To start FitNesse, run:"
echo "  cd fitnesse-tests && mvn exec:java -Dexec.mainClass=\"fitnesseMain.FitNesseMain\" -Dexec.args=\"-p 9090 -d ../fitnesse-wiki\""
echo ""
echo "Then open http://localhost:9090 in your browser."
