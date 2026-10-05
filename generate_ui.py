import os
import io

base_dir = r"C:\Users\Diksha\OneDrive\Desktop\Optimization Frame Work\src\main\resources\templates"

html_template = """<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{title} - DSA Optimization Framework</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --sidebar-bg: #111827;
            --sidebar-hover: #1f2937;
            --sidebar-text: #9ca3af;
            --sidebar-text-active: #ffffff;
            --primary: #3b82f6;
            --primary-hover: #2563eb;
            --success: #10b981;
            --success-bg: #d1fae5;
            --error: #ef4444;
            --error-bg: #fee2e2;
            --bg-color: #f3f4f6;
            --card-bg: #ffffff;
            --text-main: #1f2937;
            --text-muted: #6b7280;
            --border: #e5e7eb;
        }
        body {
            font-family: 'Inter', sans-serif; margin: 0; padding: 0;
            background-color: var(--bg-color); color: var(--text-main);
            display: flex; min-height: 100vh;
            background-image: url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M20 20.5V18H0v-2h20v-2H0v-2h20v-2H0V8h20V6H0V4h20V2H0V0h22v20h2V0h2v20h2V0h2v20h2V0h2v20h2V0h2v20h2v2H20v-1.5zM0 20h2v20H0V20zm4 0h2v20H4V20zm4 0h2v20H8V20zm4 0h2v20h-2V20zm4 0h2v20h-2V20zm4 4h20v2H20v-2zm0 4h20v2H20v-2zm0 4h20v2H20v-2zm0 4h20v2H20v-2z' fill='%239C92AC' fill-opacity='0.03' fill-rule='evenodd'/%3E%3C/svg%3E");
        }
        .sidebar { width: 260px; background-color: var(--sidebar-bg); color: var(--sidebar-text); display: flex; flex-direction: column; box-shadow: 2px 0 10px rgba(0,0,0,0.1); z-index: 10; position: fixed; height: 100vh; }
        .sidebar-header { padding: 1.5rem; display: flex; align-items: center; gap: 0.75rem; border-bottom: 1px solid rgba(255,255,255,0.05); }
        .sidebar-header i { font-size: 1.5rem; color: var(--primary); }
        .sidebar-header h2 { font-size: 1.1rem; margin: 0; color: #fff; font-weight: 600; line-height: 1.2; }
        .nav-section { margin-top: 1.5rem; padding: 0 1.5rem; font-size: 0.75rem; text-transform: uppercase; letter-spacing: 1px; font-weight: 600; color: #4b5563; }
        .nav-menu { list-style: none; padding: 0.5rem 0 0 0; margin: 0; }
        .nav-menu li { margin-bottom: 0.25rem; padding: 0 1rem; }
        .nav-menu a { color: var(--sidebar-text); text-decoration: none; display: flex; align-items: center; gap: 0.75rem; padding: 0.75rem 1rem; border-radius: 6px; transition: all 0.2s; font-size: 0.9rem; }
        .nav-menu a:hover { background-color: var(--sidebar-hover); color: var(--sidebar-text-active); }
        .nav-menu a.active { background-color: var(--primary); color: #fff; box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.5); }
        .nav-menu a i { width: 20px; text-align: center; }
        
        .main-content { flex: 1; padding: 2.5rem; overflow-y: auto; margin-left: 260px; display: flex; flex-direction: column; gap: 1.5rem; }
        .page-header { margin-bottom: 0.5rem; }
        .page-header h1 { margin: 0 0 0.25rem 0; font-size: 1.75rem; color: var(--text-main); font-weight: 700; }
        .page-header p { margin: 0; color: var(--text-muted); font-size: 0.95rem; }
        
        .card { background-color: var(--card-bg); border-radius: 10px; padding: 1.5rem; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05), 0 2px 4px -1px rgba(0,0,0,0.03); border: 1px solid var(--border); }
        .card-title { margin: 0 0 1.25rem 0; font-size: 1.1rem; font-weight: 600; color: var(--text-main); display: flex; align-items: center; gap: 0.5rem; border-bottom: 1px solid var(--border); padding-bottom: 0.75rem; }
        
        .btn { background-color: var(--primary); color: white; border: none; padding: 0.6rem 1.2rem; border-radius: 6px; cursor: pointer; font-size: 0.9rem; font-weight: 500; transition: background-color 0.2s, box-shadow 0.2s; text-decoration: none; display: inline-flex; align-items: center; gap: 0.5rem; }
        .btn:hover { background-color: var(--primary-hover); box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.4); }
        .btn-danger { background-color: var(--error); }
        .btn-danger:hover { background-color: #dc2626; box-shadow: 0 4px 6px -1px rgba(239, 68, 68, 0.4); }
        
        table { width: 100%; border-collapse: collapse; font-size: 0.9rem; }
        th, td { padding: 0.75rem 1rem; text-align: left; border-bottom: 1px solid var(--border); }
        th { background-color: #f9fafb; font-weight: 600; color: var(--text-muted); text-transform: uppercase; font-size: 0.75rem; letter-spacing: 0.5px; }
        tbody tr:hover { background-color: #f9fafb; }
        
        .badge { display: inline-flex; align-items: center; padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.75rem; font-weight: 600; }
        .badge-success { background-color: var(--success-bg); color: #065f46; border: 1px solid #a7f3d0; }
        .badge-error { background-color: var(--error-bg); color: #991b1b; border: 1px solid #fecaca; }
        .badge-neutral { background-color: #f3f4f6; color: #374151; border: 1px solid #e5e7eb; }
        
        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.5rem; }
        .stat-card { background: var(--card-bg); padding: 1.5rem; border-radius: 10px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid var(--border); display: flex; align-items: center; gap: 1rem; transition: transform 0.2s; }
        .stat-card:hover { transform: translateY(-2px); }
        .stat-icon { width: 48px; height: 48px; border-radius: 10px; background: #eff6ff; color: var(--primary); display: flex; align-items: center; justify-content: center; font-size: 1.5rem; }
        .stat-content h4 { margin: 0 0 0.25rem 0; color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; font-weight: 600; letter-spacing: 0.5px; }
        .stat-content .number { font-size: 1.75rem; font-weight: 700; color: var(--text-main); margin: 0; line-height: 1; }
        {extra_css}
    </style>
</head>
<body>
    <div class="sidebar">
        <div class="sidebar-header">
            <i class="fa-solid fa-project-diagram"></i>
            <h2>OptiFrame<br><span style="font-size:0.75rem; font-weight:400; color:#9ca3af;">DSA Framework</span></h2>
        </div>
        
        <div class="nav-section">Dashboard</div>
        <ul class="nav-menu">
            <li><a href="/" {nav1}><i class="fa-solid fa-house"></i> Overview & Upload</a></li>
            <li><a href="/pipeline" {nav2}><i class="fa-solid fa-list-check"></i> Processing Pipeline</a></li>
        </ul>

        <div class="nav-section">DSA Components</div>
        <ul class="nav-menu">
            <li><a href="/analysis" {nav3}><i class="fa-solid fa-microscope"></i> Feature Analysis</a></li>
            <li><a href="/string-algorithms" {nav4}><i class="fa-solid fa-code"></i> String Algorithms</a></li>
            <li><a href="/similarity" {nav5}><i class="fa-solid fa-not-equal"></i> Similarity Matrix</a></li>
            <li><a href="/graph" {nav6}><i class="fa-solid fa-network-wired"></i> Graph Traversal</a></li>
        </ul>

        <div class="nav-section">Outcomes</div>
        <ul class="nav-menu">
            <li><a href="/results" {nav7}><i class="fa-solid fa-layer-group"></i> Categorization</a></li>
            <li><a href="/export" {nav8}><i class="fa-solid fa-file-export"></i> JSON Export</a></li>
        </ul>
    </div>
    
    <div class="main-content">
        <div class="page-header">
            <h1>{header_title}</h1>
            <p>{header_subtitle}</p>
        </div>
        {content}
    </div>
</body>
</html>
"""

def generate_page(title, subtitle, nav_idx, content, extra_css=""):
    page = html_template.replace("{title}", title).replace("{header_title}", title).replace("{header_subtitle}", subtitle).replace("{extra_css}", extra_css)
    for i in range(1, 9):
        page = page.replace(f"{{nav{i}}}", 'class="active"' if i == nav_idx else '')
    return page.replace("{content}", content)

index = """
<div class="stats-grid">
    <div class="stat-card">
        <div class="stat-icon"><i class="fa-regular fa-file-lines"></i></div>
        <div class="stat-content"><h4>Documents</h4><p class="number" th:text="${documentCount}">0</p></div>
    </div>
    <div class="stat-card">
        <div class="stat-icon"><i class="fa-solid fa-tags"></i></div>
        <div class="stat-content"><h4>Total Features</h4><p class="number" th:text="${featureCount}">0</p></div>
    </div>
</div>

<div class="card">
    <h3 class="card-title"><i class="fa-solid fa-cloud-arrow-up"></i> Upload Documents</h3>
    <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 1rem;">Select one or more text files to begin the DSA pipeline. The system accepts .txt files.</p>
    <form action="/upload" method="post" enctype="multipart/form-data" style="display: flex; gap: 1rem; align-items: center; background: #f9fafb; padding: 1.5rem; border: 1px dashed #cbd5e1; border-radius: 8px;">
        <input type="file" name="files" multiple accept=".txt" required style="flex: 1; font-size: 0.9rem;">
        <button type="submit" class="btn"><i class="fa-solid fa-upload"></i> Process Files</button>
    </form>
</div>

<div class="card" th:if="${documents.size() > 0}">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
        <h3 class="card-title" style="margin: 0; border: none; padding: 0;"><i class="fa-solid fa-folder-open"></i> Current Workspace</h3>
        <form action="/clear" method="post" style="margin: 0;">
            <button type="submit" class="btn btn-danger"><i class="fa-solid fa-trash-can"></i> Clear All</button>
        </form>
    </div>
    <table>
        <thead><tr><th>ID</th><th>Filename</th><th>Size</th><th>Pipeline Status</th><th>Actions</th></tr></thead>
        <tbody>
            <tr th:each="doc : ${documents}">
                <td style="font-family: monospace; color: var(--text-muted);" th:text="${#strings.substring(doc.id,0,8)}"></td>
                <td style="font-weight: 500;" th:text="${doc.originalFilename}"></td>
                <td th:text="${doc.fileSize + ' bytes'}"></td>
                <td><span class="badge badge-success"><i class="fa-solid fa-check" style="margin-right:0.25rem;"></i><span th:text="${doc.currentStage}"></span></span></td>
                <td>
                    <form action="/delete" method="post" style="margin: 0;">
                        <input type="hidden" name="id" th:value="${doc.id}">
                        <button type="submit" style="background: none; border: none; color: var(--error); cursor: pointer; padding: 0.25rem;"><i class="fa-solid fa-xmark"></i></button>
                    </form>
                </td>
            </tr>
        </tbody>
    </table>
</div>
"""

pipeline_css = """
.timeline { position: relative; max-width: 800px; padding: 1rem 0; }
.timeline::before { content: ''; position: absolute; top: 0; left: 16px; height: 100%; width: 2px; background: var(--border); }
.timeline-step { position: relative; margin-bottom: 1.5rem; padding-left: 3rem; }
.timeline-icon { position: absolute; left: 4px; top: 0; width: 26px; height: 26px; border-radius: 50%; background: var(--bg-color); border: 2px solid var(--border); display: flex; align-items: center; justify-content: center; z-index: 1; transition: all 0.3s; }
.timeline-icon i { font-size: 0.7rem; color: transparent; }
.timeline-content { background: var(--card-bg); padding: 1.25rem; border-radius: 8px; border: 1px solid var(--border); box-shadow: 0 1px 3px rgba(0,0,0,0.05); transition: border-color 0.3s; }
.timeline-content h4 { margin: 0 0 0.25rem 0; color: var(--text-main); font-size: 1rem; display: flex; justify-content: space-between; }
.timeline-content p { margin: 0; color: var(--text-muted); font-size: 0.85rem; }
.timeline-step.active .timeline-icon { background: var(--success); border-color: var(--success); }
.timeline-step.active .timeline-icon i { color: white; }
.timeline-step.active .timeline-content { border-left: 4px solid var(--success); }
.status-badge { font-size: 0.7rem; padding: 0.2rem 0.5rem; border-radius: 12px; background: #f3f4f6; color: #6b7280; font-weight: 600; }
.timeline-step.active .status-badge { background: var(--success-bg); color: #065f46; content: "Completed"; }
"""
pipeline = """
<div class="card">
    <h3 class="card-title"><i class="fa-solid fa-list-check"></i> Execution Flow</h3>
    <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 1.5rem;">Displays the synchronous execution stages of the categorization engine.</p>
    
    <div class="timeline">
        <div class="timeline-step" th:classappend="${documents.size() > 0} ? 'active' : ''">
            <div class="timeline-icon"><i class="fa-solid fa-check"></i></div>
            <div class="timeline-content"><h4>1. Document Extraction <span class="status-badge" th:text="${documents.size() > 0 ? 'Completed' : 'Pending'}"></span></h4><p>Reads raw byte streams into memory buffers and initializes data structures.</p></div>
        </div>
        <div class="timeline-step" th:classappend="${documents.size() > 0} ? 'active' : ''">
            <div class="timeline-icon"><i class="fa-solid fa-check"></i></div>
            <div class="timeline-content"><h4>2. Preprocessing <span class="status-badge" th:text="${documents.size() > 0 ? 'Completed' : 'Pending'}"></span></h4><p>Normalizes case, removes punctuation and eliminates common stop-words.</p></div>
        </div>
        <div class="timeline-step" th:classappend="${documents.size() > 0} ? 'active' : ''">
            <div class="timeline-icon"><i class="fa-solid fa-check"></i></div>
            <div class="timeline-content"><h4>3. Feature Extraction <span class="status-badge" th:text="${documents.size() > 0 ? 'Completed' : 'Pending'}"></span></h4><p>Generates word frequencies mapped via Custom Hash structures.</p></div>
        </div>
        <div class="timeline-step" th:classappend="${documents.size() > 0} ? 'active' : ''">
            <div class="timeline-icon"><i class="fa-solid fa-check"></i></div>
            <div class="timeline-content"><h4>4. Feature Optimization <span class="status-badge" th:text="${documents.size() > 0 ? 'Completed' : 'Pending'}"></span></h4><p>Applies Timsort bounding to retain only high-value dominant terms.</p></div>
        </div>
        <div class="timeline-step" th:classappend="${documents.size() > 0} ? 'active' : ''">
            <div class="timeline-icon"><i class="fa-solid fa-check"></i></div>
            <div class="timeline-content"><h4>5. Similarity Matrix <span class="status-badge" th:text="${documents.size() > 0 ? 'Completed' : 'Pending'}"></span></h4><p>Calculates NxN Jaccard overlap metrics across optimized feature sets.</p></div>
        </div>
        <div class="timeline-step" th:classappend="${documents.size() > 0} ? 'active' : ''">
            <div class="timeline-icon"><i class="fa-solid fa-check"></i></div>
            <div class="timeline-content"><h4>6. Graph Edges <span class="status-badge" th:text="${documents.size() > 0 ? 'Completed' : 'Pending'}"></span></h4><p>Constructs an Adjacency List linking documents passing the similarity threshold.</p></div>
        </div>
        <div class="timeline-step" th:classappend="${documents.size() > 0} ? 'active' : ''">
            <div class="timeline-icon"><i class="fa-solid fa-check"></i></div>
            <div class="timeline-content"><h4>7. BFS/DFS Traversal <span class="status-badge" th:text="${documents.size() > 0 ? 'Completed' : 'Pending'}"></span></h4><p>Traverses the graph to discover disjoint connected components.</p></div>
        </div>
        <div class="timeline-step" th:classappend="${documents.size() > 0} ? 'active' : ''">
            <div class="timeline-icon"><i class="fa-solid fa-check"></i></div>
            <div class="timeline-content"><h4>8. Categorization <span class="status-badge" th:text="${documents.size() > 0 ? 'Completed' : 'Pending'}"></span></h4><p>Assigns human-readable labels derived from subgroup term dominance.</p></div>
        </div>
    </div>
</div>
"""

analysis_css = """
.flow-container { display: flex; align-items: stretch; gap: 1rem; margin-top: 1rem; }
.flow-box { flex: 1; background: #f9fafb; border: 1px solid var(--border); border-radius: 8px; padding: 1.25rem; }
.flow-arrow { display: flex; align-items: center; color: var(--border); font-size: 1.5rem; }
.tag { display: inline-block; background: #eff6ff; color: var(--primary); border: 1px solid #bfdbfe; padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.8rem; margin: 0.2rem; font-family: monospace; }
.tag-opt { background: #ecfdf5; color: #065f46; border-color: #a7f3d0; }
"""
analysis = """
<div class="card">
    <h3 class="card-title"><i class="fa-solid fa-chart-bar"></i> Feature Selection Optimization</h3>
    <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 1.5rem;">Examine the raw structural token counting and Timsort-based feature selection bounds.</p>
    
    <div th:each="doc : ${documents}" style="margin-bottom: 2rem; border: 1px solid var(--border); padding: 1.5rem; border-radius: 8px;">
        <h4 style="margin: 0 0 1rem 0; color: var(--text-main); font-size: 1.1rem; display: flex; align-items: center; gap: 0.5rem;"><i class="fa-regular fa-file-code text-muted"></i> [[${doc.originalFilename}]]</h4>
        <div class="flow-container">
            <div class="flow-box">
                <h5 style="margin: 0 0 0.75rem 0; color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px;">Raw Tokens Sample (First 20)</h5>
                <div>
                    <span class="tag" th:each="t, iterStat : ${doc.tokens}" th:if="${iterStat.index < 20}" th:text="${t}"></span>
                    <span th:if="${doc.tokens.size() > 20}" style="color:var(--text-muted); font-size:0.8rem; margin-left: 0.5rem;">...</span>
                </div>
            </div>
            <div class="flow-arrow"><i class="fa-solid fa-arrow-right-long"></i></div>
            <div class="flow-box">
                <h5 style="margin: 0 0 0.75rem 0; color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px;">Optimized Features (Max 15)</h5>
                <div>
                    <span class="tag tag-opt" th:each="tf : ${doc.optimizedFeatures}">
                        <strong th:text="${tf.term}"></strong> <span style="opacity: 0.7; font-size: 0.75rem;" th:text="'(' + ${tf.frequency} + ')'"></span>
                    </span>
                </div>
            </div>
        </div>
    </div>
    <div th:if="${documents.size() == 0}" style="text-align: center; padding: 2rem; color: var(--text-muted);">
        <i class="fa-solid fa-circle-info" style="font-size: 2rem; margin-bottom: 1rem; color: #cbd5e1;"></i>
        <p>No documents processed yet. Please upload files from the Dashboard.</p>
    </div>
</div>
"""

string_css = """
.form-grid { display: grid; grid-template-columns: 2fr 1fr; gap: 1.5rem; }
.form-group { margin-bottom: 1.25rem; }
.form-label { display: block; font-size: 0.85rem; font-weight: 600; color: var(--text-main); margin-bottom: 0.5rem; }
.form-control { width: 100%; padding: 0.75rem; border: 1px solid var(--border); border-radius: 6px; font-family: 'Inter', sans-serif; font-size: 0.9rem; transition: border-color 0.2s; box-sizing: border-box; }
.form-control:focus { border-color: var(--primary); outline: none; box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1); }
.algo-radio-group { display: flex; flex-direction: column; gap: 0.5rem; }
.algo-radio { display: flex; align-items: center; gap: 0.75rem; background: #f9fafb; padding: 0.75rem 1rem; border-radius: 6px; border: 1px solid var(--border); cursor: pointer; transition: background 0.2s; font-size: 0.9rem; font-weight: 500; }
.algo-radio:hover { background: #f3f4f6; }
.algo-radio input[type="radio"] { accent-color: var(--primary); width: 16px; height: 16px; }
.result-box { background: #ffffff; border: 1px solid var(--border); border-radius: 8px; padding: 1.5rem; margin-top: 2rem; box-shadow: 0 2px 4px rgba(0,0,0,0.02); }
.explanation-box { background-color: #f8fafc; padding: 1.5rem; border-radius: 6px; margin-top: 1.5rem; border: 1px solid #e2e8f0; }
.explanation-box p { margin: 0.5rem 0; font-size: 0.9rem; }
"""
string_algo = """
<div class="card">
    <h3 class="card-title"><i class="fa-solid fa-laptop-code"></i> Algorithm Laboratory</h3>
    <form action="/string-algorithms/run" method="post">
        <div class="form-grid">
            <div>
                <div class="form-group">
                    <label class="form-label">Corpus Text:</label>
                    <textarea name="text" class="form-control" rows="8" required style="font-family: monospace;" th:text="${inputText}"></textarea>
                </div>
                <div class="form-group">
                    <label class="form-label">Search Pattern(s):</label>
                    <input type="text" name="pattern" class="form-control" placeholder="Enter word to search (use commas for Aho-Corasick)" required th:value="${inputPattern}">
                </div>
            </div>
            <div>
                <label class="form-label">Select Implementation:</label>
                <div class="algo-radio-group">
                    <label class="algo-radio"><input type="radio" name="algorithm" value="naive" th:checked="${selectedAlgorithm == null or selectedAlgorithm == 'naive'}"> Naive Matching</label>
                    <label class="algo-radio"><input type="radio" name="algorithm" value="kmp" th:checked="${selectedAlgorithm == 'kmp'}"> KMP (Knuth-Morris-Pratt)</label>
                    <label class="algo-radio"><input type="radio" name="algorithm" value="z" th:checked="${selectedAlgorithm == 'z'}"> Z Algorithm</label>
                    <label class="algo-radio"><input type="radio" name="algorithm" value="rabin" th:checked="${selectedAlgorithm == 'rabin'}"> Rabin-Karp</label>
                    <label class="algo-radio"><input type="radio" name="algorithm" value="ahocorasick" th:checked="${selectedAlgorithm == 'ahocorasick'}"> Aho-Corasick (Multi)</label>
                </div>
                <button type="submit" class="btn" style="width: 100%; justify-content: center; margin-top: 1.5rem;"><i class="fa-solid fa-play"></i> Execute Algorithm</button>
            </div>
        </div>
    </form>
</div>
<div class="result-box" th:if="${result != null}">
    <div style="display: flex; align-items: center; gap: 1rem; border-bottom: 1px solid var(--border); padding-bottom: 1rem; margin-bottom: 1rem;">
        <h3 style="margin: 0; color: var(--text-main);" th:text="${result.algorithmName}"></h3>
        <span th:if="${result.found}" class="badge badge-success" style="font-size: 0.9rem; padding: 0.4rem 1rem;"><i class="fa-solid fa-check" style="margin-right: 0.5rem;"></i> Match Found</span>
        <span th:if="${!result.found}" class="badge badge-error" style="font-size: 0.9rem; padding: 0.4rem 1rem;"><i class="fa-solid fa-xmark" style="margin-right: 0.5rem;"></i> Match None</span>
    </div>
    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
        <div>
            <p style="margin: 0 0 0.5rem 0; font-weight: 600;">Match Index Positions:</p>
            <div style="background: #f1f5f9; padding: 0.75rem; border-radius: 4px; font-family: monospace; color: var(--primary);" th:text="${#lists.isEmpty(result.positions) ? '[]' : result.positions}"></div>
        </div>
        <div>
            <p style="margin: 0 0 0.5rem 0; font-weight: 600;">Comparisons Made:</p>
            <div style="background: #f1f5f9; padding: 0.75rem; border-radius: 4px; font-family: monospace; color: var(--text-main);" th:text="${result.comparisons}"></div>
        </div>
    </div>
    <div class="explanation-box">
        <h4 style="margin: 0 0 1rem 0; color: var(--text-main); font-size: 1rem; border-bottom: 1px solid #e2e8f0; padding-bottom: 0.5rem;"><i class="fa-solid fa-book-open"></i> DSA Reference Data</h4>
        <p><strong>Rationale:</strong> <span th:text="${result.whyUsed}"></span></p>
        <p><strong>Preprocessing Info:</strong> <span th:text="${result.preprocessingInfo}"></span></p>
        <div style="display: flex; gap: 2rem; margin-top: 1rem;">
            <p><strong>Time Complexity:</strong> <span class="badge badge-neutral" th:text="${result.timeComplexity}" style="font-family: monospace;"></span></p>
            <p><strong>Space Complexity:</strong> <span class="badge badge-neutral" th:text="${result.spaceComplexity}" style="font-family: monospace;"></span></p>
        </div>
    </div>
</div>
"""

similarity_css = """
.sim-table { width: 100%; border-collapse: separate; border-spacing: 0; margin-top: 1rem; border: 1px solid var(--border); border-radius: 8px; overflow: hidden; }
.sim-table th, .sim-table td { padding: 0.75rem 1rem; text-align: center; border-bottom: 1px solid var(--border); border-right: 1px solid var(--border); }
.sim-table th:last-child, .sim-table td:last-child { border-right: none; }
.sim-table tr:last-child td { border-bottom: none; }
.sim-table th { background-color: #f8fafc; color: var(--text-muted); font-size: 0.8rem; }
.sim-cell { font-family: monospace; font-weight: 600; color: #1e293b; transition: background 0.3s; }
"""
similarity = """
<div class="card">
    <h3 class="card-title"><i class="fa-solid fa-table-cells"></i> Document Similarity Matrix</h3>
    <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 1.5rem;">Matrix calculates NxN Jaccard overlap metrics based on the optimized feature subsets. Darker shades indicate stronger similarity.</p>
    
    <div style="overflow-x: auto;" th:if="${documents.size() > 0}">
        <table class="sim-table">
            <thead>
                <tr>
                    <th style="text-align: left; background: #fff;">Document Name</th>
                    <th th:each="colDoc : ${documents}" th:text="${#strings.substring(colDoc.originalFilename, 0, #strings.indexOf(colDoc.originalFilename, '.')) ?: colDoc.originalFilename}"></th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="rowDoc : ${documents}">
                    <td th:text="${rowDoc.originalFilename}" style="font-weight:600; background:#f8fafc; text-align: left; font-size: 0.85rem;"></td>
                    <td class="sim-cell" th:each="colDoc : ${documents}" 
                        th:text="${#numbers.formatDecimal(similarityService.calculateJaccardSimilarity(rowDoc, colDoc), 1, 2)}">
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div th:if="${documents.size() == 0}" style="text-align: center; padding: 2rem; color: var(--text-muted);">
        <i class="fa-solid fa-circle-info" style="font-size: 2rem; margin-bottom: 1rem; color: #cbd5e1;"></i>
        <p>No documents uploaded yet.</p>
    </div>
</div>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        document.querySelectorAll(".sim-cell").forEach(cell => {
            let val = parseFloat(cell.innerText);
            if(!isNaN(val)) {
                if (val === 1.00) cell.style.backgroundColor = "rgba(59, 130, 246, 0.8)", cell.style.color = "#fff";
                else if (val > 0.00) cell.style.backgroundColor = `rgba(59, 130, 246, ${val * 0.7 + 0.1})`;
                else cell.style.backgroundColor = "transparent", cell.style.color = "#94a3b8";
            }
        });
    });
</script>
"""

graph = """
<script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
<script>mermaid.initialize({startOnLoad:true, theme:'base', themeVariables: { primaryColor: '#eff6ff', primaryBorderColor: '#3b82f6', primaryTextColor: '#1e293b', lineColor: '#94a3b8' }});</script>
<div class="card">
    <h3 class="card-title"><i class="fa-solid fa-project-diagram"></i> Adjacency List Visualization</h3>
    
    <form action="/graph/threshold" method="post" style="background:#f8fafc; padding:1.25rem; border-radius:8px; margin-bottom:1.5rem; display: flex; align-items: center; gap: 1rem; border: 1px solid var(--border);">
        <label style="font-weight: 600; font-size: 0.9rem;">Similarity Edge Threshold:</label>
        <input type="number" name="threshold" step="0.01" min="0" max="1" th:value="${threshold}" style="padding:0.5rem; width:100px; border:1px solid #cbd5e1; border-radius:4px; font-family: monospace;">
        <button type="submit" class="btn"><i class="fa-solid fa-rotate"></i> Recompute Graph</button>
    </form>

    <div class="stats-grid" style="margin-bottom: 1.5rem;">
        <div class="stat-card" style="padding: 1rem;">
            <div class="stat-content"><h4>Vertices</h4><p class="number" style="font-size:1.5rem;" th:text="${graph.adjacencyList.size()}"></p></div>
        </div>
        <div class="stat-card" style="padding: 1rem;">
            <div class="stat-content"><h4>Edges</h4><p class="number" style="font-size:1.5rem;" th:text="${graph.edgeCount}"></p></div>
        </div>
        <div class="stat-card" style="padding: 1rem;">
            <div class="stat-content"><h4>Connected Components</h4><p class="number" style="font-size:1.5rem;" th:text="${graph.findConnectedComponents().size()}"></p></div>
        </div>
    </div>

    <div style="background: #ffffff; border: 1px solid var(--border); padding: 2rem; border-radius: 8px; text-align: center; overflow: auto; min-height: 200px;">
        <div class="mermaid">
        graph TD
        <th:block th:each="entry : ${graph.adjacencyList}">
            <th:block th:each="neighbor : ${entry.value}">
                [[${#strings.replace(entry.key.originalFilename, '.txt', '')}]] --- [[${#strings.replace(neighbor.originalFilename, '.txt', '')}]]
            </th:block>
        </th:block>
        </div>
        <p th:if="${graph.adjacencyList.size() == 0}" class="text-muted">No graph data available.</p>
    </div>
</div>
"""

results = """
<div class="card">
    <h3 class="card-title"><i class="fa-solid fa-layer-group"></i> Deterministic Categorization Labels</h3>
    <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 1.5rem;">Final algorithmic clustering derived from BFS connected components and aggregated group feature dominance.</p>
    
    <div style="display: grid; gap: 1.5rem;">
        <div th:each="category : ${categories}" style="background: #ffffff; padding: 1.5rem; border-radius: 8px; border: 1px solid var(--border); border-left: 4px solid var(--primary); box-shadow: 0 2px 4px rgba(0,0,0,0.02);">
            <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #f1f5f9; padding-bottom: 1rem; margin-bottom: 1rem;">
                <h4 style="margin: 0; color: var(--text-main); font-size: 1.25rem;"><span style="color: var(--text-muted); font-weight: normal; margin-right: 0.5rem;" th:text="${category.groupId}"></span> <span th:text="${category.categoryLabel}"></span></h4>
                <span class="badge badge-success"><i class="fa-solid fa-check" style="margin-right: 0.25rem;"></i> Classified</span>
            </div>
            
            <div style="display: grid; grid-template-columns: 1fr 1.5fr; gap: 2rem;">
                <div>
                    <h5 style="margin: 0 0 0.75rem 0; font-size: 0.8rem; color: var(--text-muted); text-transform: uppercase;">Documents in Subgraph</h5>
                    <ul style="margin: 0; padding-left: 1.25rem; color: var(--text-main); font-size: 0.9rem;">
                        <li th:each="doc : ${category.documents}" th:text="${doc.originalFilename}" style="margin-bottom: 0.25rem;"></li>
                    </ul>
                </div>
                <div>
                    <h5 style="margin: 0 0 0.75rem 0; font-size: 0.8rem; color: var(--text-muted); text-transform: uppercase;">Dominant Aggregated Features</h5>
                    <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                        <span th:each="tf : ${category.dominantFeatures}" style="background: #eff6ff; color: var(--primary); padding: 0.25rem 0.75rem; border: 1px solid #bfdbfe; border-radius: 4px; font-size: 0.85rem; font-family: monospace;">
                            <strong th:text="${tf.term}"></strong> <span style="opacity: 0.7;" th:text="'(' + ${tf.frequency} + ')'"></span>
                        </span>
                    </div>
                </div>
            </div>
            
            <div style="margin-top: 1.25rem; padding-top: 1rem; border-top: 1px dashed var(--border); font-size: 0.85rem; color: #64748b;">
                <i class="fa-solid fa-robot" style="margin-right: 0.5rem;"></i> <strong>Algorithm Rationale:</strong> <span th:text="${category.reason}"></span>
            </div>
        </div>
    </div>
    
    <div th:if="${categories.size() == 0}" style="text-align: center; padding: 2rem; color: var(--text-muted);">
        <i class="fa-solid fa-circle-info" style="font-size: 2rem; margin-bottom: 1rem; color: #cbd5e1;"></i>
        <p>No categorizations available.</p>
    </div>
</div>
"""

export_css = """
.json-container { background-color: #0f172a; color: #38bdf8; padding: 1.5rem; border-radius: 8px; font-family: 'Consolas', 'Courier New', monospace; font-size: 0.9rem; white-space: pre-wrap; margin-top: 1.5rem; max-height: 500px; overflow-y: auto; border: 1px solid #1e293b; box-shadow: inset 0 2px 4px rgba(0,0,0,0.5); }
"""
export = """
<div class="card">
    <div style="display: flex; justify-content: space-between; align-items: flex-start;">
        <div>
            <h3 class="card-title" style="border: none; padding: 0; margin-bottom: 0.5rem;"><i class="fa-solid fa-file-code"></i> JSON Export Payload</h3>
            <p style="font-size: 0.9rem; color: var(--text-muted); margin: 0;">Download the comprehensive dynamically generated topology and categorizations.</p>
        </div>
        <a href="/api/export/json" class="btn" style="background-color: var(--success);"><i class="fa-solid fa-download"></i> Download JSON</a>
    </div>
    <pre class="json-container" th:text="${jsonPreview}"></pre>
</div>
"""

def write_page(name, title, subtitle, idx, content, extra_css=""):
    with open(os.path.join(base_dir, f"{name}.html"), "w", encoding="utf-8") as f:
        f.write(generate_page(title, subtitle, idx, content, extra_css))

write_page("index", "Project Dashboard", "A DSA-Based Document Processing, Feature Optimization and Categorization System", 1, index)
write_page("pipeline", "Processing Pipeline", "Synchronous execution workflow", 2, pipeline, pipeline_css)
write_page("analysis", "DSA Analysis", "Feature selection bounds and extraction metrics", 3, analysis, analysis_css)
write_page("string-algorithms", "String Algorithms", "Pattern matching algorithmic laboratory", 4, string_algo, string_css)
write_page("similarity", "Similarity Mapping", "Jaccard intersection over union matrix", 5, similarity, similarity_css)
write_page("graph", "Graph Visualization", "Adjacency list node structures", 6, graph)
write_page("results", "Categorization Results", "Algorithmic groupings", 7, results)
write_page("export", "System Export", "Raw pipeline JSON datastructure output", 8, export, export_css)

print("UI successfully rebuilt with professional styling.")
