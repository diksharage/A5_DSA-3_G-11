package com.documentcategorization.controller;
import com.documentcategorization.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class ResultsController {
    @Autowired private AnalysisService analysisService;
    @Autowired private com.documentcategorization.graph.GraphService graphService;
    
    @GetMapping("/results")
    public String results(Model model) {
        model.addAttribute("categories", analysisService.getCategories());
        model.addAttribute("graph", analysisService.getGraph());
        model.addAttribute("threshold", graphService.getCurrentThreshold());
        model.addAttribute("documentCount", analysisService.getGraph() != null ? analysisService.getGraph().getAdjacencyList().size() : 0);
        return "results";
    }
}