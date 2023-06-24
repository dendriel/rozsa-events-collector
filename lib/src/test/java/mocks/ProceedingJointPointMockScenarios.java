package mocks;


public enum ProceedingJointPointMockScenarios {
    SUBMIT_ON_ERROR_TRUE("annotatedBeginCollectingSubmitOnErrorTrue"),
    SUBMIT_ON_ERROR_FALSE("annotatedBeginCollectingSubmitOnErrorFalse"),
    CUSTOM_FLOW("annotatedBeginCollectingCustomFlow"),
    CUSTOM_FLOW_SUBMIT_ON_ERROR_DISABLED("annotatedBeginCollectingCustomFlowSubmitOnErrorFalse")
    ;

    private final String method;

    ProceedingJointPointMockScenarios(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
