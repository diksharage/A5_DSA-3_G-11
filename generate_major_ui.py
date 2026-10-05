import os

base_dir = r"C:\Users\Diksha\OneDrive\Desktop\Optimization Frame Work\src\main\resources\templates"

CSS_BASE = """
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

:root {
    --bg-color: #f8fafc;
    --sidebar-bg: #0B1120;
    --sidebar-border: #1e293b;
    --sidebar-text: #94a3b8;
    --sidebar-text-active: #ffffff;
    --sidebar-hover: #1e293b;
    --primary: #0F172A;
    --secondary: #4C1D95;
    --accent: #2563EB;
    --success: #10B981;
    --error: #EF4444;
    --card-bg: #ffffff;
    --text-main: #0f172a;
    --text-muted: #64748b;
    --border: #e2e8f0;
}

body {
    font-family: 'Inter', sans-serif;
    margin: 0; padding: 0;
    background-color: var(--bg-color);
    color: var(--text-main);
    display: flex; min-height: 100vh;
    background-image: url("data:image/svg+xml,%3Csvg width='100' height='100' xmlns='http://www.w3.org/2000/svg'%3E%3Ccircle cx='20' cy='20' r='2' fill='%2394A3B8' opacity='0.3'/%3E%3Ccircle cx='80' cy='50' r='1.5' fill='%2394A3B8' opacity='0.3'/%3E%3Ccircle cx='40' cy='90' r='2' fill='%2394A3B8' opacity='0.3'/%3E%3Cline x1='20' y1='20' x2='80' y2='50' stroke='%2394A3B8' stroke-width='0.5' opacity='0.15'/%3E%3Cline x1='80' y1='50' x2='40' y2='90' stroke='%2394A3B8' stroke-width='0.5' opacity='0.15'/%3E%3Cline x1='40' y1='90' x2='20' y2='20' stroke='%2394A3B8' stroke-width='0.5' opacity='0.15'/%3E%3C/svg%3E");
    background-attachment: fixed;
}

/* Sidebar */
.sidebar { width: 280px; background: linear-gradient(180deg, var(--sidebar-bg) 0%, #0f172a 100%); color: var(--sidebar-text); display: flex; flex-direction: column; position: fixed; height: 100vh; border-right: 1px solid var(--sidebar-border); z-index: 50; }
.brand { padding: 2rem 1.5rem; display: flex; align-items: center; gap: 1rem; border-bottom: 1px solid var(--sidebar-border); }
.brand-icon { width: 40px; height: 40px; background: linear-gradient(135deg, var(--accent), var(--secondary)); border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 1.25rem; color: #fff; box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3); }
.brand-text { display: flex; flex-direction: column; }
.brand-title { font-size: 1rem; font-weight: 700; color: #fff; letter-spacing: 0.5px; line-height: 1.2; }
.brand-subtitle { font-size: 0.65rem; font-weight: 600; color: var(--accent); letter-spacing: 1px; margin-top: 0.25rem; }

.nav-container { padding: 1.5rem 1rem; overflow-y: auto; flex: 1; }
.nav-section { font-size: 0.7rem; font-weight: 700; color: #475569; letter-spacing: 1.5px; margin: 1.5rem 0 0.5rem 1rem; }
.nav-item { display: flex; align-items: center; gap: 1rem; padding: 0.75rem 1rem; text-decoration: none; color: var(--sidebar-text); border-radius: 8px; margin-bottom: 0.25rem; font-size: 0.9rem; font-weight: 500; transition: all 0.2s ease; position: relative; }
.nav-item i { width: 20px; text-align: center; font-size: 1rem; transition: color 0.2s; }
.nav-item:hover { background-color: var(--sidebar-hover); color: var(--sidebar-text-active); }
.nav-item.active { background: linear-gradient(90deg, rgba(37, 99, 235, 0.1) 0%, transparent 100%); color: var(--sidebar-text-active); border-left: 3px solid var(--accent); border-radius: 0 8px 8px 0; }
.nav-item.active i { color: var(--accent); }

/* Main Content */
.main-wrapper { flex: 1; margin-left: 280px; padding: 3rem 4rem; display: flex; flex-direction: column; gap: 2rem; max-width: 1400px; }

/* Dashboard Hero */
.hero { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1rem; }
.hero-content h1 { margin: 0 0 0.5rem 0; font-size: 2.25rem; font-weight: 300; color: var(--text-main); letter-spacing: -0.5px; }
.hero-highlight { font-weight: 700; background: linear-gradient(90deg, var(--primary), var(--accent)); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
.hero-content p { margin: 0; font-size: 1.1rem; color: var(--text-muted); max-width: 600px; line-height: 1.5; }
.system-status { display: flex; align-items: center; gap: 0.5rem; background: #fff; padding: 0.5rem 1rem; border-radius: 999px; border: 1px solid var(--border); font-size: 0.75rem; font-weight: 700; color: var(--text-main); letter-spacing: 1px; box-shadow: 0 2px 4px rgba(0,0,0,0.02); }
.status-dot { width: 8px; height: 8px; background-color: var(--success); border-radius: 50%; box-shadow: 0 0 8px var(--success); }

/* Cards */
.card { background: var(--card-bg); border-radius: 12px; padding: 2rem; border: 1px solid var(--border); box-shadow: 0 4px 6px -1px rgba(0,0,0,0.02), 0 2px 4px -1px rgba(0,0,0,0.02); position: relative; overflow: hidden; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; border-bottom: 1px solid var(--border); padding-bottom: 1rem; }
.card-title { margin: 0; font-size: 1.1rem; font-weight: 600; color: var(--text-main); display: flex; align-items: center; gap: 0.75rem; }
.card-title i { color: var(--accent); }

/* Metrics */
.metrics-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1.5rem; }
.metric-card { background: #fff; padding: 1.5rem; border-radius: 12px; border: 1px solid var(--border); display: flex; align-items: center; gap: 1.25rem; transition: transform 0.2s, box-shadow 0.2s; box-shadow: 0 2px 4px rgba(0,0,0,0.02); }
.metric-card:hover { transform: translateY(-3px); box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05); }
.metric-icon { width: 50px; height: 50px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }
.mi-1 { background: #eff6ff; color: #3b82f6; }
.mi-2 { background: #f5f3ff; color: #8b5cf6; }
.mi-3 { background: #ecfeff; color: #06b6d4; }
.mi-4 { background: #ecfdf5; color: #10b981; }
.metric-info h4 { margin: 0 0 0.25rem 0; font-size: 0.7rem; text-transform: uppercase; letter-spacing: 1px; color: var(--text-muted); font-weight: 700; }
.metric-info .val { margin: 0 0 0.25rem 0; font-size: 1.75rem; font-weight: 700; color: var(--text-main); line-height: 1; }
.metric-info p { margin: 0; font-size: 0.75rem; color: var(--text-muted); }

/* Horizontal Pipeline */
.horiz-pipeline { display: flex; align-items: center; justify-content: space-between; margin-top: 1rem; background: #f8fafc; padding: 1.5rem 2rem; border-radius: 8px; border: 1px solid #e2e8f0; }
.hp-node { display: flex; flex-direction: column; align-items: center; gap: 0.5rem; z-index: 2; width: 60px; }
.hp-circle { width: 24px; height: 24px; border-radius: 50%; background: #fff; border: 2px solid #cbd5e1; display: flex; align-items: center; justify-content: center; transition: all 0.3s; }
.hp-circle i { font-size: 0.6rem; color: transparent; }
.hp-label { font-size: 0.65rem; font-weight: 700; color: #64748b; text-transform: uppercase; text-align: center; }
.hp-line { flex: 1; height: 2px; background: #cbd5e1; margin: -20px 0 0 0; z-index: 1; transition: background 0.3s; }
.hp-node.completed .hp-circle { background: var(--success); border-color: var(--success); }
.hp-node.completed .hp-circle i { color: #fff; }
.hp-node.completed .hp-label { color: var(--success); }
.hp-line.completed { background: var(--success); }

/* Drag Drop Upload */
.upload-zone { border: 2px dashed #cbd5e1; border-radius: 12px; padding: 3rem 2rem; text-align: center; background: #f8fafc; cursor: pointer; transition: all 0.2s; }
.upload-zone:hover { border-color: var(--accent); background: #eff6ff; }
.upload-zone i { font-size: 3rem; color: #94a3b8; margin-bottom: 1rem; transition: color 0.2s; }
.upload-zone:hover i { color: var(--accent); }
.upload-zone h3 { margin: 0 0 0.5rem 0; font-size: 1rem; font-weight: 700; letter-spacing: 1px; color: var(--text-main); }
.upload-zone p { margin: 0; font-size: 0.9rem; color: var(--text-muted); }
.browse-btn { display: inline-block; margin-top: 1rem; background: #fff; border: 1px solid var(--border); padding: 0.5rem 1.25rem; border-radius: 6px; font-weight: 600; font-size: 0.85rem; box-shadow: 0 1px 2px rgba(0,0,0,0.05); }

/* Tables */
.modern-table { width: 100%; border-collapse: separate; border-spacing: 0; font-size: 0.9rem; }
.modern-table th { background: #f8fafc; padding: 1rem; text-align: left; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 1px; color: var(--text-muted); font-weight: 600; border-bottom: 1px solid var(--border); border-top: 1px solid var(--border); }
.modern-table th:first-child { border-left: 1px solid var(--border); border-top-left-radius: 8px; border-bottom-left-radius: 8px; }
.modern-table th:last-child { border-right: 1px solid var(--border); border-top-right-radius: 8px; border-bottom-right-radius: 8px; }
.modern-table td { padding: 1rem; border-bottom: 1px solid #f1f5f9; vertical-align: middle; }
.modern-table tr:hover td { background: #f8fafc; }
.badge { padding: 0.25rem 0.75rem; border-radius: 999px; font-size: 0.7rem; font-weight: 600; letter-spacing: 0.5px; display: inline-flex; align-items: center; gap: 0.25rem; }
.badge-success { background: #ecfdf5; color: #059669; border: 1px solid #a7f3d0; }
.badge-primary { background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; }

/* Buttons */
.btn-primary { background: var(--accent); color: #fff; border: none; padding: 0.6rem 1.25rem; border-radius: 6px; font-size: 0.85rem; font-weight: 600; cursor: pointer; transition: all 0.2s; text-decoration: none; display: inline-flex; align-items: center; gap: 0.5rem; }
.btn-primary:hover { background: #1d4ed8; box-shadow: 0 4px 6px -1px rgba(37,99,235,0.3); }
.btn-danger { background: #fff; color: var(--error); border: 1px solid #fecaca; padding: 0.4rem 0.75rem; border-radius: 6px; font-size: 0.85rem; cursor: pointer; transition: all 0.2s; }
.btn-danger:hover { background: #fef2f2; border-color: var(--error); }

/* DSA Grid */
.dsa-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 1rem; }
.dsa-chip { background: #f8fafc; border: 1px solid var(--border); padding: 1rem; border-radius: 8px; text-align: center; font-size: 0.85rem; font-weight: 600; color: var(--text-main); display: flex; flex-direction: column; align-items: center; gap: 0.5rem; transition: transform 0.2s; }
.dsa-chip:hover { transform: translateY(-2px); border-color: var(--accent); color: var(--accent); }
.dsa-chip i { font-size: 1.25rem; color: var(--text-muted); }
.dsa-chip:hover i { color: var(--accent); }
"""

def generate_layout(title, nav_idx, content):
    navs = [''] * 9
    navs[nav_idx] = 'active'
    
    html = """<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>TITLE - Optimization Framework</title>
    <style>
        CSS_BASE
    </style>
</head>
<body>
    <div class="sidebar">
        <div class="brand">
            <div class="brand-icon"><i class="fa-solid fa-diagram-project"></i></div>
            <div class="brand-text">
                <div class="brand-title">OPTIMIZATION<br>FRAMEWORK</div>
                <div class="brand-subtitle">DSA DOCUMENT ENGINE</div>
            </div>
        </div>
        <div class="nav-container">
            <div class="nav-section">OVERVIEW</div>
            <a href="/" class="nav-item NAV1"><i class="fa-solid fa-gauge"></i> Dashboard & Upload</a>
            
            <div class="nav-section">PROCESSING</div>
            <a href="/pipeline" class="nav-item NAV2"><i class="fa-solid fa-bars-progress"></i> Processing Pipeline</a>
            <a href="/analysis" class="nav-item NAV3"><i class="fa-solid fa-microscope"></i> DSA Analysis</a>
            
            <div class="nav-section">ALGORITHMS</div>
            <a href="/string-algorithms" class="nav-item NAV4"><i class="fa-solid fa-code"></i> String Algorithms</a>
            <a href="/similarity" class="nav-item NAV5"><i class="fa-solid fa-not-equal"></i> Similarity Matrix</a>
            <a href="/graph" class="nav-item NAV6"><i class="fa-solid fa-network-wired"></i> Graph Visualization</a>
            
            <div class="nav-section">RESULTS</div>
            <a href="/results" class="nav-item NAV7"><i class="fa-solid fa-layer-group"></i> Results & Categories</a>
            <a href="/export" class="nav-item NAV8"><i class="fa-solid fa-file-export"></i> Export JSON</a>
        </div>
    </div>
    
    <div class="main-wrapper">
        <th:block th:with="graph=${@graphService.buildGraph(@documentService.getAllDocuments(), @graphService.getCurrentThreshold())}, categories=${@categoryService.identifyCategories(graph.findConnectedComponents())}">
            CONTENT
        </th:block>
    </div>
</body>
</html>
"""
    html = html.replace("TITLE", title)
    html = html.replace("CSS_BASE", CSS_BASE)
    for i in range(1, 9):
        html = html.replace(f"NAV{i}", navs[i])
    html = html.replace("CONTENT", content)
    return html

INDEX_CONTENT = """
<div class="hero">
    <div class="hero-content">
        <h1>Optimization Framework<br><span class="hero-highlight">Document Processing & Categorization</span></h1>
        <p>A DSA-driven system for extracting, optimizing, comparing and grouping documents using custom algorithmic techniques.</p>
    </div>
    <div class="system-status">
        <div class="status-dot"></div> SYSTEM READY
    </div>
</div>

<div class="metrics-grid">
    <div class="metric-card">
        <div class="metric-icon mi-1"><i class="fa-regular fa-file-lines"></i></div>
        <div class="metric-info"><h4>Documents</h4><p class="val" th:text="${documentCount}">0</p><p>Processed documents</p></div>
    </div>
    <div class="metric-card">
        <div class="metric-icon mi-2"><i class="fa-solid fa-tags"></i></div>
        <div class="metric-info"><h4>Features</h4><p class="val" th:text="${featureCount}">0</p><p>Optimized terms</p></div>
    </div>
    <div class="metric-card">
        <div class="metric-icon mi-3"><i class="fa-solid fa-link"></i></div>
        <div class="metric-info"><h4>Graph Edges</h4><p class="val" th:text="${graph.edgeCount}">0</p><p>Similarity relationships</p></div>
    </div>
    <div class="metric-card">
        <div class="metric-icon mi-4"><i class="fa-solid fa-layer-group"></i></div>
        <div class="metric-info"><h4>Categories</h4><p class="val" th:text="${categories.size()}">0</p><p>Detected groups</p></div>
    </div>
</div>

<div class="card">
    <div class="card-header"><h3 class="card-title"><i class="fa-solid fa-timeline"></i> Processing Pipeline</h3></div>
    <div class="horiz-pipeline">
        <div class="hp-node" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="hp-circle"><i class="fa-solid fa-check"></i></div><div class="hp-label">Upload</div></div>
        <div class="hp-line" th:classappend="${documents.size() > 0 ? 'completed' : ''}"></div>
        <div class="hp-node" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="hp-circle"><i class="fa-solid fa-check"></i></div><div class="hp-label">Extract</div></div>
        <div class="hp-line" th:classappend="${documents.size() > 0 ? 'completed' : ''}"></div>
        <div class="hp-node" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="hp-circle"><i class="fa-solid fa-check"></i></div><div class="hp-label">Preprocess</div></div>
        <div class="hp-line" th:classappend="${documents.size() > 0 ? 'completed' : ''}"></div>
        <div class="hp-node" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="hp-circle"><i class="fa-solid fa-check"></i></div><div class="hp-label">Features</div></div>
        <div class="hp-line" th:classappend="${documents.size() > 0 ? 'completed' : ''}"></div>
        <div class="hp-node" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="hp-circle"><i class="fa-solid fa-check"></i></div><div class="hp-label">Optimize</div></div>
        <div class="hp-line" th:classappend="${documents.size() > 0 ? 'completed' : ''}"></div>
        <div class="hp-node" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="hp-circle"><i class="fa-solid fa-check"></i></div><div class="hp-label">Similarity</div></div>
        <div class="hp-line" th:classappend="${documents.size() > 0 ? 'completed' : ''}"></div>
        <div class="hp-node" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="hp-circle"><i class="fa-solid fa-check"></i></div><div class="hp-label">Graph</div></div>
        <div class="hp-line" th:classappend="${documents.size() > 0 ? 'completed' : ''}"></div>
        <div class="hp-node" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="hp-circle"><i class="fa-solid fa-check"></i></div><div class="hp-label">BFS/DFS</div></div>
        <div class="hp-line" th:classappend="${documents.size() > 0 ? 'completed' : ''}"></div>
        <div class="hp-node" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="hp-circle"><i class="fa-solid fa-check"></i></div><div class="hp-label">Categories</div></div>
    </div>
</div>

<div style="display: grid; grid-template-columns: 1fr 2fr; gap: 2rem;">
    <!-- Upload Area -->
    <div class="card" style="height: fit-content;">
        <div class="card-header"><h3 class="card-title"><i class="fa-solid fa-cloud-arrow-up"></i> Upload Documents</h3></div>
        <div class="upload-zone" onclick="document.getElementById('fileInput').click()">
            <i class="fa-solid fa-file-arrow-up"></i>
            <h3>DROP DOCUMENTS HERE</h3>
            <p>Drag & drop files here<br>or<br><span class="browse-btn">Browse Files</span></p>
            <form action="/upload" method="post" enctype="multipart/form-data" id="uploadForm" style="display:none;">
                <input type="file" id="fileInput" name="files" multiple accept=".txt" onchange="document.getElementById('uploadForm').submit()">
            </form>
        </div>
        <div style="margin-top: 1.5rem;" th:if="${documents.size() > 0}">
            <h4 style="margin:0 0 1rem 0; font-size: 0.85rem; color: var(--text-muted); text-transform: uppercase;">Document Groups Detected</h4>
            <div th:each="cat : ${categories}" style="background: #f8fafc; border: 1px solid var(--border); padding: 1rem; border-radius: 8px; margin-bottom: 0.5rem; display: flex; justify-content: space-between; align-items: center;">
                <div style="font-weight: 600; font-size: 0.9rem; color: var(--secondary);"><i class="fa-solid fa-folder-tree" style="margin-right: 0.5rem;"></i> [[${cat.categoryLabel}]]</div>
                <div class="badge badge-primary">[[${cat.documents.size()}]] docs</div>
            </div>
        </div>
    </div>

    <!-- Document Overview -->
    <div class="card" style="display: flex; flex-direction: column;">
        <div class="card-header">
            <h3 class="card-title"><i class="fa-solid fa-folder-open"></i> Current Document Collection</h3>
            <form action="/clear" method="post" style="margin:0;"><button class="btn-danger"><i class="fa-solid fa-trash-can"></i> Clear Data</button></form>
        </div>
        
        <div th:if="${documents.size() == 0}" style="text-align: center; padding: 4rem 2rem; color: var(--text-muted); flex: 1; display: flex; flex-direction: column; justify-content: center; align-items: center; background: #f8fafc; border-radius: 8px; border: 1px dashed var(--border);">
            <i class="fa-regular fa-folder-open" style="font-size: 3rem; margin-bottom: 1rem; color: #cbd5e1;"></i>
            <h4 style="margin: 0 0 0.5rem 0; color: var(--text-main);">NO DOCUMENTS PROCESSED</h4>
            <p style="margin: 0;">Upload documents to begin the DSA processing pipeline.</p>
        </div>
        
        <table class="modern-table" th:if="${documents.size() > 0}">
            <thead><tr><th>Document</th><th>Size</th><th>Features</th><th>Status</th><th>Action</th></tr></thead>
            <tbody>
                <tr th:each="doc : ${documents}">
                    <td>
                        <div style="display: flex; align-items: center; gap: 0.75rem;">
                            <div style="width:32px; height:32px; background:#eff6ff; color:#3b82f6; border-radius:6px; display:flex; align-items:center; justify-content:center;"><i class="fa-regular fa-file-lines"></i></div>
                            <div>
                                <div style="font-weight: 600; color: var(--text-main);" th:text="${doc.originalFilename}"></div>
                                <div style="font-size: 0.7rem; color: var(--text-muted); font-family: monospace;" th:text="'ID: ' + ${#strings.substring(doc.id,0,8)}"></div>
                            </div>
                        </div>
                    </td>
                    <td th:text="${doc.fileSize + ' B'}"></td>
                    <td><span class="badge badge-primary" th:text="${doc.optimizedFeatures.size()}"></span></td>
                    <td><span class="badge badge-success"><i class="fa-solid fa-check-circle"></i> [[${doc.currentStage}]]</span></td>
                    <td>
                        <form action="/delete" method="post" style="margin:0;">
                            <input type="hidden" name="id" th:value="${doc.id}">
                            <button class="btn-danger" style="padding: 0.35rem 0.6rem;"><i class="fa-solid fa-xmark"></i></button>
                        </form>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>

<div class="card" style="margin-top: 1rem;">
    <div class="card-header" style="margin-bottom: 1rem;"><h3 class="card-title"><i class="fa-solid fa-microchip"></i> Active DSA Engine Components</h3></div>
    <div class="dsa-grid">
        <div class="dsa-chip"><i class="fa-solid fa-hashtag"></i> Hash Frequency</div>
        <div class="dsa-chip"><i class="fa-solid fa-magnifying-glass"></i> Naive Matching</div>
        <div class="dsa-chip"><i class="fa-solid fa-forward-step"></i> KMP Algorithm</div>
        <div class="dsa-chip"><i class="fa-solid fa-z"></i> Z Algorithm</div>
        <div class="dsa-chip"><i class="fa-solid fa-fingerprint"></i> Rabin-Karp</div>
        <div class="dsa-chip"><i class="fa-solid fa-network-wired"></i> Aho-Corasick</div>
        <div class="dsa-chip"><i class="fa-solid fa-ruler-combined"></i> Jaccard Similarity</div>
        <div class="dsa-chip"><i class="fa-solid fa-diagram-project"></i> Graph BFS/DFS</div>
    </div>
</div>
"""

PIPELINE_CONTENT = """
<style>
.v-timeline { padding: 2rem 0; max-width: 900px; margin: 0 auto; position: relative; }
.v-timeline::before { content:''; position:absolute; left:28px; top:2rem; bottom:2rem; width:2px; background:var(--border); }
.vt-item { position: relative; padding-left: 5rem; margin-bottom: 2rem; }
.vt-number { position: absolute; left: 14px; top: 0; width: 30px; height: 30px; border-radius: 50%; background: #fff; border: 2px solid var(--border); display: flex; align-items: center; justify-content: center; font-size: 0.75rem; font-weight: 700; color: var(--text-muted); z-index: 2; transition: all 0.3s; }
.vt-card { background: #fff; border: 1px solid var(--border); border-radius: 12px; padding: 1.5rem; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.02); display: flex; justify-content: space-between; align-items: center; transition: all 0.3s; }
.vt-title { margin: 0 0 0.25rem 0; font-size: 1.1rem; font-weight: 700; color: var(--text-main); }
.vt-desc { margin: 0; font-size: 0.9rem; color: var(--text-muted); }
.vt-item.completed .vt-number { background: var(--success); border-color: var(--success); color: #fff; }
.vt-item.completed .vt-card { border-left: 4px solid var(--success); }
.vt-status { font-size: 0.75rem; font-weight: 700; letter-spacing: 1px; text-transform: uppercase; padding: 0.4rem 1rem; border-radius: 999px; background: #f1f5f9; color: #64748b; }
.vt-item.completed .vt-status { background: #ecfdf5; color: #059669; }
</style>
<div class="hero"><div class="hero-content"><h1>Processing <span class="hero-highlight">Pipeline</span></h1><p>Algorithmic execution timeline of the categorization engine.</p></div></div>

<div class="card">
    <div class="v-timeline">
        <div class="vt-item" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="vt-number">01</div><div class="vt-card"><div><h4 class="vt-title">Document Extraction</h4><p class="vt-desc">Reads raw byte streams into memory buffers and initializes data structures.</p></div><div class="vt-status" th:text="${documents.size()>0?'Completed':'Pending'}"></div></div></div>
        <div class="vt-item" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="vt-number">02</div><div class="vt-card"><div><h4 class="vt-title">Preprocessing</h4><p class="vt-desc">Normalizes case, removes punctuation and eliminates common stop-words.</p></div><div class="vt-status" th:text="${documents.size()>0?'Completed':'Pending'}"></div></div></div>
        <div class="vt-item" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="vt-number">03</div><div class="vt-card"><div><h4 class="vt-title">Feature Extraction</h4><p class="vt-desc">Generates word frequencies mapped via Custom Hash structures.</p></div><div class="vt-status" th:text="${documents.size()>0?'Completed':'Pending'}"></div></div></div>
        <div class="vt-item" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="vt-number">04</div><div class="vt-card"><div><h4 class="vt-title">Feature Optimization</h4><p class="vt-desc">Applies Timsort bounding to retain only high-value dominant terms.</p></div><div class="vt-status" th:text="${documents.size()>0?'Completed':'Pending'}"></div></div></div>
        <div class="vt-item" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="vt-number">05</div><div class="vt-card"><div><h4 class="vt-title">Similarity Matrix</h4><p class="vt-desc">Calculates NxN Jaccard overlap metrics across optimized feature sets.</p></div><div class="vt-status" th:text="${documents.size()>0?'Completed':'Pending'}"></div></div></div>
        <div class="vt-item" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="vt-number">06</div><div class="vt-card"><div><h4 class="vt-title">Graph Construction</h4><p class="vt-desc">Constructs an Adjacency List linking documents passing the similarity threshold.</p></div><div class="vt-status" th:text="${documents.size()>0?'Completed':'Pending'}"></div></div></div>
        <div class="vt-item" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="vt-number">07</div><div class="vt-card"><div><h4 class="vt-title">BFS / DFS</h4><p class="vt-desc">Traverses the graph to discover disjoint connected components.</p></div><div class="vt-status" th:text="${documents.size()>0?'Completed':'Pending'}"></div></div></div>
        <div class="vt-item" th:classappend="${documents.size() > 0 ? 'completed' : ''}"><div class="vt-number">08</div><div class="vt-card"><div><h4 class="vt-title">Categorization</h4><p class="vt-desc">Assigns human-readable labels derived from subgroup term dominance.</p></div><div class="vt-status" th:text="${documents.size()>0?'Completed':'Pending'}"></div></div></div>
    </div>
</div>
"""

ANALYSIS_CONTENT = """
<style>
.flow-diagram { display: flex; align-items: center; justify-content: space-between; background: #f8fafc; padding: 2rem; border-radius: 12px; border: 1px solid var(--border); margin-bottom: 2rem; }
.fd-node { text-align: center; width: 22%; }
.fd-icon { width: 48px; height: 48px; background: #fff; border: 2px solid var(--accent); color: var(--accent); border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; margin: 0 auto 0.75rem auto; box-shadow: 0 4px 6px rgba(37,99,235,0.1); }
.fd-node h5 { margin: 0; font-size: 0.8rem; text-transform: uppercase; letter-spacing: 1px; color: var(--text-main); }
.fd-arrow { color: #cbd5e1; font-size: 1.5rem; }
.chip { display: inline-flex; align-items: center; gap: 0.4rem; background: #fff; border: 1px solid var(--border); padding: 0.35rem 0.75rem; border-radius: 6px; font-size: 0.85rem; font-family: monospace; margin: 0.25rem; box-shadow: 0 1px 2px rgba(0,0,0,0.02); }
.chip-opt { background: #eff6ff; border-color: #bfdbfe; color: #1e40af; }
.freq-badge { background: #dbeafe; color: #1e40af; padding: 0.15rem 0.4rem; border-radius: 4px; font-size: 0.7rem; font-weight: 700; }
</style>
<div class="hero"><div class="hero-content"><h1>Algorithm <span class="hero-highlight">Analysis</span></h1><p>Inspect the transformation from raw tokens to frequency-ranked optimized features.</p></div></div>

<div class="card">
    <div class="flow-diagram">
        <div class="fd-node"><div class="fd-icon"><i class="fa-solid fa-quote-left"></i></div><h5>Raw Tokens</h5></div>
        <div class="fd-arrow"><i class="fa-solid fa-arrow-right"></i></div>
        <div class="fd-node"><div class="fd-icon"><i class="fa-solid fa-hashtag"></i></div><h5>Frequency Count</h5></div>
        <div class="fd-arrow"><i class="fa-solid fa-arrow-right"></i></div>
        <div class="fd-node"><div class="fd-icon"><i class="fa-solid fa-sort-amount-down"></i></div><h5>Feature Ranking</h5></div>
        <div class="fd-arrow"><i class="fa-solid fa-arrow-right"></i></div>
        <div class="fd-node"><div class="fd-icon"><i class="fa-solid fa-gem"></i></div><h5>Optimized Features</h5></div>
    </div>
    
    <div th:if="${documents.size() == 0}" style="text-align: center; padding: 3rem; color: var(--text-muted); border: 1px dashed var(--border); border-radius: 12px; background: #f8fafc;">
        <i class="fa-solid fa-microscope" style="font-size: 3rem; margin-bottom: 1rem; color: #cbd5e1;"></i>
        <h4>NO DOCUMENTS ANALYZED</h4>
        <p>Please upload documents on the dashboard to visualize feature extraction.</p>
    </div>

    <div th:each="doc : ${documents}" style="border: 1px solid var(--border); border-radius: 12px; padding: 1.5rem; margin-bottom: 1.5rem; background: #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.02);">
        <h4 style="margin: 0 0 1.5rem 0; color: var(--text-main); font-size: 1.1rem; border-bottom: 1px solid #f1f5f9; padding-bottom: 0.75rem;"><i class="fa-regular fa-file-code" style="color:var(--text-muted); margin-right:0.5rem;"></i> [[${doc.originalFilename}]]</h4>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
            <div style="background: #f8fafc; padding: 1.25rem; border-radius: 8px; border: 1px solid var(--border);">
                <h5 style="margin: 0 0 1rem 0; font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 1px;"><i class="fa-solid fa-align-left"></i> Raw Tokens (Sample)</h5>
                <div>
                    <span class="chip" th:each="t, iterStat : ${doc.tokens}" th:if="${iterStat.index < 25}" th:text="${t}"></span>
                    <span th:if="${doc.tokens.size() > 25}" style="color:var(--text-muted); font-size:0.8rem; margin-left: 0.5rem; font-weight:600;">... [[${doc.tokens.size() - 25}]] more</span>
                </div>
            </div>
            <div style="background: #f8fafc; padding: 1.25rem; border-radius: 8px; border: 1px solid var(--border); border-top: 4px solid var(--accent);">
                <h5 style="margin: 0 0 1rem 0; font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 1px;"><i class="fa-solid fa-gem"></i> Optimized Features</h5>
                <div>
                    <span class="chip chip-opt" th:each="tf : ${doc.optimizedFeatures}">
                        <strong th:text="${tf.term}"></strong> <span class="freq-badge" th:text="${tf.frequency}"></span>
                    </span>
                </div>
            </div>
        </div>
    </div>
</div>
"""

STRING_CONTENT = """
<style>
.algo-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem; }
.al-card { background: #fff; border: 1px solid var(--border); padding: 1.25rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.02); }
.al-card h4 { margin: 0 0 0.5rem 0; font-size: 0.95rem; color: var(--text-main); }
.al-comp { font-family: monospace; font-size: 0.8rem; color: var(--accent); background: #eff6ff; padding: 0.2rem 0.5rem; border-radius: 4px; display: inline-block; }
.lab-container { display: grid; grid-template-columns: 2fr 1fr; gap: 2rem; }
.lab-input { width: 100%; padding: 0.8rem; border: 1px solid var(--border); border-radius: 6px; font-family: monospace; font-size: 0.9rem; margin-bottom: 1rem; box-sizing: border-box; background: #f8fafc; }
.lab-input:focus { outline: none; border-color: var(--accent); box-shadow: 0 0 0 3px rgba(37,99,235,0.1); background: #fff; }
.lab-label { display: block; font-size: 0.8rem; font-weight: 700; color: var(--text-muted); text-transform: uppercase; letter-spacing: 1px; margin-bottom: 0.5rem; }
.radio-list { display: flex; flex-direction: column; gap: 0.5rem; }
.radio-item { display: flex; align-items: center; gap: 0.75rem; padding: 0.8rem 1rem; background: #fff; border: 1px solid var(--border); border-radius: 6px; cursor: pointer; font-size: 0.9rem; font-weight: 500; transition: all 0.2s; }
.radio-item:hover { border-color: var(--accent); }
.radio-item input { accent-color: var(--accent); transform: scale(1.2); }
.res-panel { background: #0f172a; color: #f8fafc; border-radius: 12px; padding: 2rem; margin-top: 2rem; box-shadow: inset 0 2px 10px rgba(0,0,0,0.5); }
.res-header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #1e293b; padding-bottom: 1rem; margin-bottom: 1.5rem; }
.res-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; }
.res-box { background: #1e293b; padding: 1.25rem; border-radius: 8px; border: 1px solid #334155; }
.res-label { font-size: 0.75rem; color: #94a3b8; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 0.5rem; }
.res-val { font-family: monospace; font-size: 1.1rem; color: #38bdf8; word-break: break-all; }
</style>
<div class="hero"><div class="hero-content"><h1>String Algorithm <span class="hero-highlight">Laboratory</span></h1><p>Execute and analyze exact string matching implementations.</p></div></div>

<div class="algo-cards">
    <div class="al-card"><h4>Naive</h4><div class="al-comp">O(N*M)</div></div>
    <div class="al-card"><h4>KMP</h4><div class="al-comp">O(N+M)</div></div>
    <div class="al-card"><h4>Z Algorithm</h4><div class="al-comp">O(N+M)</div></div>
    <div class="al-card"><h4>Rabin-Karp</h4><div class="al-comp">O(N+M) avg</div></div>
    <div class="al-card"><h4>Aho-Corasick</h4><div class="al-comp">O(N+M+Z)</div></div>
</div>

<div class="card">
    <form action="/string-algorithms/run" method="post">
        <div class="lab-container">
            <div>
                <label class="lab-label">Corpus Text (N)</label>
                <textarea name="text" class="lab-input" rows="8" required th:text="${inputText}"></textarea>
                <label class="lab-label">Search Pattern (M)</label>
                <input type="text" name="pattern" class="lab-input" placeholder="Pattern (comma-separated for Aho-Corasick)" required th:value="${inputPattern}">
            </div>
            <div>
                <label class="lab-label">Implementation</label>
                <div class="radio-list">
                    <label class="radio-item"><input type="radio" name="algorithm" value="naive" th:checked="${selectedAlgorithm == null or selectedAlgorithm == 'naive'}"> Naive Matching</label>
                    <label class="radio-item"><input type="radio" name="algorithm" value="kmp" th:checked="${selectedAlgorithm == 'kmp'}"> KMP</label>
                    <label class="radio-item"><input type="radio" name="algorithm" value="z" th:checked="${selectedAlgorithm == 'z'}"> Z Algorithm</label>
                    <label class="radio-item"><input type="radio" name="algorithm" value="rabin" th:checked="${selectedAlgorithm == 'rabin'}"> Rabin-Karp</label>
                    <label class="radio-item"><input type="radio" name="algorithm" value="ahocorasick" th:checked="${selectedAlgorithm == 'ahocorasick'}"> Aho-Corasick</label>
                </div>
                <button type="submit" class="btn-primary" style="width: 100%; justify-content: center; margin-top: 1.5rem; padding: 0.8rem;"><i class="fa-solid fa-play"></i> Execute Algorithm</button>
            </div>
        </div>
    </form>
</div>

<div class="res-panel" th:if="${result != null}">
    <div class="res-header">
        <h3 style="margin:0; font-weight:400;"><i class="fa-solid fa-terminal" style="color:#38bdf8; margin-right:0.75rem;"></i><span th:text="${result.algorithmName}"></span> Execution</h3>
        <span th:if="${result.found}" style="background:#065f46; color:#34d399; padding:0.4rem 1rem; border-radius:999px; font-size:0.8rem; font-weight:700;"><i class="fa-solid fa-check"></i> MATCH FOUND</span>
        <span th:if="${!result.found}" style="background:#991b1b; color:#fca5a5; padding:0.4rem 1rem; border-radius:999px; font-size:0.8rem; font-weight:700;"><i class="fa-solid fa-xmark"></i> MATCH NONE</span>
    </div>
    <div class="res-grid">
        <div class="res-box"><div class="res-label">Match Indices [Array]</div><div class="res-val" th:text="${#lists.isEmpty(result.positions) ? '[]' : result.positions}"></div></div>
        <div class="res-box"><div class="res-label">Comparisons Made</div><div class="res-val" style="color:#a7f3d0;" th:text="${result.comparisons}"></div></div>
    </div>
    <div style="margin-top: 2rem; background: #0b1120; padding: 1.5rem; border-radius: 8px; border: 1px solid #1e293b;">
        <div class="res-label" style="margin-bottom: 1rem;"><i class="fa-solid fa-book-open"></i> DSA Rationale & Complexity</div>
        <p style="margin: 0 0 1rem 0; font-size: 0.95rem; color: #cbd5e1;" th:text="${result.whyUsed}"></p>
        <p style="margin: 0 0 1rem 0; font-size: 0.9rem; color: #94a3b8;"><strong>Preprocessing:</strong> <span th:text="${result.preprocessingInfo}"></span></p>
        <div style="display: flex; gap: 1rem;">
            <span style="background: #1e293b; padding: 0.4rem 0.8rem; border-radius: 4px; font-family: monospace; font-size: 0.85rem; color: #cbd5e1;">Time: [[${result.timeComplexity}]]</span>
            <span style="background: #1e293b; padding: 0.4rem 0.8rem; border-radius: 4px; font-family: monospace; font-size: 0.85rem; color: #cbd5e1;">Space: [[${result.spaceComplexity}]]</span>
        </div>
    </div>
</div>
"""

SIM_CONTENT = """
<style>
.heat-table { width: 100%; border-collapse: separate; border-spacing: 0; font-size: 0.9rem; border: 1px solid var(--border); border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); }
.heat-table th { background: #f8fafc; padding: 1.25rem 1rem; text-align: center; font-size: 0.75rem; text-transform: uppercase; font-weight: 700; color: var(--text-muted); border-bottom: 1px solid var(--border); border-right: 1px solid var(--border); }
.heat-table th:first-child { text-align: left; }
.heat-table td { padding: 1rem; text-align: center; border-bottom: 1px solid var(--border); border-right: 1px solid var(--border); font-family: monospace; font-weight: 600; transition: all 0.2s; }
.heat-table tr:last-child td { border-bottom: none; }
.heat-table td:last-child, .heat-table th:last-child { border-right: none; }
.heat-label { font-family: 'Inter', sans-serif; font-weight: 600; background: #f8fafc; text-align: left !important; color: var(--text-main); font-size: 0.85rem; }
.legend { display: flex; align-items: center; justify-content: flex-end; gap: 1rem; margin-top: 1.5rem; font-size: 0.8rem; color: var(--text-muted); font-weight: 600; text-transform: uppercase; }
.legend-bar { width: 150px; height: 12px; background: linear-gradient(90deg, #f1f5f9 0%, #1e3a8a 100%); border-radius: 999px; }
</style>
<div class="hero"><div class="hero-content"><h1>Similarity <span class="hero-highlight">Matrix</span></h1><p>Jaccard overlap analysis across optimized document features.</p></div></div>

<div class="card">
    <div th:if="${documents.size() > 0}">
        <div style="overflow-x: auto;">
            <table class="heat-table">
                <thead>
                    <tr>
                        <th>Document</th>
                        <th th:each="colDoc : ${documents}" th:text="${#strings.substring(colDoc.originalFilename, 0, #strings.indexOf(colDoc.originalFilename, '.')) ?: colDoc.originalFilename}"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="rowDoc : ${documents}">
                        <td class="heat-label" th:text="${rowDoc.originalFilename}"></td>
                        <td class="heat-cell" th:each="colDoc : ${documents}" th:text="${#numbers.formatDecimal(similarityService.calculateJaccardSimilarity(rowDoc, colDoc), 1, 2)}"></td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="legend">
            <span>Low Similarity (0.0)</span>
            <div class="legend-bar"></div>
            <span>High Similarity (1.0)</span>
        </div>
    </div>
    <div th:if="${documents.size() == 0}" style="text-align: center; padding: 4rem; color: var(--text-muted); border: 1px dashed var(--border); border-radius: 12px; background: #f8fafc;">
        <i class="fa-solid fa-table-cells" style="font-size: 3rem; margin-bottom: 1rem; color: #cbd5e1;"></i>
        <h4>NO DATA TO DISPLAY</h4>
        <p>Upload documents to generate the similarity matrix.</p>
    </div>
</div>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        document.querySelectorAll(".heat-cell").forEach(cell => {
            let val = parseFloat(cell.innerText);
            if(!isNaN(val)) {
                if (val === 1.00) cell.style.backgroundColor = "#1e3a8a", cell.style.color = "#fff";
                else if (val > 0.00) cell.style.backgroundColor = `rgba(37, 99, 235, ${val * 0.8 + 0.1})`, cell.style.color = val > 0.5 ? "#fff" : "#0f172a";
                else cell.style.backgroundColor = "#f8fafc", cell.style.color = "#94a3b8";
            }
        });
    });
</script>
"""

GRAPH_CONTENT = """
<script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
<script>mermaid.initialize({startOnLoad:true, theme:'base', themeVariables: { primaryColor: '#eff6ff', primaryBorderColor: '#2563eb', primaryTextColor: '#0f172a', lineColor: '#94a3b8', fontFamily: 'Inter' }});</script>

<div class="hero"><div class="hero-content"><h1>Graph <span class="hero-highlight">Analytics Dashboard</span></h1><p>Adjacency list topology based on Jaccard similarity thresholds.</p></div></div>

<div class="metrics-grid" style="margin-bottom: 2rem;">
    <div class="metric-card">
        <div class="metric-icon" style="background:#eff6ff; color:#3b82f6;"><i class="fa-solid fa-circle-nodes"></i></div>
        <div class="metric-info"><h4>Documents (V)</h4><p class="val" th:text="${graph.adjacencyList.size()}">0</p></div>
    </div>
    <div class="metric-card">
        <div class="metric-icon" style="background:#f5f3ff; color:#8b5cf6;"><i class="fa-solid fa-link"></i></div>
        <div class="metric-info"><h4>Edges (E)</h4><p class="val" th:text="${graph.edgeCount}">0</p></div>
    </div>
    <div class="metric-card">
        <div class="metric-icon" style="background:#ecfdf5; color:#10b981;"><i class="fa-solid fa-object-group"></i></div>
        <div class="metric-info"><h4>Connected Components</h4><p class="val" th:text="${graph.findConnectedComponents().size()}">0</p></div>
    </div>
    <div class="metric-card" style="background: var(--sidebar-bg); border-color: var(--sidebar-bg);">
        <form action="/graph/threshold" method="post" style="width: 100%;">
            <label style="display:block; font-size:0.7rem; font-weight:700; color:#94a3b8; text-transform:uppercase; margin-bottom:0.5rem;">Threshold (T)</label>
            <div style="display:flex; gap:0.5rem;">
                <input type="number" name="threshold" step="0.01" min="0" max="1" th:value="${threshold}" style="width:100%; padding:0.5rem; border-radius:6px; border:1px solid #334155; background:#1e293b; color:#fff; font-family:monospace; font-size:1rem;">
                <button type="submit" class="btn-primary" style="padding: 0.5rem;"><i class="fa-solid fa-check"></i></button>
            </div>
        </form>
    </div>
</div>

<div class="card" style="min-height: 500px; display: flex; flex-direction: column;">
    <div class="card-header"><h3 class="card-title"><i class="fa-solid fa-draw-polygon"></i> Topology Visualization</h3></div>
    <div style="flex:1; background: #f8fafc; border: 1px solid var(--border); border-radius: 8px; display: flex; align-items: center; justify-content: center; overflow: auto; padding: 2rem;">
        <div class="mermaid" th:if="${graph.adjacencyList.size() > 0}">
        graph TD
        <th:block th:each="entry : ${graph.adjacencyList}">
            <th:block th:each="neighbor : ${entry.value}">
                [[${#strings.replace(entry.key.originalFilename, '.txt', '')}]] --- [[${#strings.replace(neighbor.originalFilename, '.txt', '')}]]
            </th:block>
        </th:block>
        </div>
        <div th:if="${graph.adjacencyList.size() == 0}" style="text-align: center; color: var(--text-muted);">
            <i class="fa-solid fa-project-diagram" style="font-size: 3rem; margin-bottom: 1rem; color: #cbd5e1;"></i>
            <h4>NO GRAPH DATA</h4><p>Upload documents to generate nodes.</p>
        </div>
    </div>
</div>
"""

RESULTS_CONTENT = """
<div class="hero"><div class="hero-content"><h1>Categorization <span class="hero-highlight">Complete</span></h1><p>Final algorithmic clustering derived from BFS connected components and aggregated group feature dominance.</p></div></div>

<div class="card" style="margin-bottom: 2rem;">
    <div style="display: flex; gap: 3rem; padding: 1rem;">
        <div><div style="font-size: 0.75rem; font-weight: 700; color: var(--text-muted); text-transform: uppercase;">Documents</div><div style="font-size: 2rem; font-weight: 700; color: var(--text-main);" th:text="${documentCount}">0</div></div>
        <div><div style="font-size: 0.75rem; font-weight: 700; color: var(--text-muted); text-transform: uppercase;">Groups</div><div style="font-size: 2rem; font-weight: 700; color: var(--text-main);" th:text="${categories.size()}">0</div></div>
        <div><div style="font-size: 0.75rem; font-weight: 700; color: var(--text-muted); text-transform: uppercase;">Graph Relationships</div><div style="font-size: 2rem; font-weight: 700; color: var(--text-main);" th:text="${graph.edgeCount}">0</div></div>
    </div>
</div>

<div style="display: grid; gap: 2rem;">
    <div th:each="category : ${categories}" style="background: #fff; border-radius: 12px; border: 1px solid var(--border); box-shadow: 0 4px 6px -1px rgba(0,0,0,0.02); overflow: hidden;">
        <div style="background: linear-gradient(90deg, #f8fafc 0%, #fff 100%); padding: 1.5rem; border-bottom: 1px solid var(--border); display: flex; justify-content: space-between; align-items: center;">
            <div>
                <div style="font-size: 0.75rem; font-weight: 700; color: var(--accent); letter-spacing: 1px; margin-bottom: 0.25rem;" th:text="${category.groupId}"></div>
                <h3 style="margin: 0; font-size: 1.5rem; color: var(--text-main);"><i class="fa-solid fa-folder-tree" style="color: var(--text-muted); margin-right: 0.5rem;"></i> <span th:text="${category.categoryLabel}"></span></h3>
            </div>
            <span class="badge badge-success" style="padding: 0.5rem 1rem;"><i class="fa-solid fa-check-circle"></i> Categorized</span>
        </div>
        
        <div style="padding: 2rem; display: grid; grid-template-columns: 1fr 1.5fr; gap: 3rem;">
            <div>
                <h5 style="margin: 0 0 1rem 0; font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 1px;">Documents in Subgraph</h5>
                <div style="display: flex; flex-direction: column; gap: 0.5rem;">
                    <div th:each="doc : ${category.documents}" style="background: #f1f5f9; padding: 0.75rem 1rem; border-radius: 6px; font-weight: 500; font-size: 0.9rem; color: var(--text-main); display: flex; align-items: center; gap: 0.75rem;"><i class="fa-regular fa-file-lines" style="color:var(--accent);"></i> <span th:text="${doc.originalFilename}"></span></div>
                </div>
            </div>
            <div>
                <h5 style="margin: 0 0 1rem 0; font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 1px;">Dominant Features</h5>
                <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
                    <span th:each="tf : ${category.dominantFeatures}" style="background: #eff6ff; border: 1px solid #bfdbfe; color: #1e40af; padding: 0.5rem 1rem; border-radius: 8px; font-size: 0.95rem; font-family: monospace; box-shadow: 0 1px 2px rgba(0,0,0,0.02);">
                        <strong th:text="${tf.term}"></strong> <span style="opacity: 0.6; font-size: 0.8rem; margin-left: 0.25rem;" th:text="'(' + ${tf.frequency} + ')'"></span>
                    </span>
                </div>
            </div>
        </div>
        
        <div style="background: #f8fafc; padding: 1.5rem 2rem; border-top: 1px solid var(--border); font-size: 0.9rem; color: var(--text-muted); line-height: 1.5;">
            <i class="fa-solid fa-microchip" style="margin-right: 0.5rem; color: var(--accent);"></i> <strong style="color: var(--text-main);">Explainability Rationale:</strong> <span th:text="${category.reason}"></span>
        </div>
    </div>
</div>

<div th:if="${categories.size() == 0}" class="card" style="text-align: center; padding: 4rem;">
    <i class="fa-solid fa-layer-group" style="font-size: 3rem; margin-bottom: 1rem; color: #cbd5e1;"></i>
    <h4>NO RESULTS GENERATED</h4>
    <p class="text-muted">Upload and process documents to see categorization groupings.</p>
</div>
"""

EXPORT_CONTENT = """
<div class="hero"><div class="hero-content"><h1>System <span class="hero-highlight">Export</span></h1><p>Raw JSON representation of the processing pipeline state.</p></div></div>

<div class="card">
    <div class="card-header" style="border-bottom: none; margin-bottom: 0; padding-bottom: 0;">
        <h3 class="card-title" style="border:none; margin:0;"><i class="fa-solid fa-file-code"></i> EXPORT RESULTS</h3>
        <a href="/api/export/json" class="btn-primary" style="background-color: var(--success);"><i class="fa-solid fa-download"></i> Download JSON Results</a>
    </div>
    
    <div style="background: #0B1120; border-radius: 12px; border: 1px solid #1e293b; margin-top: 1.5rem; overflow: hidden; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1);">
        <div style="background: #1e293b; padding: 0.75rem 1.5rem; display: flex; gap: 0.5rem; border-bottom: 1px solid #0f172a;">
            <div style="width:12px; height:12px; border-radius:50%; background:#ef4444;"></div>
            <div style="width:12px; height:12px; border-radius:50%; background:#f59e0b;"></div>
            <div style="width:12px; height:12px; border-radius:50%; background:#10b981;"></div>
        </div>
        <pre style="margin: 0; padding: 2rem; color: #38bdf8; font-family: 'Consolas', monospace; font-size: 0.95rem; overflow-x: auto; max-height: 600px; overflow-y: auto; line-height: 1.5;" th:text="${jsonPreview}"></pre>
    </div>
</div>
"""

def write_page(name, title, idx, content):
    with open(os.path.join(base_dir, f"{name}.html"), "w", encoding="utf-8") as f:
        f.write(generate_layout(title, idx, content))

write_page("index", "Project Dashboard", 1, INDEX_CONTENT)
write_page("pipeline", "Processing Pipeline", 2, PIPELINE_CONTENT)
write_page("analysis", "DSA Analysis", 3, ANALYSIS_CONTENT)
write_page("string-algorithms", "String Algorithms", 4, STRING_CONTENT)
write_page("similarity", "Similarity Mapping", 5, SIM_CONTENT)
write_page("graph", "Graph Visualization", 6, GRAPH_CONTENT)
write_page("results", "Categorization Results", 7, RESULTS_CONTENT)
write_page("export", "System Export", 8, EXPORT_CONTENT)

print("UI successfully rebuilt with professional styling.")

