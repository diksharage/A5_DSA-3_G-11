import os
base_dir = r"C:\Users\Diksha\OneDrive\Desktop\Optimization Frame Work"
files = {}

files["src/main/java/com/documentcategorization/controller/DocumentController.java"] = """package com.documentcategorization.controller;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.PipelineStage;
import com.documentcategorization.preprocessing.PreprocessingService;
import com.documentcategorization.features.FeatureExtractionService;
import com.documentcategorization.features.FeatureOptimizationService;
import com.documentcategorization.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
@Controller
public class DocumentController {
    private final DocumentService documentService;
    private final PreprocessingService preprocessingService;
    private final FeatureExtractionService featureExtractionService;
    private final FeatureOptimizationService featureOptimizationService;
    private final com.documentcategorization.graph.GraphService graphService;
    @Autowired
    public DocumentController(DocumentService documentService, PreprocessingService preprocessingService, FeatureExtractionService featureExtractionService, FeatureOptimizationService featureOptimizationService, com.documentcategorization.graph.GraphService graphService) {
        this.documentService = documentService; this.preprocessingService = preprocessingService; this.featureExtractionService = featureExtractionService; this.featureOptimizationService = featureOptimizationService; this.graphService = graphService;
    }
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("documentCount", documentService.getAllDocuments().size());
        model.addAttribute("featureCount", documentService.getAllDocuments().stream().mapToInt(d -> d.getOptimizedFeatures().size()).sum());
        model.addAttribute("documents", documentService.getAllDocuments());
        return "index";
    }
    @PostMapping("/upload")
    public String uploadFiles(@RequestParam("files") MultipartFile[] files, RedirectAttributes attrs) {
        for (MultipartFile f : files) {
            if (f.isEmpty() || !f.getOriginalFilename().endsWith(".txt")) continue;
            try {
                Document doc = new Document();
                doc.setId(UUID.randomUUID().toString());
                doc.setOriginalFilename(f.getOriginalFilename());
                doc.setFileType(f.getContentType());
                doc.setFileSize(f.getSize());
                doc.setRawText(new String(f.getBytes(), StandardCharsets.UTF_8));
                preprocessingService.preprocess(doc); doc.setCurrentStage(PipelineStage.PREPROCESSED);
                featureExtractionService.extractFeatures(doc); doc.setCurrentStage(PipelineStage.FEATURES_EXTRACTED);
                featureOptimizationService.optimizeFeatures(doc); doc.setCurrentStage(PipelineStage.CATEGORIZED);
                documentService.addDocument(doc);
            } catch (Exception e) {}
        }
        return "redirect:/";
    }
    @PostMapping("/clear")
    public String clearAll() { documentService.clearAllDocuments(); return "redirect:/"; }
    @PostMapping("/delete")
    public String delete(@RequestParam("id") String id) { documentService.deleteDocument(id); return "redirect:/"; }
}
"""

files["src/main/java/com/documentcategorization/controller/ExportController.java"] = """package com.documentcategorization.controller;
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
"""

for k, v in files.items():
    path = os.path.join(base_dir, k)
    with open(path, "w", encoding="utf-8") as f:
        f.write(v)
print("Part 4 complete")
