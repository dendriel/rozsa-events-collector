package com.rozsa.demoapp.configuration.collector;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PetFilterFlowKeys {
    public static final String FLOW_NAME = "pet_filtering";

    public static final String PET_RESPONSE_COLLECTOR = "pet_response_collector";

    public static final String RESPONSE_NAME = "response_name";
    public static final String FILTERS_AGE = "filters_age";
    public static final String FILTERS_COLOR = "filters_color";
    public static final String FILTERS_TYPE = "filters_type";
}
