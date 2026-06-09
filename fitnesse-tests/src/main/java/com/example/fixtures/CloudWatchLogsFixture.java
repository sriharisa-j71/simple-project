package com.example.fixtures;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class CloudWatchLogsFixture {

    private final CloudWatchLogsClient logs = CloudWatchLogsClient.builder()
            .region(Region.US_EAST_1)
            .endpointOverride(endpoint())
            .build();

    private static URI endpoint() {
        var url = System.getenv("AWS_ENDPOINT_URL");
        return url != null && !url.isBlank() ? URI.create(url) : null;
    }
    private String logGroupName;
    private String filterPattern;

    public void setLogGroupName(String logGroupName) {
        this.logGroupName = logGroupName;
    }

    public void setFilterPattern(String filterPattern) {
        this.filterPattern = filterPattern;
    }

    public boolean logContains(String expected) {
        for (int i = 0; i < 10; i++) {
            var request = FilterLogEventsRequest.builder()
                    .logGroupName(logGroupName)
                    .filterPattern(filterPattern)
                    .build();
            var response = logs.filterLogEvents(request);
            boolean found = response.events().stream()
                    .anyMatch(e -> e.message().contains(expected));
            if (found) {
                System.out.println("logContains FOUND '" + expected + "' in log group " + logGroupName);
                return true;
            }
            System.out.println("logContains retry " + (i+1) + " for '" + expected + "' in " + logGroupName + " - not found yet");
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        System.out.println("logContains FAILED after 10 retries for '" + expected + "' in " + logGroupName);
        return false;
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
