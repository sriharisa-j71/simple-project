@param String messageId
@param String body
@param String timestamp
{
  "Records": [
    {
      "messageId": "${messageId}",
      "body": ${body},
      "timestamp": "${timestamp}",
      "eventSource": "aws:sqs",
      "eventSourceARN": "arn:aws:sqs:us-east-1:000000000000:domain-event-queue"
    }
  ]
}
