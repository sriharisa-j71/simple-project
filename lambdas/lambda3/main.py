import json
import os
import boto3
import gzip
import base64
from datetime import datetime, timezone

s3 = boto3.client("s3", endpoint_url=os.environ.get("AWS_ENDPOINT_URL"))
bucket = os.environ.get("BUCKET_NAME", "event-processing-bucket")


def handle_sqs(event):
    print(f"Lambda3 (Python) received {len(event.get('Records', []))} SQS records")
    for record in event.get("Records", []):
        msg_id = record.get("messageId", "unknown")
        body = record.get("body", "")
        print(f"Lambda3 processing SQS message id={msg_id}, body_length={len(body)}")
        key = f"events/py-{msg_id}-{datetime.now(timezone.utc).isoformat()}.txt"
        s3.put_object(Bucket=bucket, Key=key, Body=body.encode())
        print(f"Lambda3 wrote SQS message {msg_id} to s3://{bucket}/{key}")


def handle_cw_logs(event):
    cw_data = event.get("awslogs", {})
    log_data = cw_data.get("data", "")
    print(f"Lambda3 received CloudWatch Logs subscription event")
    decoded = base64.b64decode(log_data)
    decompressed = gzip.decompress(decoded).decode("utf-8")
    log_events = json.loads(decompressed)

    log_group = log_events.get("logGroup", "unknown")
    log_stream = log_events.get("logStream", "unknown")
    msg_type = log_events.get("messageType", "unknown")
    owner = log_events.get("owner", "unknown")
    print(f"Lambda3 CW logs: logGroup={log_group} logStream={log_stream} messageType={msg_type} owner={owner}")

    for log_event in log_events.get("logEvents", []):
        ts = datetime.fromtimestamp(log_event["timestamp"] / 1000, tz=timezone.utc)
        print(f"Lambda3 CW log entry: id={log_event['id']} timestamp={ts.isoformat()} message={log_event['message'][:200]}")
        key = f"logs/{log_group}/{log_stream}/{log_event['id']}-{datetime.now(timezone.utc).isoformat()}.txt"
        s3.put_object(Bucket=bucket, Key=key, Body=log_event["message"].encode())
        print(f"Lambda3 archived log entry {log_event['id']} to s3://{bucket}/{key}")


def handler(event, context):
    print(f"Lambda3 (Python) invoked with event source: {event.get('Records', [{}])[0].get('eventSource', 'unknown')}")

    if "awslogs" in event:
        print("Lambda3 detected CloudWatch Logs trigger")
        handle_cw_logs(event)
        return {"statusCode": 200, "body": json.dumps({"processed": "cw_logs"})}

    if "Records" in event:
        first = event["Records"][0]
        if first.get("eventSource") == "aws:sqs":
            print("Lambda3 detected SQS trigger")
            handle_sqs(event)
            return {"statusCode": 200, "body": json.dumps({"processed": "sqs"})}

        if first.get("eventSource") == "aws:s3":
            print("Lambda3 detected S3 trigger (not processing, logging only)")
            for r in event["Records"]:
                print(f"Lambda3 observed S3 event: bucket={r['s3']['bucket']['name']} key={r['s3']['object']['key']}")
            return {"statusCode": 200, "body": json.dumps({"observed": "s3"})}

    print(f"Lambda3 unknown event type: {json.dumps(event)[:500]}")
    return {"statusCode": 200, "body": json.dumps({"processed": "unknown"})}
