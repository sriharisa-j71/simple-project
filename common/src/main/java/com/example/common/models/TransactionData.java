package com.example.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionData(
    @JsonProperty("id") String id,
    @JsonProperty("amount") double amount,
    @JsonProperty("description") String description,
    @JsonProperty("timestamp") String timestamp
) {}
