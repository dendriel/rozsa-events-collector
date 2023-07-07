package com.rozsa.demoapp.service.model;

import com.rozsa.demoapp.configuration.collector.PetFilterFlowKeys;
import com.rozsa.demoapp.domain.PetType;
import com.rozsa.events.collector.annotations.CollectField;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PetFilter {

    private String name;

    @CollectField(flow = PetFilterFlowKeys.PET_FLOW, key = PetFilterFlowKeys.FILTERS_AGE)
    private Integer age;

    @CollectField(flow = PetFilterFlowKeys.PET_FLOW, key = PetFilterFlowKeys.FILTERS_COLOR)
    private String color;

    @CollectField(flow = PetFilterFlowKeys.PET_FLOW, key = PetFilterFlowKeys.FILTERS_TYPE)
    private PetType type;
}
