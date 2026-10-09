package com.documentcategorization.categorization;
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
            List<TermFrequency> dom = allFeatures.subList(0, Math.min(allFeatures.size(), 3));
            
            String label;
            String reasonStr;
            
            if (dom.isEmpty()) {
                label = "Undetermined";
                reasonStr = "Category could not be determined because no optimized features were extracted from the document(s), meaning no similarity connections could be established.";
            } else {
                label = dom.stream().map(TermFrequency::getTerm).map(t -> t.substring(0,1).toUpperCase() + t.substring(1))
                    .collect(Collectors.joining(" / "));
                
                if (comp.size() == 1) {
                    reasonStr = "This document forms an isolated connected component. Its optimized features did not share sufficient Jaccard similarity (no graph edges) with other documents under the current threshold. The label '" + label + "' was derived from its own dominant features.";
                } else {
                    reasonStr = "These " + comp.size() + " documents were grouped into a single connected component via BFS traversal because their optimized features share sufficient similarity, forming continuous graph edges between them. The label '" + label + "' was aggregated from their shared dominant features.";
                }
            }

            CategoryResult res = new CategoryResult();
            res.setGroupId("Group " + groupId++);
            res.setCategoryLabel(label);
            res.setDocuments(comp);
            res.setDominantFeatures(dom);
            res.setReason(reasonStr);
            results.add(res);
        }
        return results;
    }
}
