import os
base_dir = r"C:\Users\Diksha\OneDrive\Desktop\Optimization Frame Work"
c_dir = os.path.join(base_dir, "src", "main", "java", "com", "documentcategorization", "controller")

files = {}

files["PipelineController.java"] = """package com.documentcategorization.controller;
import com.documentcategorization.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class PipelineController {
    @Autowired private DocumentService documentService;
    @GetMapping("/pipeline")
    public String pipeline(Model model) {
        model.addAttribute("documents", documentService.getAllDocuments());
        return "pipeline";
    }
}"""

files["AnalysisController.java"] = """package com.documentcategorization.controller;
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
}"""

files["StringAlgorithmsController.java"] = """package com.documentcategorization.controller;
import com.documentcategorization.algorithms.StringAlgorithmsService;
import com.documentcategorization.model.StringSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
@Controller
public class StringAlgorithmsController {
    @Autowired private StringAlgorithmsService stringAlgorithmsService;
    @GetMapping("/string-algorithms")
    public String index(Model model) { return "string-algorithms"; }
    @PostMapping("/string-algorithms/run")
    public String run(@RequestParam("text") String text, @RequestParam("pattern") String pattern, @RequestParam("algorithm") String algorithm, Model model) {
        StringSearchResult result = null;
        if(algorithm.equals("naive")) result = stringAlgorithmsService.naiveSearch(text, pattern);
        else if(algorithm.equals("kmp")) result = stringAlgorithmsService.kmpSearch(text, pattern);
        else if(algorithm.equals("z")) result = stringAlgorithmsService.zAlgorithmSearch(text, pattern);
        else if(algorithm.equals("rabin")) result = stringAlgorithmsService.rabinKarpSearch(text, pattern);
        else if(algorithm.equals("ahocorasick")) result = stringAlgorithmsService.ahoCorasickSearch(text, Arrays.asList(pattern.split(",")));
        
        model.addAttribute("result", result);
        model.addAttribute("inputText", text);
        model.addAttribute("inputPattern", pattern);
        model.addAttribute("selectedAlgorithm", algorithm);
        return "string-algorithms";
    }
}"""

files["SimilarityController.java"] = """package com.documentcategorization.controller;
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
}"""

files["GraphController.java"] = """package com.documentcategorization.controller;
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
}"""

files["ResultsController.java"] = """package com.documentcategorization.controller;
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
}"""

for k, v in files.items():
    with open(os.path.join(c_dir, k), "w", encoding="utf-8") as f:
        f.write(v)
print("Controllers restored!")
