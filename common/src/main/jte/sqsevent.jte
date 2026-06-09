@param String messageId
@param String body
@param String timestamp
{
  "Records": [
    {
      "messageId": "${messageId}",
      "receiptHandle": "AQEB${messageId}Handle",
      "body": "${body}",
      "attributes": {
        "ApproximateReceiveCount": "1",
        "SentTimestamp": "${timestamp}",
        "SenderId": "AIDAJNIERSAMPLE",
        "ApproximateFirstReceiveTimestamp": "${timestamp}"
      },
      "messageAttributes": {},
      "md5OfBody": "md5hash",
      "eventSource": "aws:sqs",
      "eventSourceARN": "arn:aws:sqs:us-east-1:000000000000:event-ingress-queue",
      "awsRegion": "us-east-1"
    }
  ]
}
