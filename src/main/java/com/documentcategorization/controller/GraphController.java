package com.documentcategorization.controller;

import com.documentcategorization.graph.DocumentGraph;
import com.documentcategorization.graph.GraphService;
import com.documentcategorization.model.CategoryResult;
import com.documentcategorization.model.Document;
import com.documentcategorization.service.AnalysisService;
import com.documentcategorization.service.DocumentService;
import com.documentcategorization.similarity.SimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GraphController {

    private final AnalysisService analysisService;
    private final GraphService graphService;
    private final DocumentService documentService;
    private final SimilarityService similarityService;

    @Autowired
    public GraphController(AnalysisService analysisService,
                           GraphService graphService,
                           DocumentService documentService,
                           SimilarityService similarityService) {
        this.analysisService = analysisService;
        this.graphService = graphService;
        this.documentService = documentService;
        this.similarityService = similarityService;
    }

    @GetMapping("/graph")
    public String graph(
            @RequestParam(value = "selectedDocId", required = false) String selectedDocId,
            @RequestParam(value = "traverseDocId", required = false) String traverseDocId,
            @RequestParam(value = "algorithm", required = false) String algorithm,
            Model model) {

        populateGraphModel(model, selectedDocId, traverseDocId, algorithm);
        return "graph";
    }

    @PostMapping("/graph/threshold")
    public String updateThreshold(@RequestParam(value = "threshold", required = false) Double threshold) {
        if (threshold != null && !threshold.isNaN() && !threshold.isInfinite()) {
            double clamped = Math.max(0.0, Math.min(1.0, threshold));
            graphService.setCurrentThreshold(clamped);
            if (!documentService.getAllDocuments().isEmpty()) {
                analysisService.runFullAnalysis();
            }
        }
        return "redirect:/graph";
    }

    @PostMapping("/graph/traverse")
    public String traverseGraph(
            @RequestParam(value = "docId", required = false) String docId,
            @RequestParam(value = "algorithm", required = false, defaultValue = "bfs") String algorithm,
            @RequestParam(value = "selectedDocId", required = false) String selectedDocId,
            Model model) {

        String activeSelected = (selectedDocId != null && !selectedDocId.isEmpty()) ? selectedDocId : docId;
        populateGraphModel(model, activeSelected, docId, algorithm);
        return "graph";
    }

    @GetMapping("/api/graph/state")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getGraphStateJson(
            @RequestParam(value = "selectedDocId", required = false) String selectedDocId,
            @RequestParam(value = "traverseDocId", required = false) String traverseDocId,
            @RequestParam(value = "algorithm", required = false) String algorithm) {

        org.springframework.ui.ExtendedModelMap model = new org.springframework.ui.ExtendedModelMap();
        populateGraphModel(model, selectedDocId, traverseDocId, algorithm);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("hasDocs", model.getAttribute("hasDocs"));
        response.put("graphReady", model.getAttribute("graphReady"));
        response.put("vertexCount", model.getAttribute("vertexCount"));
        response.put("edgeCount", model.getAttribute("edgeCount"));
        response.put("componentCount", model.getAttribute("componentCount"));
        response.put("threshold", model.getAttribute("threshold"));
        response.put("vertices", model.getAttribute("graphVertices"));
        response.put("edges", model.getAttribute("graphEdges"));
        response.put("components", model.getAttribute("graphComponents"));
        response.put("traversalIds", model.getAttribute("traversalIds"));
        response.put("traversalNames", model.getAttribute("traversalNames"));
        response.put("traversalAlgo", model.getAttribute("traversalAlgo"));
        response.put("traversalStart", model.getAttribute("traversalStart"));
        return ResponseEntity.ok(response);
    }

    private void populateGraphModel(Model model, String selectedDocId, String traverseDocId, String algorithm) {
        List<Document> allDocs = documentService.getAllDocuments();
        DocumentGraph graph = analysisService.getGraph();
        boolean hasDocs = !allDocs.isEmpty();
        boolean graphReady = graph != null && analysisService.getStatus() == AnalysisService.AnalysisStatus.COMPLETED;
        double threshold = graphService.getCurrentThreshold();

        model.addAttribute("hasDocs", hasDocs);
        model.addAttribute("graphReady", graphReady);
        model.addAttribute("graph", graph);
        model.addAttribute("threshold", threshold);

        if (!graphReady) {
            model.addAttribute("documents", Collections.emptyList());
            model.addAttribute("vertexCount", 0);
            model.addAttribute("edgeCount", 0);
            model.addAttribute("componentCount", 0);
            model.addAttribute("graphVertices", Collections.emptyList());
            model.addAttribute("graphEdges", Collections.emptyList());
            model.addAttribute("graphComponents", Collections.emptyList());
            model.addAttribute("selectedDocId", "");
            model.addAttribute("traversalIds", Collections.emptyList());
            model.addAttribute("traversalNames", Collections.emptyList());
            return;
        }

        // Preserve consistent vertex order based on uploaded documents present in graph
        List<Document> orderedVertices = new ArrayList<>();
        for (Document d : allDocs) {
            if (graph.getAdjacencyList().containsKey(d)) {
                orderedVertices.add(d);
            }
        }
        for (Document d : graph.getAdjacencyList().keySet()) {
            if (!orderedVertices.contains(d)) {
                orderedVertices.add(d);
            }
        }

        // Build Connected Components from backend DocumentGraph.findConnectedComponents()
        List<List<Document>> rawComponents = graph.findConnectedComponents();
        List<CategoryResult> categories = analysisService.getCategories() != null ? analysisService.getCategories() : Collections.emptyList();

        Map<String, Integer> docToCompNum = new HashMap<>();
        Map<String, String> docToCategory = new HashMap<>();
        Map<Integer, List<String>> compToDocIds = new HashMap<>();
        Map<Integer, List<String>> compToDocNames = new HashMap<>();
        List<Map<String, Object>> graphComponents = new ArrayList<>();

        for (int i = 0; i < rawComponents.size(); i++) {
            int compNum = i + 1;
            List<Document> compDocs = rawComponents.get(i);
            List<String> cIds = compDocs.stream().map(Document::getId).collect(Collectors.toList());
            List<String> cNames = compDocs.stream().map(Document::getOriginalFilename).collect(Collectors.toList());
            compToDocIds.put(compNum, cIds);
            compToDocNames.put(compNum, cNames);

            String catLabel = "Component #" + compNum;
            for (CategoryResult cr : categories) {
                if (cr.getDocuments() != null && !compDocs.isEmpty() && cr.getDocuments().contains(compDocs.get(0))) {
                    catLabel = cr.getCategoryLabel();
                    break;
                }
            }

            for (Document d : compDocs) {
                docToCompNum.put(d.getId(), compNum);
                docToCategory.put(d.getId(), catLabel);
            }

            Map<String, Object> compMap = new LinkedHashMap<>();
            compMap.put("componentNumber", compNum);
            compMap.put("componentId", "Component #" + compNum);
            compMap.put("categoryLabel", catLabel);
            compMap.put("documentIds", cIds);
            compMap.put("documentNames", cNames);
            compMap.put("size", compDocs.size());
            compMap.put("isolated", compDocs.size() == 1);
            graphComponents.add(compMap);
        }

        // Build Vertex payloads with exact adjacency lists and incident edge weights
        List<Map<String, Object>> graphVertices = new ArrayList<>();
        for (Document d : orderedVertices) {
            List<Document> adj = graph.getAdjacencyList().getOrDefault(d, Collections.emptyList());
            List<Map<String, Object>> neighborsList = new ArrayList<>();
            for (Document nb : adj) {
                double sim = similarityService.calculateJaccardSimilarity(d, nb);
                Map<String, Object> nbMap = new LinkedHashMap<>();
                nbMap.put("id", nb.getId());
                nbMap.put("filename", nb.getOriginalFilename());
                nbMap.put("weight", sim);
                nbMap.put("formattedWeight", String.format(Locale.US, "%.2f", sim));
                nbMap.put("exactWeight", String.format(Locale.US, "%.4f", sim));
                neighborsList.add(nbMap);
            }

            int compNum = docToCompNum.getOrDefault(d.getId(), 1);
            Map<String, Object> vMap = new LinkedHashMap<>();
            vMap.put("id", d.getId());
            vMap.put("filename", d.getOriginalFilename());
            vMap.put("degree", neighborsList.size());
            vMap.put("isolated", neighborsList.isEmpty());
            vMap.put("componentNumber", compNum);
            vMap.put("componentId", "Component #" + compNum);
            vMap.put("categoryLabel", docToCategory.getOrDefault(d.getId(), "Uncategorized"));
            vMap.put("componentMemberIds", compToDocIds.getOrDefault(compNum, Collections.singletonList(d.getId())));
            vMap.put("componentMemberNames", compToDocNames.getOrDefault(compNum, Collections.singletonList(d.getOriginalFilename())));
            vMap.put("neighbors", neighborsList);
            graphVertices.add(vMap);
        }

        // Build Unique Undirected Edges (each unordered pair rendered exactly once)
        List<Map<String, Object>> graphEdges = new ArrayList<>();
        for (Document docA : orderedVertices) {
            List<Document> adj = graph.getAdjacencyList().getOrDefault(docA, Collections.emptyList());
            for (Document docB : adj) {
                if (docA.getId().compareTo(docB.getId()) < 0) {
                    double sim = similarityService.calculateJaccardSimilarity(docA, docB);
                    Map<String, Object> eMap = new LinkedHashMap<>();
                    eMap.put("edgeId", docA.getId() + "--" + docB.getId());
                    eMap.put("sourceId", docA.getId());
                    eMap.put("sourceName", docA.getOriginalFilename());
                    eMap.put("targetId", docB.getId());
                    eMap.put("targetName", docB.getOriginalFilename());
                    eMap.put("weight", sim);
                    eMap.put("formattedWeight", String.format(Locale.US, "%.2f", sim));
                    eMap.put("exactWeight", String.format(Locale.US, "%.4f", sim));
                    eMap.put("componentNumber", docToCompNum.getOrDefault(docA.getId(), 1));
                    graphEdges.add(eMap);
                }
            }
        }

        String resolvedSelectedId = "";
        if (selectedDocId != null && !selectedDocId.isEmpty()) {
            for (Document d : orderedVertices) {
                if (d.getId().equals(selectedDocId)) {
                    resolvedSelectedId = d.getId();
                    break;
                }
            }
        }

        // Backend BFS/DFS Traversal if requested
        List<String> traversalIds = new ArrayList<>();
        List<String> traversalNames = new ArrayList<>();
        if (traverseDocId != null && !traverseDocId.isEmpty() && algorithm != null && !algorithm.isEmpty()) {
            Document startDoc = null;
            for (Document d : orderedVertices) {
                if (d.getId().equals(traverseDocId)) {
                    startDoc = d;
                    break;
                }
            }
            if (startDoc != null) {
                String algoUpper = algorithm.trim().toUpperCase(Locale.ROOT);
                List<Document> traversal = "DFS".equals(algoUpper)
                        ? graph.dfsTraversal(startDoc)
                        : graph.bfsTraversal(startDoc);
                for (Document td : traversal) {
                    traversalIds.add(td.getId());
                    traversalNames.add(td.getOriginalFilename());
                }
                model.addAttribute("traversalResult", traversal);
                model.addAttribute("traversalAlgo", "DFS".equals(algoUpper) ? "DFS" : "BFS");
                model.addAttribute("traversalStart", startDoc.getOriginalFilename());
                model.addAttribute("traversalStartId", startDoc.getId());
                if (resolvedSelectedId.isEmpty()) {
                    resolvedSelectedId = startDoc.getId();
                }
            }
        }

        model.addAttribute("documents", orderedVertices);
        model.addAttribute("vertexCount", orderedVertices.size());
        model.addAttribute("edgeCount", graph.getEdgeCount());
        model.addAttribute("componentCount", rawComponents.size());
        model.addAttribute("graphVertices", graphVertices);
        model.addAttribute("graphEdges", graphEdges);
        model.addAttribute("graphComponents", graphComponents);
        model.addAttribute("selectedDocId", resolvedSelectedId);
        model.addAttribute("traversalIds", traversalIds);
        model.addAttribute("traversalNames", traversalNames);
    }
}
