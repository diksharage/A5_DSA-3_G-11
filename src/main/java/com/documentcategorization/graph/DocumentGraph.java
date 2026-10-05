package com.documentcategorization.graph;
import com.documentcategorization.model.Document;
import java.util.*;
public class DocumentGraph {
    private Map<Document, List<Document>> adjacencyList = new HashMap<>();
    private int edgeCount = 0;
    
    public void addVertex(Document doc) {
        adjacencyList.putIfAbsent(doc, new ArrayList<>());
    }
    public void addEdge(Document doc1, Document doc2) {
        addVertex(doc1); addVertex(doc2);
        adjacencyList.get(doc1).add(doc2);
        adjacencyList.get(doc2).add(doc1);
        edgeCount++;
    }
    public int getEdgeCount() { return edgeCount; }
    public Map<Document, List<Document>> getAdjacencyList() { return adjacencyList; }
    
    public List<List<Document>> findConnectedComponents() {
        List<List<Document>> components = new ArrayList<>();
        Set<Document> visited = new HashSet<>();
        for (Document doc : adjacencyList.keySet()) {
            if (!visited.contains(doc)) {
                List<Document> component = new ArrayList<>();
                bfs(doc, visited, component);
                components.add(component);
            }
        }
        return components;
    }
    private void bfs(Document start, Set<Document> visited, List<Document> component) {
        Queue<Document> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            Document curr = queue.poll();
            component.add(curr);
            for (Document neighbor : adjacencyList.getOrDefault(curr, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }
}
