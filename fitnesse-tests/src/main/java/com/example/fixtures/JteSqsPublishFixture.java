package com.example.fixtures;

import com.example.common.JteTemplateEngine;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.net.URI;
import java.time.Instant;

public class JteSqsPublishFixture {

    private final SqsClient sqs = SqsClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(endpoint())
            .build();

    private static URI endpoint() {
        var url = System.getenv("AWS_ENDPOINT_URL");
        return url != null && !url.isBlank() ? URI.create(url) : null;
    }
    private final JteTemplateEngine jte = new JteTemplateEngine();

    private String queueName;
    private String transactionId;
    private double amount;
    private String description;

    public void setQueueUrl(String queueName) {
        this.queueName = queueName;
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

        var url = sqs.getQueueUrl(r -> r.queueName(queueName)).queueUrl();
        var request = SendMessageRequest.builder()
                .queueUrl(url)
                .messageBody(body)
                .build();
        var response = sqs.sendMessage(request);
        return response.messageId();
    }
}
