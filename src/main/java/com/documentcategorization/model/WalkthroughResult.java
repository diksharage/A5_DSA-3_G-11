package com.documentcategorization.model;

import java.util.ArrayList;
import java.util.List;

public class WalkthroughResult {
    private String algorithmKey;
    private String algorithmName;
    private String category;
    private String inputSummary;
    private String finalResultSummary;
    private Object finalResultData;
    private String timeComplexity;
    private String spaceComplexity;
    private String complexityNote;
    private int totalSteps;
    private boolean truncated;
    private List<WalkthroughStep> steps = new ArrayList<>();

    public String getAlgorithmKey() {
        return algorithmKey;
    }

    public void setAlgorithmKey(String algorithmKey) {
        this.algorithmKey = algorithmKey;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInputSummary() {
        return inputSummary;
    }

    public void setInputSummary(String inputSummary) {
        this.inputSummary = inputSummary;
    }

    public String getFinalResultSummary() {
        return finalResultSummary;
    }

    public void setFinalResultSummary(String finalResultSummary) {
        this.finalResultSummary = finalResultSummary;
    }

    public Object getFinalResultData() {
        return finalResultData;
    }

    public void setFinalResultData(Object finalResultData) {
        this.finalResultData = finalResultData;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public void setTimeComplexity(String timeComplexity) {
        this.timeComplexity = timeComplexity;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public void setSpaceComplexity(String spaceComplexity) {
        this.spaceComplexity = spaceComplexity;
    }

    public String getComplexityNote() {
        return complexityNote;
    }

    public void setComplexityNote(String complexityNote) {
        this.complexityNote = complexityNote;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public List<WalkthroughStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WalkthroughStep> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
        this.totalSteps = this.steps.size();
    }
}
