package main

import (
	"context"
	"fmt"
	"log"
	"os"
	"strings"
	"time"

	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

var (
	bucketName string
	s3Client   *s3.Client
)

func init() {
	bucketName = os.Getenv("BUCKET_NAME")
	if bucketName == "" {
		bucketName = "event-processing-bucket"
	}

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

func handler(ctx context.Context, sqsEvent events.SQSEvent) error {
	log.Printf("Lambda1 invoked with %d SQS records", len(sqsEvent.Records))

	for _, message := range sqsEvent.Records {
		log.Printf("Lambda1 processing SQS message ID=%s, groupId=%s, seqNumber=%s",
			message.MessageId, message.Attributes["MessageGroupId"], message.Attributes["SequenceNumber"])

		objectKey := fmt.Sprintf("events/%s-%s.txt", message.MessageId, time.Now().Format(time.RFC3339))
		log.Printf("Lambda1 generating S3 object key=%s", objectKey)

		_, err := s3Client.PutObject(ctx, &s3.PutObjectInput{
			Bucket: aws.String(bucketName),
			Key:    aws.String(objectKey),
			Body:   strings.NewReader(message.Body),
		})
		if err != nil {
			log.Printf("Lambda1 ERROR writing to S3 bucket=%s key=%s: %v", bucketName, objectKey, err)
			return err
		}

		log.Printf("Lambda1 successfully wrote event %s to s3://%s/%s", message.MessageId, bucketName, objectKey)
	}

	log.Printf("Lambda1 completed processing %d records successfully", len(sqsEvent.Records))
	return nil
}

func main() {
	lambda.Start(handler)
}
