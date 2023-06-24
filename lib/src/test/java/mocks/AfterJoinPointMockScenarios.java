package mocks;

import java.util.List;

public enum AfterJoinPointMockScenarios {
    SIMPLE_RETURN("stringReturnCollectReturnAnnotation", List.of()),
    RETURN_WITH_KEY("stringReturnWithKeyCollectReturnAnnotation", List.of()),
    RETURN_WITH_EXPLICIT_KEY("stringReturnWithExplicitKeyCollectReturnAnnotation", List.of()),
    RETURN_WITH_SCAN_FIELDS("returnWithScanFieldsTrueCollectReturnAnnotation", List.of()),
    RETURN_WITH_RECURSIVE_SCAN_FIELDS("returnWithRecursiveScanFieldsTrueCollectReturnAnnotation", List.of()),
    CUSTOM_FLOW_SIMPLE_RETURN("customFlowStringReturnCollectReturnAnnotation", List.of()),
    CUSTOM_FLOW_RETURN_WITH_SCAN_FIELDS("customFlowReturnWithScanFieldsTrueCollectReturnAnnotation", List.of()),
    CUSTOM_FLOW_RETURN_WITH_RECURSIVE_SCAN_FIELDS("customFlowReturnWithRecursiveScanFieldsTrueCollectReturnAnnotation", List.of()),
    FINISH_COLLECTING("finishCollectionAnnotation", List.of()),
    CUSTOM_FLOW_FINISH_COLLECTING("customFlowFinishCollectionAnnotation", List.of()),
    ;


    private final String method;
    private final List<Class> parameters;

    AfterJoinPointMockScenarios(String method, List<Class> parameters) {
        this.method = method;
        this.parameters = parameters;
    }

    public String getMethod() {
        return method;
    }

    public List<Class> getParameters() {
        return parameters;
    }
}
