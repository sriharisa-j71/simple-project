terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.2"
    }
  }
}

provider "aws" {
  region                      = var.aws_region
  access_key                  = "test"
  secret_key                  = "test"
  skip_credentials_validation = true
  skip_requesting_account_id  = true
  skip_metadata_api_check     = true
  s3_use_path_style           = true

  endpoints {
    sqs           = "http://localhost:4566"
    s3            = "http://localhost:4566"
    lambda        = "http://localhost:4566"
    iam           = "http://localhost:4566"
    sts           = "http://localhost:4566"
    cloudwatch    = "http://localhost:4566"
    cloudwatchlogs = "http://localhost:4566"
  }
}

# CloudWatch Log Groups for Lambda
resource "aws_cloudwatch_log_group" "lambda1_logs" {
  name              = "/aws/lambda/${var.lambda1_name}"
  retention_in_days = 7
}

resource "aws_cloudwatch_log_group" "lambda2_logs" {
  name              = "/aws/lambda/${var.lambda2_name}"
  retention_in_days = 7
}

resource "aws_cloudwatch_log_group" "lambda3_logs" {
  name              = "/aws/lambda/${var.lambda3_name}"
  retention_in_days = 7
}

# SQS Queue
resource "aws_sqs_queue" "event_queue" {
  name                       = var.sqs_queue_name
  delay_seconds              = 0
  max_message_size           = 262144
  message_retention_seconds  = 86400
  receive_wait_time_seconds  = 0
}

# S3 Bucket
resource "aws_s3_bucket" "event_bucket" {
  bucket = var.s3_bucket_name
  force_destroy = true
}

# IAM Role for Lambda
resource "aws_iam_role" "lambda_role" {
  name = "lambda-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy" "lambda_policy" {
  name = "lambda-execution-policy"
  role = aws_iam_role.lambda_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "arn:aws:logs:*:*:*"
      },
      {
        Effect = "Allow"
        Action = [
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ]
        Resource = aws_sqs_queue.event_queue.arn
      },
      {
        Effect = "Allow"
        Action = [
          "s3:PutObject",
          "s3:GetObject"
        ]
        Resource = "${aws_s3_bucket.event_bucket.arn}/*"
      }
    ]
  })
}

# Lambda 1: SQS Trigger -> S3 Write
resource "aws_lambda_function" "lambda1" {
  function_name = var.lambda1_name
  handler       = "bootstrap"
  runtime       = "provided.al2023"
  role          = aws_iam_role.lambda_role.arn
  filename      = var.lambda1_zip_path
  source_code_hash = filebase64sha256(var.lambda1_zip_path)

  environment {
    variables = {
      BUCKET_NAME = aws_s3_bucket.event_bucket.id
      AWS_REGION  = var.aws_region
    }
  }
}

resource "aws_lambda_event_source_mapping" "sqs_trigger" {
  event_source_arn = aws_sqs_queue.event_queue.arn
  function_name    = aws_lambda_function.lambda1.arn
  batch_size       = 1
  enabled          = true
}

resource "aws_lambda_event_source_mapping" "sqs_trigger_py" {
  event_source_arn = aws_sqs_queue.event_queue.arn
  function_name    = aws_lambda_function.lambda3.arn
  batch_size       = 1
  enabled          = true
}

# Lambda 2: S3 Trigger -> DB Insert
resource "aws_lambda_function" "lambda2" {
  function_name = var.lambda2_name
  handler       = "bootstrap"
  runtime       = "provided.al2023"
  role          = aws_iam_role.lambda_role.arn
  filename      = var.lambda2_zip_path
  source_code_hash = filebase64sha256(var.lambda2_zip_path)

  environment {
    variables = {
      BUCKET_NAME     = aws_s3_bucket.event_bucket.id
      DB_HOST         = var.db_host
      DB_PORT         = var.db_port
      DB_NAME         = var.db_name
      DB_USER         = var.db_user
      DB_PASSWORD     = var.db_password
      AWS_REGION      = var.aws_region
    }
  }
}

# Lambda 3: SQS Trigger -> S3 Write (Python)
resource "aws_lambda_function" "lambda3" {
  function_name = var.lambda3_name
  handler       = "main.handler"
  runtime       = "python3.12"
  role          = aws_iam_role.lambda_role.arn
  filename      = var.lambda3_zip_path
  source_code_hash = filebase64sha256(var.lambda3_zip_path)

  environment {
    variables = {
      BUCKET_NAME = aws_s3_bucket.event_bucket.id
      AWS_REGION  = var.aws_region
    }
  }
}

resource "aws_s3_bucket_notification" "bucket_notification" {
  bucket = aws_s3_bucket.event_bucket.id

  lambda_function {
    lambda_function_arn = aws_lambda_function.lambda2.arn
    events              = ["s3:ObjectCreated:Put", "s3:ObjectCreated:Post", "s3:ObjectCreated:Copy"]
    filter_suffix       = ".txt"
  }

  depends_on = [aws_lambda_permission.allow_s3]
}

resource "aws_lambda_permission" "allow_sqs" {
  statement_id  = "AllowExecutionFromSQS"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.lambda1.function_name
  principal     = "sqs.amazonaws.com"
  source_arn    = aws_sqs_queue.event_queue.arn
}

resource "aws_lambda_permission" "allow_sqs_py" {
  statement_id  = "AllowExecutionFromSQSPython"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.lambda3.function_name
  principal     = "sqs.amazonaws.com"
  source_arn    = aws_sqs_queue.event_queue.arn
}

resource "aws_lambda_permission" "allow_s3" {
  statement_id  = "AllowExecutionFromS3"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.lambda2.function_name
  principal     = "s3.amazonaws.com"
  source_arn    = aws_s3_bucket.event_bucket.arn
}

resource "aws_lambda_permission" "allow_cw_logs" {
  statement_id  = "AllowExecutionFromCloudWatchLogs"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.lambda3.function_name
  principal     = "logs.${var.aws_region}.amazonaws.com"
  source_arn    = aws_cloudwatch_log_group.lambda1_logs.arn
}

resource "aws_cloudwatch_log_subscription_filter" "lambda1_to_lambda3" {
  name            = "lambda1-to-lambda3-subscription"
  log_group_name  = aws_cloudwatch_log_group.lambda1_logs.name
  filter_pattern  = ""
  destination_arn = aws_lambda_function.lambda3.arn
  depends_on      = [aws_lambda_permission.allow_cw_logs]
}
