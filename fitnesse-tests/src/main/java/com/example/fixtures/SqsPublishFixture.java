package com.example.fixtures;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class SqsPublishFixture {

    private final SqsClient sqs = SqsClient.builder()
            .region(Region.US_EAST_1)
            .build();
    private String queueUrl;
    private String messageBody;

    public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String publish() {
        var request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();
        var response = sqs.sendMessage(request);
        return response.messageId();
    }
}
