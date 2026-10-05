import os

base_dir = r"C:\Users\Diksha\OneDrive\Desktop\Optimization Frame Work"
src_dir = os.path.join(base_dir, "src", "main", "java", "com", "documentcategorization")
res_dir = os.path.join(base_dir, "src", "main", "resources")

def make_dirs(path):
    if not os.path.exists(path):
        os.makedirs(path)

# Create directories
for d in ["model", "datastructures", "algorithms", "preprocessing", "features", "similarity", "graph", "categorization", "service", "controller"]:
    make_dirs(os.path.join(src_dir, d))
make_dirs(os.path.join(res_dir, "templates"))
make_dirs(os.path.join(res_dir, "static", "css"))
make_dirs(os.path.join(res_dir, "static", "js"))

files = {}

files["pom.xml"] = """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.2.4</version>
		<relativePath/>
	</parent>
	<groupId>com.optframework</groupId>
	<artifactId>optimization-framework</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>optimization-framework</name>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
		</dependency>
	</dependencies>
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
</project>
"""

files["src/main/java/com/documentcategorization/OptimizationFrameworkApplication.java"] = """package com.documentcategorization;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class OptimizationFrameworkApplication {
    public static void main(String[] args) {
        SpringApplication.run(OptimizationFrameworkApplication.class, args);
    }
}
"""

files["src/main/resources/application.properties"] = """spring.application.name=optimization-framework
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
"""

files["src/main/java/com/documentcategorization/model/Document.java"] = """package com.documentcategorization.model;
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
"""

files["src/main/java/com/documentcategorization/model/TermFrequency.java"] = """package com.documentcategorization.model;
public class TermFrequency implements Comparable<TermFrequency> {
    private String term;
    private int frequency;
    public TermFrequency(String term, int frequency) { this.term = term; this.frequency = frequency; }
    public String getTerm() { return term; }
    public int getFrequency() { return frequency; }
    @Override
    public int compareTo(TermFrequency other) {
        return Integer.compare(other.frequency, this.frequency); // Descending
    }
}
"""

files["src/main/java/com/documentcategorization/model/PipelineStage.java"] = """package com.documentcategorization.model;
public enum PipelineStage {
    UPLOADED, PREPROCESSED, FEATURES_EXTRACTED, FEATURES_OPTIMIZED, SIMILARITY_CALCULATED, GRAPH_BUILT, CATEGORIZED
}
"""

files["src/main/java/com/documentcategorization/model/StringSearchResult.java"] = """package com.documentcategorization.model;
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
"""

files["src/main/java/com/documentcategorization/model/CategoryResult.java"] = """package com.documentcategorization.model;
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
"""

files["src/main/java/com/documentcategorization/datastructures/TermFrequencyMap.java"] = """package com.documentcategorization.datastructures;
import com.documentcategorization.model.TermFrequency;
import java.util.ArrayList;
import java.util.List;
public class TermFrequencyMap {
    private static class HashNode {
        String key;
        int value;
        HashNode next;
        public HashNode(String key, int value) { this.key = key; this.value = value; }
    }
    private HashNode[] buckets;
    private int capacity;
    public TermFrequencyMap(int capacity) {
        this.capacity = capacity;
        this.buckets = new HashNode[capacity];
    }
    private int getIndex(String key) {
        return Math.abs(key.hashCode() % capacity);
    }
    public void increment(String key) {
        int index = getIndex(key);
        HashNode head = buckets[index];
        while (head != null) {
            if (head.key.equals(key)) { head.value++; return; }
            head = head.next;
        }
        HashNode newNode = new HashNode(key, 1);
        newNode.next = buckets[index];
        buckets[index] = newNode;
    }
    public List<TermFrequency> toList() {
        List<TermFrequency> list = new ArrayList<>();
        for (HashNode node : buckets) {
            while (node != null) {
                list.add(new TermFrequency(node.key, node.value));
                node = node.next;
            }
        }
        return list;
    }
}
"""

for k, v in files.items():
    path = os.path.join(base_dir, k)
    make_dirs(os.path.dirname(path))
    with open(path, "w", encoding="utf-8") as f:
        f.write(v)
print("Part 1 complete")
