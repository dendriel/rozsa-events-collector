package com.rozsa.events.collector;


import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig
public class EventsCollectorFlowConfigurationTest {

    @Test
    void givenConfigWithDefaultValues_whenCalledWithAnyFlowName_thenShouldReturnDefaults() {
       String submitEndpoint = "https://default.com/collect";
       String eventIdKey = "df_key";
       String eventHeader = "x-flow-xpto";

        FlowsConfigurations flowsConfigurations = new FlowsConfigurations(Map.of());

        EventsCollectorFlowConfiguration config = new EventsCollectorFlowConfiguration(
                submitEndpoint, eventIdKey, eventHeader, flowsConfigurations
        );

        assertEquals(submitEndpoint, config.getSubmitEndpoint("xpto"));
        assertEquals(eventIdKey, config.getEventIdKey("foo"));
        assertEquals(eventHeader, config.getEventHeader("bar"));
    }

    @Test
    void givenConfigWithCustomFlow_whenCalledWithCustomFlowName_thenShouldReturnCustomValues() {
        String submitEndpoint = "https://default.com/collect";
        String eventIdKey = "df_key";
        String eventHeader = "x-flow-xpto";

        String flowName = "customized-flow";
        String flowSubmitEndpoint = "https://custom.flow.com/collect";
        String flowEventIdKey = "flow_key";
        String flowEventHeader = "x-flow-custom";

        FlowsConfigurations.FlowConfiguration flowConfig = new FlowsConfigurations.FlowConfiguration(flowSubmitEndpoint, flowEventIdKey, flowEventHeader);

        FlowsConfigurations flowsConfigurations = new FlowsConfigurations(Map.of(flowName, flowConfig));

        EventsCollectorFlowConfiguration config = new EventsCollectorFlowConfiguration(
                submitEndpoint, eventIdKey, eventHeader, flowsConfigurations
        );

        assertEquals(flowSubmitEndpoint, config.getSubmitEndpoint(flowName));
        assertEquals(flowEventIdKey, config.getEventIdKey(flowName));
        assertEquals(flowEventHeader, config.getEventHeader(flowName));
    }

    @Test
    void givenPartialCustomFlowConfig_whenCalledWithCustomFlowName_thenShouldReturnCustomValuesOnlyIfPresent() {
        String submitEndpoint = "https://default.com/collect";
        String eventIdKey = "df_key";
        String eventHeader = "x-flow-xpto";

        String flowName = "customized-flow";
        String flowEventHeader = "x-flow-custom";

        FlowsConfigurations.FlowConfiguration flowConfig = new FlowsConfigurations.FlowConfiguration(null, null, flowEventHeader);

        FlowsConfigurations flowsConfigurations = new FlowsConfigurations(Map.of(flowName, flowConfig));

        EventsCollectorFlowConfiguration config = new EventsCollectorFlowConfiguration(
                submitEndpoint, eventIdKey, eventHeader, flowsConfigurations
        );

        assertEquals(submitEndpoint, config.getSubmitEndpoint(flowName));
        assertEquals(eventIdKey, config.getEventIdKey(flowName));
        assertEquals(flowEventHeader, config.getEventHeader(flowName));
    }
}
