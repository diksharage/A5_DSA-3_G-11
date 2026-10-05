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
