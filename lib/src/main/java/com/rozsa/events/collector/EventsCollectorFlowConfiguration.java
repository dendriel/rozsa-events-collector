package com.rozsa.events.collector;

import java.util.Map;
import java.util.Optional;

/**
 * Wraps custom flows configurations from plus default flow configuration.
 * When retrieving a flow, if it doesn't have a custom configuration, will return default configuration.
 */
public class EventsCollectorFlowConfiguration {
    private final FlowsConfigurations flowsConfigurations;

    private final FlowsConfigurations.FlowConfiguration defaultFlow;

    public EventsCollectorFlowConfiguration(
            final String defaultSubmitEndpoint,
            final String defaultEventKey,
            final FlowsConfigurations flowsConfigurations
    ) {
        this.flowsConfigurations = Optional.ofNullable(flowsConfigurations)
                .orElse(FlowsConfigurations.create());
        defaultFlow = new FlowsConfigurations.FlowConfiguration(defaultSubmitEndpoint, defaultEventKey);
    }

    public FlowsConfigurations.FlowConfiguration getByName(final String flowName) {
        return flowsConfigurations.getOrDefault(flowName, defaultFlow);
    }
}
