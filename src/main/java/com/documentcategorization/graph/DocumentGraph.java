package com.documentcategorization.graph;

import com.documentcategorization.model.Document;
import com.documentcategorization.model.WalkthroughStep;

import java.util.*;
import java.util.stream.Collectors;

public class DocumentGraph {
    private Map<Document, List<Document>> adjacencyList = new HashMap<>();
    private int edgeCount = 0;

    public void addVertex(Document doc) {
        adjacencyList.putIfAbsent(doc, new ArrayList<>());
    }

    public void addEdge(Document doc1, Document doc2) {
        addVertex(doc1);
        addVertex(doc2);
        adjacencyList.get(doc1).add(doc2);
        adjacencyList.get(doc2).add(doc1);
        edgeCount++;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public Map<Document, List<Document>> getAdjacencyList() {
        return adjacencyList;
    }

    public List<List<Document>> findConnectedComponents() {
        return findConnectedComponents(null);
    }

    public List<List<Document>> findConnectedComponents(List<WalkthroughStep> steps) {
        List<List<Document>> components = new ArrayList<>();
        Set<Document> visited = new LinkedHashSet<>();
        List<Document> allVertices = new ArrayList<>(adjacencyList.keySet());

        if (steps != null) {
            Map<String, Object> initState = new LinkedHashMap<>();
            initState.put("Total Graph Vertices", allVertices.size());
            initState.put("Total Graph Edges", edgeCount);
            initState.put("Initial Unvisited Documents", toNames(allVertices));
            initState.put("Visited Documents", Collections.emptyList());
            initState.put("Connected Components Discovered", Collections.emptyList());
            addStep(steps, "1. Initialization", "Initialize Connected Components Discovery",
                    "Starting connected components scan across " + allVertices.size() + " document vertex/vertices. All vertices begin unvisited.",
                    initState);
        }

        int compIdx = 1;
        for (Document doc : allVertices) {
            if (!visited.contains(doc)) {
                if (steps != null) {
                    Map<String, Object> startState = new LinkedHashMap<>();
                    startState.put("Selected Unvisited Starting Document", doc.getOriginalFilename());
                    startState.put("Remaining Unvisited Before Traversal", toNames(getUnvisited(allVertices, visited)));
                    startState.put("Visited Documents So Far", toNames(visited));
                    startState.put("Traversal Method", "Breadth-First Search (bfs)");
                    addStep(steps, "2. Select Unvisited Vertex",
                            "Start Component #" + compIdx + " at " + doc.getOriginalFilename(),
                            "Document '" + doc.getOriginalFilename() + "' is unvisited. Launching BFS from '" + doc.getOriginalFilename() + "' to discover all reachable documents in Component #" + compIdx + ".",
                            startState);
                }

                List<Document> component = new ArrayList<>();
                bfsForComponent(doc, visited, component, steps, compIdx, allVertices);
                components.add(component);

                if (steps != null) {
                    Map<String, Object> compDoneState = new LinkedHashMap<>();
                    compDoneState.put("Component #" + compIdx + " Documents", toNames(component));
                    compDoneState.put("Component Type", component.size() == 1 ? "Singleton Component (Isolated Document)" : "Multi-Document Connected Cluster (" + component.size() + " docs)");
                    compDoneState.put("Visited Documents", toNames(visited));
                    compDoneState.put("Remaining Unvisited Documents", toNames(getUnvisited(allVertices, visited)));
                    compDoneState.put("Components Found So Far", formatComponents(components));
                    addStep(steps, "4. Component Completed",
                            "Completed Component #" + compIdx + ": " + toNames(component),
                            component.size() == 1
                                    ? "Document '" + doc.getOriginalFilename() + "' has no unvisited neighbors and forms singleton Component #" + compIdx + "."
                                    : "BFS finished exploring all reachable vertices from '" + doc.getOriginalFilename() + "', forming Component #" + compIdx + " with " + component.size() + " documents.",
                            compDoneState);
                }
                compIdx++;
            } else if (steps != null) {
                Map<String, Object> skipState = new LinkedHashMap<>();
                skipState.put("Vertex Checked", doc.getOriginalFilename());
                skipState.put("Status", "Already Visited (Skipped)");
                skipState.put("Visited Documents", toNames(visited));
                skipState.put("Remaining Unvisited Documents", toNames(getUnvisited(allVertices, visited)));
                addStep(steps, "2. Check Next Vertex",
                        "Skip Already Visited Document: " + doc.getOriginalFilename(),
                        "Document '" + doc.getOriginalFilename() + "' was already visited in an earlier component; skipping.",
                        skipState);
            }
        }

        if (steps != null) {
            Map<String, Object> finalState = new LinkedHashMap<>();
            finalState.put("Total Connected Components", components.size());
            finalState.put("Visited Documents", toNames(visited));
            finalState.put("Unvisited Documents", Collections.emptyList());
            finalState.put("Final Connected Components", formatComponents(components));
            addStep(steps, "5. Final Connected Components",
                    "Discovered " + components.size() + " Connected Component(s)",
                    "All " + allVertices.size() + " document vertices have been visited and partitioned into " + components.size() + " connected component(s).",
                    finalState);
        }

        return components;
    }

    private void bfsForComponent(Document start, Set<Document> visited, List<Document> component,
                                 List<WalkthroughStep> steps, int compIdx, List<Document> allVertices) {
        Queue<Document> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            Document curr = queue.poll();
            component.add(curr);
            List<Document> neighbors = adjacencyList.getOrDefault(curr, Collections.emptyList());
            List<Document> newlyDiscovered = new ArrayList<>();
            for (Document neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    newlyDiscovered.add(neighbor);
                }
            }
            if (steps != null) {
                Map<String, Object> stepState = new LinkedHashMap<>();
                stepState.put("Component In Progress", "Component #" + compIdx);
                stepState.put("Selected Starting Document", start.getOriginalFilename());
                stepState.put("Current Document Visited", curr.getOriginalFilename());
                stepState.put("Neighbors Inspected", toNames(neighbors));
                stepState.put("Newly Discovered Documents", toNames(newlyDiscovered));
                stepState.put("BFS Queue After Step", toNames(queue));
                stepState.put("Component #" + compIdx + " Discovered So Far", toNames(component));
                stepState.put("Visited Documents", toNames(visited));
                stepState.put("Remaining Unvisited Documents", toNames(getUnvisited(allVertices, visited)));
                addStep(steps, "3. BFS Component Traversal",
                        "BFS Visit '" + curr.getOriginalFilename() + "' in Component #" + compIdx,
                        "Dequeued '" + curr.getOriginalFilename() + "', added to Component #" + compIdx + ", inspected " + neighbors.size() + " neighbor(s), and enqueued " + newlyDiscovered.size() + " newly discovered document(s).",
                        stepState);
            }
        }
    }

    private void bfs(Document start, Set<Document> visited, List<Document> component) {
        Queue<Document> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            Document curr = queue.poll();
            component.add(curr);
            for (Document neighbor : adjacencyList.getOrDefault(curr, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }

    public List<Document> bfsTraversal(Document start) {
        return bfsTraversal(start, null);
    }

    public List<Document> bfsTraversal(Document start, List<WalkthroughStep> steps) {
        List<Document> traversal = new ArrayList<>();
        if (start == null || !adjacencyList.containsKey(start)) return traversal;
        Set<Document> visited = new LinkedHashSet<>();
        Queue<Document> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        if (steps != null) {
            Map<String, Object> initState = new LinkedHashMap<>();
            initState.put("Starting Document", start.getOriginalFilename());
            initState.put("Initial Queue", toNames(queue));
            initState.put("Visited Documents", toNames(visited));
            initState.put("Traversal Order So Far", Collections.emptyList());
            addStep(steps, "1. Initialization",
                    "Enqueue Starting Document: " + start.getOriginalFilename(),
                    "Initialized BFS queue with starting document '" + start.getOriginalFilename() + "' and marked it as visited.",
                    initState);
        }

        while (!queue.isEmpty()) {
            List<String> queueBefore = steps != null ? toNames(queue) : null;
            Document curr = queue.poll();
            traversal.add(curr);
            List<Document> neighbors = adjacencyList.getOrDefault(curr, Collections.emptyList());
            List<Document> newlyDiscovered = new ArrayList<>();
            List<String> neighborDetails = new ArrayList<>();

            for (Document neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    newlyDiscovered.add(neighbor);
                    neighborDetails.add(neighbor.getOriginalFilename() + " (unvisited -> enqueued & marked visited)");
                } else {
                    neighborDetails.add(neighbor.getOriginalFilename() + " (already visited -> skipped)");
                }
            }

            if (steps != null) {
                Map<String, Object> stepState = new LinkedHashMap<>();
                stepState.put("Queue Before Poll", queueBefore);
                stepState.put("Current Document Being Visited", curr.getOriginalFilename());
                stepState.put("Neighbors Inspected (Adjacency Order)", neighborDetails);
                stepState.put("Newly Discovered Documents", toNames(newlyDiscovered));
                stepState.put("Queue After Processing", toNames(queue));
                stepState.put("Visited Documents", toNames(visited));
                stepState.put("Traversal Order So Far", toNames(traversal));
                addStep(steps, "2. Queue Processing & Neighbor Exploration",
                        "Dequeue & Visit: " + curr.getOriginalFilename(),
                        "Dequeued '" + curr.getOriginalFilename() + "' and appended to traversal order. Inspected " + neighbors.size() + " neighbor(s); discovered " + newlyDiscovered.size() + " new document(s).",
                        stepState);
            }
        }

        if (steps != null) {
            Map<String, Object> finalState = new LinkedHashMap<>();
            finalState.put("Starting Document", start.getOriginalFilename());
            finalState.put("Queue After Completion", Collections.emptyList());
            finalState.put("Visited Documents", toNames(visited));
            finalState.put("Final BFS Traversal Order", toNames(traversal));
            addStep(steps, "3. Final BFS Traversal Order",
                    "BFS Traversal Complete (" + traversal.size() + " document(s) visited)",
                    "Queue is now empty. All documents reachable from '" + start.getOriginalFilename() + "' have been visited level-by-level.",
                    finalState);
        }

        return traversal;
    }

    public List<Document> dfsTraversal(Document start) {
        return dfsTraversal(start, null);
    }

    public List<Document> dfsTraversal(Document start, List<WalkthroughStep> steps) {
        List<Document> traversal = new ArrayList<>();
        if (start == null || !adjacencyList.containsKey(start)) return traversal;
        Set<Document> visited = new LinkedHashSet<>();

        if (steps != null) {
            Map<String, Object> initState = new LinkedHashMap<>();
            initState.put("Starting Document", start.getOriginalFilename());
            initState.put("Implementation Type", "Recursive DFS (dfsHelper call stack)");
            initState.put("Visited Documents", Collections.emptyList());
            initState.put("Traversal Order", Collections.emptyList());
            addStep(steps, "1. Initialization",
                    "Start Recursive DFS at " + start.getOriginalFilename(),
                    "Invoking recursive dfsHelper('" + start.getOriginalFilename() + "', visited, traversal).",
                    initState);
        }

        List<Document> callStack = new ArrayList<>();
        dfsHelper(start, visited, traversal, steps, callStack);

        if (steps != null) {
            Map<String, Object> finalState = new LinkedHashMap<>();
            finalState.put("Starting Document", start.getOriginalFilename());
            finalState.put("Recursion Call Stack", Collections.emptyList());
            finalState.put("Visited Documents", toNames(visited));
            finalState.put("Final DFS Traversal Order", toNames(traversal));
            addStep(steps, "4. Final DFS Traversal Order",
                    "DFS Traversal Complete (" + traversal.size() + " document(s) visited)",
                    "All recursive dfsHelper calls have returned. Final depth-first traversal order produced.",
                    finalState);
        }

        return traversal;
    }

    private void dfsHelper(Document curr, Set<Document> visited, List<Document> traversal,
                           List<WalkthroughStep> steps, List<Document> callStack) {
        visited.add(curr);
        traversal.add(curr);
        callStack.add(curr);
        List<Document> neighbors = adjacencyList.getOrDefault(curr, Collections.emptyList());

        if (steps != null) {
            Map<String, Object> visitState = new LinkedHashMap<>();
            visitState.put("Starting Document", callStack.get(0).getOriginalFilename());
            visitState.put("Current Document", curr.getOriginalFilename());
            visitState.put("Recursion Depth", callStack.size() - 1);
            visitState.put("Active Recursion Call Stack", toNames(callStack));
            visitState.put("Neighbors in Adjacency List", toNames(neighbors));
            visitState.put("Visited Documents", toNames(visited));
            visitState.put("Traversal Progression", toNames(traversal));
            addStep(steps, "2. Recursive Visit (dfsHelper)",
                    "Visit '" + curr.getOriginalFilename() + "' at Recursion Depth " + (callStack.size() - 1),
                    "Entered dfsHelper('" + curr.getOriginalFilename() + "'). Marked '" + curr.getOriginalFilename() + "' as visited and added to traversal order. Will now inspect " + neighbors.size() + " neighbor(s).",
                    visitState);
        }

        for (Document neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                if (steps != null) {
                    Map<String, Object> recState = new LinkedHashMap<>();
                    recState.put("Current Document", curr.getOriginalFilename());
                    recState.put("Neighbor Considered", neighbor.getOriginalFilename());
                    recState.put("Neighbor Status", "Unvisited -> Recurse into dfsHelper('" + neighbor.getOriginalFilename() + "')");
                    recState.put("Active Recursion Call Stack", toNames(callStack));
                    recState.put("Visited Documents", toNames(visited));
                    recState.put("Traversal Progression", toNames(traversal));
                    addStep(steps, "3. Neighbor Inspection & Recursive Call",
                            "Recurse from '" + curr.getOriginalFilename() + "' -> '" + neighbor.getOriginalFilename() + "'",
                            "Neighbor '" + neighbor.getOriginalFilename() + "' is unvisited. Making recursive call dfsHelper('" + neighbor.getOriginalFilename() + "').",
                            recState);
                }
                dfsHelper(neighbor, visited, traversal, steps, callStack);
            } else if (steps != null) {
                Map<String, Object> skipState = new LinkedHashMap<>();
                skipState.put("Current Document", curr.getOriginalFilename());
                skipState.put("Neighbor Considered", neighbor.getOriginalFilename());
                skipState.put("Neighbor Status", "Already Visited -> Skip");
                skipState.put("Active Recursion Call Stack", toNames(callStack));
                skipState.put("Visited Documents", toNames(visited));
                skipState.put("Traversal Progression", toNames(traversal));
                addStep(steps, "3. Neighbor Inspection",
                        "Skip Visited Neighbor '" + neighbor.getOriginalFilename() + "' of '" + curr.getOriginalFilename() + "'",
                        "Neighbor '" + neighbor.getOriginalFilename() + "' is already in the visited set; skipping recursive call.",
                        skipState);
            }
        }

        callStack.remove(callStack.size() - 1);
        if (steps != null && !callStack.isEmpty()) {
            Document caller = callStack.get(callStack.size() - 1);
            Map<String, Object> backtrackState = new LinkedHashMap<>();
            backtrackState.put("Completed Document", curr.getOriginalFilename());
            backtrackState.put("Returning To Caller", caller.getOriginalFilename());
            backtrackState.put("Active Recursion Call Stack", toNames(callStack));
            backtrackState.put("Visited Documents", toNames(visited));
            backtrackState.put("Traversal Progression", toNames(traversal));
            addStep(steps, "3. Recursive Backtrack",
                    "Backtrack from '" + curr.getOriginalFilename() + "' to '" + caller.getOriginalFilename() + "'",
                    "All neighbors of '" + curr.getOriginalFilename() + "' have been considered. Returning up the recursion stack to '" + caller.getOriginalFilename() + "'.",
                    backtrackState);
        }
    }

    private List<Document> getUnvisited(List<Document> allVertices, Set<Document> visited) {
        List<Document> unvisited = new ArrayList<>();
        for (Document d : allVertices) {
            if (!visited.contains(d)) {
                unvisited.add(d);
            }
        }
        return unvisited;
    }

    private List<String> toNames(Collection<Document> docs) {
        return docs.stream().map(Document::getOriginalFilename).collect(Collectors.toList());
    }

    private List<String> formatComponents(List<List<Document>> components) {
        List<String> formatted = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            formatted.add("Component #" + (i + 1) + ": " + toNames(components.get(i)));
        }
        return formatted;
    }

    private void addStep(List<WalkthroughStep> steps, String stage, String operation, String explanation, Map<String, Object> state) {
        steps.add(new WalkthroughStep(steps.size() + 1, stage, operation, explanation, state));
    }
}
