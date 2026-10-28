package com.documentcategorization.service;

import com.documentcategorization.algorithms.StringAlgorithmsService;
import com.documentcategorization.features.FeatureOptimizationService;
import com.documentcategorization.graph.DocumentGraph;
import com.documentcategorization.model.*;
import com.documentcategorization.similarity.SimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WalkthroughService {

    private final DocumentService documentService;
    private final AnalysisService analysisService;
    private final FeatureOptimizationService featureOptimizationService;
    private final StringAlgorithmsService stringAlgorithmsService;
    private final SimilarityService similarityService;

    @Autowired
    public WalkthroughService(DocumentService documentService,
                              AnalysisService analysisService,
                              FeatureOptimizationService featureOptimizationService,
                              StringAlgorithmsService stringAlgorithmsService,
                              SimilarityService similarityService) {
        this.documentService = documentService;
        this.analysisService = analysisService;
        this.featureOptimizationService = featureOptimizationService;
        this.stringAlgorithmsService = stringAlgorithmsService;
        this.similarityService = similarityService;
    }

    public Map<String, String> getSupportedAlgorithms() {
        Map<String, String> algos = new LinkedHashMap<>();
        algos.put("mergesort", "1. Custom Merge Sort — Feature Optimization");
        algos.put("naive", "2. Naive String Matching");
        algos.put("kmp", "3. KMP String Matching");
        algos.put("z", "4. Z Algorithm String Matching");
        algos.put("rabinkarp", "5. Rabin-Karp String Matching");
        algos.put("ahocorasick", "6. Aho-Corasick String Matching");
        algos.put("jaccard", "7. Jaccard Similarity");
        algos.put("bfs", "8. BFS Graph Traversal");
        algos.put("dfs", "9. DFS Graph Traversal");
        algos.put("components", "10. Connected Components");
        return algos;
    }

    public WalkthroughResult executeWalkthrough(String algorithm, String docId, String docIdB, String pattern) {
        if (algorithm == null || algorithm.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select an algorithm to run the walkthrough.");
        }

        List<Document> allDocs = documentService.getAllDocuments();
        if (allDocs.isEmpty()) {
            throw new IllegalArgumentException("No documents are currently uploaded. Please upload at least one document on the Dashboard first.");
        }

        String algoKey = algorithm.trim().toLowerCase(Locale.ROOT);
        switch (algoKey) {
            case "mergesort":
                return runMergeSortWalkthrough(docId);
            case "naive":
                return runStringWalkthrough("naive", docId, pattern);
            case "kmp":
                return runStringWalkthrough("kmp", docId, pattern);
            case "z":
                return runStringWalkthrough("z", docId, pattern);
            case "rabinkarp":
                return runStringWalkthrough("rabinkarp", docId, pattern);
            case "ahocorasick":
                return runStringWalkthrough("ahocorasick", docId, pattern);
            case "jaccard":
                return runJaccardWalkthrough(docId, docIdB);
            case "bfs":
                return runBfsWalkthrough(docId);
            case "dfs":
                return runDfsWalkthrough(docId);
            case "components":
                return runConnectedComponentsWalkthrough();
            default:
                throw new IllegalArgumentException("Unsupported algorithm selected: " + algorithm);
        }
    }

    private void requireAnalysisCompleted() {
        if (analysisService.getStatus() != AnalysisService.AnalysisStatus.COMPLETED || analysisService.getGraph() == null) {
            throw new IllegalArgumentException("Full Analysis has not been run for the current documents. Please run 'Run Full Analysis' on the Dashboard first.");
        }
    }

    private Document requireDocument(String docId, String label) {
        if (docId == null || docId.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a valid " + label + ".");
        }
        return documentService.getDocumentById(docId.trim())
                .orElseThrow(() -> new IllegalArgumentException("The selected " + label + " was not found. Please select an existing uploaded document."));
    }

    private WalkthroughResult runMergeSortWalkthrough(String docId) {
        requireAnalysisCompleted();
        Document doc = requireDocument(docId, "document");
        if (doc.getOptimizedFeatures() == null || doc.getOptimizedFeatures().isEmpty()) {
            throw new IllegalArgumentException("Selected document '" + doc.getOriginalFilename() + "' has no extracted features to sort.");
        }

        List<WalkthroughStep> steps = new ArrayList<>();
        List<TermFrequency> sorted = featureOptimizationService.traceMergeSort(doc, steps);
        List<String> formattedFinal = FeatureOptimizationService.formatFeatureList(sorted);

        WalkthroughResult result = new WalkthroughResult();
        result.setAlgorithmKey("mergesort");
        result.setAlgorithmName("Custom Merge Sort — Feature Optimization");
        result.setCategory("Feature Optimization");
        result.setInputSummary("Document: " + doc.getOriginalFilename() + " (" + sorted.size() + " feature terms)");
        result.setFinalResultSummary("Top " + sorted.size() + " Sorted Features: " + String.join(", ", formattedFinal));
        result.setFinalResultData(formattedFinal);
        result.setTimeComplexity("O(U log U)");
        result.setSpaceComplexity("O(U)");
        result.setComplexityNote("Theoretical complexity where U is the number of unique extracted terms sorted. Divide-and-conquer recurrence T(U) = 2T(U/2) + O(U) guarantees O(U log U) comparisons in best, average, and worst cases, with O(U) auxiliary space for temporary merge subarrays. (Theoretical asymptotic complexity, not measured wall-clock time.)");
        result.setSteps(steps);
        return result;
    }

    private WalkthroughResult runStringWalkthrough(String algoKey, String docId, String pattern) {
        Document doc = requireDocument(docId, "document");
        if (doc.getRawText() == null || doc.getRawText().trim().isEmpty()) {
            throw new IllegalArgumentException("Selected document '" + doc.getOriginalFilename() + "' has empty extracted text.");
        }
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a non-empty search pattern for the string-matching walkthrough.");
        }

        String text = doc.getRawText();
        String cleanPattern = pattern.trim();
        List<WalkthroughStep> steps = new ArrayList<>();
        StringSearchResult searchRes;

        WalkthroughResult result = new WalkthroughResult();
        result.setAlgorithmKey(algoKey);
        result.setCategory("String Matching");

        if ("naive".equals(algoKey)) {
            searchRes = stringAlgorithmsService.naiveSearch(text, cleanPattern, steps);
            result.setAlgorithmName("Naive String Matching");
            result.setTimeComplexity("O((N - M + 1) * M) worst-case, O(N) best-case");
            result.setSpaceComplexity("O(1) auxiliary space");
            result.setComplexityNote("Theoretical complexity where N = text length (" + text.length() + ") and M = pattern length (" + cleanPattern.length() + "). Checks each of the (N - M + 1) alignments using up to M character comparisons with O(1) extra working memory. (Theoretical complexity, distinct from runtime clock measurements.)");
        } else if ("kmp".equals(algoKey)) {
            searchRes = stringAlgorithmsService.kmpSearch(text, cleanPattern, steps);
            result.setAlgorithmName("Knuth-Morris-Pratt (KMP) String Matching");
            result.setTimeComplexity("O(N + M)");
            result.setSpaceComplexity("O(M)");
            result.setComplexityNote("Theoretical complexity where N = text length (" + text.length() + ") and M = pattern length (" + cleanPattern.length() + "). Precomputes the LPS table of size M in O(M) time and scans the text in O(N) time without backtracking the text pointer. Auxiliary space is O(M) for the int[M] LPS table.");
        } else if ("z".equals(algoKey)) {
            searchRes = stringAlgorithmsService.zAlgorithmSearch(text, cleanPattern, steps);
            result.setAlgorithmName("Z Algorithm String Matching");
            result.setTimeComplexity("O(N + M)");
            result.setSpaceComplexity("O(N + M)");
            result.setComplexityNote("Theoretical complexity where N = text length (" + text.length() + ") and M = pattern length (" + cleanPattern.length() + "). Constructs concatenated string P + '$' + T of length N + M + 1 and maintains Z-box boundaries [L, R] to compute Z-values in linear O(N + M) time and O(N + M) auxiliary space.");
        } else if ("rabinkarp".equals(algoKey)) {
            searchRes = stringAlgorithmsService.rabinKarpSearch(text, cleanPattern, steps);
            result.setAlgorithmName("Rabin-Karp String Matching");
            result.setTimeComplexity("O(N + M) average-case, O((N - M + 1) * M) worst-case");
            result.setSpaceComplexity("O(1) auxiliary space");
            result.setComplexityNote("Theoretical complexity where N = text length (" + text.length() + ") and M = pattern length (" + cleanPattern.length() + "). Uses polynomial rolling hash (radix d = 256, prime modulus q = 101) to update window hashes in O(1) time and only performs O(M) character verification when window hash equals pattern hash.");
        } else {
            List<String> patterns = Arrays.stream(cleanPattern.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            if (patterns.isEmpty()) {
                throw new IllegalArgumentException("Please enter at least one non-empty pattern for Aho-Corasick.");
            }
            searchRes = stringAlgorithmsService.ahoCorasickSearch(text, patterns, steps);
            result.setAlgorithmName("Aho-Corasick Multi-Pattern String Matching");
            result.setTimeComplexity("O(N + M + Z)");
            result.setSpaceComplexity("O(M * Σ)");
            result.setComplexityNote("Theoretical complexity where N = text length (" + text.length() + "), M = sum of pattern lengths, Z = total pattern occurrences found (" + searchRes.getPositions().size() + "), and Σ = 256 (ASCII alphabet size in ACTrieNode). Builds trie and BFS failure links in O(M * Σ) and scans text in a single pass.");
        }

        result.setInputSummary("Document: " + doc.getOriginalFilename() + " (N = " + text.length() + " chars) | Pattern: \"" + cleanPattern + "\"");
        List<Integer> allPositions = searchRes.getPositions() != null ? searchRes.getPositions() : Collections.emptyList();
        List<Integer> displayPositions = allPositions.size() > 50
                ? new ArrayList<>(allPositions.subList(0, 50))
                : allPositions;
        String posText = allPositions.size() > 50
                ? displayPositions + " ... (" + allPositions.size() + " total)"
                : allPositions.toString();
        if (searchRes.isFound()) {
            result.setFinalResultSummary("Match Found (" + allPositions.size() + " occurrence(s)) at index positions: " + posText + " | Comparisons: " + searchRes.getComparisons());
        } else {
            result.setFinalResultSummary("No matches found (Positions: []) | Comparisons: " + searchRes.getComparisons());
        }
        result.setFinalResultData(displayPositions);
        result.setSteps(steps);
        return result;
    }

    private WalkthroughResult runJaccardWalkthrough(String docIdA, String docIdB) {
        requireAnalysisCompleted();
        Document docA = requireDocument(docIdA, "Document A");
        Document docB = requireDocument(docIdB, "Document B");

        List<WalkthroughStep> steps = new ArrayList<>();
        double score = similarityService.calculateJaccardSimilarity(docA, docB, steps);

        WalkthroughResult result = new WalkthroughResult();
        result.setAlgorithmKey("jaccard");
        result.setAlgorithmName("Jaccard Similarity Calculation");
        result.setCategory("Similarity Calculation");
        result.setInputSummary("Document A: " + docA.getOriginalFilename() + " vs Document B: " + docB.getOriginalFilename());
        result.setFinalResultSummary(String.format(Locale.US, "J(%s, %s) = %.4f (Matrix Display: %.2f)",
                docA.getOriginalFilename(), docB.getOriginalFilename(), score, score));
        result.setFinalResultData(score);
        result.setTimeComplexity("O(|A| + |B|)");
        result.setSpaceComplexity("O(|A| + |B|)");
        result.setComplexityNote("Theoretical complexity where |A| and |B| are the sizes of the optimized feature sets of Document A and Document B (each bounded by MAX_FEATURES = 15). Uses HashSet retainAll (intersection) and addAll (union) in O(|A| + |B|) average time and space.");
        result.setSteps(steps);
        return result;
    }

    private WalkthroughResult runBfsWalkthrough(String docId) {
        requireAnalysisCompleted();
        Document startDoc = requireDocument(docId, "starting document");
        DocumentGraph graph = analysisService.getGraph();
        if (!graph.getAdjacencyList().containsKey(startDoc)) {
            throw new IllegalArgumentException("Selected document is not present in the current analysis graph. Please re-run Full Analysis.");
        }

        List<WalkthroughStep> steps = new ArrayList<>();
        List<Document> traversal = graph.bfsTraversal(startDoc, steps);
        List<String> names = traversal.stream().map(Document::getOriginalFilename).collect(Collectors.toList());

        WalkthroughResult result = new WalkthroughResult();
        result.setAlgorithmKey("bfs");
        result.setAlgorithmName("Breadth-First Search (BFS) Graph Traversal");
        result.setCategory("Graph Traversal");
        result.setInputSummary("Starting Document: " + startDoc.getOriginalFilename() + " | Graph: " + graph.getAdjacencyList().size() + " vertices, " + graph.getEdgeCount() + " edges");
        result.setFinalResultSummary("BFS Traversal Order (" + names.size() + " visited): " + String.join(" -> ", names));
        result.setFinalResultData(names);
        result.setTimeComplexity("O(V + E)");
        result.setSpaceComplexity("O(V)");
        result.setComplexityNote("Theoretical complexity where V is the number of document vertices and E is the number of similarity edges in the reachable component. Each reachable vertex is enqueued/dequeued at most once and each adjacency list edge is inspected in O(V + E) time with O(V) auxiliary space for the FIFO Queue and Visited Set.");
        result.setSteps(steps);
        return result;
    }

    private WalkthroughResult runDfsWalkthrough(String docId) {
        requireAnalysisCompleted();
        Document startDoc = requireDocument(docId, "starting document");
        DocumentGraph graph = analysisService.getGraph();
        if (!graph.getAdjacencyList().containsKey(startDoc)) {
            throw new IllegalArgumentException("Selected document is not present in the current analysis graph. Please re-run Full Analysis.");
        }

        List<WalkthroughStep> steps = new ArrayList<>();
        List<Document> traversal = graph.dfsTraversal(startDoc, steps);
        List<String> names = traversal.stream().map(Document::getOriginalFilename).collect(Collectors.toList());

        WalkthroughResult result = new WalkthroughResult();
        result.setAlgorithmKey("dfs");
        result.setAlgorithmName("Depth-First Search (DFS) Graph Traversal");
        result.setCategory("Graph Traversal");
        result.setInputSummary("Starting Document: " + startDoc.getOriginalFilename() + " | Graph: " + graph.getAdjacencyList().size() + " vertices, " + graph.getEdgeCount() + " edges");
        result.setFinalResultSummary("DFS Traversal Order (" + names.size() + " visited): " + String.join(" -> ", names));
        result.setFinalResultData(names);
        result.setTimeComplexity("O(V + E)");
        result.setSpaceComplexity("O(V)");
        result.setComplexityNote("Theoretical complexity where V is the number of document vertices and E is the number of similarity edges in the reachable component. Uses recursive dfsHelper calls (call stack depth <= V) and a Visited Set of size O(V).");
        result.setSteps(steps);
        return result;
    }

    private WalkthroughResult runConnectedComponentsWalkthrough() {
        requireAnalysisCompleted();
        DocumentGraph graph = analysisService.getGraph();

        List<WalkthroughStep> steps = new ArrayList<>();
        List<List<Document>> components = graph.findConnectedComponents(steps);

        List<String> compSummaries = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            List<String> docNames = components.get(i).stream().map(Document::getOriginalFilename).collect(Collectors.toList());
            compSummaries.add("Component #" + (i + 1) + ": " + docNames);
        }

        WalkthroughResult result = new WalkthroughResult();
        result.setAlgorithmKey("components");
        result.setAlgorithmName("Connected Components Discovery");
        result.setCategory("Graph Clustering");
        result.setInputSummary("Backend Document Graph (" + graph.getAdjacencyList().size() + " vertices, " + graph.getEdgeCount() + " undirected similarity edges)");
        result.setFinalResultSummary("Discovered " + components.size() + " Connected Component(s): " + String.join(" | ", compSummaries));
        result.setFinalResultData(compSummaries);
        result.setTimeComplexity("O(V + E)");
        result.setSpaceComplexity("O(V)");
        result.setComplexityNote("Theoretical complexity where V is total document vertices (" + graph.getAdjacencyList().size() + ") and E is total undirected similarity edges (" + graph.getEdgeCount() + "). Iterates over all vertices and runs BFS from each unvisited vertex; every vertex and edge is processed a constant number of times in O(V + E) total time and O(V) auxiliary space.");
        result.setSteps(steps);
        return result;
    }
}
