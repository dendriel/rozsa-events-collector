package rozsa.events.collector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rozsa.events.collector.api.EventsIdGenerator;

@Configuration
@EnableAutoConfiguration
public class EventsCollectorAutoConfiguration {

    // TODO: validate condition is working.
    @Bean
    @ConditionalOnMissingBean(EventsIdGenerator.class)
    public EventsIdGenerator provideEventsIdGenerator() {
        return new UUIDEventsIdGenerator();
    }

//            @Value("${rozsa.events-collector.submit.endpoint:http://localhost:8080/collect}") final String endpoint


    @Bean
    public EventsCollectorManager provideEventsCollectorManager(
            @Value("${rozsa.events-collector.event.key:id}")
            final String id,
            final EventsIdGenerator eventsIdGenerator
            ) {

        return new EventsCollectorManager(id, eventsIdGenerator);
    }
}
