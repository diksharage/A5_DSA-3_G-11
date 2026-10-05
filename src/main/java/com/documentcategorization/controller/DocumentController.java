package com.documentcategorization.controller;
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
