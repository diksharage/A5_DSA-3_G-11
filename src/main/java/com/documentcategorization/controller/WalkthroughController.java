package com.documentcategorization.controller;

import com.documentcategorization.model.Document;
import com.documentcategorization.model.WalkthroughResult;
import com.documentcategorization.service.AnalysisService;
import com.documentcategorization.service.DocumentService;
import com.documentcategorization.service.WalkthroughService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class WalkthroughController {

    private final WalkthroughService walkthroughService;
    private final DocumentService documentService;
    private final AnalysisService analysisService;

    @Autowired
    public WalkthroughController(WalkthroughService walkthroughService,
                                 DocumentService documentService,
                                 AnalysisService analysisService) {
        this.walkthroughService = walkthroughService;
        this.documentService = documentService;
        this.analysisService = analysisService;
    }

    @GetMapping("/walkthrough")
    public String viewWalkthroughPage(
            @RequestParam(value = "algorithm", required = false) String algorithm,
            @RequestParam(value = "algo", required = false) String algo,
            @RequestParam(value = "docId", required = false) String docId,
            @RequestParam(value = "docAId", required = false) String docAId,
            @RequestParam(value = "startDocId", required = false) String startDocId,
            @RequestParam(value = "docIdB", required = false) String docIdB,
            @RequestParam(value = "docBId", required = false) String docBId,
            @RequestParam(value = "pattern", required = false) String pattern,
            Model model) {

        String effAlgo = resolveFirstNonEmpty(algorithm, algo);
        String effDocA = resolveFirstNonEmpty(docId, docAId, startDocId);
        String effDocB = resolveFirstNonEmpty(docIdB, docBId);

        populateCommonModel(model, effAlgo, effDocA, effDocB, pattern);

        if (effAlgo != null && !effAlgo.trim().isEmpty()) {
            String resolvedDocA = (String) model.getAttribute("selectedDocId");
            String resolvedDocB = (String) model.getAttribute("selectedDocIdB");
            try {
                WalkthroughResult result = walkthroughService.executeWalkthrough(effAlgo, resolvedDocA, resolvedDocB, pattern);
                model.addAttribute("walkthroughResult", result);
            } catch (IllegalArgumentException ex) {
                model.addAttribute("error", ex.getMessage());
            } catch (Exception ex) {
                model.addAttribute("error", "Unable to generate walkthrough: " + ex.getMessage());
            }
        }

        return "walkthrough";
    }

    @PostMapping("/walkthrough")
    public String startWalkthrough(
            @RequestParam(value = "algorithm", required = false) String algorithm,
            @RequestParam(value = "algo", required = false) String algo,
            @RequestParam(value = "docId", required = false) String docId,
            @RequestParam(value = "docAId", required = false) String docAId,
            @RequestParam(value = "startDocId", required = false) String startDocId,
            @RequestParam(value = "docIdB", required = false) String docIdB,
            @RequestParam(value = "docBId", required = false) String docBId,
            @RequestParam(value = "pattern", required = false) String pattern,
            Model model) {

        String effAlgo = resolveFirstNonEmpty(algorithm, algo);
        String effDocA = resolveFirstNonEmpty(docId, docAId, startDocId);
        String effDocB = resolveFirstNonEmpty(docIdB, docBId);

        populateCommonModel(model, effAlgo, effDocA, effDocB, pattern);

        try {
            WalkthroughResult result = walkthroughService.executeWalkthrough(effAlgo, effDocA, effDocB, pattern);
            model.addAttribute("walkthroughResult", result);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            model.addAttribute("error", "Unable to generate walkthrough: " + ex.getMessage());
        }

        return "walkthrough";
    }

    @GetMapping("/api/walkthrough")
    @ResponseBody
    public ResponseEntity<?> apiWalkthrough(
            @RequestParam(value = "algorithm", required = false) String algorithm,
            @RequestParam(value = "algo", required = false) String algo,
            @RequestParam(value = "docId", required = false) String docId,
            @RequestParam(value = "docAId", required = false) String docAId,
            @RequestParam(value = "startDocId", required = false) String startDocId,
            @RequestParam(value = "docIdB", required = false) String docIdB,
            @RequestParam(value = "docBId", required = false) String docBId,
            @RequestParam(value = "pattern", required = false) String pattern) {

        String effAlgo = resolveFirstNonEmpty(algorithm, algo);
        String effDocA = resolveFirstNonEmpty(docId, docAId, startDocId);
        String effDocB = resolveFirstNonEmpty(docIdB, docBId);

        try {
            WalkthroughResult result = walkthroughService.executeWalkthrough(effAlgo, effDocA, effDocB, pattern);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage() != null ? ex.getMessage() : "Unable to generate walkthrough."));
        }
    }

    private String resolveFirstNonEmpty(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) {
                return v.trim();
            }
        }
        return null;
    }

    private void populateCommonModel(Model model, String algorithm, String docId, String docIdB, String pattern) {
        List<Document> docs = documentService.getAllDocuments();
        boolean hasDocs = !docs.isEmpty();
        boolean analysisReady = analysisService.getStatus() == AnalysisService.AnalysisStatus.COMPLETED
                && analysisService.getGraph() != null;

        String defaultDocA = (docId != null && !docId.isEmpty()) ? docId : (hasDocs ? docs.get(0).getId() : "");
        String defaultDocB = (docIdB != null && !docIdB.isEmpty()) ? docIdB : (docs.size() > 1 ? docs.get(1).getId() : defaultDocA);

        model.addAttribute("documents", docs);
        model.addAttribute("hasDocs", hasDocs);
        model.addAttribute("analysisReady", analysisReady);
        model.addAttribute("algorithms", walkthroughService.getSupportedAlgorithms());
        model.addAttribute("selectedAlgorithm", (algorithm != null && !algorithm.isEmpty()) ? algorithm : "mergesort");
        model.addAttribute("selectedDocId", defaultDocA);
        model.addAttribute("selectedDocIdB", defaultDocB);
        model.addAttribute("inputPattern", pattern != null ? pattern : "");
        model.addAttribute("emptySteps", Collections.emptyList());
    }
}
