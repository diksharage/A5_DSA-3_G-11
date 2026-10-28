package com.documentcategorization.similarity;

import com.documentcategorization.model.Document;
import com.documentcategorization.model.TermFrequency;
import com.documentcategorization.model.WalkthroughStep;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SimilarityService {

    public double calculateJaccardSimilarity(Document d1, Document d2) {
        return calculateJaccardSimilarity(d1, d2, null);
    }

    public double calculateJaccardSimilarity(Document d1, Document d2, List<WalkthroughStep> steps) {
        if (d1 == null || d2 == null || d1.getOptimizedFeatures() == null || d2.getOptimizedFeatures() == null) {
            if (steps != null) {
                Map<String, Object> errState = new LinkedHashMap<>();
                errState.put("Document A", d1 != null ? d1.getOriginalFilename() : "None");
                errState.put("Document B", d2 != null ? d2.getOriginalFilename() : "None");
                errState.put("Similarity", 0.0);
                addStep(steps, "1. Feature Check", "Missing Optimized Features",
                        "One or both documents do not have optimized features available; returning 0.0.", errState);
            }
            return 0.0;
        }

        Set<String> s1 = d1.getOptimizedFeatures().stream().map(TermFrequency::getTerm).collect(Collectors.toSet());
        Set<String> s2 = d2.getOptimizedFeatures().stream().map(TermFrequency::getTerm).collect(Collectors.toSet());

        if (steps != null) {
            List<String> sortedA = new ArrayList<>(s1);
            Collections.sort(sortedA);
            Map<String, Object> step1 = new LinkedHashMap<>();
            step1.put("Document A", d1.getOriginalFilename());
            step1.put("Set A (Optimized Features)", sortedA);
            step1.put("|A| (Set A Size)", s1.size());
            addStep(steps, "1. Document A Optimized Feature Set",
                    "Extract Feature Set A from " + d1.getOriginalFilename(),
                    "Collected " + s1.size() + " unique optimized feature term(s) from Document A (" + d1.getOriginalFilename() + ").",
                    step1);

            List<String> sortedB = new ArrayList<>(s2);
            Collections.sort(sortedB);
            Map<String, Object> step2 = new LinkedHashMap<>();
            step2.put("Document B", d2.getOriginalFilename());
            step2.put("Set B (Optimized Features)", sortedB);
            step2.put("|B| (Set B Size)", s2.size());
            addStep(steps, "2. Document B Optimized Feature Set",
                    "Extract Feature Set B from " + d2.getOriginalFilename(),
                    "Collected " + s2.size() + " unique optimized feature term(s) from Document B (" + d2.getOriginalFilename() + ").",
                    step2);
        }

        if (s1.isEmpty() && s2.isEmpty()) {
            if (steps != null) {
                Map<String, Object> emptyState = new LinkedHashMap<>();
                emptyState.put("Intersection (A ∩ B)", Collections.emptyList());
                emptyState.put("Union (A ∪ B)", Collections.emptyList());
                emptyState.put("|A ∩ B|", 0);
                emptyState.put("|A ∪ B|", 0);
                emptyState.put("Formula Handling", "Both Set A and Set B are empty (|A ∪ B| = 0); existing implementation returns 0.0 to avoid division by zero.");
                emptyState.put("Final Jaccard Similarity J(A,B)", 0.0);
                addStep(steps, "3. Empty Union Check", "Handle Empty Feature Sets",
                        "Both feature sets are empty (|A ∪ B| = 0). Returning 0.0 per the existing implementation.",
                        emptyState);
            }
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(s1);
        intersection.retainAll(s2);
        Set<String> union = new HashSet<>(s1);
        union.addAll(s2);

        double similarity = (double) intersection.size() / union.size();

        if (steps != null) {
            List<String> sortedInter = new ArrayList<>(intersection);
            Collections.sort(sortedInter);
            List<String> sortedUnion = new ArrayList<>(union);
            Collections.sort(sortedUnion);

            Map<String, Object> step3 = new LinkedHashMap<>();
            step3.put("Operation", "A ∩ B (intersection.retainAll(B))");
            step3.put("Intersection Set (A ∩ B)", sortedInter);
            step3.put("Shared Terms Count", intersection.size());
            addStep(steps, "3. Set Intersection (A ∩ B)",
                    "Compute Intersection A ∩ B",
                    "Identified " + intersection.size() + " shared feature term(s) present in both Document A and Document B.",
                    step3);

            Map<String, Object> step4 = new LinkedHashMap<>();
            step4.put("Operation", "A ∪ B (union.addAll(B))");
            step4.put("Union Set (A ∪ B)", sortedUnion);
            step4.put("Total Unique Terms", union.size());
            addStep(steps, "4. Set Union (A ∪ B)",
                    "Compute Union A ∪ B",
                    "Combined all unique feature terms from both documents into union set A ∪ B of size " + union.size() + ".",
                    step4);

            Map<String, Object> step5 = new LinkedHashMap<>();
            step5.put("|A| (Document A Features)", s1.size());
            step5.put("|B| (Document B Features)", s2.size());
            step5.put("|A ∩ B| (Intersection Size)", intersection.size());
            step5.put("|A ∪ B| (Union Size)", union.size());
            addStep(steps, "5. Intersection & Union Sizes",
                    "Determine Cardinalities |A ∩ B| and |A ∪ B|",
                    "Intersection size |A ∩ B| = " + intersection.size() + " and Union size |A ∪ B| = " + union.size() + ".",
                    step5);

            String formulaSub = "J(A,B) = |A ∩ B| / |A ∪ B| = " + intersection.size() + " / " + union.size();
            Map<String, Object> step6 = new LinkedHashMap<>();
            step6.put("Formula", "J(A,B) = |A ∩ B| / |A ∪ B|");
            step6.put("Substitution", formulaSub);
            step6.put("Numerator |A ∩ B|", intersection.size());
            step6.put("Denominator |A ∪ B|", union.size());
            addStep(steps, "6. Formula Substitution",
                    "Substitute Cardinalities into J(A,B)",
                    "Substituted |A ∩ B| = " + intersection.size() + " and |A ∪ B| = " + union.size() + " into J(A,B) = |A ∩ B| / |A ∪ B|.",
                    step6);

            Map<String, Object> step7 = new LinkedHashMap<>();
            step7.put("Document A", d1.getOriginalFilename());
            step7.put("Document B", d2.getOriginalFilename());
            step7.put("Formula Evaluation", intersection.size() + " / " + union.size());
            step7.put("Exact Similarity Value", similarity);
            step7.put("Formatted Value (Matrix Display)", String.format(Locale.US, "%.2f", similarity));
            addStep(steps, "7. Final Similarity Value",
                    "Final Jaccard Similarity = " + String.format(Locale.US, "%.4f", similarity) + " (" + String.format(Locale.US, "%.2f", similarity) + ")",
                    "Computed final Jaccard similarity J(A,B) = " + similarity + " (displayed as " + String.format(Locale.US, "%.2f", similarity) + " on the Similarity Matrix page).",
                    step7);
        }

        return similarity;
    }

    private void addStep(List<WalkthroughStep> steps, String stage, String operation, String explanation, Map<String, Object> state) {
        steps.add(new WalkthroughStep(steps.size() + 1, stage, operation, explanation, state));
    }
}
