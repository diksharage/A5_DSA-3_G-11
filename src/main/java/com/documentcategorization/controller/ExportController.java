package com.documentcategorization.controller;
import com.documentcategorization.categorization.CategoryService;
import com.documentcategorization.graph.DocumentGraph;
import com.documentcategorization.graph.GraphService;
import com.documentcategorization.model.CategoryResult;
import com.documentcategorization.model.Document;
import com.documentcategorization.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;
@Controller
public class ExportController {
    private final DocumentService documentService;
    private final GraphService graphService;
    private final CategoryService categoryService;
    @Autowired
    public ExportController(DocumentService documentService, GraphService graphService, CategoryService categoryService) {
        this.documentService = documentService; this.graphService = graphService; this.categoryService = categoryService;
    }
    @GetMapping("/export")
    public String exportPage(org.springframework.ui.Model model) { 
        try { 
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper(); 
            model.addAttribute("jsonPreview", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(generateExportData())); 
        } catch(Exception e) { model.addAttribute("jsonPreview", "{}"); }
        return "export";
    }
    @GetMapping(value = "/api/export/json")
    public ResponseEntity<Map<String, Object>> exportJson() { 
        Map<String, Object> exportData = generateExportData(); 
        return new ResponseEntity<>(exportData, HttpStatus.OK); 
    } 
    private Map<String, Object> generateExportData() {
        Map<String, Object> exportData = new LinkedHashMap<>();
        List<Document> docs = documentService.getAllDocuments();
        DocumentGraph graph = graphService.buildGraph(docs, graphService.getCurrentThreshold());
        List<CategoryResult> categories = categoryService.identifyCategories(graph.findConnectedComponents());
        exportData.put("project", "Optimization Framework for Document Categorization");
        exportData.put("totalDocuments", docs.size());
        exportData.put("totalEdges", graph.getEdgeCount());
        exportData.put("totalCategories", categories.size());
        exportData.put("categories", categories);
        return exportData;
    }
}
