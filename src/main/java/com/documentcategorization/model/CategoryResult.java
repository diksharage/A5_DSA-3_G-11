package com.documentcategorization.model;
import java.util.List;
public class CategoryResult {
    private String groupId;
    private String categoryLabel;
    private List<Document> documents;
    private List<TermFrequency> dominantFeatures;
    private String reason;
    // Getters and setters
    public String getGroupId() { return groupId; } public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getCategoryLabel() { return categoryLabel; } public void setCategoryLabel(String categoryLabel) { this.categoryLabel = categoryLabel; }
    public List<Document> getDocuments() { return documents; } public void setDocuments(List<Document> documents) { this.documents = documents; }
    public List<TermFrequency> getDominantFeatures() { return dominantFeatures; } public void setDominantFeatures(List<TermFrequency> dominantFeatures) { this.dominantFeatures = dominantFeatures; }
    public String getReason() { return reason; } public void setReason(String reason) { this.reason = reason; }
}
