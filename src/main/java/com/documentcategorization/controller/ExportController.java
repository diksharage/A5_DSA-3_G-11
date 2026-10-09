package com.documentcategorization.controller;
import com.documentcategorization.model.CategoryResult;
import com.documentcategorization.model.Document;
import com.documentcategorization.service.AnalysisService;
import com.documentcategorization.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;
@Controller
public class ExportController {
    private final DocumentService documentService;
    private final AnalysisService analysisService;
    @Autowired
    public ExportController(DocumentService documentService, AnalysisService analysisService) {
        this.documentService = documentService; this.analysisService = analysisService;
    }
    @GetMapping("/export")
    public String exportPage(org.springframework.ui.Model model) { 
        try { 
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper(); 
            model.addAttribute("jsonPreview", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(generateExportData())); 
        } catch(Exception e) { model.addAttribute("jsonPreview", "{}"); }
        return "export";
    }
    @GetMapping(value = "/api/export/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> exportJson() { 
        Map<String, Object> exportData = generateExportData(); 
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=categorization-results.json");
        return new ResponseEntity<>(exportData, headers, HttpStatus.OK); 
    } 
    private Map<String, Object> generateExportData() {
        Map<String, Object> exportData = new LinkedHashMap<>();
        List<Document> docs = documentService.getAllDocuments();
        exportData.put("project", "Optimization Framework for Document Categorization");
        exportData.put("totalDocuments", docs.size());
        
        if (analysisService.getGraph() != null) {
            exportData.put("totalEdges", analysisService.getGraph().getEdgeCount());
        } else {
            exportData.put("totalEdges", 0);
        }
        
        if (analysisService.getCategories() != null) {
            exportData.put("totalCategories", analysisService.getCategories().size());
            exportData.put("categories", analysisService.getCategories());
        } else {
            exportData.put("totalCategories", 0);
            exportData.put("categories", new ArrayList<>());
        }
        
        return exportData;
    }
}
