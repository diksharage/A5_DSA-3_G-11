package com.documentcategorization.features;
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
        
        // Custom Merge Sort to replace Collections.sort() for strict DSA compliance
        if (extracted != null && extracted.size() > 1) {
            mergeSort(extracted, 0, extracted.size() - 1);
        }
        
        doc.setOptimizedFeatures(extracted.subList(0, Math.min(extracted.size(), MAX_FEATURES)));
    }

    private void mergeSort(List<TermFrequency> list, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(list, left, mid);
            mergeSort(list, mid + 1, right);
            merge(list, left, mid, right);
        }
    }

    private void merge(List<TermFrequency> list, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        List<TermFrequency> leftList = new java.util.ArrayList<>(n1);
        List<TermFrequency> rightList = new java.util.ArrayList<>(n2);

        for (int i = 0; i < n1; ++i)
            leftList.add(list.get(left + i));
        for (int j = 0; j < n2; ++j)
            rightList.add(list.get(mid + 1 + j));

        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            // Sort descending: larger frequency comes first. If equal, sort lexicographically.
            int cmp = Integer.compare(rightList.get(j).getFrequency(), leftList.get(i).getFrequency());
            if (cmp == 0) {
                cmp = leftList.get(i).getTerm().compareTo(rightList.get(j).getTerm());
            }

            if (cmp <= 0) {
                list.set(k, leftList.get(i));
                i++;
            } else {
                list.set(k, rightList.get(j));
                j++;
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
    }
}
