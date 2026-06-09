package main

import (
	"context"
	"database/sql"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"os"
	"strings"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	_ "github.com/lib/pq"
)

var (
	bucketName string
	dbHost     string
	dbPort     string
	dbName     string
	dbUser     string
	dbPassword string
	s3Client   *s3.Client
)

type TransactionRecord struct {
	ID          string  `json:"id"`
	Amount      float64 `json:"amount"`
	Description string  `json:"description,omitempty"`
	Timestamp   string  `json:"timestamp"`
}

func init() {
	bucketName = os.Getenv("BUCKET_NAME")
	dbHost = getEnvOrDefault("DB_HOST", "localhost")
	dbPort = getEnvOrDefault("DB_PORT", "5432")
	dbName = getEnvOrDefault("DB_NAME", "eventstore")
	dbUser = getEnvOrDefault("DB_USER", "testuser")
	dbPassword = getEnvOrDefault("DB_PASSWORD", "testpass")

	cfg, err := config.LoadDefaultConfig(context.Background(),
		config.WithRegion(os.Getenv("AWS_REGION")),
	)
	if err != nil {
		log.Fatalf("failed to load config: %v", err)
	}

	s3Client = s3.NewFromConfig(cfg, func(o *s3.Options) {
		o.UsePathStyle = true
	})
}

func getEnvOrDefault(key, defaultVal string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return defaultVal
}

func getDBConnection() (*sql.DB, error) {
	connStr := fmt.Sprintf("host=%s port=%s user=%s password=%s dbname=%s sslmode=disable",
		dbHost, dbPort, dbUser, dbPassword, dbName)
	return sql.Open("postgres", connStr)
}

func ensureTableExists(db *sql.DB) error {
	query := `
	CREATE TABLE IF NOT EXISTS transactions (
		id TEXT PRIMARY KEY,
		amount NUMERIC(12,2) NOT NULL,
		description TEXT,
		created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
	)`
	_, err := db.Exec(query)
	return err
}

func handler(ctx context.Context, s3Event events.S3Event) error {
	log.Printf("Lambda2 invoked with %d S3 event records", len(s3Event.Records))

	for i, record := range s3Event.Records {
		bucket := record.S3.Bucket.Name
		key := record.S3.Object.Key

		log.Printf("Lambda2 processing event[%d] eventName=%s bucket=%s key=%s", i, record.EventName, bucket, key)
		log.Printf("Lambda2 fetching S3 object s3://%s/%s", bucket, key)

		result, err := s3Client.GetObject(ctx, &s3.GetObjectInput{
			Bucket: aws.String(bucket),
			Key:    aws.String(key),
		})
		if err != nil {
			log.Printf("Lambda2 ERROR reading S3 object s3://%s/%s: %v", bucket, key, err)
			return err
		}
		defer result.Body.Close()

		log.Printf("Lambda2 successfully read S3 object, size=%d bytes", *result.ContentLength)

		bodyBytes, err := io.ReadAll(result.Body)
		if err != nil {
			log.Printf("Lambda2 ERROR reading S3 object body: %v", err)
			return err
		}
		body := strings.TrimSpace(string(bodyBytes))
		log.Printf("Lambda2 read %d bytes from s3://%s/%s", len(body), bucket, key)

		var tx TransactionRecord
		if err := json.Unmarshal([]byte(body), &tx); err != nil {
			log.Printf("Lambda2 body is not JSON, inserting as raw text: %.100s", body)
			tx = TransactionRecord{
				ID:          key,
				Amount:      0,
				Description: body,
				Timestamp:   "",
			}
		} else {
			log.Printf("Lambda2 parsed JSON transaction: id=%s amount=%.2f", tx.ID, tx.Amount)
		}

		log.Printf("Lambda2 connecting to PostgreSQL at %s:%s/%s", dbHost, dbPort, dbName)
		db, err := getDBConnection()
		if err != nil {
			log.Printf("Lambda2 ERROR connecting to PostgreSQL: %v", err)
			return err
		}
		defer db.Close()

		if err := db.Ping(); err != nil {
			log.Printf("Lambda2 ERROR pinging PostgreSQL: %v", err)
			return err
		}
		log.Printf("Lambda2 connected to PostgreSQL successfully")

		if err := ensureTableExists(db); err != nil {
			log.Printf("Lambda2 ERROR creating transactions table: %v", err)
			return err
		}

		insertQuery := `INSERT INTO transactions (id, amount, description, created_at)
			VALUES ($1, $2, $3, NOW())
			ON CONFLICT (id) DO NOTHING`
		resultExec, err := db.Exec(insertQuery, tx.ID, tx.Amount, tx.Description)
		if err != nil {
			log.Printf("Lambda2 ERROR inserting transaction id=%s: %v", tx.ID, err)
			return err
		}

		rowsAffected, _ := resultExec.RowsAffected()
		log.Printf("Lambda2 inserted transaction id=%s into PostgreSQL, rowsAffected=%d", tx.ID, rowsAffected)
	}

	log.Printf("Lambda2 completed processing %d S3 records", len(s3Event.Records))
	return nil
}

func main() {
	lambda.Start(handler)
}
