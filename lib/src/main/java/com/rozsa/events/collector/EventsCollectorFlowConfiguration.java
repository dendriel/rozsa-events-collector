package com.rozsa.events.collector;

import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.function.Function;

/**
 * Wraps custom flows configurations from plus default flow configuration.
 * When retrieving a flow, if it doesn't have a custom configuration, will return default configuration.
 */
public class EventsCollectorFlowConfiguration {
    private final FlowsConfigurations flowsConfigurations;

    private final FlowsConfigurations.FlowConfiguration defaultFlow;

    public EventsCollectorFlowConfiguration(
            final String defaultSubmitEndpoint,
            final String defaultEventIdKey,
            final String eventHeader,
            final FlowsConfigurations flowsConfigurations
    ) {
        this.flowsConfigurations = Optional.ofNullable(flowsConfigurations)
                .orElse(FlowsConfigurations.create());
        defaultFlow = new FlowsConfigurations.FlowConfiguration(defaultSubmitEndpoint, defaultEventIdKey, eventHeader);
    }

    public String getEventIdKey(final String flowName) {
        return getOrDefault(flowName, FlowsConfigurations.FlowConfiguration::eventIdKey);
    }

    public String getSubmitEndpoint(final String flowName) {
        return getOrDefault(flowName, FlowsConfigurations.FlowConfiguration::submitEndpoint);
    }

    public String getEventHeader(final String flowName) {
        return getOrDefault(flowName, FlowsConfigurations.FlowConfiguration::eventHeader);
    }

    private String getOrDefault(final String flowName,
                               Function<FlowsConfigurations.FlowConfiguration, String> getter
    ) {
        FlowsConfigurations.FlowConfiguration flow = flowsConfigurations.getOrDefault(flowName, defaultFlow);
        if (!StringUtils.hasText(getter.apply(flow))) {
            return getter.apply(defaultFlow);
        }
        return getter.apply(flow);
    }
}
