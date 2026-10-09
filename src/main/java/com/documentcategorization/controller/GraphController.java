package com.documentcategorization.controller;
import com.documentcategorization.service.AnalysisService;
import com.documentcategorization.graph.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
public class GraphController {
    @Autowired private AnalysisService analysisService;
    @Autowired private GraphService graphService;
    
    @GetMapping("/graph")
    public String graph(Model model) {
        model.addAttribute("graph", analysisService.getGraph());
        model.addAttribute("threshold", graphService.getCurrentThreshold());
        return "graph";
    }
    @PostMapping("/graph/threshold")
    public String updateThreshold(@RequestParam("threshold") double threshold) {
        graphService.setCurrentThreshold(threshold);
        // Note: Changing threshold requires re-running analysis to take effect in the global state
        analysisService.runFullAnalysis(); 
        return "redirect:/graph";
    }
}