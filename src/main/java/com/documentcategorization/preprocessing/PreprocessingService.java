package com.documentcategorization.preprocessing;
import com.documentcategorization.model.Document;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class PreprocessingService {
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList("the", "and", "is", "in", "it", "of", "to", "a", "for", "on", "this"));
    public void preprocess(Document doc) {
        if (doc.getRawText() == null) return;
        String[] rawTokens = doc.getRawText().toLowerCase().replaceAll("[^a-z0-9\\s]", "").split("\\s+");
        List<String> cleanTokens = new ArrayList<>();
        for (String t : rawTokens) {
            if (!t.isEmpty() && !STOP_WORDS.contains(t)) cleanTokens.add(t);
        }
        doc.setTokens(cleanTokens);
    }
}
