package com.documentcategorization.controller;
import com.documentcategorization.service.DocumentService;
import com.documentcategorization.similarity.SimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class SimilarityController {
    @Autowired private DocumentService documentService;
    @Autowired private SimilarityService similarityService;
    @GetMapping("/similarity")
    public String similarity(Model model) {
        model.addAttribute("documents", documentService.getAllDocuments());
        model.addAttribute("similarityService", similarityService);
        return "similarity";
    }
}