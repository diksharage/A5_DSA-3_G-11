package com.documentcategorization.model;
import java.util.ArrayList;
import java.util.List;
public class Document {
    private String id;
    private String originalFilename;
    private String fileType;
    private long fileSize;
    private String rawText;
    private List<String> tokens = new ArrayList<>();
    private List<TermFrequency> extractedFeatures = new ArrayList<>();
    private List<TermFrequency> optimizedFeatures = new ArrayList<>();
    private PipelineStage currentStage = PipelineStage.UPLOADED;
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }
    public List<String> getTokens() { return tokens; }
    public void setTokens(List<String> tokens) { this.tokens = tokens; }
    public List<TermFrequency> getExtractedFeatures() { return extractedFeatures; }
    public void setExtractedFeatures(List<TermFrequency> extractedFeatures) { this.extractedFeatures = extractedFeatures; }
    public List<TermFrequency> getOptimizedFeatures() { return optimizedFeatures; }
    public void setOptimizedFeatures(List<TermFrequency> optimizedFeatures) { this.optimizedFeatures = optimizedFeatures; }
    public PipelineStage getCurrentStage() { return currentStage; }
    public void setCurrentStage(PipelineStage currentStage) { this.currentStage = currentStage; }
}
