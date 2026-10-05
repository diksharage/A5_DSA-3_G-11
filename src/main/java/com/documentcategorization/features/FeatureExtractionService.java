package com.documentcategorization.features;
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
