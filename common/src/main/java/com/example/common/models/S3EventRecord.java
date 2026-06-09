package com.example.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record S3EventRecord(
    @JsonProperty("eventName") String eventName,
    @JsonProperty("s3") S3Entity s3
) {}
