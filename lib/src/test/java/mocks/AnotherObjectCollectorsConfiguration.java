package mocks;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.api.ObjectCollector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AnotherObjectCollectorsConfiguration {
    public static final String ANOTHER_CUSTOM_OBJECT_COLLECTOR = "anotherCustomObjectCollector";
    public static final String ANOTHER_CUSTOM_OBJECT_COLLECTOR_NAME = "^mocks.AnotherObjectCollectorsConfiguration\\$\\$Lambda\\$\\S+$";

    @Bean(ANOTHER_CUSTOM_OBJECT_COLLECTOR)
    public ObjectCollector createAnotherTestingObjectCollector() {
        return (String flow, Object source, EventsCollectorManager eventsCollectorManager) -> {};
    }
}
