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
       String eventKey = "df_key";
       String eventHeader = "x-flow-xpto";

        FlowsConfigurations flowsConfigurations = new FlowsConfigurations(Map.of());

        EventsCollectorFlowConfiguration config = new EventsCollectorFlowConfiguration(
                submitEndpoint, eventKey, eventHeader, flowsConfigurations
        );

        assertEquals(submitEndpoint, config.getSubmitEndpoint("xpto"));
        assertEquals(eventKey, config.getEventKey("foo"));
        assertEquals(eventHeader, config.getEventHeader("bar"));
    }

    @Test
    void givenConfigWithCustomFlow_whenCalledWithCustomFlowName_thenShouldReturnCustomValues() {
        String submitEndpoint = "https://default.com/collect";
        String eventKey = "df_key";
        String eventHeader = "x-flow-xpto";

        String flowName = "customized-flow";
        String flowSubmitEndpoint = "https://custom.flow.com/collect";
        String flowEventKey = "flow_key";
        String flowEventHeader = "x-flow-custom";

        FlowsConfigurations.FlowConfiguration flowConfig = new FlowsConfigurations.FlowConfiguration(flowSubmitEndpoint, flowEventKey, flowEventHeader);

        FlowsConfigurations flowsConfigurations = new FlowsConfigurations(Map.of(flowName, flowConfig));

        EventsCollectorFlowConfiguration config = new EventsCollectorFlowConfiguration(
                submitEndpoint, eventKey, eventHeader, flowsConfigurations
        );

        assertEquals(flowSubmitEndpoint, config.getSubmitEndpoint(flowName));
        assertEquals(flowEventKey, config.getEventKey(flowName));
        assertEquals(flowEventHeader, config.getEventHeader(flowName));
    }

    @Test
    void givenPartialCustomFlowConfig_whenCalledWithCustomFlowName_thenShouldReturnCustomValuesOnlyIfPresent() {
        String submitEndpoint = "https://default.com/collect";
        String eventKey = "df_key";
        String eventHeader = "x-flow-xpto";

        String flowName = "customized-flow";
        String flowEventHeader = "x-flow-custom";

        FlowsConfigurations.FlowConfiguration flowConfig = new FlowsConfigurations.FlowConfiguration(null, null, flowEventHeader);

        FlowsConfigurations flowsConfigurations = new FlowsConfigurations(Map.of(flowName, flowConfig));

        EventsCollectorFlowConfiguration config = new EventsCollectorFlowConfiguration(
                submitEndpoint, eventKey, eventHeader, flowsConfigurations
        );

        assertEquals(submitEndpoint, config.getSubmitEndpoint(flowName));
        assertEquals(eventKey, config.getEventKey(flowName));
        assertEquals(flowEventHeader, config.getEventHeader(flowName));
    }
}
