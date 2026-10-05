package com.documentcategorization.similarity;
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
