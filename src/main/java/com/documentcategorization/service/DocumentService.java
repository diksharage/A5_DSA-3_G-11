package com.documentcategorization.service;
import com.documentcategorization.model.Document;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class DocumentService {
    private final List<Document> documents = new ArrayList<>();
    public void addDocument(Document document) {
        boolean exists = documents.stream().anyMatch(d -> d.getOriginalFilename().equals(document.getOriginalFilename()));
        if (!exists) { documents.add(document); }
    }
    public List<Document> getAllDocuments() { return Collections.unmodifiableList(documents); }
    public boolean deleteDocument(String id) { return documents.removeIf(d -> d.getId().equals(id)); }
    public void clearAllDocuments() { documents.clear(); }
    public Optional<Document> getDocumentById(String id) {
        return documents.stream().filter(d -> d.getId().equals(id)).findFirst();
    }
}
