# Optimization Framework for Document Categorization

## 1. Project Title
Optimization Framework for Document Categorization: A DSA-Based Document Processing, Feature Optimization and Categorization System

## 2. Project Overview
This project is a dynamic, high-performance web application designed to automatically categorize documents using pure Data Structures and Algorithms (DSA) without relying on Machine Learning or external NLP APIs. It performs text extraction, feature optimization, similarity analysis, and graph-based clustering to logically group related documents and assign dynamic category labels.

## 3. Problem Statement
Categorizing large volumes of text manually is inefficient and prone to human error. While modern Machine Learning models can solve this, they require massive datasets, powerful hardware, and introduce a 'black-box' opacity. There is a strong academic and practical need for a fully deterministic, transparent categorization system built purely from foundational computer science principles and mathematical evaluations.

## 4. Objectives
* Build a 100% custom Java DSA engine for document processing.
* Accept diverse file formats (TXT, PDF, DOCX) and unify them into a single processing pipeline.
* Implement a memory-efficient HashMap to catalog term frequencies.
* Filter noise and isolate dominant topics using a custom (U \log U)$ Merge Sort algorithm.
* Construct an adjacency list graph based on Jaccard Similarity evaluations.
* Traverse the graph using BFS and DFS to isolate Connected Components.
* Generate contextual, dynamic labels strictly based on isolated dominant features.

## 5. Key Features
* **Multi-Format Extraction:** Supports .txt, .pdf, and .docx document parsing.
* **Pure DSA Pipeline:** No database, no ML, no LLMs. Fully in-memory state.
* **Dynamic Similarity Matrix:** Real-time matrix calculating overlap between all document subsets.
* **Graph Visualization:** Visually charts the undirected relationships between document nodes.
* **Full Transparency:** Provides an interactive Document Details breakdown and a JSON Export representation.

## 6. System Architecture
1. **Upload Layer:** Spring Boot Controller parsing multipart files.
2. **Extraction Layer:** Apache Tika isolates raw string content.
3. **Preprocessing Layer:** Converts to lowercase, strips punctuation, applies O(1) HashSet stop-word filtering.
4. **Feature Layer:** Counts frequencies via Chaining Hash Map, isolates Top 15 via Merge Sort.
5. **Graph Layer:** Mathematically computes Jaccard overlaps to build an Adjacency List.
6. **Traversal Layer:** BFS maps Connected Components to cluster documents.

## 7. Technology Stack
* **Backend:** Java 21, Spring Boot 3, Maven
* **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript
* **Text Extraction:** Apache Tika Core
* **Deployment:** GitHub, Render (PaaS)

## 8. DSA Algorithms Used
* **Custom Merge Sort:** (U \log U)$ isolating dominant features.
* **Chaining Hash Map:** (1)$ amortized term mapping.
* **Jaccard Similarity:** Mathematical set intersection over union.
* **Adjacency List:** (V + E)$ graph memory representation.
* **Breadth-First Search (BFS):** Graph traversal and component isolation.
* **Depth-First Search (DFS):** Alternative graph traversal mapping.
* **String Matching Algorithms:** Naive, KMP, Z-Algorithm, Rabin-Karp, Aho-Corasick.

## 9. Processing Workflow
Upload -> Text Extraction -> Tokenization -> Stop-word Filter -> Feature Mapping -> Merge Sort Optimization -> Jaccard Network Plotting -> BFS Component Clustering -> Dynamic Category Labeling

## 10. File Support
* **.TXT:** Standard UTF-8 Text
* **.PDF:** Portable Document Format (extracted natively via Tika)
* **.DOCX:** Microsoft Word Format (extracted natively via Tika)

## 11. Similarity Method
**Jaccard Index:** (A,B) = \frac{|A \cap B|}{|A \cup B|}$
Evaluates the mathematical intersection divided by the union of the Top 15 optimized features of two documents. Only documents exceeding a configurable threshold > 0.1 are granted a graph edge.

## 12. Graph-Based Grouping
Documents serve as Vertices. Similarities $> 0.1$ act as undirected Edges. The system builds a bidirectional Adjacency List to map traversable relationships in memory.

## 13. Dynamic Category Identification
Categories are **not hardcoded**. The system aggregates the features of every document trapped within a BFS Connected Component, merges them, and extracts the top dominant terms to synthetically generate a label (e.g. Agriculture / Tractor / Farming).

## 14. Complexity Analysis
* **Feature Processing:** Time (U \log U)$, Space (U)$
* **Jaccard Matrix:** Time (N^2)$, Space (N^2)$
* **Graph Traversal (BFS):** Time (V + E)$, Space (V)$
* **Aho-Corasick Automaton:** Time (N + M + Z)$

## 15. How to Run Locally
1. Ensure **Java 21** and **Maven** are installed.
2. Clone the repository.
3. Run ./mvnw clean package in the root directory.
4. Run java -jar target/optimization-framework-0.0.1-SNAPSHOT.jar
5. Navigate to http://localhost:8080.

## 16. Testing
The system maintains strict regression stability across all inputs. End-to-end tests validate that clearing documents safely resets single-source-of-truth states, algorithms scale flawlessly across independent topologies, and JSON exports accurately model cyclic graph hierarchies safely.

## 17. Screenshots section
*(Screenshots can be placed in src/main/resources/static/images/ and linked here. Includes Dashboard, Graph View, and Document Breakdown.)*

## 18. Deployment section
The application is fully configured for cloud deployment on **Render**. It binds automatically to Render\'s dynamic $PORT environment variable and executes directly via the Maven-built executable JAR file.

## 19. Limitations
* In-memory graph processing is heavily bounded by JVM Heap limits. Extreme inputs (>1000s of massive texts) will require optimization or database offloading.
* Pure Jaccard similarity treats synonyms as disjoint features (e.g., doctor and physician will not match) due to the strict omission of AI embeddings.

## 20. Team Members
**Rage Diksha** – 2510030249
**Nethi Greeshma** – 2510030250
*Section 5, CSE*
