output "sqs_queue_url" {
  description = "URL of the SQS queue"
  value       = aws_sqs_queue.event_queue.url
}

output "sqs_queue_arn" {
  description = "ARN of the SQS queue"
  value       = aws_sqs_queue.event_queue.arn
}

output "s3_bucket_name" {
  description = "Name of the S3 bucket"
  value       = aws_s3_bucket.event_bucket.id
}

output "s3_bucket_arn" {
  description = "ARN of the S3 bucket"
  value       = aws_s3_bucket.event_bucket.arn
}

output "lambda1_name" {
  description = "Name of Lambda 1"
  value       = aws_lambda_function.lambda1.function_name
}

output "lambda2_name" {
  description = "Name of Lambda 2"
  value       = aws_lambda_function.lambda2.function_name
}

output "lambda1_log_group" {
  description = "CloudWatch log group for Lambda 1"
  value       = "/aws/lambda/${aws_lambda_function.lambda1.function_name}"
}

output "lambda2_log_group" {
  description = "CloudWatch log group for Lambda 2"
  value       = "/aws/lambda/${aws_lambda_function.lambda2.function_name}"
}

output "lambda3_name" {
  description = "Name of Lambda 3 (Python)"
  value       = aws_lambda_function.lambda3.function_name
}

output "lambda3_log_group" {
  description = "CloudWatch log group for Lambda 3"
  value       = "/aws/lambda/${aws_lambda_function.lambda3.function_name}"
}
