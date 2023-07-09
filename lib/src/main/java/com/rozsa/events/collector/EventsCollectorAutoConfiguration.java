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
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;
import java.time.Duration;

@EnableConfigurationProperties(FlowsConfigurations.class)
public class EventsCollectorAutoConfiguration {

    @Bean
    public EventsCollectorManager provideEventsCollectorManager(
            final EventsCollectorFlowConfiguration configuration,
            final EventsIdGenerator eventsIdGenerator,
            final EventsSubmitter eventsSubmitter
            ) {

        return new EventsCollectorManager(configuration, eventsIdGenerator, eventsSubmitter);
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
            final EventsCollectorFlowConfiguration configuration,
            final HttpClient httpClient
    ) {
        return new HttpEventsSubmitter(configuration, httpClient);
    }

    @Bean("eventsSubmitterHttpClient")
    @ConditionalOnMissingBean(value = HttpClient.class, name = "eventsSubmitterHttpClient")
    public HttpClient provideHttpClient(
            @Value("${rozsa.events-collector.submit-timeout:3000}") final Integer timeoutInMillis
    ) {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeoutInMillis))
                .build();
    }

    @Bean
    public EventsCollectorFlowConfiguration provideEventsCollectorFlowConfiguration(
            @Value("${rozsa.events-collector.submit-endpoint:http://localhost:8080/collect}") final String submitEndpoint,
            @Value("${rozsa.events-collector.event-id-key:id}") final String eventIdKey,
            @Value("${rozsa.events-collector.event-flow-header:x-flow}") final String eventFlowHeader,
            final FlowsConfigurations flowsConfigurations
    ) {
        return new EventsCollectorFlowConfiguration(submitEndpoint, eventIdKey, eventFlowHeader, flowsConfigurations);
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
