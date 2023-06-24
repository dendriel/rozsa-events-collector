package mocks;

import com.rozsa.events.collector.annotations.CollectField;

public class CustomFlowCollectObjectMock extends CollectObjectMock {
    public static final String FIELD_OVERRIDE_CUSTOM_FLOW = "customFlowForFieldCapture";

    public static final String FIELD_OVERRIDE_NAME = "fieldWithCustomFlow";

    @CollectField(flow = FIELD_OVERRIDE_CUSTOM_FLOW)
    public Integer fieldWithCustomFlow;

    public CustomFlowCollectObjectMock(Boolean finalFieldKey, Double fieldCustomKey, Integer fieldWithCustomFlow) {
        super(finalFieldKey, fieldCustomKey);
        this.fieldWithCustomFlow = fieldWithCustomFlow;
    }
}
