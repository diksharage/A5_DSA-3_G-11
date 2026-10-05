package com.documentcategorization.controller;
import com.documentcategorization.graph.GraphService;
import com.documentcategorization.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
public class GraphController {
    @Autowired private DocumentService documentService;
    @Autowired private GraphService graphService;
    @GetMapping("/graph")
    public String graph(Model model) {
        model.addAttribute("graph", graphService.buildGraph(documentService.getAllDocuments(), graphService.getCurrentThreshold()));
        model.addAttribute("threshold", graphService.getCurrentThreshold());
        return "graph";
    }
    @PostMapping("/graph/threshold")
    public String updateThreshold(@RequestParam("threshold") double threshold) {
        graphService.setCurrentThreshold(threshold);
        return "redirect:/graph";
    }
}