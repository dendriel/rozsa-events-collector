package mocks;

import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.api.ObjectCollector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.function.Function;

@Configuration
public class ObjectCollectorsConfiguration {
    public static final String CUSTOM_OBJECT_COLLECTOR = "customObjectCollector";
    public static final String INVALID_CUSTOM_OBJECT_COLLECTOR = "invalidCustomObjectCollector";
    public static final String BROKEN_CUSTOM_OBJECT_COLLECTOR = "brokenCustomObjectCollector";
    public static final String CUSTOM_OBJECT_COLLECTOR_NAME = "^mocks.ObjectCollectorsConfiguration\\$\\$Lambda\\$\\S+$";
    public static final String CUSTOM_OBJECT_COLLECTOR_KEY = "customized_key";

    @Bean(CUSTOM_OBJECT_COLLECTOR)
    public ObjectCollector createTestingObjectCollector() {
        return (Object source, EventsCollectorManager eventsCollectorManager) -> {
            ObjectForCustomCollection target = (ObjectForCustomCollection) source;

            String value = formatCollectedValue(target);
            eventsCollectorManager.collect(CUSTOM_OBJECT_COLLECTOR_KEY, value);
        };
    }

    public static String formatCollectedValue(ObjectForCustomCollection target) {
        return String.format("%s is %d years old and loves %s",
                target.getName(), target.getAge(), StringUtils.join(target.getHobies(), ", "));
    }

    @Bean(INVALID_CUSTOM_OBJECT_COLLECTOR)
    public Function<String, Void> createInvalidTestingObjectCollector() {
        return (String input) -> null;
    }

    @Lazy
    @Bean(BROKEN_CUSTOM_OBJECT_COLLECTOR)
    public ObjectCollector throwsExceptionWhileCreatingObjectCollector() {
        throw new RuntimeException();
    }
}
