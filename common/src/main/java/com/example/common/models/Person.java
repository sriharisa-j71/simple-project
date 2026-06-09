package com.example.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Person(
    @JsonProperty("name") String name,
    @JsonProperty("email") String email,
    @JsonProperty("age") int age
) {}
