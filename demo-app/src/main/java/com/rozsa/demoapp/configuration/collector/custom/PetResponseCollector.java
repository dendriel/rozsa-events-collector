package com.rozsa.demoapp.configuration.collector.custom;

import com.rozsa.demoapp.configuration.collector.PetFilterFlowKeys;
import com.rozsa.demoapp.resources.dto.PetResponse;
import com.rozsa.events.collector.EventsCollectorManager;
import com.rozsa.events.collector.api.ObjectCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.rozsa.demoapp.configuration.collector.PetFilterFlowKeys.PET_RESPONSE_ENTITY_COLLECTOR;

@Slf4j
@Configuration
public class PetResponseCollector {
    @Bean(PET_RESPONSE_ENTITY_COLLECTOR)
    public ObjectCollector petResponseCollector() {
        return (String flow, Object source, EventsCollectorManager eventsCollectorManager) -> {

            log.info("PetResponseCollector was called!");

            if (source instanceof ResponseEntity<?> target) {
                if (target.getStatusCode() != HttpStatus.OK) {
                    return;
                }

                if (target.getBody() instanceof PetResponse petResponse) {
                    eventsCollectorManager.collect(flow, PetFilterFlowKeys.RESPONSE_NAME, petResponse.getName());
                }
            }
        };
    }
}
