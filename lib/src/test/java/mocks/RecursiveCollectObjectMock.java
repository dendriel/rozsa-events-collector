package mocks;

import com.rozsa.events.collector.annotations.CollectField;

import java.util.Map;

public class RecursiveCollectObjectMock {
    public static final String COMPANION_FIELD_KEY = "custom_key_companionFieldToCapture";

    private Float randomField01;

    public Object randomField02;

    private Map<String, Object> randomField03;

    @CollectField(COMPANION_FIELD_KEY)
    private int companionFieldToCapture;

    @CollectField(scanFields = true)
    CollectObjectMock collectTarget;

    public RecursiveCollectObjectMock(CollectObjectMock collectTarget, int companionFieldToCapture) {
        this.collectTarget = collectTarget;
        this.companionFieldToCapture = companionFieldToCapture;
    }
}
