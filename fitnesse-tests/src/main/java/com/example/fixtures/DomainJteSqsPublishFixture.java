package com.example.fixtures;

import com.example.common.JteTemplateEngine;
import com.example.common.models.BankAccount;
import com.example.common.models.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public class DomainJteSqsPublishFixture {

    private final SqsClient sqs = SqsClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(endpoint())
            .build();

    private static URI endpoint() {
        var url = System.getenv("AWS_ENDPOINT_URL");
        return url != null && !url.isBlank() ? URI.create(url) : null;
    }

    private final JteTemplateEngine jte = new JteTemplateEngine();
    private final ObjectMapper mapper = new ObjectMapper();

    private String queueName;
    private String transactionId;
    private double amount;
    private String description;
    private String merchant;
    private String accountJson;

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

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public void setAccount(String accountJson) {
        this.accountJson = accountJson;
    }

    public String publish() throws Exception {
        var bankAccount = mapper.readValue(accountJson, BankAccount.class);
        var timestamp = Instant.now().toString();
        var transaction = new Transaction(
            transactionId, amount, description, timestamp, bankAccount, merchant
        );
        var body = jte.renderDomainTransaction(transaction);
        var messageId = UUID.randomUUID().toString();
        var event = jte.renderDomainSqsEvent(messageId, body, timestamp);

        var url = sqs.getQueueUrl(r -> r.queueName(queueName)).queueUrl();
        var request = SendMessageRequest.builder()
                .queueUrl(url)
                .messageBody(event)
                .build();
        var response = sqs.sendMessage(request);
        return response.messageId();
    }
}
