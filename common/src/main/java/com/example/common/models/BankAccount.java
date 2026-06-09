package com.example.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BankAccount(
    @JsonProperty("accountNumber") String accountNumber,
    @JsonProperty("accountHolder") Person accountHolder,
    @JsonProperty("balance") double balance,
    @JsonProperty("accountType") String accountType
) {}
