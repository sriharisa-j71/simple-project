@param String bucketName
@param String objectKey
@param String eventTime
{
  "Records": [
    {
      "eventVersion": "2.1",
      "eventSource": "aws:s3",
      "awsRegion": "us-east-1",
      "eventTime": "${eventTime}",
      "eventName": "ObjectCreated:Put",
      "s3": {
        "s3SchemaVersion": "1.0",
        "bucket": {
          "name": "${bucketName}",
          "arn": "arn:aws:s3:::${bucketName}"
        },
        "object": {
          "key": "${objectKey}",
          "size": 1024,
          "eTag": "d41d8cd98f00b204e9800998ecf8427e"
        }
      }
    }
  ]
}
