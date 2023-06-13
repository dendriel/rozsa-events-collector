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

public class HttpEventsSubmitter implements EventsSubmitter {
    public static final Logger log = LoggerFactory.getLogger(HttpEventsSubmitter.class);

    private final String contentTypeHeader = "Content-Type";
    private final String jsonContentType = "application/json";

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
    public void submit(final Map<String, Object> eventData) throws IOException {
        if (eventData.size() == 0) {
            log.debug("Can't submit an event without data.");
            return;
        }

        List<Map<String, Object>> events = List.of(eventData);
        final String body = mapper.writeValueAsString(events);

        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .header(contentTypeHeader,jsonContentType)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        CompletableFuture<Integer> completableFuture = httpClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode);

        completableFuture.whenComplete((integer, throwable) -> {
            log.trace("Events submission has completed with status {} - throwable {} - total events submitted {}", integer, throwable, events.size());
        });
    }
}
