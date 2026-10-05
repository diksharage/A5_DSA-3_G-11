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
        .dashboard-stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1.5rem; margin-bottom: 2rem; }
        .stat-card { background: var(--bg-white); padding: 1.5rem; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); border-left: 4px solid var(--accent); }
        .stat-card h4 { margin: 0 0 0.5rem 0; color: #666; font-size: 0.9rem; text-transform: uppercase; }
        .stat-card .number { font-size: 2rem; font-weight: bold; color: var(--primary); margin: 0; }
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

index_content = """
<div class="header" style="margin-bottom: 2rem;">
    <h1 style="color: var(--primary); margin-bottom: 0.5rem; font-size: 1.8rem;">Optimization Framework for Document Categorization</h1>
    <p style="color: var(--secondary); font-size: 1.1rem; margin-top: 0;">DSA-Based Document Processing, Feature Optimization & Categorization System</p>
</div>

<div class="card" style="margin-bottom: 2rem;">
    <h3 style="margin-top: 0; border-bottom: 1px solid #eee; padding-bottom: 0.5rem;">System Workflow</h3>
    <div style="display: flex; flex-wrap: wrap; align-items: center; gap: 0.5rem; font-size: 0.95rem; color: var(--secondary); font-weight: 600;">
        <span style="background-color: #eef2f7; padding: 4px 8px; border-radius: 4px;">Upload</span> <span style="color: #ccc;">?</span>
        <span style="background-color: #eef2f7; padding: 4px 8px; border-radius: 4px;">Preprocess</span> <span style="color: #ccc;">?</span>
        <span style="background-color: #eef2f7; padding: 4px 8px; border-radius: 4px;">Features</span> <span style="color: #ccc;">?</span>
        <span style="background-color: #eef2f7; padding: 4px 8px; border-radius: 4px;">Optimization</span> <span style="color: #ccc;">?</span>
        <span style="background-color: #eef2f7; padding: 4px 8px; border-radius: 4px;">Similarity</span> <span style="color: #ccc;">?</span>
        <span style="background-color: #eef2f7; padding: 4px 8px; border-radius: 4px;">Graph</span> <span style="color: #ccc;">?</span>
        <span style="background-color: #eef2f7; padding: 4px 8px; border-radius: 4px;">BFS/DFS</span> <span style="color: #ccc;">?</span>
        <span style="background-color: #eef2f7; padding: 4px 8px; border-radius: 4px;">Categories</span>
    </div>
</div>

<div class="dashboard-stats">
    <div class="stat-card"><h4>Documents</h4><p class="number" th:text="${documentCount}">0</p></div>
    <div class="stat-card"><h4>Total Features</h4><p class="number" th:text="${featureCount}">0</p></div>
</div>

<div class="card">
    <h3>Upload Documents</h3>
    <form action="/upload" method="post" enctype="multipart/form-data" style="margin-top: 1rem; display: flex; gap: 1rem; align-items: center;">
        <input type="file" name="files" multiple accept=".txt" required style="padding: 0.5rem; border: 1px solid var(--border); border-radius: 4px;">
        <button type="submit" class="btn">Upload Files</button>
    </form>
    <form action="/clear" method="post" style="margin-top: 1rem;">
        <button type="submit" class="btn" style="background-color: var(--error);">Clear All Data</button>
    </form>
</div>

<div class="card" th:if="${documents.size() > 0}">
    <h3>Current Document Collection</h3>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Filename</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="doc : ${documents}">
                <td th:text="${#strings.substring(doc.id,0,8)} + '...'"></td>
                <td th:text="${doc.originalFilename}"></td>
                <td><span style="background-color: var(--success); color: white; padding: 4px 8px; border-radius: 12px; font-size: 0.85rem;" th:text="${doc.currentStage}"></span></td>
                <td>
                    <form action="/delete" method="post" style="margin: 0;">
                        <input type="hidden" name="id" th:value="${doc.id}">
                        <button type="submit" style="background: none; border: none; color: var(--error); cursor: pointer; text-decoration: underline;">Delete</button>
                    </form>
                </td>
            </tr>
        </tbody>
    </table>
</div>
"""

pipeline_content = """
<style>
.pipeline-stage { background: #f8f9fa; border: 1px solid var(--border); padding: 1rem; margin-bottom: 1rem; border-radius: 6px; display: flex; justify-content: space-between; align-items: center; }
.pipeline-stage::after { content: "Not Started"; background: #e2e3e5; color: #666; padding: 4px 10px; border-radius: 12px; font-size: 0.85rem; font-weight: bold; }
.pipeline-stage.completed { border-left: 4px solid var(--success); }
.pipeline-stage.completed::after { content: "Completed"; background: #d4edda; color: #155724; }
</style>
<div class="card">
    <h3>Processing Pipeline</h3>
    <p>Displays the synchronous execution stages of the categorization engine.</p>
    
    <div class="pipeline-stage" th:classappend="${documents.size() > 0} ? 'completed' : ''"><strong>1. Document Extraction</strong><span>Reads bytes from text files into memory.</span></div>
    <div class="pipeline-stage" th:classappend="${documents.size() > 0} ? 'completed' : ''"><strong>2. Preprocessing</strong><span>Normalizes case, removes punctuation and stop-words.</span></div>
    <div class="pipeline-stage" th:classappend="${documents.size() > 0} ? 'completed' : ''"><strong>3. Feature Extraction</strong><span>Generates word frequencies via Custom Hash Map.</span></div>
    <div class="pipeline-stage" th:classappend="${documents.size() > 0} ? 'completed' : ''"><strong>4. Feature Optimization</strong><span>Timsort retains top terms bounding max features.</span></div>
    <div class="pipeline-stage" th:classappend="${documents.size() > 0} ? 'completed' : ''"><strong>5. Similarity Matrix</strong><span>Calculates NxN Jaccard overlap metrics.</span></div>
    <div class="pipeline-stage" th:classappend="${documents.size() > 0} ? 'completed' : ''"><strong>6. Graph Edges</strong><span>Constructs Adjacency List based on thresholds.</span></div>
    <div class="pipeline-stage" th:classappend="${documents.size() > 0} ? 'completed' : ''"><strong>7. BFS/DFS Traversal</strong><span>Discovers disjoint connected components.</span></div>
    <div class="pipeline-stage" th:classappend="${documents.size() > 0} ? 'completed' : ''"><strong>8. Categorization</strong><span>Assigns labels derived from subgroup term dominance.</span></div>
</div>
"""

analysis_content = """
<div class="card">
    <h3>DSA Analysis: Feature Optimization</h3>
    <p>Examine the raw structural token counting and Timsort-based feature selection bounds.</p>
    
    <div th:each="doc : ${documents}" style="margin-bottom: 2rem; border: 1px solid #eee; padding: 1rem; border-radius: 8px;">
        <h4 style="color: var(--primary); margin-top: 0;" th:text="${doc.originalFilename}"></h4>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
            <div>
                <h5 style="color: #666; border-bottom: 1px solid #eee; padding-bottom: 0.5rem;">Raw Tokens Sample (First 20)</h5>
                <p style="font-size: 0.9rem; color: #555; word-wrap: break-word;" 
                   th:text="${doc.tokens.size() > 20 ? #strings.listJoin(doc.tokens.subList(0,20), ', ') + '...' : #strings.listJoin(doc.tokens, ', ')}"></p>
            </div>
            <div>
                <h5 style="color: #666; border-bottom: 1px solid #eee; padding-bottom: 0.5rem;">Optimized Features (Max 15)</h5>
                <div style="display: flex; flex-wrap: wrap; gap: 0.5rem;">
                    <span th:each="tf : ${doc.optimizedFeatures}" 
                          style="background: #eef2f7; padding: 4px 8px; border-radius: 4px; font-size: 0.85rem; border: 1px solid #cce5ff;">
                        <strong th:text="${tf.term}"></strong> <span style="color: #888; font-size: 0.8rem;" th:text="'(' + ${tf.frequency} + ')'"></span>
                    </span>
                </div>
            </div>
        </div>
    </div>
    
    <div th:if="${documents.size() == 0}" class="alert">
        <p>No documents processed yet. Please upload files from the Dashboard.</p>
    </div>
</div>
"""

with open(os.path.join(base_dir, "index.html"), "w") as f: f.write(get_page("Dashboard", 1, index_content))
with open(os.path.join(base_dir, "pipeline.html"), "w") as f: f.write(get_page("Pipeline", 2, pipeline_content))
with open(os.path.join(base_dir, "analysis.html"), "w") as f: f.write(get_page("Analysis", 3, analysis_content))
print("Templates 1-3 complete")
