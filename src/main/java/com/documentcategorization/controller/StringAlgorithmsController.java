package com.documentcategorization.controller;

import com.documentcategorization.algorithms.StringAlgorithmsService;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.StringSearchResult;
import com.documentcategorization.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Optional;

@Controller
public class StringAlgorithmsController {
    
    private final StringAlgorithmsService stringAlgorithmsService;
    private final DocumentService documentService;
    
    @Autowired
    public StringAlgorithmsController(StringAlgorithmsService stringAlgorithmsService, DocumentService documentService) {
        this.stringAlgorithmsService = stringAlgorithmsService;
        this.documentService = documentService;
    }
    
    @GetMapping("/string-algorithms")
    public String index(Model model) {
        model.addAttribute("documents", documentService.getAllDocuments());
        return "string-algorithms";
    }
    
    @PostMapping("/string-algorithms")
    public String runAlgorithm(
            @RequestParam(value = "docId", required = false) String docId,
            @RequestParam(value = "text", required = false) String text, 
            @RequestParam("pattern") String pattern, 
            @RequestParam("algorithm") String algorithm, 
            RedirectAttributes attrs,
            Model model) {
        
        if (pattern == null || pattern.trim().isEmpty()) {
            attrs.addFlashAttribute("error", "Please enter a pattern to search.");
            return "redirect:/string-algorithms";
        }

        String searchTarget = text;
        String docName = "Manual Text Input";

        if (docId != null && !docId.trim().isEmpty()) {
            Optional<Document> docOpt = documentService.getDocumentById(docId);
            if (docOpt.isPresent()) {
                searchTarget = docOpt.get().getRawText(); // Or preprocessed if preferred
                docName = docOpt.get().getOriginalFilename();
                model.addAttribute("selectedDocId", docId);
            }
        }
        
        if (searchTarget == null || searchTarget.trim().isEmpty()) {
            attrs.addFlashAttribute("error", "Target text is empty. Please select a valid document or enter manual text.");
            return "redirect:/string-algorithms";
        }
        
        StringSearchResult result = null;
        if(algorithm.equals("naive")) result = stringAlgorithmsService.naiveSearch(searchTarget, pattern);
        else if(algorithm.equals("kmp")) result = stringAlgorithmsService.kmpSearch(searchTarget, pattern);
        else if(algorithm.equals("z")) result = stringAlgorithmsService.zAlgorithmSearch(searchTarget, pattern);
        else if(algorithm.equals("rabin")) result = stringAlgorithmsService.rabinKarpSearch(searchTarget, pattern);
        else if(algorithm.equals("ahocorasick")) result = stringAlgorithmsService.ahoCorasickSearch(searchTarget, Arrays.asList(pattern.split(",")));
        
        model.addAttribute("result", result);
        model.addAttribute("inputText", searchTarget);
        model.addAttribute("inputPattern", pattern);
        model.addAttribute("selectedAlgorithm", algorithm);
        model.addAttribute("docName", docName);
        model.addAttribute("documents", documentService.getAllDocuments());
        
        return "string-algorithms";
    }
}