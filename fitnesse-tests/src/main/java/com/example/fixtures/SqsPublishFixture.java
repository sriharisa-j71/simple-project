package com.example.fixtures;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import java.net.URI;

public class SqsPublishFixture {

    private final SqsClient sqs = SqsClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(endpoint())
            .build();

    private static URI endpoint() {
        var url = System.getenv("AWS_ENDPOINT_URL");
        return url != null && !url.isBlank() ? URI.create(url) : null;
    }
    private String queueUrl;
    private String queueName;
    private String messageBody;

    public void setQueueUrl(String queueName) {
        this.queueName = queueName;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String publish() {
        var url = sqs.getQueueUrl(r -> r.queueName(queueName)).queueUrl();
        var request = SendMessageRequest.builder()
                .queueUrl(url)
                .messageBody(messageBody)
                .build();
        var response = sqs.sendMessage(request);
        return response.messageId();
    }
}
