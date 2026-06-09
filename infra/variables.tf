variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "sqs_queue_name" {
  description = "Name of the SQS queue"
  type        = string
  default     = "event-ingress-queue"
}

variable "s3_bucket_name" {
  description = "Name of the S3 bucket"
  type        = string
  default     = "event-processing-bucket"
}

variable "lambda1_name" {
  description = "Name of Lambda 1"
  type        = string
  default     = "sqs-to-s3-processor"
}

variable "lambda2_name" {
  description = "Name of Lambda 2"
  type        = string
  default     = "s3-to-db-loader"
}

variable "lambda1_zip_path" {
  description = "Path to the Lambda 1 deployment zip"
  type        = string
  default     = "../lambdas/lambda1/function.zip"
}

variable "lambda2_zip_path" {
  description = "Path to the Lambda 2 deployment zip"
  type        = string
  default     = "../lambdas/lambda2/function.zip"
}

variable "lambda3_name" {
  description = "Name of Lambda 3 (Python)"
  type        = string
  default     = "sqs-to-s3-processor-py"
}

variable "lambda3_zip_path" {
  description = "Path to the Lambda 3 (Python) deployment zip"
  type        = string
  default     = "../lambdas/lambda3/function.zip"
}

variable "db_host" {
  description = "PostgreSQL host"
  type        = string
  default     = "localhost"
}

variable "db_port" {
  description = "PostgreSQL port"
  type        = string
  default     = "5432"
}

variable "db_name" {
  description = "PostgreSQL database name"
  type        = string
  default     = "eventstore"
}

variable "db_user" {
  description = "PostgreSQL user"
  type        = string
  default     = "testuser"
}

variable "db_password" {
  description = "PostgreSQL password"
  type        = string
  default     = "testpass"
}
