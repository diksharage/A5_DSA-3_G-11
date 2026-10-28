package com.documentcategorization.controller;

import com.documentcategorization.graph.DocumentGraph;
import com.documentcategorization.graph.GraphService;
import com.documentcategorization.model.CategoryResult;
import com.documentcategorization.model.Document;
import com.documentcategorization.model.TermFrequency;
import com.documentcategorization.service.AnalysisService;
import com.documentcategorization.service.DocumentService;
import com.documentcategorization.similarity.SimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ResultsController {

    private final AnalysisService analysisService;
    private final GraphService graphService;
    private final DocumentService documentService;
    private final SimilarityService similarityService;

    private static class PairMetrics {
        final double score;
        final int intersectionSize;
        final int unionSize;
        final List<String> sharedTerms;

        PairMetrics(double score, int intersectionSize, int unionSize, List<String> sharedTerms) {
            this.score = score;
            this.intersectionSize = intersectionSize;
            this.unionSize = unionSize;
            this.sharedTerms = sharedTerms;
        }
    }

    @Autowired
    public ResultsController(AnalysisService analysisService,
                             GraphService graphService,
                             DocumentService documentService,
                             SimilarityService similarityService) {
        this.analysisService = analysisService;
        this.graphService = graphService;
        this.documentService = documentService;
        this.similarityService = similarityService;
    }

    @GetMapping("/results")
    public String results(Model model) {
        List<Document> allDocs = documentService.getAllDocuments();
        List<CategoryResult> categories = analysisService.getCategories() != null
                ? analysisService.getCategories()
                : Collections.emptyList();
        DocumentGraph graph = analysisService.getGraph();
        double threshold = graphService.getCurrentThreshold();
        boolean analysisCompleted = analysisService.getStatus() == AnalysisService.AnalysisStatus.COMPLETED && graph != null;

        int edgeCount = graph != null ? graph.getEdgeCount() : 0;

        List<Map<String, Object>> groupExplanations = buildGroupExplanations(categories, graph, allDocs, threshold);
        List<Map<String, Object>> documentExplanations = buildDocumentExplanations(allDocs, categories, graph, threshold);

        model.addAttribute("categories", categories);
        model.addAttribute("graph", graph);
        model.addAttribute("threshold", threshold);
        model.addAttribute("documentCount", allDocs.size());
        model.addAttribute("edgeCount", edgeCount);
        model.addAttribute("analysisCompleted", analysisCompleted);
        model.addAttribute("groupExplanations", groupExplanations);
        model.addAttribute("documentExplanations", documentExplanations);

        return "results";
    }

    @GetMapping(value = "/api/results/explanations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getResultsExplanationsApi() {
        List<Document> allDocs = documentService.getAllDocuments();
        List<CategoryResult> categories = analysisService.getCategories() != null
                ? analysisService.getCategories()
                : Collections.emptyList();
        DocumentGraph graph = analysisService.getGraph();
        double threshold = graphService.getCurrentThreshold();
        boolean analysisCompleted = analysisService.getStatus() == AnalysisService.AnalysisStatus.COMPLETED && graph != null;

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("documentCount", allDocs.size());
        response.put("groupCount", categories.size());
        response.put("edgeCount", graph != null ? graph.getEdgeCount() : 0);
        response.put("threshold", threshold);
        response.put("analysisCompleted", analysisCompleted);
        response.put("groups", buildGroupExplanations(categories, graph, allDocs, threshold));
        response.put("documents", buildDocumentExplanations(allDocs, categories, graph, threshold));

        return ResponseEntity.ok(response);
    }

    private PairMetrics computePairMetrics(Document docA, Document docB) {
        Set<String> s1 = getFeatureTermSet(docA);
        Set<String> s2 = getFeatureTermSet(docB);
        List<String> shared = new ArrayList<>();
        for (String term : s1) {
            if (s2.contains(term)) {
                shared.add(term);
            }
        }
        Set<String> union = new LinkedHashSet<>(s1);
        union.addAll(s2);
        double score = similarityService.calculateJaccardSimilarity(docA, docB);
        return new PairMetrics(score, shared.size(), union.size(), shared);
    }

    private Set<String> getFeatureTermSet(Document doc) {
        if (doc == null || doc.getOptimizedFeatures() == null) {
            return Collections.emptySet();
        }
        return doc.getOptimizedFeatures().stream()
                .map(TermFrequency::getTerm)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<Map<String, Object>> buildGroupExplanations(List<CategoryResult> categories,
                                                              DocumentGraph graph,
                                                              List<Document> allDocs,
                                                              double threshold) {
        List<Map<String, Object>> groupList = new ArrayList<>();
        if (categories == null || categories.isEmpty()) {
            return groupList;
        }

        int fallbackIdx = 1;
        for (CategoryResult cat : categories) {
            Map<String, Object> gMap = new LinkedHashMap<>();
            int compIdx = cat.getComponentIndex() > 0 ? cat.getComponentIndex() : fallbackIdx;
            fallbackIdx++;

            List<Document> compDocs = cat.getDocuments() != null ? cat.getDocuments() : Collections.emptyList();
            List<String> docNames = compDocs.stream().map(Document::getOriginalFilename).collect(Collectors.toList());

            gMap.put("groupId", cat.getGroupId());
            gMap.put("componentIndex", compIdx);
            gMap.put("categoryLabel", cat.getCategoryLabel());
            gMap.put("documentCount", compDocs.size());
            gMap.put("documentNames", docNames);

            List<Map<String, Object>> memberDocsInfo = new ArrayList<>();
            for (Document d : compDocs) {
                Map<String, Object> dInfo = new LinkedHashMap<>();
                dInfo.put("id", d.getId());
                dInfo.put("filename", d.getOriginalFilename());
                List<String> topFeatFormatted = new ArrayList<>();
                if (d.getOptimizedFeatures() != null) {
                    for (TermFrequency tf : d.getOptimizedFeatures()) {
                        topFeatFormatted.add(tf.getTerm() + " (" + tf.getFrequency() + ")");
                    }
                }
                dInfo.put("optimizedFeatureCount", d.getOptimizedFeatures() != null ? d.getOptimizedFeatures().size() : 0);
                dInfo.put("optimizedFeaturesFormatted", topFeatFormatted);
                memberDocsInfo.add(dInfo);
            }
            gMap.put("documents", memberDocsInfo);

            // Dominant & Aggregated features with per-document breakdown
            Set<String> domTermSet = new HashSet<>();
            List<Map<String, Object>> domFeaturesList = new ArrayList<>();
            Map<String, List<String>> contribMap = cat.getFeatureContributions() != null
                    ? cat.getFeatureContributions()
                    : Collections.emptyMap();

            if (cat.getDominantFeatures() != null) {
                int rank = 1;
                for (TermFrequency tf : cat.getDominantFeatures()) {
                    domTermSet.add(tf.getTerm());
                    Map<String, Object> dfMap = new LinkedHashMap<>();
                    String termStr = tf.getTerm() != null ? tf.getTerm() : "";
                    dfMap.put("rank", rank++);
                    dfMap.put("term", termStr);
                    dfMap.put("titleTerm", termStr.isEmpty() ? "" : termStr.substring(0, 1).toUpperCase() + termStr.substring(1));
                    dfMap.put("frequency", tf.getFrequency());
                    List<String> contribs = contribMap.getOrDefault(tf.getTerm(), Collections.emptyList());
                    dfMap.put("contributions", contribs);
                    dfMap.put("contributionsSummary", String.join(" + ", contribs));
                    domFeaturesList.add(dfMap);
                }
            }
            gMap.put("dominantFeatures", domFeaturesList);

            List<Map<String, Object>> rankedAggList = new ArrayList<>();
            List<TermFrequency> allAgg = cat.getAggregatedFeatures() != null ? cat.getAggregatedFeatures() : Collections.emptyList();
            int maxAggDisplay = Math.min(allAgg.size(), 10);
            for (int i = 0; i < maxAggDisplay; i++) {
                TermFrequency tf = allAgg.get(i);
                Map<String, Object> afMap = new LinkedHashMap<>();
                afMap.put("rank", i + 1);
                afMap.put("term", tf.getTerm());
                afMap.put("frequency", tf.getFrequency());
                afMap.put("selectedForLabel", domTermSet.contains(tf.getTerm()));
                List<String> contribs = contribMap.getOrDefault(tf.getTerm(), Collections.emptyList());
                afMap.put("contributions", contribs);
                afMap.put("contributionsSummary", String.join(" + ", contribs));
                rankedAggList.add(afMap);
            }
            gMap.put("rankedAggregatedFeatures", rankedAggList);
            gMap.put("totalUniqueAggregatedFeatures", allAgg.size());
            gMap.put("totalPooledFeatureEntries", cat.getTotalFeatureInstances());
            gMap.put("fallbackUsed", cat.isFallbackUsed());
            gMap.put("labelExplanation", cat.getLabelExplanation());
            gMap.put("reason", cat.getReason());

            // Internal similarity edges & transitive pairs within this component
            List<Map<String, Object>> internalEdges = new ArrayList<>();
            List<Map<String, Object>> transitivePairs = new ArrayList<>();

            for (int i = 0; i < compDocs.size(); i++) {
                for (int j = i + 1; j < compDocs.size(); j++) {
                    Document docA = compDocs.get(i);
                    Document docB = compDocs.get(j);
                    PairMetrics pm = computePairMetrics(docA, docB);
                    double score = pm.score;

                    boolean hasEdge = false;
                    if (graph != null && graph.getAdjacencyList().containsKey(docA)) {
                        hasEdge = graph.getAdjacencyList().get(docA).contains(docB);
                    }

                    if (hasEdge) {
                        Map<String, Object> edgeMap = new LinkedHashMap<>();
                        edgeMap.put("docAId", docA.getId());
                        edgeMap.put("docAName", docA.getOriginalFilename());
                        edgeMap.put("docBId", docB.getId());
                        edgeMap.put("docBName", docB.getOriginalFilename());
                        edgeMap.put("jaccard", Math.round(score * 1000.0) / 1000.0);
                        edgeMap.put("jaccardFormatted", String.format(Locale.US, "%.3f", score));
                        edgeMap.put("jaccardShort", String.format(Locale.US, "%.2f", score));
                        edgeMap.put("intersectionSize", pm.intersectionSize);
                        edgeMap.put("unionSize", pm.unionSize);
                        edgeMap.put("sharedTerms", pm.sharedTerms);
                        internalEdges.add(edgeMap);
                    } else if (compDocs.size() > 1) {
                        List<String> pathNames = findBfsShortestPath(graph, docA, docB);
                        Map<String, Object> transMap = new LinkedHashMap<>();
                        transMap.put("docAId", docA.getId());
                        transMap.put("docAName", docA.getOriginalFilename());
                        transMap.put("docBId", docB.getId());
                        transMap.put("docBName", docB.getOriginalFilename());
                        transMap.put("jaccardFormatted", String.format(Locale.US, "%.3f", score));
                        transMap.put("intersectionSize", pm.intersectionSize);
                        transMap.put("unionSize", pm.unionSize);
                        transMap.put("bfsPath", pathNames);
                        transMap.put("bfsPathFormatted", String.join(" -> ", pathNames));
                        transitivePairs.add(transMap);
                    }
                }
            }

            gMap.put("internalEdges", internalEdges);
            gMap.put("internalEdgeCount", internalEdges.size());
            gMap.put("transitivePairs", transitivePairs);
            gMap.put("hasTransitivePairs", !transitivePairs.isEmpty());
            gMap.put("isIsolated", compDocs.size() == 1);

            // Actual BFS discovery order from the component's first vertex
            List<String> bfsOrder = new ArrayList<>();
            if (graph != null && !compDocs.isEmpty()) {
                List<Document> visited = graph.bfsTraversal(compDocs.get(0));
                for (Document vd : visited) {
                    bfsOrder.add(vd.getOriginalFilename());
                }
            } else {
                bfsOrder.addAll(docNames);
            }
            gMap.put("bfsDiscoveryOrder", bfsOrder);
            gMap.put("bfsDiscoveryOrderFormatted", String.join(" -> ", bfsOrder));

            // Closest external document comparison
            Map<String, Object> closestExternal = findClosestExternalMatch(compDocs, allDocs);
            gMap.put("closestExternalMatch", closestExternal);

            // Detailed connectivity explanation
            String connectivityExplanation;
            if (compDocs.size() == 1) {
                Document singleDoc = compDocs.get(0);
                if (allDocs.size() == 1) {
                    connectivityExplanation = singleDoc.getOriginalFilename()
                            + " is the only uploaded document in the workspace (1 vertex, 0 edges). BFS visits this single vertex and forms Connected Component #"
                            + compIdx + ".";
                } else if (closestExternal != null) {
                    connectivityExplanation = singleDoc.getOriginalFilename()
                            + " has 0 graph edges because its highest Jaccard similarity with any other document is "
                            + closestExternal.get("jaccardFormatted") + " (with " + closestExternal.get("externalDocName")
                            + ", |A ∩ B| = " + closestExternal.get("intersectionSize") + ", |A ∪ B| = " + closestExternal.get("unionSize")
                            + "), which is strictly below the threshold " + threshold
                            + ". Therefore, BFS isolates it as Connected Component #" + compIdx + ".";
                } else {
                    connectivityExplanation = singleDoc.getOriginalFilename()
                            + " has 0 similarity edges meeting threshold " + threshold
                            + ", so BFS discovers it as an isolated 1-vertex connected component (Component #" + compIdx + ").";
                }
            } else if (transitivePairs.isEmpty()) {
                connectivityExplanation = "All " + compDocs.size() + " documents in Connected Component #" + compIdx
                        + " are directly connected to one another by " + internalEdges.size()
                        + " undirected graph edge(s) with Jaccard similarity >= " + threshold
                        + ". Starting BFS at " + compDocs.get(0).getOriginalFilename()
                        + " visits [" + String.join(" -> ", bfsOrder) + "], grouping them into " + cat.getGroupId() + ".";
            } else {
                connectivityExplanation = "These " + compDocs.size() + " documents are linked by " + internalEdges.size()
                        + " direct edge(s) with Jaccard similarity >= " + threshold + " and " + transitivePairs.size()
                        + " transitive path relationship(s). Because graph grouping is defined by connected components (reachability via any path of edges), BFS starting at "
                        + compDocs.get(0).getOriginalFilename() + " traverses [" + String.join(" -> ", bfsOrder)
                        + "] and assigns all reachable vertices to " + cat.getGroupId() + ".";
            }
            gMap.put("connectivityExplanation", connectivityExplanation);

            groupList.add(gMap);
        }

        return groupList;
    }

    private List<Map<String, Object>> buildDocumentExplanations(List<Document> allDocs,
                                                                 List<CategoryResult> categories,
                                                                 DocumentGraph graph,
                                                                 double threshold) {
        List<Map<String, Object>> docList = new ArrayList<>();
        if (allDocs == null || allDocs.isEmpty()) {
            return docList;
        }

        for (Document doc : allDocs) {
            Map<String, Object> dMap = new LinkedHashMap<>();
            dMap.put("docId", doc.getId());
            dMap.put("filename", doc.getOriginalFilename());
            dMap.put("fileType", doc.getFileType() != null ? doc.getFileType() : "Unknown");

            CategoryResult matchedCat = null;
            int compIdx = 0;
            if (categories != null) {
                int idx = 1;
                for (CategoryResult cr : categories) {
                    if (cr.getDocuments() != null && cr.getDocuments().contains(doc)) {
                        matchedCat = cr;
                        compIdx = cr.getComponentIndex() > 0 ? cr.getComponentIndex() : idx;
                        break;
                    }
                    idx++;
                }
            }

            String groupId = matchedCat != null ? matchedCat.getGroupId() : "Unassigned";
            String categoryLabel = matchedCat != null ? matchedCat.getCategoryLabel() : "Uncategorized (Run Analysis)";
            List<Document> compDocs = matchedCat != null && matchedCat.getDocuments() != null
                    ? matchedCat.getDocuments()
                    : Collections.singletonList(doc);

            Set<String> groupDomTerms = new HashSet<>();
            if (matchedCat != null && matchedCat.getDominantFeatures() != null) {
                for (TermFrequency tf : matchedCat.getDominantFeatures()) {
                    groupDomTerms.add(tf.getTerm());
                }
            }

            dMap.put("groupId", groupId);
            dMap.put("componentIndex", compIdx);
            dMap.put("categoryLabel", categoryLabel);
            dMap.put("componentSize", compDocs.size());

            // Document's Top 15 optimized features + indicator if feature is one of the group's dominant features
            List<Map<String, Object>> optFeatures = new ArrayList<>();
            List<String> matchedDominantTerms = new ArrayList<>();
            if (doc.getOptimizedFeatures() != null) {
                int rank = 1;
                for (TermFrequency tf : doc.getOptimizedFeatures()) {
                    Map<String, Object> fMap = new LinkedHashMap<>();
                    fMap.put("rank", rank++);
                    fMap.put("term", tf.getTerm());
                    fMap.put("frequency", tf.getFrequency());
                    boolean isDom = groupDomTerms.contains(tf.getTerm());
                    fMap.put("isGroupDominant", isDom);
                    if (isDom) {
                        matchedDominantTerms.add(tf.getTerm() + " (" + tf.getFrequency() + ")");
                    }
                    optFeatures.add(fMap);
                }
            }
            dMap.put("optimizedFeatures", optFeatures);
            dMap.put("optimizedFeatureCount", optFeatures.size());
            dMap.put("matchedDominantTerms", matchedDominantTerms);

            // Direct neighbors (graph edges >= threshold)
            List<Map<String, Object>> directNeighbors = new ArrayList<>();
            Set<Document> neighborSet = new HashSet<>();
            if (graph != null && graph.getAdjacencyList().containsKey(doc)) {
                for (Document nbr : graph.getAdjacencyList().get(doc)) {
                    neighborSet.add(nbr);
                    PairMetrics pm = computePairMetrics(doc, nbr);
                    double score = pm.score;

                    Map<String, Object> nMap = new LinkedHashMap<>();
                    nMap.put("neighborId", nbr.getId());
                    nMap.put("neighborName", nbr.getOriginalFilename());
                    nMap.put("jaccard", Math.round(score * 1000.0) / 1000.0);
                    nMap.put("jaccardFormatted", String.format(Locale.US, "%.3f", score));
                    nMap.put("jaccardShort", String.format(Locale.US, "%.2f", score));
                    nMap.put("intersectionSize", pm.intersectionSize);
                    nMap.put("unionSize", pm.unionSize);
                    nMap.put("sharedTerms", pm.sharedTerms);
                    nMap.put("sharedTermsSummary", pm.sharedTerms.isEmpty() ? "none" : String.join(", ", pm.sharedTerms));
                    directNeighbors.add(nMap);
                }
            }
            dMap.put("directNeighbors", directNeighbors);
            dMap.put("directNeighborCount", directNeighbors.size());

            // Transitive peers in the same component (not directly connected)
            List<Map<String, Object>> transitivePeers = new ArrayList<>();
            for (Document peer : compDocs) {
                if (!peer.equals(doc) && !neighborSet.contains(peer)) {
                    PairMetrics pm = computePairMetrics(doc, peer);
                    List<String> bfsPath = findBfsShortestPath(graph, doc, peer);
                    Map<String, Object> pMap = new LinkedHashMap<>();
                    pMap.put("peerId", peer.getId());
                    pMap.put("peerName", peer.getOriginalFilename());
                    pMap.put("jaccardFormatted", String.format(Locale.US, "%.3f", pm.score));
                    pMap.put("intersectionSize", pm.intersectionSize);
                    pMap.put("unionSize", pm.unionSize);
                    pMap.put("bfsPath", bfsPath);
                    pMap.put("bfsPathFormatted", String.join(" -> ", bfsPath));
                    transitivePeers.add(pMap);
                }
            }
            dMap.put("transitivePeers", transitivePeers);
            dMap.put("hasTransitivePeers", !transitivePeers.isEmpty());

            // Closest non-neighbor across all other documents
            Map<String, Object> closestNonNeighbor = findClosestNonNeighborForDoc(doc, allDocs, neighborSet);
            dMap.put("closestNonNeighbor", closestNonNeighbor);

            // Membership type and detailed explanation
            String membershipType;
            String membershipBadge;
            String membershipExplanation;

            if (matchedCat == null) {
                membershipType = "UNANALYZED";
                membershipBadge = "Pending Analysis";
                membershipExplanation = "Document has been uploaded but full DSA analysis has not been executed yet.";
            } else if (compDocs.size() == 1) {
                membershipType = "ISOLATED";
                membershipBadge = "Isolated Component (1 Doc)";
                if (allDocs.size() == 1) {
                    membershipExplanation = doc.getOriginalFilename() + " belongs to " + groupId + " (" + categoryLabel
                            + ") as the sole uploaded document in the graph. Its category label is derived directly from its own top optimized features.";
                } else if (closestNonNeighbor != null) {
                    membershipExplanation = doc.getOriginalFilename() + " forms an isolated 1-document connected component (" + groupId
                            + ") because none of its pairwise Jaccard similarity scores reached the threshold (" + threshold
                            + "). Its highest similarity with any other document was " + closestNonNeighbor.get("jaccardFormatted")
                            + " with " + closestNonNeighbor.get("docName") + " (|A ∩ B| = " + closestNonNeighbor.get("intersectionSize")
                            + ", |A ∪ B| = " + closestNonNeighbor.get("unionSize") + " < " + threshold
                            + "). Consequently, it has 0 graph edges and receives the label '" + categoryLabel + "' from its own dominant features.";
                } else {
                    membershipExplanation = doc.getOriginalFilename() + " forms an isolated connected component (" + groupId
                            + ") because its Jaccard similarity with all other documents is 0.000 (< " + threshold
                            + "). With 0 graph edges, BFS places it in its own group labeled '" + categoryLabel + "'.";
                }
            } else if (transitivePeers.isEmpty()) {
                membershipType = "DIRECT_CLUSTER";
                membershipBadge = "Direct Graph Edges (" + directNeighbors.size() + ")";
                String nbrSummary = directNeighbors.stream()
                        .map(n -> n.get("neighborName") + " (J = " + n.get("jaccardFormatted")
                                + ", shared: [" + n.get("sharedTermsSummary") + "])")
                        .collect(Collectors.joining("; "));
                membershipExplanation = doc.getOriginalFilename() + " belongs to " + groupId + " (" + categoryLabel
                        + ") because it has " + directNeighbors.size() + " direct graph edge(s) meeting threshold >= " + threshold
                        + ": " + nbrSummary + ". BFS traversal starting in Connected Component #" + compIdx
                        + " visits all connected vertices and pools their top features to generate '" + categoryLabel + "'.";
            } else {
                membershipType = "DIRECT_AND_TRANSITIVE";
                membershipBadge = "Direct (" + directNeighbors.size() + ") + Transitive (" + transitivePeers.size() + ")";
                String nbrSummary = directNeighbors.stream()
                        .map(n -> n.get("neighborName") + " (J = " + n.get("jaccardFormatted") + ")")
                        .collect(Collectors.joining(", "));
                String transSummary = transitivePeers.stream()
                        .map(p -> p.get("peerName") + " (direct J = " + p.get("jaccardFormatted") + " < " + threshold
                                + ", connected via path: " + p.get("bfsPathFormatted") + ")")
                        .collect(Collectors.joining("; "));
                membershipExplanation = doc.getOriginalFilename() + " belongs to " + groupId + " (" + categoryLabel
                        + ") via direct edge(s) to " + nbrSummary + " and transitive graph connectivity to "
                        + transSummary + ". Because connected components group all vertices reachable along any chain of valid edges, BFS places all "
                        + compDocs.size() + " documents into the same category.";
            }

            dMap.put("membershipType", membershipType);
            dMap.put("membershipBadge", membershipBadge);
            dMap.put("membershipExplanation", membershipExplanation);

            docList.add(dMap);
        }

        return docList;
    }

    private List<String> findBfsShortestPath(DocumentGraph graph, Document start, Document target) {
        if (graph == null || start == null || target == null) {
            return Collections.emptyList();
        }
        if (start.equals(target)) {
            return Collections.singletonList(start.getOriginalFilename());
        }
        Map<Document, List<Document>> adj = graph.getAdjacencyList();
        if (!adj.containsKey(start) || !adj.containsKey(target)) {
            return Collections.emptyList();
        }

        Queue<Document> queue = new LinkedList<>();
        Map<Document, Document> parentMap = new HashMap<>();
        Set<Document> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        boolean found = false;
        while (!queue.isEmpty()) {
            Document curr = queue.poll();
            if (curr.equals(target)) {
                found = true;
                break;
            }
            for (Document nbr : adj.getOrDefault(curr, Collections.emptyList())) {
                if (!visited.contains(nbr)) {
                    visited.add(nbr);
                    parentMap.put(nbr, curr);
                    queue.add(nbr);
                }
            }
        }

        if (!found) {
            return Collections.emptyList();
        }

        LinkedList<String> path = new LinkedList<>();
        Document step = target;
        while (step != null) {
            path.addFirst(step.getOriginalFilename());
            step = parentMap.get(step);
        }
        return path;
    }

    private Map<String, Object> findClosestExternalMatch(List<Document> compDocs, List<Document> allDocs) {
        if (compDocs == null || allDocs == null || allDocs.size() <= compDocs.size()) {
            return null;
        }
        Set<Document> compSet = new HashSet<>(compDocs);
        Document bestInternal = null;
        Document bestExternal = null;
        PairMetrics bestPm = null;

        for (Document inDoc : compDocs) {
            for (Document exDoc : allDocs) {
                if (compSet.contains(exDoc)) continue;
                PairMetrics pm = computePairMetrics(inDoc, exDoc);
                if (bestPm == null || pm.score > bestPm.score) {
                    bestPm = pm;
                    bestInternal = inDoc;
                    bestExternal = exDoc;
                }
            }
        }

        if (bestPm == null || bestExternal == null) {
            return null;
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("internalDocName", bestInternal.getOriginalFilename());
        res.put("externalDocName", bestExternal.getOriginalFilename());
        res.put("jaccard", Math.round(bestPm.score * 1000.0) / 1000.0);
        res.put("jaccardFormatted", String.format(Locale.US, "%.3f", bestPm.score));
        res.put("intersectionSize", bestPm.intersectionSize);
        res.put("unionSize", bestPm.unionSize);
        res.put("sharedTerms", bestPm.sharedTerms);
        return res;
    }

    private Map<String, Object> findClosestNonNeighborForDoc(Document doc, List<Document> allDocs, Set<Document> neighborSet) {
        if (allDocs == null || allDocs.size() <= 1) {
            return null;
        }
        Document bestOther = null;
        PairMetrics bestPm = null;

        for (Document other : allDocs) {
            if (other.equals(doc) || neighborSet.contains(other)) continue;
            PairMetrics pm = computePairMetrics(doc, other);
            if (bestPm == null || pm.score > bestPm.score) {
                bestPm = pm;
                bestOther = other;
            }
        }

        if (bestPm == null || bestOther == null) {
            return null;
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("docId", bestOther.getId());
        res.put("docName", bestOther.getOriginalFilename());
        res.put("jaccard", Math.round(bestPm.score * 1000.0) / 1000.0);
        res.put("jaccardFormatted", String.format(Locale.US, "%.3f", bestPm.score));
        res.put("intersectionSize", bestPm.intersectionSize);
        res.put("unionSize", bestPm.unionSize);
        res.put("sharedTerms", bestPm.sharedTerms);
        return res;
    }
}
