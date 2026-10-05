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
        Collections.sort(extracted);
        doc.setOptimizedFeatures(extracted.subList(0, Math.min(extracted.size(), MAX_FEATURES)));
    }
}
