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
            @RequestParam(value = "pattern", required = false) String pattern,
            @RequestParam(value = "algorithm", required = false, defaultValue = "kmp") String algorithm,
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
                searchTarget = docOpt.get().getRawText();
                docName = docOpt.get().getOriginalFilename();
                model.addAttribute("selectedDocId", docId);
            } else {
                attrs.addFlashAttribute("error", "Selected document was not found.");
                return "redirect:/string-algorithms";
            }
        }

        if (searchTarget == null || searchTarget.trim().isEmpty()) {
            attrs.addFlashAttribute("error", "Target text is empty. Please select a valid document or enter manual text.");
            return "redirect:/string-algorithms";
        }

        StringSearchResult result = null;
        if ("naive".equals(algorithm)) result = stringAlgorithmsService.naiveSearch(searchTarget, pattern);
        else if ("kmp".equals(algorithm)) result = stringAlgorithmsService.kmpSearch(searchTarget, pattern);
        else if ("z".equals(algorithm)) result = stringAlgorithmsService.zAlgorithmSearch(searchTarget, pattern);
        else if ("rabin".equals(algorithm) || "rabinkarp".equals(algorithm)) result = stringAlgorithmsService.rabinKarpSearch(searchTarget, pattern);
        else if ("ahocorasick".equals(algorithm)) result = stringAlgorithmsService.ahoCorasickSearch(searchTarget, Arrays.asList(pattern.split(",")));
        else {
            attrs.addFlashAttribute("error", "Unsupported string algorithm selected.");
            return "redirect:/string-algorithms";
        }

        String displayInputText = searchTarget.length() > 4000
                ? searchTarget.substring(0, 4000) + "\n... [Preview truncated at 4,000 of " + searchTarget.length() + " characters]"
                : searchTarget;

        model.addAttribute("result", result);
        model.addAttribute("inputText", displayInputText);
        model.addAttribute("inputPattern", pattern);
        model.addAttribute("selectedAlgorithm", algorithm);
        model.addAttribute("docName", docName);
        model.addAttribute("documents", documentService.getAllDocuments());

        return "string-algorithms";
    }

    @PostMapping("/string-algorithms/compare")
    public String compareAlgorithms(
            @RequestParam(value = "docId", required = false) String docId,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "pattern", required = false) String pattern,
            RedirectAttributes attrs,
            Model model) {

        if (pattern == null || pattern.trim().isEmpty()) {
            attrs.addFlashAttribute("error", "Please enter a pattern to compare.");
            return "redirect:/string-algorithms";
        }

        String searchTarget = text;
        String docName = "Manual Text Input";

        if (docId != null && !docId.trim().isEmpty()) {
            Optional<Document> docOpt = documentService.getDocumentById(docId);
            if (docOpt.isPresent()) {
                searchTarget = docOpt.get().getRawText();
                docName = docOpt.get().getOriginalFilename();
                model.addAttribute("selectedDocId", docId);
            } else {
                attrs.addFlashAttribute("error", "Selected document was not found.");
                return "redirect:/string-algorithms";
            }
        }

        if (searchTarget == null || searchTarget.trim().isEmpty()) {
            attrs.addFlashAttribute("error", "Target text is empty.");
            return "redirect:/string-algorithms";
        }

        java.util.List<StringSearchResult> compareResults = new java.util.ArrayList<>();
        compareResults.add(stringAlgorithmsService.naiveSearch(searchTarget, pattern));
        compareResults.add(stringAlgorithmsService.kmpSearch(searchTarget, pattern));
        compareResults.add(stringAlgorithmsService.zAlgorithmSearch(searchTarget, pattern));
        compareResults.add(stringAlgorithmsService.rabinKarpSearch(searchTarget, pattern));
        compareResults.add(stringAlgorithmsService.ahoCorasickSearch(searchTarget, Arrays.asList(pattern.split(","))));

        String displayInputText = searchTarget.length() > 4000
                ? searchTarget.substring(0, 4000) + "\n... [Preview truncated at 4,000 of " + searchTarget.length() + " characters]"
                : searchTarget;

        model.addAttribute("compareResults", compareResults);
        model.addAttribute("inputText", displayInputText);
        model.addAttribute("inputPattern", pattern);
        model.addAttribute("docName", docName);
        model.addAttribute("documents", documentService.getAllDocuments());

        return "string-algorithms";
    }
}
