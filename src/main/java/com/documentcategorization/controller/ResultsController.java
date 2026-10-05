package com.documentcategorization.controller;
import com.documentcategorization.categorization.CategoryService;
import com.documentcategorization.graph.DocumentGraph;
import com.documentcategorization.graph.GraphService;
import com.documentcategorization.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class ResultsController {
    @Autowired private DocumentService documentService;
    @Autowired private GraphService graphService;
    @Autowired private CategoryService categoryService;
    @GetMapping("/results")
    public String results(Model model) {
        DocumentGraph graph = graphService.buildGraph(documentService.getAllDocuments(), graphService.getCurrentThreshold());
        model.addAttribute("categories", categoryService.identifyCategories(graph.findConnectedComponents()));
        return "results";
    }
}