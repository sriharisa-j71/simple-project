package com.example.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SqsMessage(
    @JsonProperty("MessageId") String messageId,
    @JsonProperty("ReceiptHandle") String receiptHandle,
    @JsonProperty("Body") String body
) {}
