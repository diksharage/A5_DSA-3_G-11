package com.documentcategorization.model;
import java.util.List;
import java.util.Map;
public class StringSearchResult {
    private String algorithmName;
    private boolean found;
    private List<Integer> positions;
    private Map<String, List<Integer>> multiPositions;
    private int comparisons;
    private String preprocessingInfo;
    private String purpose;
    private String whyUsed;
    private String input;
    private String output;
    private String mainDataStructure;
    private String timeComplexity;
    private String spaceComplexity;
    // Getters and setters
    public String getAlgorithmName() { return algorithmName; } public void setAlgorithmName(String algorithmName) { this.algorithmName = algorithmName; }
    public boolean isFound() { return found; } public void setFound(boolean found) { this.found = found; }
    public List<Integer> getPositions() { return positions; } public void setPositions(List<Integer> positions) { this.positions = positions; }
    public Map<String, List<Integer>> getMultiPositions() { return multiPositions; } public void setMultiPositions(Map<String, List<Integer>> multiPositions) { this.multiPositions = multiPositions; }
    public int getComparisons() { return comparisons; } public void setComparisons(int comparisons) { this.comparisons = comparisons; }
    public String getPreprocessingInfo() { return preprocessingInfo; } public void setPreprocessingInfo(String preprocessingInfo) { this.preprocessingInfo = preprocessingInfo; }
    public String getPurpose() { return purpose; } public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getWhyUsed() { return whyUsed; } public void setWhyUsed(String whyUsed) { this.whyUsed = whyUsed; }
    public String getInput() { return input; } public void setInput(String input) { this.input = input; }
    public String getOutput() { return output; } public void setOutput(String output) { this.output = output; }
    public String getMainDataStructure() { return mainDataStructure; } public void setMainDataStructure(String mainDataStructure) { this.mainDataStructure = mainDataStructure; }
    public String getTimeComplexity() { return timeComplexity; } public void setTimeComplexity(String timeComplexity) { this.timeComplexity = timeComplexity; }
    public String getSpaceComplexity() { return spaceComplexity; } public void setSpaceComplexity(String spaceComplexity) { this.spaceComplexity = spaceComplexity; }
}
