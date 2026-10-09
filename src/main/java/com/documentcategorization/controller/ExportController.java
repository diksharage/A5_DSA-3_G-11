package com.documentcategorization.controller;

import com.documentcategorization.model.CategoryResult;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.TermFrequency;
import com.documentcategorization.service.AnalysisService;
import com.documentcategorization.service.DocumentService;
import com.documentcategorization.graph.GraphService;
import com.documentcategorization.similarity.SimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;

@Controller
public class ExportController {
    private final DocumentService documentService;
    private final AnalysisService analysisService;
    private final GraphService graphService;
    private final SimilarityService similarityService;

    @Autowired
    public ExportController(DocumentService documentService, AnalysisService analysisService, GraphService graphService, SimilarityService similarityService) {
        this.documentService = documentService;
        this.analysisService = analysisService;
        this.graphService = graphService;
        this.similarityService = similarityService;
    }

    @GetMapping("/export")
    public String exportPage(org.springframework.ui.Model model) {
        try {
            List<Document> docs = documentService.getAllDocuments();
            if (docs.isEmpty()) {
                model.addAttribute("error", "No documents available for export.");
                return "export";
            }
            if (analysisService.getGraph() == null || analysisService.getCategories() == null) {
                model.addAttribute("error", "Run Full Analysis before exporting results.");
                return "export";
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            model.addAttribute("jsonPreview", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(generateExportData()));
        } catch(Exception e) {
            model.addAttribute("jsonPreview", "{}");
        }
        return "export";
    }

    @GetMapping(value = "/api/export/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exportJson() {
        List<Document> docs = documentService.getAllDocuments();
        if (docs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "No documents available for export."));
        }
        if (analysisService.getGraph() == null || analysisService.getCategories() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Run Full Analysis before exporting results."));
        }

        Map<String, Object> exportData = generateExportData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document-categorization-analysis.json");
        return new ResponseEntity<>(exportData, headers, HttpStatus.OK);
    }

    private Map<String, Object> generateExportData() {
        Map<String, Object> exportData = new LinkedHashMap<>();
        
        exportData.put("project", Collections.singletonMap("name", "Optimization Framework for Document Categorization"));
        
        List<Document> docs = documentService.getAllDocuments();
        int docCount = docs.size();
        int edgeCount = analysisService.getGraph() != null ? analysisService.getGraph().getEdgeCount() : 0;
        int compCount = analysisService.getCategories() != null ? analysisService.getCategories().size() : 0;
        
        Map<String, Object> analysisMeta = new LinkedHashMap<>();
        analysisMeta.put("documentCount", docCount);
        analysisMeta.put("similarityThreshold", graphService.getCurrentThreshold());
        analysisMeta.put("edgeCount", edgeCount);
        analysisMeta.put("connectedComponentCount", compCount);
        exportData.put("analysis", analysisMeta);

        List<Map<String, Object>> docsList = new ArrayList<>();
        for (Document d : docs) {
            Map<String, Object> docMap = new LinkedHashMap<>();
            docMap.put("id", d.getId());
            docMap.put("filename", d.getOriginalFilename());
            docMap.put("fileType", d.getFileType() != null ? d.getFileType() : "Unknown");
            docMap.put("fileSize", d.getFileSize());
            docMap.put("status", d.getCurrentStage().toString());
            
            List<String> features = new ArrayList<>();
            if (d.getOptimizedFeatures() != null) {
                for (TermFrequency tf : d.getOptimizedFeatures()) {
                    features.add(tf.getTerm());
                }
            }
            docMap.put("features", features);
            
            String group = "None";
            String category = "Uncategorized";
            if (analysisService.getCategories() != null) {
                for (CategoryResult cr : analysisService.getCategories()) {
                    if (cr.getDocuments().contains(d)) {
                        group = cr.getGroupId();
                        category = cr.getCategoryLabel();
                        break;
                    }
                }
            }
            docMap.put("group", group);
            docMap.put("category", category);
            
            docsList.add(docMap);
        }
        exportData.put("documents", docsList);

        Map<String, Object> simMap = new LinkedHashMap<>();
        List<Map<String, Object>> rels = new ArrayList<>();
        if (analysisService.getGraph() != null) {
            Map<Document, List<Document>> adj = analysisService.getGraph().getAdjacencyList();
            for (Map.Entry<Document, List<Document>> entry : adj.entrySet()) {
                Document docA = entry.getKey();
                for (Document docB : entry.getValue()) {
                    // Only record one direction
                    if (docA.getId().compareTo(docB.getId()) < 0) {
                        Map<String, Object> rel = new LinkedHashMap<>();
                        rel.put("documentA", docA.getOriginalFilename());
                        rel.put("documentB", docB.getOriginalFilename());
                        rel.put("jaccard", Math.round(similarityService.calculateJaccardSimilarity(docA, docB) * 1000.0) / 1000.0);
                        rels.add(rel);
                    }
                }
            }
        }
        simMap.put("relationships", rels);
        exportData.put("similarity", simMap);

        Map<String, Object> graphMap = new LinkedHashMap<>();
        List<String> vertices = new ArrayList<>();
        for (Document d : docs) {
            vertices.add(d.getOriginalFilename());
        }
        graphMap.put("vertices", vertices);
        graphMap.put("edges", rels); // the unique edges are exactly the relationships above
        exportData.put("graph", graphMap);

        List<Map<String, Object>> compList = new ArrayList<>();
        List<Map<String, Object>> catList = new ArrayList<>();
        if (analysisService.getCategories() != null) {
            int compId = 1;
            for (CategoryResult cr : analysisService.getCategories()) {
                Map<String, Object> comp = new LinkedHashMap<>();
                comp.put("id", compId);
                List<String> cDocs = new ArrayList<>();
                for (Document cd : cr.getDocuments()) {
                    cDocs.add(cd.getOriginalFilename());
                }
                comp.put("documents", cDocs);
                compList.add(comp);

                Map<String, Object> catMap = new LinkedHashMap<>();
                catMap.put("component", compId);
                catMap.put("category", cr.getCategoryLabel());
                catMap.put("documents", cDocs);
                catList.add(catMap);

                compId++;
            }
        }
        exportData.put("connectedComponents", compList);
        exportData.put("categories", catList);

        return exportData;
    }
}
