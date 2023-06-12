package com.rozsa.events.collector;

import com.rozsa.events.collector.api.EventsIdGenerator;
import com.rozsa.events.collector.api.EventsSubmitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableAutoConfiguration
public class EventsCollectorAutoConfiguration {

    // TODO: validate if conditional is working.
    @Bean
    @ConditionalOnMissingBean(EventsIdGenerator.class)
    public EventsIdGenerator provideEventsIdGenerator() {
        return new UUIDEventsIdGenerator();
    }

    @Bean
    public EventsCollectorManager provideEventsCollectorManager(
            @Value("${rozsa.events-collector.event.key:id}")
            final String id,
            final EventsIdGenerator eventsIdGenerator,
            final EventsSubmitter eventsSubmitter
            ) {

        return new EventsCollectorManager(id, eventsIdGenerator, eventsSubmitter);
    }

    @Bean
    @ConditionalOnMissingBean(EventsSubmitter.class)
    public EventsSubmitter provideRestEventsSubmitter(
            final HttpClient httpClient,
            @Value("${rozsa.events-collector.submit.endpoint:http://localhost:8080/collect}") final String endpoint
    ) {
        return new HttpEventsSubmitter(httpClient, endpoint);
    }

    @Bean("eventsSubmitterHttpClient")
    @ConditionalOnMissingBean(value = HttpClient.class, name = "eventsSubmitterHttpClient")
    public HttpClient provideHttpClient(
            @Value("${rozsa.events-collector.submit.timeout:3000}") final Integer timeoutInMillis
    ) {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeoutInMillis))
                .build();
    }
}
