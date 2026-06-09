package com.example.fixtures;

import com.example.common.JteTemplateEngine;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.Instant;
import java.util.UUID;

public class JteSqsPublishFixture {

    private final SqsClient sqs = SqsClient.builder()
            .region(Region.US_EAST_1)
            .build();
    private final JteTemplateEngine jte = new JteTemplateEngine();

    private String queueUrl;
    private String transactionId;
    private double amount;
    private String description;

    public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String publish() {
        var timestamp = Instant.now().toString();
        var body = jte.renderTransaction(transactionId, amount, description, timestamp);
        var sqsEvent = jte.renderSqsEvent(UUID.randomUUID().toString(), body, timestamp);

        var request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(sqsEvent)
                .build();
        var response = sqs.sendMessage(request);
        return response.messageId();
    }
}
