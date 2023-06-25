package com.rozsa.events.collector;

import com.rozsa.events.collector.api.EventsIdGenerator;
import com.rozsa.events.collector.api.EventsSubmitter;
import com.rozsa.events.collector.aspects.BeginCollectingAspect;
import com.rozsa.events.collector.aspects.CollectAspect;
import com.rozsa.events.collector.aspects.CollectReturnAspect;
import com.rozsa.events.collector.aspects.FinishCollectingAspect;
import com.rozsa.events.collector.cached.ObjectCollectorManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;
import java.time.Duration;

public class EventsCollectorAutoConfiguration {

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
    public ObjectCollectorManager provideObjectCollectorManager(final ApplicationContext applicationContext) {
        return new ObjectCollectorManager(applicationContext);
    }

    // TODO: validate if conditional is working.
    @Bean
    @ConditionalOnMissingBean(EventsIdGenerator.class)
    public EventsIdGenerator provideEventsIdGenerator() {
        return new UUIDEventsIdGenerator();
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

    @Bean
    public BeginCollectingAspect provideBeginCollectingAspect(EventsCollectorManager eventsCollectorManager) {
        return new BeginCollectingAspect(eventsCollectorManager);
    }

    @Bean
    public CollectAspect provideCollectAspect(EventsCollectorManager eventsCollectorManager, ObjectCollectorManager objectCollectorManager) {
        return new CollectAspect(eventsCollectorManager, objectCollectorManager);
    }
    @Bean
    public CollectReturnAspect provideCollectReturnAspect(EventsCollectorManager eventsCollectorManager, ObjectCollectorManager objectCollectorManager) {
        return new CollectReturnAspect(eventsCollectorManager, objectCollectorManager);
    }
    @Bean
    public FinishCollectingAspect provideFinishCollectingAspect(EventsCollectorManager eventsCollectorManager) {
        return new FinishCollectingAspect(eventsCollectorManager);
    }
}
