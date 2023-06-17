package mocks;

import com.rozsa.events.collector.annotations.CollectField;

import java.util.List;

public class CollectObjectMock {
    public static final String FINAL_FIELD_DEFAULT_KEY = "finalFieldKey";
    public static final String FIELD_CUSTOM_KEY = "field_custom_key";

    @CollectField
    private final Boolean finalFieldKey;

    @CollectField(key = FIELD_CUSTOM_KEY)
    public Double finalFieldCustomKey;

    private String randomField01;

    public Object randomField02;

    public List<Boolean> randomField03;

    public CollectObjectMock(Boolean finalFieldKey, Double finalFieldCustomKey) {
        this.finalFieldKey = finalFieldKey;
        this.finalFieldCustomKey = finalFieldCustomKey;
    }
}
