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

results_content = """
<div class="card">
    <h3>Final Categorization Results</h3>
    <p>Displays the purely algorithmic, deterministic category labeling based on graph connected components and aggregated group feature dominance.</p>
    
    <div th:each="category : ${categories}" style="background: var(--bg-light); padding: 1.5rem; border-radius: 8px; margin-bottom: 1.5rem; border-left: 4px solid var(--accent);">
        <h4 style="margin-top: 0; color: var(--primary);" th:text="${category.groupId} + ' - ' + ${category.categoryLabel}"></h4>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
            <div>
                <strong>Documents in Group:</strong>
                <ul style="margin-top: 0.5rem;">
                    <li th:each="doc : ${category.documents}" th:text="${doc.originalFilename}"></li>
                </ul>
            </div>
            <div>
                <strong>Dominant Features:</strong>
                <div style="margin-top: 0.5rem; display: flex; gap: 0.5rem; flex-wrap: wrap;">
                    <span th:each="tf : ${category.dominantFeatures}" style="background: #fff; padding: 4px 8px; border: 1px solid var(--border); border-radius: 4px; font-size: 0.85rem;" th:text="${tf.term} + ' (' + ${tf.frequency} + ')'"></span>
                </div>
            </div>
        </div>
        <div style="margin-top: 1rem; padding-top: 1rem; border-top: 1px solid var(--border); font-size: 0.9rem; color: #666;">
            <strong>Explainability:</strong> <span th:text="${category.reason}"></span>
        </div>
    </div>
    <div th:if="${categories.size() == 0}">
        <p>No documents uploaded yet.</p>
    </div>
</div>
"""

export_content = """
<div class="card">
    <h3>Export Data</h3>
    <p>Download the comprehensive JSON payload containing the dynamically generated graph topology, optimized document features, and final algorithmic category groupings.</p>
    <a href="/api/export/json" class="btn" style="background-color: var(--success);">&darr; Download JSON Results</a>
    <pre style="background-color: #1e1e1e; color: #d4d4d4; padding: 1.5rem; border-radius: 8px; font-family: 'Consolas', monospace; white-space: pre-wrap; margin-top: 2rem; max-height: 400px; overflow-y: auto;" th:text="${jsonPreview}"></pre>
</div>
"""

with open(os.path.join(base_dir, "results.html"), "w") as f: f.write(get_page("Results", 7, results_content))
with open(os.path.join(base_dir, "export.html"), "w") as f: f.write(get_page("Export", 8, export_content))
print("Templates 7-8 complete")
