package com.documentcategorization.features;

import com.documentcategorization.datastructures.TermFrequencyMap;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.TermFrequency;
import com.documentcategorization.model.WalkthroughStep;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeatureOptimizationService {
    private static final int MAX_FEATURES = 15;

    public void optimizeFeatures(Document doc) {
        List<TermFrequency> extracted = doc.getExtractedFeatures();

        // Custom Merge Sort to replace Collections.sort() for strict DSA compliance
        sortFeatureList(extracted);

        if (extracted != null) {
            doc.setOptimizedFeatures(new ArrayList<>(extracted.subList(0, Math.min(extracted.size(), MAX_FEATURES))));
        }
    }

    public void sortFeatureList(List<TermFrequency> list) {
        if (list != null && list.size() > 1) {
            mergeSort(list, 0, list.size() - 1, null, 0);
        }
    }

    public List<TermFrequency> traceMergeSort(Document doc, List<WalkthroughStep> steps) {
        List<TermFrequency> workingList = buildInitialFeatureListForWalkthrough(doc);

        Map<String, Object> initState = new LinkedHashMap<>();
        initState.put("Document", doc.getOriginalFilename());
        initState.put("Feature Count", workingList.size());
        initState.put("Initial Feature List", formatFeatureList(workingList));
        initState.put("Sorting Rule", "Frequency descending; tie-breaker: Term lexicographical (A-Z)");
        addStep(steps, "1. Initial Feature List", "Initialize Unsorted Feature List",
                "Loaded " + workingList.size() + " extracted feature(s) in their pre-sort hash-bucket order before executing Custom Merge Sort.",
                initState);

        if (workingList.size() > 1) {
            mergeSort(workingList, 0, workingList.size() - 1, steps, 0);
        }

        List<TermFrequency> finalOptimized = new ArrayList<>(workingList.subList(0, Math.min(workingList.size(), MAX_FEATURES)));
        Map<String, Object> finalState = new LinkedHashMap<>();
        finalState.put("Final Sorted Feature List", formatFeatureList(finalOptimized));
        finalState.put("Total Features Retained", finalOptimized.size());
        addStep(steps, "6. Final Sorted Feature List", "Merge Sort Complete",
                "Custom Merge Sort finished sorting the feature list in descending order of frequency (with lexicographical tie-breaking).",
                finalState);

        return finalOptimized;
    }

    private List<TermFrequency> buildInitialFeatureListForWalkthrough(Document doc) {
        List<TermFrequency> optimized = doc.getOptimizedFeatures();
        if (doc.getTokens() != null && !doc.getTokens().isEmpty()) {
            TermFrequencyMap map = new TermFrequencyMap(100);
            for (String token : doc.getTokens()) {
                map.increment(token);
            }
            List<TermFrequency> rawBucketOrder = map.toList();
            if (rawBucketOrder.size() <= MAX_FEATURES || optimized == null || optimized.isEmpty()) {
                return rawBucketOrder;
            }
            Set<String> optTerms = optimized.stream().map(TermFrequency::getTerm).collect(Collectors.toSet());
            List<TermFrequency> filteredInBucketOrder = new ArrayList<>();
            for (TermFrequency tf : rawBucketOrder) {
                if (optTerms.contains(tf.getTerm())) {
                    filteredInBucketOrder.add(new TermFrequency(tf.getTerm(), tf.getFrequency()));
                }
            }
            if (!filteredInBucketOrder.isEmpty()) {
                return filteredInBucketOrder;
            }
        }
        if (optimized != null && !optimized.isEmpty()) {
            List<TermFrequency> copy = new ArrayList<>();
            for (TermFrequency tf : optimized) {
                copy.add(new TermFrequency(tf.getTerm(), tf.getFrequency()));
            }
            return copy;
        }
        return new ArrayList<>();
    }

    private void mergeSort(List<TermFrequency> list, int left, int right, List<WalkthroughStep> steps, int depth) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            if (steps != null) {
                String stage = (depth == 0) ? "2. Division into Smaller Subarrays" : "3. Recursive Subdivision";
                Map<String, Object> divState = new LinkedHashMap<>();
                divState.put("Recursion Depth", depth);
                divState.put("Subarray Range", "[" + left + " .. " + right + "] (mid = " + mid + ")");
                divState.put("Current Subarray", formatFeatureList(list.subList(left, right + 1)));
                divState.put("Left Half [" + left + " .. " + mid + "]", formatFeatureList(list.subList(left, mid + 1)));
                divState.put("Right Half [" + (mid + 1) + " .. " + right + "]", formatFeatureList(list.subList(mid + 1, right + 1)));
                addStep(steps, stage,
                        "Divide Range [" + left + " .. " + right + "] at mid = " + mid,
                        "Split subarray of size " + (right - left + 1) + " into left subarray [" + left + ".." + mid + "] and right subarray [" + (mid + 1) + ".." + right + "].",
                        divState);
            }
            mergeSort(list, left, mid, steps, depth + 1);
            mergeSort(list, mid + 1, right, steps, depth + 1);
            merge(list, left, mid, right, steps, depth);
        }
    }

    private void merge(List<TermFrequency> list, int left, int mid, int right, List<WalkthroughStep> steps, int depth) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        List<TermFrequency> leftList = new ArrayList<>(n1);
        List<TermFrequency> rightList = new ArrayList<>(n2);

        for (int i = 0; i < n1; ++i)
            leftList.add(list.get(left + i));
        for (int j = 0; j < n2; ++j)
            rightList.add(list.get(mid + 1 + j));

        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            TermFrequency leftElem = leftList.get(i);
            TermFrequency rightElem = rightList.get(j);

            // Sort descending: larger frequency comes first. If equal, sort lexicographically.
            int freqCmp = Integer.compare(rightElem.getFrequency(), leftElem.getFrequency());
            int cmp = freqCmp;
            boolean tieBrokenByTerm = false;
            if (cmp == 0) {
                cmp = leftElem.getTerm().compareTo(rightElem.getTerm());
                tieBrokenByTerm = true;
            }

            TermFrequency chosen;
            String reason;
            if (cmp <= 0) {
                chosen = leftElem;
                list.set(k, leftElem);
                reason = tieBrokenByTerm
                        ? "Equal frequency (" + leftElem.getFrequency() + "); '" + leftElem.getTerm() + "' comes before '" + rightElem.getTerm() + "' lexicographically -> select Left"
                        : "Left frequency (" + leftElem.getFrequency() + ") > Right frequency (" + rightElem.getFrequency() + ") -> select Left";
                i++;
            } else {
                chosen = rightElem;
                list.set(k, rightElem);
                reason = tieBrokenByTerm
                        ? "Equal frequency (" + rightElem.getFrequency() + "); '" + rightElem.getTerm() + "' comes before '" + leftElem.getTerm() + "' lexicographically -> select Right"
                        : "Right frequency (" + rightElem.getFrequency() + ") > Left frequency (" + leftElem.getFrequency() + ") -> select Right";
                j++;
            }

            if (steps != null) {
                Map<String, Object> cmpState = new LinkedHashMap<>();
                cmpState.put("Merge Range", "[" + left + " .. " + right + "]");
                cmpState.put("Left Element", formatFeature(leftElem));
                cmpState.put("Right Element", formatFeature(rightElem));
                cmpState.put("Decision", reason);
                cmpState.put("Placed at Index " + k, formatFeature(chosen));
                addStep(steps, "4. Comparison of Elements",
                        "Compare " + formatFeature(leftElem) + " vs " + formatFeature(rightElem),
                        reason + ". Placed " + formatFeature(chosen) + " at position " + k + ".",
                        cmpState);
            }
            k++;
        }

        while (i < n1) {
            list.set(k, leftList.get(i));
            i++;
            k++;
        }

        while (j < n2) {
            list.set(k, rightList.get(j));
            j++;
            k++;
        }

        if (steps != null) {
            Map<String, Object> mergeState = new LinkedHashMap<>();
            mergeState.put("Merged Range", "[" + left + " .. " + right + "]");
            mergeState.put("Left Subarray Input", formatFeatureList(leftList));
            mergeState.put("Right Subarray Input", formatFeatureList(rightList));
            mergeState.put("Merged Sorted Subarray", formatFeatureList(list.subList(left, right + 1)));
            mergeState.put("Full Array State", formatFeatureList(list));
            addStep(steps, "5. Merging Sorted Subarrays",
                    "Merged Subarrays into [" + left + " .. " + right + "]",
                    "Combined sorted halves [" + left + ".." + mid + "] and [" + (mid + 1) + ".." + right + "] into a single sorted segment of size " + (right - left + 1) + ".",
                    mergeState);
        }
    }

    public static String formatFeature(TermFrequency tf) {
        return tf.getTerm() + " (" + tf.getFrequency() + ")";
    }

    public static List<String> formatFeatureList(List<TermFrequency> list) {
        List<String> formatted = new ArrayList<>(list.size());
        for (TermFrequency tf : list) {
            formatted.add(formatFeature(tf));
        }
        return formatted;
    }

    private void addStep(List<WalkthroughStep> steps, String stage, String operation, String explanation, Map<String, Object> state) {
        steps.add(new WalkthroughStep(steps.size() + 1, stage, operation, explanation, state));
    }
}
