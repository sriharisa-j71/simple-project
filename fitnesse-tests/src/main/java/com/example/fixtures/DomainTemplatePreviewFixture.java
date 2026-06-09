package com.example.fixtures;

import com.example.common.JteTemplateEngine;
import com.example.common.models.BankAccount;
import com.example.common.models.Person;
import com.example.common.models.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

public class DomainTemplatePreviewFixture {

    private final JteTemplateEngine jte = new JteTemplateEngine();
    private final ObjectMapper mapper = new ObjectMapper();

    private String transactionId;
    private double amount;
    private String description;
    private String merchant;
    private String accountJson;

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

    public String renderedTransaction() throws Exception {
        var bankAccount = mapper.readValue(accountJson, BankAccount.class);
        var timestamp = Instant.now().toString();
        var transaction = new Transaction(
            transactionId, amount, description, timestamp, bankAccount, merchant
        );
        return jte.renderDomainTransaction(transaction);
    }

    public String renderedSqsEvent() throws Exception {
        var bankAccount = mapper.readValue(accountJson, BankAccount.class);
        var timestamp = Instant.now().toString();
        var transaction = new Transaction(
            transactionId, amount, description, timestamp, bankAccount, merchant
        );
        var body = jte.renderDomainTransaction(transaction);
        var messageId = UUID.randomUUID().toString();
        return jte.renderDomainSqsEvent(messageId, body, timestamp);
    }
}
