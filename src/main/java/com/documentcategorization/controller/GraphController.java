package com.documentcategorization.controller;
import com.documentcategorization.service.AnalysisService;
import com.documentcategorization.graph.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.documentcategorization.model.Document;
import com.documentcategorization.graph.DocumentGraph;
import java.util.List;
@Controller
public class GraphController {
    @Autowired private AnalysisService analysisService;
    @Autowired private GraphService graphService;
    
    @GetMapping("/graph")
    public String graph(Model model) {
        DocumentGraph graph = analysisService.getGraph();
        model.addAttribute("graph", graph);
        model.addAttribute("threshold", graphService.getCurrentThreshold());
        model.addAttribute("documents", graph != null ? graph.getAdjacencyList().keySet() : java.util.Collections.emptySet());
        return "graph";
    }
    @PostMapping("/graph/threshold")
    public String updateThreshold(@RequestParam("threshold") double threshold) {
        graphService.setCurrentThreshold(threshold);
        // Note: Changing threshold requires re-running analysis to take effect in the global state
        analysisService.runFullAnalysis(); 
        return "redirect:/graph";
    }

    @PostMapping("/graph/traverse")
    public String traverseGraph(
            @RequestParam("docId") String docId,
            @RequestParam("algorithm") String algorithm,
            Model model) {
        
        DocumentGraph graph = analysisService.getGraph();
        model.addAttribute("graph", graph);
        model.addAttribute("threshold", graphService.getCurrentThreshold());
        model.addAttribute("documents", analysisService.getGraph() != null ? analysisService.getGraph().getAdjacencyList().keySet() : java.util.Collections.emptySet());
        
        Document startDoc = null;
        if (graph != null) {
            for (Document d : graph.getAdjacencyList().keySet()) {
                if (d.getId().equals(docId)) { startDoc = d; break; }
            }
        }
        
        if (startDoc != null) {
            List<Document> traversal = algorithm.equals("DFS") ? graph.dfsTraversal(startDoc) : graph.bfsTraversal(startDoc);
            model.addAttribute("traversalResult", traversal);
            model.addAttribute("traversalAlgo", algorithm);
            model.addAttribute("traversalStart", startDoc.getOriginalFilename());
        }
        
        return "graph";
    }
}