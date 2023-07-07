package com.rozsa.events.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rozsa.events.collector.api.EventsSubmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Simple HTTP events submitter.
 */
public class HttpEventsSubmitter implements EventsSubmitter {
    public static final Logger log = LoggerFactory.getLogger(HttpEventsSubmitter.class);

    private static final String contentTypeHeader = "Content-Type";
    private static final String jsonContentType = "application/json";
    public static final String flowNameHeader = "x-flow";

    private final HttpClient httpClient;

    private final String endpoint;

    private final ObjectMapper mapper;

    public HttpEventsSubmitter(final HttpClient httpClient, final String endpoint) {
        this.httpClient = httpClient;
        this.endpoint = endpoint;

        this.mapper = new ObjectMapper();
    }

    // TODO: configure thread pool
    // TODO: coalescence
    @Override
    public void submit(final String flow, final Map<String, Object> eventData) throws IOException {
        if (eventData.size() == 0) {
            log.debug("Can't submit an event without data.");
            return;
        }

        log.debug("Submitting event: {}", eventData);

        List<Map<String, Object>> events = List.of(eventData);
        final String body = mapper.writeValueAsString(events);

        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .header(contentTypeHeader,jsonContentType)
                .header(flowNameHeader, flow)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        CompletableFuture<Integer> completableFuture = httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode);

        completableFuture.whenComplete((integer, throwable) -> {
            log.debug("Events submission has completed with status {} - throwable {} - total events submitted {}", integer, throwable, events.size());
        });
    }
}
