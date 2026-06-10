# Event-Driven Testing Pipeline

Multi-module Maven project for end-to-end testing of an event-driven architecture: SQS → Lambda → S3 → Lambda → PostgreSQL, running locally via [Floci](https://github.com/floci/floci) (LocalStack-compatible).

## Architecture

```
┌─────────┐   SQS event    ┌──────────┐   S3 PutObject   ┌────────┐
│  SQS     │──────────────▶│ Lambda 1 │─────────────────▶│  S3    │
│ Queue    │               │ (Go)     │                  │ Bucket │
└─────────┘               └──────────┘                  └───┬────┘
      │                                                     │
      │ SQS event                                            │ s3:ObjectCreated
      │                                                     │
      ▼                                                     ▼
┌──────────┐                                       ┌──────────┐
│ Lambda 3 │◀──── CloudWatch Logs Subscription ────│ Lambda 2 │
│ (Python) │                                       │ (Go)     │
│ SQS→S3   │                                       │ S3→PG    │
│ Log→S3   │                                       └────┬─────┘
└──────────┘                                            │
                                                        │ INSERT
                                                        ▼
                                                 ┌──────────┐
                                                 │PostgreSQL │
                                                 │ eventstore│
                                                 └──────────┘
```

- **Lambda 1** (Go): Triggered by SQS, writes message body as `.txt` to S3
- **Lambda 2** (Go): Triggered by S3 `ObjectCreated`, reads file, inserts into PostgreSQL `transactions` table
- **Lambda 3** (Python): Triggered by both SQS (parallel path to Lambda 1) and CloudWatch Logs subscription filter from Lambda 1's log group; archives logs to S3

## Project Structure

```
├── pom.xml                     # Parent Maven POM (Java 21)
├── common/                     # Shared library: AWS clients, JTE templates, DuckDB, DB connector
├── fitnesse-tests/             # FitNesse test fixtures + wiki
├── infra/                      # OpenTofu (hashicorp/aws ~> 6.2)
├── lambdas/
│   ├── lambda1/main.go         # Go: SQS → S3
│   ├── lambda2/main.go         # Go: S3 → PostgreSQL
│   └── lambda3/main.py         # Python: SQS + CloudWatch Logs → S3
├── postgres/init/              # PostgreSQL init SQL (auto-creates schema)
├── scripts/                    # Build & health-check helpers
└── docker-compose.yml          # Floci (AWS mock) + PostgreSQL 16
```

## Prerequisites

- **Java 21** (set via `JAVA_HOME`)
- **Maven** 3.9+
- **Go** 1.21+
- **Python** 3.12+
- **Docker** + Docker Compose
- **OpenTofu** 1.6+
- **AWS credentials** at `~/.aws/credentials` (can be dummy for local dev)

## Quick Start

```bash
# 1. Build everything
./scripts/setup-local-env.sh

# 2. Verify output
tofu output -state=infra/terraform.tfstate
```

Or step by step:

```bash
# Build Go lambdas
cd lambdas/lambda1 && GOOS=linux GOARCH=amd64 go build -o bootstrap main.go && zip -j function.zip bootstrap && rm bootstrap
cd lambdas/lambda2 && GOOS=linux GOARCH=amd64 go build -o bootstrap main.go && zip -j function.zip bootstrap && rm bootstrap

# Build Python lambda
cd lambdas/lambda3 && zip -j function.zip main.py requirements.txt

# Build Maven
mvn clean package -DskipTests

# Start services
docker compose up -d --wait

# Deploy infrastructure
cd infra && tofu init && tofu apply -auto-approve
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `AWS_ENDPOINT_URL` | — | AWS service endpoint override (set to `http://localhost:4566` for Floci) |
| `AWS_REGION` | `us-east-1` | AWS region |
| `JTE_TEMPLATES` | — | **Required** absolute path to JTE template directory (e.g. `/path/to/common/src/main/jte`) |
| `BUCKET_NAME` | `event-processing-bucket` | S3 bucket for event files |
| `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USER` / `DB_PASSWORD` | localhost / 5432 / eventstore / testuser / testpass | PostgreSQL connection |

## Testing with FitNesse

```bash
cd fitnesse-tests && mvn exec:java \
  -Dexec.mainClass="fitnesseMain.FitNesseMain" \
  -Dexec.args="-p 9090 -d ../fitnesse-wiki"
```

Open http://localhost:9090 and run the `SimpleProjectTest` suite.

The wiki provides decision tables for:
- Publishing events to SQS (raw JSON or JTE-generated)
- Verifying Lambda execution via CloudWatch Logs
- Asserting records in PostgreSQL via `ExistsInDB`
- DuckDB analytics queries on Parquet/CSV data
- HTTP health checks against Floci

## Fixtures

| Fixture | Service |
|---------|---------|
| `SqsPublishFixture` | Publish raw messages to SQS |
| `JteSqsPublishFixture` | Publish JTE-generated SQS events |
| `CloudWatchLogsFixture` | Query CloudWatch Logs via filter pattern |
| `ExistsInDB` | Assert record exists in PostgreSQL |
| `DuckDbQueryFixture` | Run analytics queries via DuckDB |
| `FetchByHttpGetCall` | HTTP GET health checks |

## OpenTofu Resources

| Resource | Name |
|----------|------|
| SQS Queue | `event-ingress-queue` |
| S3 Bucket | `event-processing-bucket` |
| Lambda 1 (Go) | `sqs-to-s3-processor` |
| Lambda 2 (Go) | `s3-to-db-loader` |
| Lambda 3 (Python) | `sqs-to-s3-processor-py` |
| CloudWatch Log Group x3 | `/aws/lambda/{name}` |
| Subscription Filter | Lambda1 logs → Lambda3 |
| IAM Role | `lambda-execution-role` |

## End-to-End Flow

1. **Publish** a JSON transaction to `event-ingress-queue`
2. **Lambda 1** picks it up, writes to S3 as `events/{id}-{timestamp}.txt`
3. **S3 notification** triggers **Lambda 2**
4. **Lambda 2** reads from S3, parses JSON, inserts into `transactions` table
5. **CloudWatch subscription** forwards Lambda 1 logs to **Lambda 3**
6. **Lambda 3** archives log entries to S3 under `logs/` prefix

## PostgreSQL Schema

```sql
CREATE TABLE transactions (
    id TEXT PRIMARY KEY,
    amount NUMERIC(12,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

Auto-created at container startup via `postgres/init/01-schema.sql`.
