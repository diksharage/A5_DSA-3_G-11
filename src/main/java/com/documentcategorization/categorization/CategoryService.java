package com.documentcategorization.categorization;

import com.documentcategorization.features.FeatureOptimizationService;
import com.documentcategorization.model.CategoryResult;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.TermFrequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final FeatureOptimizationService featureOptimizationService;

    @Autowired
    public CategoryService(FeatureOptimizationService featureOptimizationService) {
        this.featureOptimizationService = featureOptimizationService;
    }

    public List<CategoryResult> identifyCategories(List<List<Document>> components) {
        List<CategoryResult> results = new ArrayList<>();
        int groupId = 1;

        for (List<Document> comp : components) {
            int currentIdx = groupId++;
            Map<String, Integer> agg = new LinkedHashMap<>();
            Map<String, List<String>> contributionsByTerm = new LinkedHashMap<>();
            int totalPooledInstances = 0;

            for (Document d : comp) {
                if (d.getOptimizedFeatures() == null) continue;
                for (TermFrequency tf : d.getOptimizedFeatures()) {
                    totalPooledInstances++;
                    agg.put(tf.getTerm(), agg.getOrDefault(tf.getTerm(), 0) + tf.getFrequency());
                    contributionsByTerm
                            .computeIfAbsent(tf.getTerm(), k -> new ArrayList<>())
                            .add(d.getOriginalFilename() + " (" + tf.getFrequency() + ")");
                }
            }

            List<TermFrequency> allFeatures = new ArrayList<>();
            for (Map.Entry<String, Integer> e : agg.entrySet()) {
                allFeatures.add(new TermFrequency(e.getKey(), e.getValue()));
            }
            featureOptimizationService.sortFeatureList(allFeatures);

            List<TermFrequency> dom = new ArrayList<>(allFeatures.subList(0, Math.min(allFeatures.size(), 3)));

            Map<String, List<String>> orderedContributions = new LinkedHashMap<>();
            for (TermFrequency tf : allFeatures) {
                orderedContributions.put(tf.getTerm(), contributionsByTerm.getOrDefault(tf.getTerm(), Collections.emptyList()));
            }

            String label;
            String reasonStr;
            String labelExpl;
            boolean isFallback = dom.isEmpty();

            if (isFallback) {
                label = "Undetermined";
                reasonStr = "Category could not be determined because no optimized features were extracted from the document(s), meaning no similarity connections could be established.";
                labelExpl = "Fallback label 'Undetermined' applied: 0 optimized features were available across the "
                        + comp.size() + " member document(s) in Connected Component #" + currentIdx
                        + ", so no dominant terms could be ranked or selected.";
            } else {
                label = dom.stream()
                        .map(TermFrequency::getTerm)
                        .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                        .collect(Collectors.joining(" / "));

                String domSummary = dom.stream()
                        .map(tf -> tf.getTerm() + " (total freq " + tf.getFrequency() + ")")
                        .collect(Collectors.joining(", "));

                if (dom.size() < 3) {
                    labelExpl = "Connected Component #" + currentIdx + " (" + comp.size() + " document" + (comp.size() == 1 ? "" : "s")
                            + ") yielded only " + allFeatures.size() + " unique optimized feature(s) (" + domSummary
                            + "). Because fewer than 3 unique features exist, all " + dom.size()
                            + " available ranked feature(s) were title-cased and joined to form '" + label + "'.";
                } else {
                    labelExpl = "Aggregated " + totalPooledInstances + " optimized feature entries across " + comp.size()
                            + " member document(s) into " + allFeatures.size()
                            + " unique ranked terms. Selected the Top 3 dominant features by aggregated frequency — "
                            + domSummary + " — and formatted them into the dynamic category label '" + label + "'.";
                }

                if (comp.size() == 1) {
                    reasonStr = "This document (" + comp.get(0).getOriginalFilename()
                            + ") forms an isolated connected component (Component #" + currentIdx
                            + "). Its Top 15 optimized features did not share sufficient Jaccard similarity (no graph edges >= threshold) with any other document. The category label '"
                            + label + "' was derived directly from its own top ranked features (" + domSummary + ").";
                } else {
                    String docNames = comp.stream().map(Document::getOriginalFilename).collect(Collectors.joining(", "));
                    reasonStr = "These " + comp.size() + " documents (" + docNames
                            + ") were grouped into Connected Component #" + currentIdx
                            + " via BFS graph traversal because their optimized features share sufficient Jaccard similarity to form a connected subgraph. Their member features were summed and ranked, producing the dominant features ("
                            + domSummary + ") and generating the category label '" + label + "'.";
                }
            }

            CategoryResult res = new CategoryResult();
            res.setComponentIndex(currentIdx);
            res.setGroupId("Group " + currentIdx);
            res.setCategoryLabel(label);
            res.setDocuments(comp);
            res.setDominantFeatures(dom);
            res.setAggregatedFeatures(allFeatures);
            res.setFeatureContributions(orderedContributions);
            res.setTotalFeatureInstances(totalPooledInstances);
            res.setFallbackUsed(isFallback);
            res.setLabelExplanation(labelExpl);
            res.setReason(reasonStr);
            results.add(res);
        }
        return results;
    }
}
