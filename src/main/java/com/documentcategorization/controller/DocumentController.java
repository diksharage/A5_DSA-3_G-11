package com.documentcategorization.controller;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.PipelineStage;
import com.documentcategorization.service.AnalysisService;
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
    private final AnalysisService analysisService;

    @Autowired
    public DocumentController(DocumentService documentService, AnalysisService analysisService) {
        this.documentService = documentService; 
        this.analysisService = analysisService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("documentCount", documentService.getAllDocuments().size());
        model.addAttribute("featureCount", documentService.getAllDocuments().stream().mapToInt(d -> d.getOptimizedFeatures() != null ? d.getOptimizedFeatures().size() : 0).sum());
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
                doc.setCurrentStage(PipelineStage.UPLOADED);
                documentService.addDocument(doc);
            } catch (Exception e) {}
        }
        return "redirect:/";
    }

    @PostMapping("/run-analysis")
    public String runAnalysis(RedirectAttributes attrs) {
        if (documentService.getAllDocuments().isEmpty()) {
            attrs.addFlashAttribute("error", "No documents available. Upload documents before running analysis.");
            return "redirect:/";
        }
        analysisService.runFullAnalysis();
        return "redirect:/";
    }

    @PostMapping("/clear")
    public String clearAll() { 
        documentService.clearAllDocuments(); 
        analysisService.reset();
        return "redirect:/"; 
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") String id) { 
        documentService.deleteDocument(id); 
        // Note: In a real system, deleting a document might invalidate the current analysis.
        // We'll reset it to keep the state clean.
        analysisService.reset();
        return "redirect:/"; 
    }

    @GetMapping("/document/{id}")
    public String documentDetails(@PathVariable("id") String id, Model model, RedirectAttributes attrs) {
        java.util.Optional<Document> docOpt = documentService.getDocumentById(id);
        if (!docOpt.isPresent()) {
            attrs.addFlashAttribute("error", "Document not found.");
            return "redirect:/";
        }
        Document doc = docOpt.get();
        model.addAttribute("doc", doc);

        // Find category and group
        String category = "Uncategorized (Run Analysis)";
        String group = "None";
        if (analysisService.getCategories() != null) {
            for (com.documentcategorization.model.CategoryResult cr : analysisService.getCategories()) {
                if (cr.getDocuments().contains(doc)) {
                    category = cr.getCategoryLabel();
                    group = cr.getGroupId();
                    break;
                }
            }
        }
        model.addAttribute("category", category);
        model.addAttribute("group", group);

        // Find similar documents
        java.util.List<Document> similarDocs = new java.util.ArrayList<>();
        if (analysisService.getGraph() != null && analysisService.getGraph().getAdjacencyList().containsKey(doc)) {
            similarDocs = analysisService.getGraph().getAdjacencyList().get(doc);
        }
        model.addAttribute("similarDocs", similarDocs);

        return "document-details";
    }
}
