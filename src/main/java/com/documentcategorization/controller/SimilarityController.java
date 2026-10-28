package com.documentcategorization.controller;

import com.documentcategorization.graph.GraphService;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.TermFrequency;
import com.documentcategorization.service.AnalysisService;
import com.documentcategorization.service.DocumentService;
import com.documentcategorization.similarity.SimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SimilarityController {

    private final DocumentService documentService;
    private final SimilarityService similarityService;
    private final AnalysisService analysisService;
    private final GraphService graphService;

    @Autowired
    public SimilarityController(DocumentService documentService,
                                SimilarityService similarityService,
                                AnalysisService analysisService,
                                GraphService graphService) {
        this.documentService = documentService;
        this.similarityService = similarityService;
        this.analysisService = analysisService;
        this.graphService = graphService;
    }

    @GetMapping("/similarity")
    public String similarity(Model model) {
        List<Document> docs = documentService.getAllDocuments();
        boolean hasDocs = !docs.isEmpty();
        boolean analysisReady = analysisService.getStatus() == AnalysisService.AnalysisStatus.COMPLETED;
        double threshold = graphService.getCurrentThreshold();

        List<Map<String, Object>> matrixRows = buildMatrixRows(docs, threshold);
        List<Map<String, Object>> pairwiseList = buildPairwiseList(docs, threshold);

        model.addAttribute("documents", docs);
        model.addAttribute("hasDocs", hasDocs);
        model.addAttribute("analysisReady", analysisReady);
        model.addAttribute("threshold", threshold);
        model.addAttribute("matrixRows", matrixRows);
        model.addAttribute("pairwiseList", pairwiseList);
        model.addAttribute("similarityService", similarityService);
        return "similarity";
    }

    @GetMapping("/api/similarity/matrix")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSimilarityMatrixJson() {
        List<Document> docs = documentService.getAllDocuments();
        double threshold = graphService.getCurrentThreshold();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("documentCount", docs.size());
        payload.put("analysisCompleted", analysisService.getStatus() == AnalysisService.AnalysisStatus.COMPLETED);
        payload.put("threshold", threshold);
        payload.put("documents", docs.stream().map(Document::getOriginalFilename).collect(Collectors.toList()));
        payload.put("matrix", buildMatrixRows(docs, threshold));
        payload.put("pairs", buildPairwiseList(docs, threshold));
        return ResponseEntity.ok(payload);
    }

    private List<Map<String, Object>> buildMatrixRows(List<Document> docs, double threshold) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Document rowDoc : docs) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("docId", rowDoc.getId());
            row.put("filename", rowDoc.getOriginalFilename());
            int featCount = rowDoc.getOptimizedFeatures() != null ? rowDoc.getOptimizedFeatures().size() : 0;
            row.put("featureCount", featCount);

            Set<String> s1 = getFeatureTermSet(rowDoc);
            List<Map<String, Object>> cells = new ArrayList<>();

            for (Document colDoc : docs) {
                Set<String> s2 = getFeatureTermSet(colDoc);
                Set<String> inter = new HashSet<>(s1);
                inter.retainAll(s2);
                Set<String> union = new HashSet<>(s1);
                union.addAll(s2);

                double score = similarityService.calculateJaccardSimilarity(rowDoc, colDoc);
                boolean isDiagonal = rowDoc.getId().equals(colDoc.getId());
                boolean meetsThreshold = !isDiagonal && score >= threshold && score > 0.0;

                Map<String, Object> cell = new LinkedHashMap<>();
                cell.put("rowDoc", rowDoc.getOriginalFilename());
                cell.put("colDoc", colDoc.getOriginalFilename());
                cell.put("rowId", rowDoc.getId());
                cell.put("colId", colDoc.getId());
                cell.put("diagonal", isDiagonal);
                cell.put("score", score);
                cell.put("formattedScore", String.format(Locale.US, "%.2f", score));
                cell.put("exactScore", String.format(Locale.US, "%.4f", score));
                cell.put("sharedCount", inter.size());
                cell.put("unionCount", union.size());
                cell.put("meetsThreshold", meetsThreshold);
                cell.put("heatClass", resolveHeatClass(score, isDiagonal));
                cells.add(cell);
            }
            row.put("cells", cells);
            rows.add(row);
        }
        return rows;
    }

    private List<Map<String, Object>> buildPairwiseList(List<Document> docs, double threshold) {
        List<Map<String, Object>> pairs = new ArrayList<>();
        for (int i = 0; i < docs.size(); i++) {
            Document d1 = docs.get(i);
            Set<String> s1 = getFeatureTermSet(d1);
            for (int j = i + 1; j < docs.size(); j++) {
                Document d2 = docs.get(j);
                Set<String> s2 = getFeatureTermSet(d2);
                Set<String> inter = new HashSet<>(s1);
                inter.retainAll(s2);
                Set<String> union = new HashSet<>(s1);
                union.addAll(s2);

                double score = similarityService.calculateJaccardSimilarity(d1, d2);
                List<String> sharedTerms = new ArrayList<>(inter);
                Collections.sort(sharedTerms);

                Map<String, Object> pair = new LinkedHashMap<>();
                pair.put("docAId", d1.getId());
                pair.put("docBId", d2.getId());
                pair.put("docA", d1.getOriginalFilename());
                pair.put("docB", d2.getOriginalFilename());
                pair.put("sharedCount", inter.size());
                pair.put("unionCount", union.size());
                pair.put("sharedTerms", sharedTerms);
                pair.put("score", score);
                pair.put("formattedScore", String.format(Locale.US, "%.2f", score));
                pair.put("exactScore", String.format(Locale.US, "%.4f", score));
                pair.put("meetsThreshold", score >= threshold && score > 0.0);
                pairs.add(pair);
            }
        }
        return pairs;
    }

    private Set<String> getFeatureTermSet(Document doc) {
        if (doc == null || doc.getOptimizedFeatures() == null) return Collections.emptySet();
        return doc.getOptimizedFeatures().stream().map(TermFrequency::getTerm).collect(Collectors.toSet());
    }

    private String resolveHeatClass(double score, boolean isDiagonal) {
        if (isDiagonal && score >= 0.9999) return "heat-diag";
        if (score <= 0.0) return "heat-zero";
        if (score < 0.25) return "heat-low";
        if (score < 0.50) return "heat-mod";
        if (score < 0.75) return "heat-high";
        if (score < 1.00) return "heat-vhigh";
        return "heat-max";
    }
}
