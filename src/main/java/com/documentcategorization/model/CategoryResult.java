package com.documentcategorization.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CategoryResult {
    private String groupId;
    private int componentIndex;
    private String categoryLabel;
    private List<Document> documents = new ArrayList<>();
    private List<TermFrequency> dominantFeatures = new ArrayList<>();
    private List<TermFrequency> aggregatedFeatures = new ArrayList<>();
    private Map<String, List<String>> featureContributions = new LinkedHashMap<>();
    private int totalFeatureInstances;
    private boolean fallbackUsed;
    private String labelExplanation;
    private String reason;

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public int getComponentIndex() { return componentIndex; }
    public void setComponentIndex(int componentIndex) { this.componentIndex = componentIndex; }

    public String getCategoryLabel() { return categoryLabel; }
    public void setCategoryLabel(String categoryLabel) { this.categoryLabel = categoryLabel; }

    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }

    public List<TermFrequency> getDominantFeatures() { return dominantFeatures; }
    public void setDominantFeatures(List<TermFrequency> dominantFeatures) { this.dominantFeatures = dominantFeatures; }

    public List<TermFrequency> getAggregatedFeatures() { return aggregatedFeatures; }
    public void setAggregatedFeatures(List<TermFrequency> aggregatedFeatures) { this.aggregatedFeatures = aggregatedFeatures; }

    public Map<String, List<String>> getFeatureContributions() { return featureContributions; }
    public void setFeatureContributions(Map<String, List<String>> featureContributions) { this.featureContributions = featureContributions; }

    public int getTotalFeatureInstances() { return totalFeatureInstances; }
    public void setTotalFeatureInstances(int totalFeatureInstances) { this.totalFeatureInstances = totalFeatureInstances; }

    public boolean isFallbackUsed() { return fallbackUsed; }
    public void setFallbackUsed(boolean fallbackUsed) { this.fallbackUsed = fallbackUsed; }

    public String getLabelExplanation() { return labelExplanation; }
    public void setLabelExplanation(String labelExplanation) { this.labelExplanation = labelExplanation; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
