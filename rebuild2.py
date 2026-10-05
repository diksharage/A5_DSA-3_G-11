import os

base_dir = r"C:\Users\Diksha\OneDrive\Desktop\Optimization Frame Work"
files = {}

files["src/main/java/com/documentcategorization/graph/DocumentGraph.java"] = """package com.documentcategorization.graph;
import com.documentcategorization.model.Document;
import java.util.*;
public class DocumentGraph {
    private Map<Document, List<Document>> adjacencyList = new HashMap<>();
    private int edgeCount = 0;
    
    public void addVertex(Document doc) {
        adjacencyList.putIfAbsent(doc, new ArrayList<>());
    }
    public void addEdge(Document doc1, Document doc2) {
        addVertex(doc1); addVertex(doc2);
        adjacencyList.get(doc1).add(doc2);
        adjacencyList.get(doc2).add(doc1);
        edgeCount++;
    }
    public int getEdgeCount() { return edgeCount; }
    public Map<Document, List<Document>> getAdjacencyList() { return adjacencyList; }
    
    public List<List<Document>> findConnectedComponents() {
        List<List<Document>> components = new ArrayList<>();
        Set<Document> visited = new HashSet<>();
        for (Document doc : adjacencyList.keySet()) {
            if (!visited.contains(doc)) {
                List<Document> component = new ArrayList<>();
                bfs(doc, visited, component);
                components.add(component);
            }
        }
        return components;
    }
    private void bfs(Document start, Set<Document> visited, List<Document> component) {
        Queue<Document> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            Document curr = queue.poll();
            component.add(curr);
            for (Document neighbor : adjacencyList.getOrDefault(curr, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }
}
"""

files["src/main/java/com/documentcategorization/service/DocumentService.java"] = """package com.documentcategorization.service;
import com.documentcategorization.model.Document;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class DocumentService {
    private final List<Document> documents = new ArrayList<>();
    public void addDocument(Document document) {
        boolean exists = documents.stream().anyMatch(d -> d.getOriginalFilename().equals(document.getOriginalFilename()));
        if (!exists) { documents.add(document); }
    }
    public List<Document> getAllDocuments() { return Collections.unmodifiableList(documents); }
    public boolean deleteDocument(String id) { return documents.removeIf(d -> d.getId().equals(id)); }
    public void clearAllDocuments() { documents.clear(); }
    public Optional<Document> getDocumentById(String id) {
        return documents.stream().filter(d -> d.getId().equals(id)).findFirst();
    }
}
"""

files["src/main/java/com/documentcategorization/preprocessing/PreprocessingService.java"] = """package com.documentcategorization.preprocessing;
import com.documentcategorization.model.Document;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class PreprocessingService {
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList("the", "and", "is", "in", "it", "of", "to", "a", "for", "on", "this"));
    public void preprocess(Document doc) {
        if (doc.getRawText() == null) return;
        String[] rawTokens = doc.getRawText().toLowerCase().replaceAll("[^a-z0-9\\\\s]", "").split("\\\\s+");
        List<String> cleanTokens = new ArrayList<>();
        for (String t : rawTokens) {
            if (!t.isEmpty() && !STOP_WORDS.contains(t)) cleanTokens.add(t);
        }
        doc.setTokens(cleanTokens);
    }
}
"""

files["src/main/java/com/documentcategorization/features/FeatureExtractionService.java"] = """package com.documentcategorization.features;
import com.documentcategorization.datastructures.TermFrequencyMap;
import com.documentcategorization.model.Document;
import org.springframework.stereotype.Service;
@Service
public class FeatureExtractionService {
    public void extractFeatures(Document doc) {
        TermFrequencyMap map = new TermFrequencyMap(100);
        for (String token : doc.getTokens()) { map.increment(token); }
        doc.setExtractedFeatures(map.toList());
    }
}
"""

files["src/main/java/com/documentcategorization/features/FeatureOptimizationService.java"] = """package com.documentcategorization.features;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.TermFrequency;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
@Service
public class FeatureOptimizationService {
    private static final int MAX_FEATURES = 15;
    public void optimizeFeatures(Document doc) {
        List<TermFrequency> extracted = doc.getExtractedFeatures();
        Collections.sort(extracted);
        doc.setOptimizedFeatures(extracted.subList(0, Math.min(extracted.size(), MAX_FEATURES)));
    }
}
"""

for k, v in files.items():
    path = os.path.join(base_dir, k)
    with open(path, "w", encoding="utf-8") as f:
        f.write(v)
print("Part 2 complete")
