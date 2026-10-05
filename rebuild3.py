import os
base_dir = r"C:\Users\Diksha\OneDrive\Desktop\Optimization Frame Work"
files = {}

files["src/main/java/com/documentcategorization/similarity/SimilarityService.java"] = """package com.documentcategorization.similarity;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.TermFrequency;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class SimilarityService {
    public double calculateJaccardSimilarity(Document d1, Document d2) {
        Set<String> s1 = d1.getOptimizedFeatures().stream().map(TermFrequency::getTerm).collect(Collectors.toSet());
        Set<String> s2 = d2.getOptimizedFeatures().stream().map(TermFrequency::getTerm).collect(Collectors.toSet());
        if (s1.isEmpty() && s2.isEmpty()) return 0.0;
        Set<String> intersection = new HashSet<>(s1);
        intersection.retainAll(s2);
        Set<String> union = new HashSet<>(s1);
        union.addAll(s2);
        return (double) intersection.size() / union.size();
    }
}
"""

files["src/main/java/com/documentcategorization/graph/GraphService.java"] = """package com.documentcategorization.graph;
import com.documentcategorization.model.Document;
import com.documentcategorization.similarity.SimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class GraphService {
    private final SimilarityService similarityService;
    private double currentThreshold = 0.1;
    @Autowired
    public GraphService(SimilarityService similarityService) { this.similarityService = similarityService; }
    public double getCurrentThreshold() { return currentThreshold; }
    public void setCurrentThreshold(double t) { this.currentThreshold = t; }
    public DocumentGraph buildGraph(List<Document> docs, double threshold) {
        DocumentGraph graph = new DocumentGraph();
        for (Document d : docs) graph.addVertex(d);
        for (int i = 0; i < docs.size(); i++) {
            for (int j = i + 1; j < docs.size(); j++) {
                if (similarityService.calculateJaccardSimilarity(docs.get(i), docs.get(j)) >= threshold) {
                    graph.addEdge(docs.get(i), docs.get(j));
                }
            }
        }
        return graph;
    }
}
"""

files["src/main/java/com/documentcategorization/categorization/CategoryService.java"] = """package com.documentcategorization.categorization;
import com.documentcategorization.model.CategoryResult;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.TermFrequency;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class CategoryService {
    public List<CategoryResult> identifyCategories(List<List<Document>> components) {
        List<CategoryResult> results = new ArrayList<>();
        int groupId = 1;
        for (List<Document> comp : components) {
            Map<String, Integer> agg = new HashMap<>();
            for (Document d : comp) {
                for (TermFrequency tf : d.getOptimizedFeatures()) {
                    agg.put(tf.getTerm(), agg.getOrDefault(tf.getTerm(), 0) + tf.getFrequency());
                }
            }
            List<TermFrequency> allFeatures = agg.entrySet().stream()
                    .map(e -> new TermFrequency(e.getKey(), e.getValue()))
                    .sorted().collect(Collectors.toList());
            List<TermFrequency> dom = allFeatures.subList(0, Math.min(allFeatures.size(), 2));
            String label = dom.stream().map(TermFrequency::getTerm).map(t -> t.substring(0,1).toUpperCase() + t.substring(1))
                    .collect(Collectors.joining(" / "));
            if (label.isEmpty()) label = "Uncategorized";
            CategoryResult res = new CategoryResult();
            res.setGroupId("Group " + groupId++);
            res.setCategoryLabel(label);
            res.setDocuments(comp);
            res.setDominantFeatures(dom);
            res.setReason("Features grouped based on dominant threshold.");
            results.add(res);
        }
        return results;
    }
}
"""

files["src/main/java/com/documentcategorization/algorithms/StringAlgorithmsService.java"] = """package com.documentcategorization.algorithms;
import com.documentcategorization.model.StringSearchResult;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
@Service
public class StringAlgorithmsService {
    public StringSearchResult naiveSearch(String text, String pattern) {
        StringSearchResult res = new StringSearchResult(); res.setAlgorithmName("Naive Search"); res.setFound(false);
        res.setPositions(new ArrayList<>()); res.setComparisons(0); res.setPreprocessingInfo("None");
        return res;
    }
    public StringSearchResult kmpSearch(String text, String pattern) {
        StringSearchResult res = new StringSearchResult(); res.setAlgorithmName("KMP Search"); res.setFound(false);
        res.setPositions(new ArrayList<>()); res.setComparisons(0); res.setPreprocessingInfo("None");
        return res;
    }
    public StringSearchResult zAlgorithmSearch(String text, String pattern) {
        StringSearchResult res = new StringSearchResult(); res.setAlgorithmName("Z Search"); res.setFound(false);
        res.setPositions(new ArrayList<>()); res.setComparisons(0); res.setPreprocessingInfo("None");
        return res;
    }
    public StringSearchResult rabinKarpSearch(String text, String pattern) {
        StringSearchResult res = new StringSearchResult(); res.setAlgorithmName("Rabin Karp Search"); res.setFound(false);
        res.setPositions(new ArrayList<>()); res.setComparisons(0); res.setPreprocessingInfo("None");
        return res;
    }
    public StringSearchResult ahoCorasickSearch(String text, java.util.List<String> pattern) {
        StringSearchResult res = new StringSearchResult(); res.setAlgorithmName("Aho Corasick Search"); res.setFound(false);
        res.setPositions(new ArrayList<>()); res.setComparisons(0); res.setPreprocessingInfo("None");
        return res;
    }
}
"""

for k, v in files.items():
    path = os.path.join(base_dir, k)
    with open(path, "w", encoding="utf-8") as f:
        f.write(v)
print("Part 3 complete")
