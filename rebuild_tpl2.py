import os
base_dir = r"C:\Users\Diksha\OneDrive\Desktop\Optimization Frame Work\src\main\resources\templates"

html_base = """<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>{title} - DSA Document Categorization</title>
    <style>
        :root { --primary: #1e1e2f; --secondary: #2a2a40; --accent: #4a90e2; --success: #2ecc71; --error: #e74c3c; --text-light: #f5f6fa; --text-dark: #2f3640; --bg-light: #f5f6fa; --bg-white: #ffffff; --border: #e1e4e8; }
        body { font-family: 'Segoe UI', sans-serif; margin: 0; padding: 0; background-color: var(--bg-light); color: var(--text-dark); display: flex; min-height: 100vh; }
        .sidebar { width: 250px; background-color: var(--primary); color: var(--text-light); padding: 2rem 1rem; box-shadow: 2px 0 5px rgba(0,0,0,0.1); }
        .sidebar h2 { font-size: 1.2rem; margin-bottom: 2rem; text-align: center; border-bottom: 1px solid var(--secondary); padding-bottom: 1rem; }
        .nav-menu { list-style: none; padding: 0; }
        .nav-menu li { margin-bottom: 0.5rem; }
        .nav-menu a { color: var(--text-light); text-decoration: none; display: block; padding: 0.8rem; border-radius: 5px; }
        .nav-menu a:hover, .nav-menu a.active { background-color: var(--accent); }
        .main-content { flex: 1; padding: 2rem; overflow-y: auto; }
        .card { background-color: var(--bg-white); border-radius: 8px; padding: 2rem; box-shadow: 0 2px 10px rgba(0,0,0,0.05); margin-bottom: 2rem; }
        .card h3 { margin-top: 0; color: var(--secondary); border-bottom: 2px solid var(--bg-light); padding-bottom: 0.5rem; }
        .btn { background-color: var(--accent); color: white; border: none; padding: 0.8rem 1.5rem; border-radius: 5px; cursor: pointer; font-size: 1rem; text-decoration:none; display:inline-block; }
        .btn:hover { background-color: #357abd; }
        table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
        th, td { padding: 12px 15px; text-align: left; border-bottom: 1px solid var(--border); }
        th { background-color: #f8f9fa; font-weight: 600; color: var(--secondary); }
    </style>
</head>
<body>
    <div class="sidebar">
        <h2>Optimization Framework</h2>
        <ul class="nav-menu">
            <li><a href="/" {nav1}>Dashboard & Upload</a></li>
            <li><a href="/pipeline" {nav2}>Processing Pipeline</a></li>
            <li><a href="/analysis" {nav3}>DSA Analysis</a></li>
            <li><a href="/string-algorithms" {nav4}>String Algorithms</a></li>
            <li><a href="/similarity" {nav5}>Similarity</a></li>
            <li><a href="/graph" {nav6}>Graph Visualization</a></li>
            <li><a href="/results" {nav7}>Results & Categories</a></li>
            <li><a href="/export" {nav8}>Export</a></li>
        </ul>
    </div>
    <div class="main-content">
        {content}
    </div>
</body>
</html>"""

def get_page(title, nav_idx, content):
    p = html_base.replace("{title}", title)
    for i in range(1, 9):
        p = p.replace(f"{{nav{i}}}", 'class="active"' if i == nav_idx else '')
    return p.replace("{content}", content)

string_content = """
<style>
.form-group { margin-bottom: 1rem; }
.form-control { width: 100%; padding: 0.5rem; border: 1px solid var(--border); border-radius: 4px; font-family: monospace; }
.result-box { background: #f8f9fa; border: 1px solid var(--border); border-radius: 8px; padding: 1.5rem; margin-top: 2rem; }
.status-badge { padding: 0.4rem 1rem; border-radius: 50px; font-weight: bold; color: white; margin-left: 1rem; background: var(--success); }
.status-not-found { background: var(--error); }
.explanation-box { background-color: #eef2f7; padding: 1.5rem; border-radius: 8px; margin-top: 1rem; }
</style>
<div class="card">
    <h3>DSA Playground: String Algorithms</h3>
    <form action="/string-algorithms/run" method="post">
        <div class="form-group">
            <label>Text:</label><br>
            <textarea name="text" class="form-control" rows="4" required th:text="${inputText}"></textarea>
        </div>
        <div class="form-group">
            <label>Pattern(s):</label><br>
            <input type="text" name="pattern" class="form-control" required th:value="${inputPattern}">
        </div>
        <div class="form-group">
            <label>Algorithm:</label><br>
            <select name="algorithm" class="form-control" style="width:auto;">
                <option value="naive" th:selected="${selectedAlgorithm == 'naive'}">Naive Matching</option>
                <option value="kmp" th:selected="${selectedAlgorithm == 'kmp'}">KMP</option>
                <option value="z" th:selected="${selectedAlgorithm == 'z'}">Z Algorithm</option>
                <option value="rabin" th:selected="${selectedAlgorithm == 'rabin'}">Rabin-Karp</option>
                <option value="ahocorasick" th:selected="${selectedAlgorithm == 'ahocorasick'}">Aho-Corasick</option>
            </select>
        </div>
        <button type="submit" class="btn">Run Algorithm</button>
    </form>
</div>
<div class="result-box" th:if="${result != null}">
    <h3 th:text="${result.algorithmName}"></h3>
    <p>Matches found at positions: <span th:text="${#lists.isEmpty(result.positions) ? 'None' : result.positions}"></span></p>
    <div class="explanation-box">
        <h4>DSA Guide Reference</h4>
        <p><strong>Why Used:</strong> <span th:text="${result.whyUsed != null ? result.whyUsed : 'String sequence validation.'}"></span></p>
        <p><strong>Time Complexity:</strong> <span th:text="${result.timeComplexity != null ? result.timeComplexity : 'O(N+M)'}"></span></p>
        <p><strong>Space Complexity:</strong> <span th:text="${result.spaceComplexity != null ? result.spaceComplexity : 'O(M)'}"></span></p>
    </div>
</div>
"""

similarity_content = """
<div class="card">
    <h3>Document Similarity Matrix</h3>
    <p><strong>Why used:</strong> Documents are represented using their optimized features. Similarity is calculated using the implemented Jaccard similarity method. The similarity threshold determines whether a graph edge is created.</p>
    
    <div style="overflow-x: auto; margin-top: 1.5rem;" th:if="${documents.size() > 0}">
        <table style="min-width: 600px;">
            <thead>
                <tr>
                    <th>Document</th>
                    <th th:each="colDoc : ${documents}" th:text="${colDoc.originalFilename}" style="text-align:center;"></th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="rowDoc : ${documents}">
                    <td th:text="${rowDoc.originalFilename}" style="font-weight:600; background:#f8f9fa;"></td>
                    <td th:each="colDoc : ${documents}" 
                        th:text="${#numbers.formatDecimal(similarityService.calculateJaccardSimilarity(rowDoc, colDoc), 1, 2)}"
                        style="text-align:center;">
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div th:if="${documents.size() == 0}">
        <p>No documents uploaded yet.</p>
    </div>
</div>
"""

graph_content = """
<script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
<script>mermaid.initialize({startOnLoad:true, theme:'default'});</script>
<div class="card">
    <h3>Document Graph Visualization</h3>
    
    <form action="/graph/threshold" method="post" style="background:#eef2f7; padding:1rem; border-radius:6px; margin-bottom:1.5rem;">
        <label><strong>Similarity Edge Threshold:</strong></label>
        <input type="number" name="threshold" step="0.01" min="0" max="1" th:value="${threshold}" style="padding:0.4rem; margin:0 1rem; width:80px; border:1px solid #ccc; border-radius:4px;">
        <button type="submit" class="btn" style="padding:0.4rem 1rem;">Update Graph</button>
    </form>

    <div class="dashboard-stats" style="display: flex; gap: 2rem; margin-bottom: 2rem;">
        <div><strong>Documents (Vertices):</strong> <span th:text="${graph.adjacencyList.size()}"></span></div>
        <div><strong>Similarities (Edges):</strong> <span th:text="${graph.edgeCount}"></span></div>
        <div><strong>Connected Components:</strong> <span th:text="${graph.findConnectedComponents().size()}"></span></div>
    </div>

    <div class="mermaid" style="background: #fff; border: 1px solid var(--border); padding: 2rem; border-radius: 8px; text-align: center;">
        graph TD
        <th:block th:each="entry : ${graph.adjacencyList}">
            <th:block th:each="neighbor : ${entry.value}">
                [[${#strings.replace(entry.key.originalFilename, '.txt', '')}]] --- [[${#strings.replace(neighbor.originalFilename, '.txt', '')}]]
            </th:block>
        </th:block>
    </div>
</div>
"""

with open(os.path.join(base_dir, "string-algorithms.html"), "w") as f: f.write(get_page("String Algorithms", 4, string_content))
with open(os.path.join(base_dir, "similarity.html"), "w") as f: f.write(get_page("Similarity", 5, similarity_content))
with open(os.path.join(base_dir, "graph.html"), "w") as f: f.write(get_page("Graph", 6, graph_content))
print("Templates 4-6 complete")
