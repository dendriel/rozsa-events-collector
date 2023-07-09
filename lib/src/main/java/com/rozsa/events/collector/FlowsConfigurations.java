package com.rozsa.events.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Maps custom flows configurations.
 */
@ConfigurationProperties(prefix = "rozsa.events-collector")
public class FlowsConfigurations {
    private final Map<String, FlowConfiguration> flows;

    public static FlowsConfigurations create() {
        return new FlowsConfigurations(Map.of());
    }

    public FlowsConfigurations(final Map<String, FlowConfiguration> flows) {
        this.flows = flows;
    }

    public FlowConfiguration getOrDefault(final String name, final FlowConfiguration defaultConfig) {
        return flows.getOrDefault(name, defaultConfig);
    }

    public record FlowConfiguration(String submitEndpoint, String eventIdKey, String eventFlowHeader) {}
}
