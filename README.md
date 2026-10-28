# Optimization Framework for Document Categorization

**Subtitle:** A DSA-Based Document Processing, Feature Optimization and Categorization System

---

## 1. Project Overview
The **Optimization Framework for Document Categorization** is a deterministic, category-independent web application built with **Java 21**, **Spring Boot 3**, and **Thymeleaf** that processes, compares, clusters, and categorizes documents using pure **Data Structures and Algorithms (DSA)**. Rather than relying on Machine Learning, Large Language Models, vector embeddings, or external databases, the system executes an end-to-end algorithmic pipeline in memory—from multi-format text extraction and custom hash-table term counting to custom Merge Sort feature optimization, Jaccard similarity matrix construction, adjacency-list graph traversal (BFS/DFS), connected-component discovery, and explainable category label synthesis.

---

## 2. Problem Statement
Categorizing unstructured documents manually is slow and inconsistent, while modern AI/ML classification pipelines introduce black-box decision-making, heavy compute dependencies, and training-set bias. There is a clear academic and engineering need for a **100% transparent, deterministic, and explainable document categorization engine** where every feature count, sort comparison, similarity score, graph edge, traversal step, and category assignment can be traced mathematically back to fundamental data structures and algorithms.

---

## 3. Objectives
- Build a unified **Java 21 + Spring Boot 3** backend that acts as the single source of truth for all algorithmic state.
- Extract plain text from **TXT**, **PDF**, and **DOCX** files using Apache Tika.
- Normalize text and filter stop words in $O(1)$ average lookup time per token using a `HashSet`.
- Count term frequencies using a custom separate-chaining hash table (`TermFrequencyMap`).
- Rank and isolate the **Top 15** dominant features per document using a custom divide-and-conquer **Merge Sort** implementation (`FeatureOptimizationService`).
- Compute pairwise **Jaccard Similarity** across documents and render an interactive **Similarity Heatmap** and accessible pairwise breakdown table.
- Construct an undirected **Adjacency-List Document Graph** (`DocumentGraph`) and support interactive vertex inspection, neighbor highlighting, **BFS**, **DFS**, and **Connected Components** discovery.
- Generate **dynamic, category-independent labels** and detailed grouping explanations from the top aggregated features of each connected component without hardcoded topic dictionaries.
- Provide an **Interactive DSA Algorithm Walkthrough** (`/walkthrough`) that traces all 10 implemented algorithms step by step on real user inputs.

---

## 4. Features
1. **Dashboard & Multi-Format Upload (`/`):** Upload `.txt`, `.pdf`, and `.docx` files, delete individual documents, clear all state, or trigger **Run Full Analysis**.
2. **Processing Pipeline (`/pipeline`):** Track each document across pipeline stages (`UPLOADED`, `PREPROCESSED`, `FEATURES_EXTRACTED`, `FEATURES_OPTIMIZED`, `GRAPH_BUILT`, `CATEGORIZED`).
3. **DSA Analysis (`/analysis`):** Inspect extracted vs. Merge-Sorted Top 15 optimized features per document alongside theoretical complexity metrics.
4. **Document Details (`/document/{id}`):** View raw extracted text, preprocessed tokens, full term-frequency table, Top 15 optimized features, similar documents, connected component membership, and dynamic category rationale.
5. **String Algorithm Analysis (`/string-algorithms`):** Execute and benchmark **Naive**, **KMP**, **Z Algorithm**, **Rabin-Karp**, and **Aho-Corasick** pattern matching against uploaded documents or custom text.
6. **Interactive DSA Walkthrough (`/walkthrough`):** Step-by-step interactive execution trace (`First`, `Previous`, `Next`, `Play/Pause`, `Reset`, `Last`) for all 10 implemented algorithms using real backend state.
7. **Similarity Matrix & Heatmap (`/similarity`):** Dynamic $N \times N$ Jaccard similarity heatmap with full document labels, color legend, threshold badges, and an accessible pairwise table.
8. **Interactive Graph Visualization (`/graph`):** Interactive SVG topology canvas supporting vertex selection, neighbor and incident-edge weight inspection, connected-component highlighting, and backend **BFS** / **DFS** traversal order visualization.
9. **Explainable Category Results (`/results`):** Group-level and document-level explainability showing exact feature aggregation, Top 3 dominant feature selection, direct Jaccard edges, transitive BFS paths, and isolated-vertex explanations.
10. **JSON Export (`/export`, `/api/export/json`):** Download or preview the complete structured analysis output as JSON.

---

## 5. System Architecture
```text
+-----------------------------------------------------------------------+
|                   Presentation Layer (Thymeleaf + HTML/CSS/JS)        |
|  Dashboard | Pipeline | Analysis | Walkthrough | Strings | Similarity |
|  Interactive Graph | Explainable Results | Document Details | Export  |
+-----------------------------------------------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                     Spring Boot 3 Controller Layer                    |
|  DocumentController | PipelineController | AnalysisController         |
|  WalkthroughController | StringAlgorithmsController                   |
|  SimilarityController | GraphController | ResultsController | Export  |
+-----------------------------------------------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                 Core Java DSA & Service Layer (In-Memory)             |
|  TextExtractionService (Tika) -> PreprocessingService (HashSet)       |
|  -> FeatureExtractionService (Custom TermFrequencyMap Chaining Hash)  |
|  -> FeatureOptimizationService (Custom Merge Sort, Top 15 Selection)  |
|  -> SimilarityService (Jaccard Set Intersection / Union)              |
|  -> GraphService & DocumentGraph (Adjacency List, BFS, DFS, Comps)    |
|  -> CategoryService (Component Feature Aggregation & Label Synthesis) |
+-----------------------------------------------------------------------+
```

---

## 6. Technology Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.2.4 (`spring-boot-starter-web`, `spring-boot-starter-thymeleaf`)
- **Frontend:** Thymeleaf, HTML5, CSS3, Vanilla JavaScript (zero frontend calculation of backend algorithms)
- **Document Parsing:** Apache Tika 2.9.1 (`tika-core`, `tika-parsers-standard-package`)
- **Serialization:** Jackson Databind (`jackson-databind`)
- **Build Tool:** Apache Maven (with Maven Wrapper `mvnw` / `mvnw.cmd`)
- **Storage Architecture:** Thread-safe in-memory state (`CopyOnWriteArrayList`, `ConcurrentHashMap`, `LinkedHashMap`) — no external database required

---

## 7. DSA Algorithms Actually Implemented
| # | Algorithm / Data Structure | Source File | Purpose in System |
| :--- | :--- | :--- | :--- |
| 1 | **Separate-Chaining Hash Table** (`TermFrequencyMap`) | `datastructures/TermFrequencyMap.java` | Counts token frequencies using bucket chains (`HashNode[]`). |
| 2 | **Custom Merge Sort** | `features/FeatureOptimizationService.java` | Sorts extracted `TermFrequency` items descending by frequency (tie-broken lexicographically A–Z) to isolate Top 15 features and rank component features. |
| 3 | **Naive String Matching** | `algorithms/StringAlgorithmsService.java` | Sliding-window character-by-character pattern search. |
| 4 | **Knuth-Morris-Pratt (KMP)** | `algorithms/StringAlgorithmsService.java` | Builds the Longest Proper Prefix-Suffix (`LPS`) array for linear-time matching without backtracking. |
| 5 | **Z Algorithm** | `algorithms/StringAlgorithmsService.java` | Concatenates `pattern + "$" + text` and maintains a `[L, R]` Z-box window to compute prefix match lengths. |
| 6 | **Rabin-Karp Rolling Hash** | `algorithms/StringAlgorithmsService.java` | Polynomial rolling hash (`d = 256`, `q = 101`) with character-by-character spurious-hit verification. |
| 7 | **Aho-Corasick Automaton** | `algorithms/StringAlgorithmsService.java` | Multi-pattern Trie with BFS-constructed failure links and output sets. |
| 8 | **Jaccard Similarity** | `similarity/SimilarityService.java` | Computes set intersection and union over Top 15 optimized document features. |
| 9 | **Breadth-First Search (BFS)** | `graph/DocumentGraph.java` | FIFO queue traversal over the adjacency list to explore connected vertices level by level. |
| 10 | **Depth-First Search (DFS)** | `graph/DocumentGraph.java` | Recursive backtracking traversal over the adjacency list. |
| 11 | **Connected Components** | `graph/DocumentGraph.java` | Iterates over all graph vertices, launching BFS from each unvisited vertex to partition the graph into maximal connected components. |

---

## 8. Processing Pipeline
1. **Upload & Text Extraction:** Multipart file upload (`.txt`, `.pdf`, `.docx`) is parsed by `TextExtractionService` using Apache Tika.
2. **Preprocessing:** `PreprocessingService` converts text to lowercase, strips non-alphanumeric punctuation, splits on whitespace, and filters stop words using an $O(1)$ `HashSet`.
3. **Feature Extraction:** `FeatureExtractionService` inserts all cleaned tokens into a custom 100-bucket `TermFrequencyMap` and converts the chains into a `List<TermFrequency>`.
4. **Feature Optimization:** `FeatureOptimizationService` runs custom recursive **Merge Sort** ($O(U \log U)$) and slices the **Top 15** highest-frequency features.
5. **Pairwise Similarity:** `SimilarityService` evaluates Jaccard similarity $J(A, B)$ across all document pairs using their Top 15 feature sets.
6. **Graph Construction:** `GraphService` adds every document as a vertex in `DocumentGraph` and creates an undirected edge $(A, B)$ whenever $J(A, B) \ge \text{threshold}$ (default `0.1`, configurable in UI).
7. **Connected Components & Categorization:** `DocumentGraph.findConnectedComponents()` partitions vertices via BFS, and `CategoryService.identifyCategories()` aggregates member features, ranks them via custom Merge Sort, and generates the category label and grouping rationale.

---

## 9. Similarity Calculation
Pairwise document similarity is computed strictly on the **Top 15 optimized feature sets** $A$ and $B$ using the Jaccard Index:

$$J(A, B) = \frac{|A \cap B|}{|A \cup B|}$$

- **Range:** $0.0 \le J(A, B) \le 1.0$
- **Empty Union Handling:** If both feature sets are empty ($|A \cup B| = 0$), $J(A, B) = 0.0$.
- **Self-Similarity (Diagonal):** For any non-empty document $A$, $J(A, A) = 1.0$.

---

## 10. Graph-Based Grouping
- **Vertices ($V$):** Every uploaded document is represented as a vertex in `DocumentGraph` (`Map<Document, List<Document>> adjacencyList`).
- **Undirected Edges ($E$):** For each unique pair $(d_i, d_j)$ with $i < j$, if $J(d_i, d_j) \ge \text{threshold}$, an undirected edge is added in both directions (`adj.get(u).add(v)` and `adj.get(v).add(u)`) and `edgeCount` is incremented once per unique undirected edge.
- **Connected Components ($C$):** Documents belong to the same group if and only if they lie in the same connected component—either connected directly by an edge ($J \ge \text{threshold}$) or transitively through a path of intermediate documents ($A \leftrightarrow B \leftrightarrow C$). Documents with 0 edges form isolated single-document components.

---

## 11. Dynamic Category Identification
Category labels are **never hardcoded**. For each discovered connected component:
1. **Pool Features:** Collect the Top 15 optimized features from every member document in the component.
2. **Sum Frequencies:** Compute $\text{AggFreq}(t) = \sum_{d \in \text{Component}} \text{freq}(t, d)$ for each unique term $t$.
3. **Rank via Custom Merge Sort:** Sort aggregated terms descending by summed frequency, breaking ties lexicographically (A–Z).
4. **Select Dominant Terms:** Take the top $\min(3, U)$ ranked terms.
5. **Format Label:** Title-case and join the selected terms with `" / "` (e.g., `Neural / Network / Learning`). If a component has 0 extracted features, the honest fallback label `"Undetermined"` is assigned.

---

## 12. Complexity Analysis
*(Note: All complexities below represent theoretical asymptotic bounds based on implementation structure, distinct from measured wall-clock execution time.)*

| Stage / Algorithm | Time Complexity | Auxiliary Space Complexity | Parameter Definitions |
| :--- | :--- | :--- | :--- |
| **Preprocessing & Tokenization** | $O(N)$ | $O(N)$ | $N =$ character length / token count of document |
| **Feature Extraction (`TermFrequencyMap`)** | $O(N)$ average, $O(N \cdot U)$ worst-case | $O(U)$ | $U =$ unique non-stopword terms |
| **Custom Merge Sort (`FeatureOptimizationService`)** | $O(U \log U)$ | $O(U)$ | $U =$ unique terms sorted |
| **Pairwise Jaccard Similarity (Single Pair)** | $O(F)$ | $O(F)$ | $F \le 15$ optimized features per document |
| **Full Similarity Matrix & Graph Construction** | $O(D^2 \cdot F)$ | $O(D^2)$ matrix, $O(V + E)$ graph | $D = V =$ document count, $E =$ undirected edges |
| **BFS / DFS / Connected Components** | $O(V + E)$ | $O(V)$ | $V =$ vertices, $E =$ undirected edges |
| **Naive String Matching** | $O((N - M + 1) \cdot M)$ worst-case | $O(1)$ | $N =$ text length, $M =$ pattern length |
| **KMP String Matching** | $O(N + M)$ | $O(M)$ | $M =$ LPS table size |
| **Z Algorithm** | $O(N + M)$ | $O(N + M)$ | Concatenated string length $N + M + 1$ |
| **Rabin-Karp String Matching** | $O(N + M)$ average, $O(N \cdot M)$ worst-case | $O(1)$ | Rolling hash window of size $M$ |
| **Aho-Corasick Automaton** | $O(N + \sum M_k + Z)$ | $O(\sum M_k \cdot |\Sigma|)$ | $\sum M_k =$ total pattern length, $Z =$ matches |

---

## 13. Supported File Types
- **`.txt`** — Plain UTF-8 / ASCII text documents
- **`.pdf`** — Portable Document Format (parsed via Apache Tika PDFBox parser)
- **`.docx`** — Microsoft Word OpenXML documents (parsed via Apache Tika POI parser)

Unsupported extensions or files without extractable text are rejected safely with a user-facing validation banner.

---

## 14. Installation and Local Execution

### Prerequisites
- **Java 21** (`jdk-21` or higher)
- **Maven 3.9+** (or use the included `./mvnw` / `mvnw.cmd` wrapper)

### Build & Run Commands
```bash
# 1. Clone the repository
git clone https://github.com/diksharage/A5_DSA-3_G-11.git
cd "A5_DSA-3_G-11"

# 2. Build the executable JAR
./mvnw clean package

# 3. Start the Spring Boot server (defaults to port 8080, or $PORT if set)
java -jar target/optimization-framework-0.0.1-SNAPSHOT.jar
```
Open `http://localhost:8080` in your browser.

---

## 15. Testing
The project is verified through automated end-to-end HTTP and API integration checks covering:
- Multi-format upload (`.txt`, `.pdf`, `.docx`), individual document deletion, and `Clear All` state reset.
- Edge cases: `0` documents, `1` document, `2` related documents, `2` unrelated documents, multi-component graphs with transitive connections, isolated vertices, unsupported file extensions, empty search patterns, not-found patterns, and export-before-analysis rejection.
- Data consistency across Dashboard, Similarity Heatmap (`/api/similarity/matrix`), Interactive Graph (`/api/graph/state`), Explainable Category Results (`/api/results/explanations`), Interactive Walkthrough (`/api/walkthrough`), and JSON Export (`/api/export/json`).

---

## 16. Screenshots & Presentation Pages
For final demonstration and presentation, capture the following live application views:
1. **Dashboard (`/`)** — Empty state and uploaded mixed-format document list (`.txt`, `.pdf`, `.docx`).
2. **Completed Processing Pipeline (`/pipeline`)** — All stages marked complete through `CATEGORIZED`.
3. **DSA Analysis (`/analysis`)** — Extracted vs. Merge-Sorted Top 15 optimized features.
4. **Similarity Matrix & Heatmap (`/similarity`)** — Color-coded $N \times N$ Jaccard matrix and accessible pairwise breakdown table.
5. **Interactive Graph Visualization (`/graph`)** — Selected vertex inspector, neighbor/edge weights, and connected-component highlight.
6. **BFS & DFS Traversal (`/graph`)** — Highlighted traversal sequence with numbered visit badges (`#1`, `#2`, ...).
7. **Interactive DSA Walkthrough (`/walkthrough`)** — Step-by-step execution trace for Merge Sort, KMP/Aho-Corasick, Jaccard, BFS/DFS, and Connected Components.
8. **Explainable Category Results (`/results`)** — Connected-component group summaries, Top 3 feature aggregation tables, and per-document membership explanations.
9. **Document Details (`/document/{id}`)** — Complete single-document inspection page.
10. **JSON Export (`/export`)** — Pretty-printed JSON preview and download.

---

## 17. Deployment
- **Cloud Platform:** Configured for **Render** Web Service deployment.
- **Port Binding:** `src/main/resources/application.properties` configures `server.port=${PORT:8080}` so Spring Boot binds automatically to Render's injected `PORT` environment variable while defaulting to `8080` locally.
- **Build Command:** `./mvnw clean package -DskipTests`
- **Start Command:** `java -jar target/optimization-framework-0.0.1-SNAPSHOT.jar`

---

## 18. Limitations
- **Volatile In-Memory State:** By design (no external database), all uploaded documents and analysis results are stored in JVM memory. Restarting the server or container resets the workspace state.
- **Pairwise $O(D^2)$ Similarity Scaling:** Computing the full all-pairs Jaccard matrix scales quadratically with the number of documents $D$, making it ideal for classroom and medium-scale document collections (tens to hundreds of documents) rather than millions of records.
- **Lexical Matching Only:** Because ML embeddings and semantic models are intentionally excluded, synonyms with distinct surface strings (e.g., `"cardiac"` vs. `"heart"`) are treated as distinct tokens unless they co-occur with shared vocabulary.

---

## 19. Team Members
- **Rage Diksha** – `2510030249`
- **Nethi Greeshma** – `2510030250`
- **Section:** Section 5, Department of Computer Science and Engineering (CSE)
