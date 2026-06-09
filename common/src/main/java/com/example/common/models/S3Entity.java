package com.example.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record S3Entity(
    @JsonProperty("bucket") S3Bucket bucket,
    @JsonProperty("object") S3Object object
) {}
