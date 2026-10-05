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
        .sidebar { width: 250px; background-color: var(--primary); color: var(--text-light); padding: 2rem 1rem; }
        .sidebar h2 { font-size: 1.2rem; margin-bottom: 2rem; border-bottom: 1px solid var(--secondary); padding-bottom: 1rem; }
        .nav-menu { list-style: none; padding: 0; }
        .nav-menu a { color: var(--text-light); text-decoration: none; display: block; padding: 0.8rem; border-radius: 5px; }
        .nav-menu a.active { background-color: var(--accent); }
        .main-content { flex: 1; padding: 2rem; }
        .card { background-color: var(--bg-white); padding: 2rem; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
    </style>
</head>
<body>
    <div class="sidebar">
        <h2>Optimization Framework</h2>
        <ul class="nav-menu">
            <li><a href="/" {nav1}>Dashboard & Upload</a></li>
            <li><a href="/export" {nav2}>Export</a></li>
        </ul>
    </div>
    <div class="main-content">
        <div class="card">
            {content}
        </div>
    </div>
</body>
</html>"""

t_index = html_base.replace("{title}", "Dashboard").replace("{nav1}", 'class="active"').replace("{nav2}", "")
t_index = t_index.replace("{content}", """
<h1 style="color: var(--primary); font-size: 1.8rem;">Optimization Framework for Document Categorization</h1>
<p style="color: var(--secondary); font-size: 1.1rem;">DSA-Based Document Processing, Feature Optimization & Categorization System</p>
<h3>Documents Uploaded: <span th:text="${documentCount}">0</span></h3>
<form action="/upload" method="post" enctype="multipart/form-data">
    <input type="file" name="files" multiple accept=".txt">
    <button type="submit">Upload</button>
</form>
<form action="/clear" method="post" style="margin-top:1rem;"><button type="submit">Clear All</button></form>
""")

t_export = html_base.replace("{title}", "Export").replace("{nav1}", "").replace("{nav2}", 'class="active"')
t_export = t_export.replace("{content}", """
<h3>Export Data</h3>
<a href="/api/export/json" style="padding:10px; background:var(--success); color:white; text-decoration:none; border-radius:4px;">Download JSON Results</a>
<pre style="background:#1e1e1e; color:#d4d4d4; padding:1rem; border-radius:4px; margin-top:2rem;" th:text="${jsonPreview}"></pre>
""")

with open(os.path.join(base_dir, "index.html"), "w") as f: f.write(t_index)
with open(os.path.join(base_dir, "export.html"), "w") as f: f.write(t_export)
print("Templates complete")
