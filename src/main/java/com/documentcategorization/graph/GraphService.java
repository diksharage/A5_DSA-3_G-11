package com.documentcategorization.graph;
import com.documentcategorization.model.Document;
import com.documentcategorization.similarity.SimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class GraphService {
    private final SimilarityService similarityService;
    private double currentThreshold = 0.1;
    @Autowired
    public GraphService(SimilarityService similarityService) { this.similarityService = similarityService; }
    public double getCurrentThreshold() { return currentThreshold; }
    public void setCurrentThreshold(double t) { this.currentThreshold = t; }
    public DocumentGraph buildGraph(List<Document> docs, double threshold) {
        DocumentGraph graph = new DocumentGraph();
        for (Document d : docs) graph.addVertex(d);
        for (int i = 0; i < docs.size(); i++) {
            for (int j = i + 1; j < docs.size(); j++) {
                if (similarityService.calculateJaccardSimilarity(docs.get(i), docs.get(j)) >= threshold) {
                    graph.addEdge(docs.get(i), docs.get(j));
                }
            }
        }
        return graph;
    }
}
