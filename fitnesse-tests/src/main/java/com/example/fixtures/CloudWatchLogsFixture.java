package com.example.fixtures;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import java.util.List;
import java.util.stream.Collectors;

public class CloudWatchLogsFixture {

    private final CloudWatchLogsClient logs = CloudWatchLogsClient.builder()
            .region(Region.US_EAST_1)
            .build();
    private String logGroupName;
    private String filterPattern;

    public void setLogGroupName(String logGroupName) {
        this.logGroupName = logGroupName;
    }

    public void setFilterPattern(String filterPattern) {
        this.filterPattern = filterPattern;
    }

    public boolean logContains(String expected) {
        var request = FilterLogEventsRequest.builder()
                .logGroupName(logGroupName)
                .filterPattern(filterPattern)
                .build();
        var response = logs.filterLogEvents(request);
        return response.events().stream()
                .anyMatch(e -> e.message().contains(expected));
    }

    public List<String> matchingMessages() {
        var request = FilterLogEventsRequest.builder()
                .logGroupName(logGroupName)
                .filterPattern(filterPattern)
                .build();
        return logs.filterLogEvents(request).events().stream()
                .map(e -> e.message())
                .collect(Collectors.toList());
    }
}
