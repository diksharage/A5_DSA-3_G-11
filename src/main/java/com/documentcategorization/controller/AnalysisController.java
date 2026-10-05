package com.documentcategorization.controller;
import com.documentcategorization.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class AnalysisController {
    @Autowired private DocumentService documentService;
    @GetMapping("/analysis")
    public String analysis(Model model) {
        model.addAttribute("documents", documentService.getAllDocuments());
        return "analysis";
    }
}