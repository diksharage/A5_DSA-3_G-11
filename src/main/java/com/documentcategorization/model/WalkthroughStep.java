package com.documentcategorization.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class WalkthroughStep {
    private int stepNumber;
    private String stage;
    private String operation;
    private String explanation;
    private Map<String, Object> state = new LinkedHashMap<>();

    public WalkthroughStep() {
    }

    public WalkthroughStep(int stepNumber, String stage, String operation, String explanation, Map<String, Object> state) {
        this.stepNumber = stepNumber;
        this.stage = stage;
        this.operation = operation;
        this.explanation = explanation;
        if (state != null) {
            this.state = state;
        }
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Map<String, Object> getState() {
        return state;
    }

    public void setState(Map<String, Object> state) {
        this.state = state;
    }
}
