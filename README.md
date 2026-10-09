# Optimization Framework for Document Categorization

A deterministic Data Structures and Algorithms (DSA) engine for automatic document feature extraction, similarity analysis, and robust text categorization.

## Project Overview
This project is an advanced algorithmic framework built using Java and Spring Boot to categorize large unstructured text documents automatically. It completely avoids black-box Machine Learning or LLMs, and strictly relies on foundational DSA concepts like custom hash mapping, exact string matching, set intersection (Jaccard similarity), and graph traversals.

## Problem Statement
The exponential growth of digital documents requires fast, explainable, and deterministic categorization methods. Traditional manual sorting is time-consuming, while modern ML models lack transparency ("black-box") and demand massive computing resources. We need an explainable, graph-theoretic approach to accurately group similar documents together based on their dominant extracted features.

## Objectives
- Build a lightweight, scalable document processor in Java.
- Implement efficient exact string matching algorithms to locate patterns within large corpora.
- Optimize high-frequency terms using Timsort.
- Construct an adjacency-list based document graph using Jaccard Similarity.
- Traverse the graph (BFS/DFS) to discover connected document clusters and output deterministic categories.

## System Architecture
The application runs entirely on a Java Spring Boot backend:
1. **File Upload Controller:** Receives and streams unstructured `txt` files.
2. **Analysis Orchestrator:** Global service managing the Pipeline execution state.
3. **Thymeleaf Frontend:** Server-side rendered views dynamically mapping the true Java state visually.

## DSA Algorithms
The framework exclusively relies on the following core DSA strategies:
- **Feature Storage:** `HashMap` / `ArrayList` mapping Term Frequencies.
- **Sorting:** `Timsort` ($O(N \log N)$) to extract Dominant Features.
- **String Searching:** Naive, KMP ($O(N+M)$), Z-Algorithm ($O(N+M)$), Rabin-Karp ($O(N+M)$), Aho-Corasick Automaton ($O(N+M+Z)$).
- **Similarity Evaluation:** Jaccard Set Overlap.
- **Topology:** Hash-based Adjacency Lists mapping the Document Graph.
- **Graph Traversal:** Breadth-First Search (BFS) and Depth-First Search (DFS) for component detection.

## Processing Workflow
1. **Document Extraction** - Raw byte streams read into memory buffers.
2. **Preprocessing** - Case normalization, punctuation stripping, and stop-word filtering.
3. **Feature Extraction** - Frequency mapping of distinct tokens.
4. **Feature Optimization** - Discarding low-value noise and sorting dominant traits.
5. **Similarity Matrix** - $N \times N$ calculation of structural overlap across all files.
6. **Graph Construction** - Linking sufficient similarity scores as weighted edges.
7. **BFS / DFS** - Traversal mapping of Disjoint Connected Components.
8. **Categorization** - Rationale generation and human-readable label assignment.

## Technology Stack
- **Backend:** Java 21, Spring Boot 3
- **Build Tool:** Maven
- **Frontend:** Thymeleaf, HTML5, Vanilla CSS
- **Visualization:** Mermaid.js (for Dynamic Graph Drawing)

## How to Run
### Prerequisites
- JDK 17 or newer
- Maven (optional, wrapper is included)

### Local Execution
Clone the repository and run the application locally using the Maven wrapper:
```bash
./mvnw spring-boot:run
```
*(On Windows, use `.\mvnw spring-boot:run`)*

The server will automatically start on `http://localhost:8080`.

## Testing
- Upload `.txt` test files using the dashboard interface.
- Select the `Run Full Analysis` execution hook to monitor pipeline variables.
- Navigate to the `String Algorithms` view to run KMP/Aho-Corasick side-by-side performance comparisons.

## Complexity
- **Time Complexity (Overall Pipeline):** $O(N \log N)$ governed by Feature Sorting and $O(V + E)$ for Category generation.
- **Space Complexity:** $O(V^2)$ for the Similarity Matrix constraint check, and $O(V + E)$ for the Graph Adjacency List.

## Deployment
The framework is fully containerized and compatible with Render.com using native Java execution.
- **Build Command:** `./mvnw clean package -DskipTests`
- **Start Command:** `java -jar target/optimization-framework-0.0.1-SNAPSHOT.jar`

## Team Members
**Section 5, CSE**
- **Rage Diksha** – 2510030249
- **Nethi Greeshma** – 2510030250
