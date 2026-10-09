package com.documentcategorization.service;

import com.documentcategorization.categorization.CategoryService;
import com.documentcategorization.features.FeatureExtractionService;
import com.documentcategorization.features.FeatureOptimizationService;
import com.documentcategorization.graph.DocumentGraph;
import com.documentcategorization.graph.GraphService;
import com.documentcategorization.model.CategoryResult;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.PipelineStage;
import com.documentcategorization.preprocessing.PreprocessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalysisService {
    public enum AnalysisStatus {
        NOT_STARTED, RUNNING, COMPLETED, FAILED
    }

    private AnalysisStatus status = AnalysisStatus.NOT_STARTED;
    private DocumentGraph currentGraph = null;
    private List<CategoryResult> currentCategories = new ArrayList<>();
    private String errorMessage = null;

    private final DocumentService documentService;
    private final PreprocessingService preprocessingService;
    private final FeatureExtractionService featureExtractionService;
    private final FeatureOptimizationService featureOptimizationService;
    private final GraphService graphService;
    private final CategoryService categoryService;

    @Autowired
    public AnalysisService(DocumentService documentService, PreprocessingService preprocessingService, FeatureExtractionService featureExtractionService, FeatureOptimizationService featureOptimizationService, GraphService graphService, CategoryService categoryService) {
        this.documentService = documentService;
        this.preprocessingService = preprocessingService;
        this.featureExtractionService = featureExtractionService;
        this.featureOptimizationService = featureOptimizationService;
        this.graphService = graphService;
        this.categoryService = categoryService;
    }

    public void runFullAnalysis() {
        List<Document> docs = documentService.getAllDocuments();
        if (docs.isEmpty()) {
            return; // Or throw error
        }
        
        try {
            status = AnalysisStatus.RUNNING;
            errorMessage = null;

            // 1. Process documents
            for (Document doc : docs) {
                if (doc.getCurrentStage() == PipelineStage.UPLOADED) {
                    preprocessingService.preprocess(doc); 
                    doc.setCurrentStage(PipelineStage.PREPROCESSED);
                    
                    featureExtractionService.extractFeatures(doc); 
                    doc.setCurrentStage(PipelineStage.FEATURES_EXTRACTED);
                    
                    featureOptimizationService.optimizeFeatures(doc); 
                    doc.setCurrentStage(PipelineStage.FEATURES_OPTIMIZED);
                }
            }

            // 2. Graph and Similarity
            currentGraph = graphService.buildGraph(docs, graphService.getCurrentThreshold());
            for (Document doc : docs) {
                doc.setCurrentStage(PipelineStage.GRAPH_BUILT);
            }

            // 3. BFS/DFS & Categorization
            currentCategories = categoryService.identifyCategories(currentGraph.findConnectedComponents());
            for (Document doc : docs) {
                doc.setCurrentStage(PipelineStage.CATEGORIZED);
            }

            status = AnalysisStatus.COMPLETED;
        } catch (Exception e) {
            status = AnalysisStatus.FAILED;
            errorMessage = e.getMessage();
        }
    }

    public void reset() {
        status = AnalysisStatus.NOT_STARTED;
        currentGraph = null;
        currentCategories = new ArrayList<>();
        errorMessage = null;
    }

    public AnalysisStatus getStatus() { return status; }
    public DocumentGraph getGraph() { return currentGraph; }
    public List<CategoryResult> getCategories() { return currentCategories; }
    public String getErrorMessage() { return errorMessage; }
}
