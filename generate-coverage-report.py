#!/usr/bin/env python3
"""
Script de génération de rapport de couverture pour EnglishFlow
Lit les rapports Jacoco HTML et génère un rapport complet
"""

import os
import re
import xml.etree.ElementTree as ET
from datetime import datetime
from pathlib import Path

# Liste des microservices
SERVICES = [
    "api-gateway",
    "auth-service",
    "club-service",
    "community-service",
    "complaints-service",
    "courses-service",
    "event-service",
    "exam-service",
    "gamification-service",
    "learning-service",
    "messaging-service",
    "payment-service",
    "sponsors-service",
]

def extract_coverage_from_html(html_path):
    """Extrait le pourcentage de couverture depuis le fichier HTML Jacoco"""
    try:
        with open(html_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Chercher la ligne tfoot avec le total
        # Format: <tfoot><tr><td>Total</td><td class="bar">...</td><td class="ctr2">78 %</td>
        match = re.search(r'<tfoot>.*?<td class="ctr2">(\d+)\s*%</td>', content)
        if match:
            return int(match.group(1))
        return None
    except (OSError, IOError):
        return None

def count_tests(surefire_dir):
    """Compte le nombre de tests depuis les rapports Surefire"""
    if not os.path.exists(surefire_dir):
        return 0
    
    total_tests = 0
    for xml_file in Path(surefire_dir).glob("TEST-*.xml"):
        try:
            tree = ET.parse(xml_file)
            root = tree.getroot()
            tests = root.get('tests', '0')
            total_tests += int(tests)
        except (ET.ParseError, ValueError, OSError):
            continue
    
    return total_tests

def get_service_metrics(service):
    """Récupère les métriques pour un service"""
    base_path = f"backend/{service}"
    jacoco_html = f"{base_path}/target/site/jacoco/index.html"
    surefire_dir = f"{base_path}/target/surefire-reports"
    
    test_count = count_tests(surefire_dir)
    coverage = extract_coverage_from_html(jacoco_html)
    
    return {
        'service': service,
        'tests': test_count,
        'coverage': coverage
    }

def get_badge_info(coverage):
    """Retourne les informations de badge selon la couverture"""
    if coverage is None:
        return 'no-data', 'N/A', 'Pas de données'
    
    if coverage >= 90:
        return 'excellent', coverage, 'Excellent'
    
    if coverage >= 80:
        return 'good', coverage, 'Bon'
    
    if coverage >= 50:
        return 'medium', coverage, 'Moyen'
    
    return 'low', coverage, 'Faible'

def generate_text_report(metrics, filename):
    """Génère le rapport texte"""
    total_tests = sum(m['tests'] for m in metrics)
    services_with_coverage = [m for m in metrics if m['coverage'] is not None]
    avg_coverage = sum(m['coverage'] for m in services_with_coverage) / len(services_with_coverage) if services_with_coverage else 0
    
    with open(filename, 'w', encoding='utf-8') as f:
        f.write("=" * 70 + "\n")
        f.write("   📊 RAPPORT DE COUVERTURE - ENGLISHFLOW MICROSERVICES\n")
        f.write("=" * 70 + "\n")
        f.write(f"Date: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n")
        f.write("-" * 70 + "\n")
        f.write(f"{'SERVICE':<25} | {'TESTS':<15} | {'COUVERTURE':<15}\n")
        f.write("-" * 70 + "\n")
        
        for m in metrics:
            coverage_str = f"{m['coverage']}%" if m['coverage'] is not None else "N/A"
            f.write(f"{m['service']:<25} | {m['tests']:>3} tests{'':<7} | {coverage_str:<15}\n")
        
        f.write("-" * 70 + "\n")
        f.write(f"{'TOTAL':<25} | {total_tests:>3} tests{'':<7} | {avg_coverage:.0f}% (moyenne)\n")
        f.write("-" * 70 + "\n\n")
        f.write("📈 Statistiques globales:\n")
        f.write(f"   - Nombre de microservices: {len(metrics)}\n")
        f.write(f"   - Services avec couverture: {len(services_with_coverage)}\n")
        f.write(f"   - Total de tests: {total_tests}\n")
        f.write(f"   - Couverture moyenne: {avg_coverage:.0f}%\n\n")
        f.write("✅ Rapport généré avec succès!\n")

def generate_html_header(metrics, total_tests, avg_coverage, services_with_coverage):
    """Génère l'en-tête HTML du rapport"""
    return f"""<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rapport de Couverture - EnglishFlow</title>
    <style>
        body {{
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            margin: 0;
            padding: 20px;
        }}
        .container {{
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 12px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            overflow: hidden;
        }}
        .header {{
            background: linear-gradient(135deg, #2D5757 0%, #3D3D60 100%);
            color: #F7EDE2;
            padding: 30px;
            text-align: center;
        }}
        .header h1 {{
            margin: 0;
            font-size: 32px;
        }}
        .header p {{
            margin: 10px 0 0 0;
            opacity: 0.9;
        }}
        .stats {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            padding: 30px;
            background: #f8f9fa;
        }}
        .stat-card {{
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            text-align: center;
        }}
        .stat-card h3 {{
            margin: 0 0 10px 0;
            color: #666;
            font-size: 14px;
            text-transform: uppercase;
        }}
        .stat-card .value {{
            font-size: 36px;
            font-weight: bold;
            color: #2D5757;
        }}
        table {{
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }}
        th {{
            background: #2D5757;
            color: white;
            padding: 15px;
            text-align: left;
            font-weight: 600;
        }}
        td {{
            padding: 12px 15px;
            border-bottom: 1px solid #e0e0e0;
        }}
        tr:hover {{
            background: #f8f9fa;
        }}
        .coverage-bar {{
            width: 100%;
            height: 24px;
            background: #e0e0e0;
            border-radius: 12px;
            overflow: hidden;
        }}
        .coverage-fill {{
            height: 100%;
            background: linear-gradient(90deg, #10B981 0%, #059669 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
            font-size: 12px;
        }}
        .coverage-fill.low {{
            background: linear-gradient(90deg, #EF4444 0%, #DC2626 100%);
        }}
        .coverage-fill.medium {{
            background: linear-gradient(90deg, #F59E0B 0%, #D97706 100%);
        }}
        .service-name {{
            font-weight: 600;
            color: #2D5757;
        }}
        .test-count {{
            color: #3B82F6;
            font-weight: 600;
        }}
        .no-data {{
            color: #999;
            font-style: italic;
        }}
        .badge {{
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: bold;
            margin-left: 8px;
        }}
        .badge.excellent {{
            background: #10B981;
            color: white;
        }}
        .badge.good {{
            background: #3B82F6;
            color: white;
        }}
        .badge.medium {{
            background: #F59E0B;
            color: white;
        }}
        .badge.low {{
            background: #EF4444;
            color: white;
        }}
        .footer {{
            text-align: center;
            padding: 20px;
            color: #666;
            font-size: 14px;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📊 Rapport de Couverture de Tests</h1>
            <p>EnglishFlow - Microservices Architecture</p>
            <p>Généré le {datetime.now().strftime('%d/%m/%Y à %H:%M:%S')}</p>
        </div>
        
        <div class="stats">
            <div class="stat-card">
                <h3>Microservices</h3>
                <div class="value">{len(metrics)}</div>
            </div>
            <div class="stat-card">
                <h3>Total Tests</h3>
                <div class="value">{total_tests}</div>
            </div>
            <div class="stat-card">
                <h3>Couverture Moyenne</h3>
                <div class="value">{avg_coverage:.0f}%</div>
            </div>
            <div class="stat-card">
                <h3>Services Testés</h3>
                <div class="value">{len(services_with_coverage)}/{len(metrics)}</div>
            </div>
        </div>
        
        <div style="padding: 30px;">
            <h2 style="color: #2D5757; margin-bottom: 20px;">Détails par Microservice</h2>
            <table>
                <thead>
                    <tr>
                        <th>Service</th>
                        <th>Nombre de Tests</th>
                        <th>Couverture</th>
                        <th>Visualisation</th>
                    </tr>
                </thead>
                <tbody>
"""

def generate_service_row(metric):
    """Génère une ligne HTML pour un service"""
    badge_class, _, badge_text = get_badge_info(metric['coverage'])
    coverage_str = f"{metric['coverage']}%" if metric['coverage'] is not None else "N/A"
    coverage_width = metric['coverage'] if metric['coverage'] is not None else 0
    
    badge_html = f'<span class="badge {badge_class}">{badge_text}</span>' if metric['coverage'] is not None else ''
    
    if metric['coverage'] is not None:
        fill_class = ''
        if metric['coverage'] < 50:
            fill_class = 'low'
        elif metric['coverage'] < 80:
            fill_class = 'medium'
        
        visualization = f'<div class="coverage-bar"><div class="coverage-fill {fill_class}" style="width: {coverage_width}%">{coverage_str}</div></div>'
    else:
        visualization = '<span class="no-data">Pas de données</span>'
    
    return f"""                    <tr>
                        <td class="service-name">{metric['service']}{badge_html}</td>
                        <td class="test-count">{metric['tests']} tests</td>
                        <td><strong>{coverage_str}</strong></td>
                        <td>{visualization}</td>
                    </tr>
"""

def generate_html_report(metrics, filename):
    """Génère le rapport HTML"""
    total_tests = sum(m['tests'] for m in metrics)
    services_with_coverage = [m for m in metrics if m['coverage'] is not None]
    avg_coverage = sum(m['coverage'] for m in services_with_coverage) / len(services_with_coverage) if services_with_coverage else 0
    
    html = generate_html_header(metrics, total_tests, avg_coverage, services_with_coverage)
    
    for m in metrics:
        html += generate_service_row(m)
    
    html += """                </tbody>
            </table>
        </div>
        
        <div class="footer">
            <p><strong>EnglishFlow</strong> - Plateforme d'apprentissage de l'anglais</p>
            <p>© 2026 Jungle in English. Tous droits réservés.</p>
        </div>
    </div>
</body>
</html>"""
    
    with open(filename, 'w', encoding='utf-8') as f:
        f.write(html)

def main():
    print("=" * 70)
    print("   📊 RAPPORT DE COUVERTURE - ENGLISHFLOW MICROSERVICES")
    print("=" * 70)
    print()
    print("🔄 Analyse des rapports Jacoco...")
    print()
    
    metrics = []
    for service in SERVICES:
        if os.path.exists(f"backend/{service}"):
            print(f"📦 Analyse de {service}...")
            m = get_service_metrics(service)
            metrics.append(m)
            coverage_str = f"{m['coverage']}%" if m['coverage'] is not None else "N/A"
            print(f"   ✅ {m['tests']} tests | Couverture: {coverage_str}")
    
    # Générer les rapports
    timestamp = datetime.now().strftime('%Y%m%d-%H%M%S')
    text_file = f"coverage-report-{timestamp}.txt"
    html_file = f"coverage-report-{timestamp}.html"
    
    generate_text_report(metrics, text_file)
    generate_html_report(metrics, html_file)
    
    print()
    print("=" * 70)
    print("   ✅ RAPPORTS GÉNÉRÉS AVEC SUCCÈS")
    print("=" * 70)
    print()
    print(f"📄 Rapport texte: {text_file}")
    print(f"🌐 Rapport HTML:  {html_file}")
    print()
    print("Pour ouvrir le rapport HTML:")
    print(f"   open {html_file}")
    print()

if __name__ == "__main__":
    main()
