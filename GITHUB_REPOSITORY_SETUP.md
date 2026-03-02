# Guide de Configuration du Dépôt GitHub selon les Standards Esprit

Ce document vous guide pour mettre à jour votre dépôt GitHub afin de respecter les standards Esprit School of Engineering pour les projets PI.

---

## 📋 Checklist des Modifications Requises

### ✅ 1. README.md
- [x] Créé et conforme aux standards Esprit
- [x] Mention "Developed at Esprit School of Engineering – Tunisia"
- [x] Année académique 2025-2026 incluse
- [x] Structure complète avec toutes les sections obligatoires

### ⚠️ 2. Nom du Dépôt (Action Manuelle Requise)

**Nom actuel**: `EnglishFlow-PI`  
**Nom requis**: `Esprit-PIDEV-3A-2026-JungleInEnglish` (ou similaire)

#### Convention de Nommage Esprit
```
Esprit-[PI]-[Classe]-[AU]-[NomDuProjet]
```

**Exemples pour votre projet**:
- `Esprit-PIDEV-3A-2026-JungleInEnglish`
- `Esprit-PIDEV-4A-2026-EnglishFlow`
- `Esprit-PI-3A10-2026-JungleInEnglish`

#### Comment Renommer le Dépôt

1. **Sur GitHub**:
   - Allez sur votre dépôt: https://github.com/Khaalilabd/EnglishFlow-PI
   - Cliquez sur **Settings** (en haut à droite)
   - Dans la section **Repository name**, entrez le nouveau nom
   - Cliquez sur **Rename**

2. **Mettez à jour votre dépôt local**:
   ```bash
   # Mettez à jour l'URL remote
   git remote set-url origin https://github.com/Khaalilabd/Esprit-PIDEV-3A-2026-JungleInEnglish.git
   
   # Vérifiez la nouvelle URL
   git remote -v
   ```

3. **Informez votre équipe**:
   - Tous les membres doivent mettre à jour leur remote URL
   - Partagez la nouvelle URL du dépôt

---

### ⚠️ 3. Description du Dépôt (Action Manuelle Requise)

#### Description Actuelle
Probablement vide ou incomplète

#### Description Requise
Ajoutez cette description dans les paramètres GitHub:

```
English learning platform with microservices architecture. Developed at Esprit School of Engineering – Tunisia. Academic Year 2025-2026. Technologies: Angular 18, Spring Boot 3, PostgreSQL, JWT, OAuth2.
```

#### Comment Ajouter la Description

1. Allez sur votre dépôt GitHub
2. Cliquez sur l'icône **⚙️ (Settings)** à côté de "About" (en haut à droite)
3. Dans le champ **Description**, collez la description ci-dessus
4. Cochez **Use your GitHub Pages website** si vous déployez sur GitHub Pages
5. Cliquez sur **Save changes**

---

### ⚠️ 4. Topics GitHub (Action Manuelle Requise)

#### Topics Obligatoires Minimum

Ajoutez ces topics à votre dépôt:

```
esprit-school-of-engineering
academic-project
esprit-pidev
2025-2026
angular
spring-boot
microservices
java
typescript
postgresql
jwt-authentication
oauth2
e-learning
english-learning
```

#### Topics Recommandés Additionnels

```
spring-cloud
eureka
api-gateway
docker
rest-api
full-stack
education-technology
tunisia
```

#### Comment Ajouter les Topics

1. Allez sur votre dépôt GitHub
2. Cliquez sur l'icône **⚙️ (Settings)** à côté de "About"
3. Dans le champ **Topics**, ajoutez les topics un par un
4. Appuyez sur **Entrée** après chaque topic
5. Cliquez sur **Save changes**

**Astuce**: Vous pouvez copier-coller tous les topics séparés par des espaces, GitHub les séparera automatiquement.

---

### ⚠️ 5. Visibilité du Dépôt

#### Recommandation Esprit
- **Public** (recommandé pour les projets académiques)
- Permet de partager votre travail avec la communauté
- Facilite l'évaluation par les professeurs

#### Comment Vérifier/Modifier

1. Allez dans **Settings** > **General**
2. Descendez jusqu'à **Danger Zone**
3. Vérifiez que le dépôt est **Public**
4. Si privé, cliquez sur **Change visibility** > **Change to public**

---

### ✅ 6. Fichier .gitignore

Vérifiez que les fichiers sensibles sont bien ignorés:

```gitignore
# Fichiers sensibles
.env
*.env
!.env.example

# Credentials
**/application-local.yml
**/application-local.properties

# Logs
logs/
*.log
*.log.gz

# Build artifacts
target/
dist/
node_modules/

# IDE
.idea/
.vscode/
*.iml

# OS
.DS_Store
Thumbs.db
```

---

### ✅ 7. Fichier LICENSE (Optionnel mais Recommandé)

Pour un projet académique, vous pouvez ajouter:

```
Academic Project License

Copyright (c) 2025-2026 Esprit School of Engineering

This project is developed for academic purposes as part of the Integrated Project (PI) 
curriculum at Esprit School of Engineering, Tunisia.

All rights reserved.
```

Créez un fichier `LICENSE` à la racine avec ce contenu.

---

## 🚀 Déploiement (Recommandé par Esprit)

### Options de Déploiement Gratuites

#### Frontend (Angular)
1. **Vercel** (Recommandé)
   - Connectez votre dépôt GitHub
   - Déploiement automatique à chaque push
   - URL: `https://jungle-in-english.vercel.app`

2. **Netlify**
   - Alternative à Vercel
   - Configuration similaire

3. **GitHub Pages**
   - Gratuit avec GitHub
   - Parfait pour les projets académiques

#### Backend (Spring Boot)
1. **Railway** (Recommandé)
   - Supporte PostgreSQL
   - Déploiement facile des microservices
   - Free tier généreux

2. **Render**
   - Free tier disponible
   - Supporte Docker

3. **Heroku**
   - Classique et fiable
   - Free tier limité

### Ajoutez l'URL de Déploiement

Une fois déployé, ajoutez l'URL dans:
1. La description du dépôt GitHub
2. Le README.md (section "Demo")
3. Les topics GitHub (ex: `deployed-on-vercel`)

---

## 📊 Badges GitHub (Optionnel mais Professionnel)

Ajoutez des badges au début de votre README pour un aspect professionnel:

```markdown
![Angular](https://img.shields.io/badge/Angular-18-red?logo=angular)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?logo=springboot)
![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue?logo=postgresql)
![License](https://img.shields.io/badge/License-Academic-yellow)
![Esprit](https://img.shields.io/badge/Esprit-2025--2026-red)
```

---

## ✅ Vérification Finale

Avant de soumettre votre projet, vérifiez:

- [ ] Le nom du dépôt suit la convention Esprit
- [ ] La description mentionne "Esprit School of Engineering"
- [ ] Les topics obligatoires sont ajoutés
- [ ] Le README.md est complet et professionnel
- [ ] Les fichiers sensibles (.env) ne sont pas versionnés
- [ ] Le projet est déployé (si possible)
- [ ] L'URL de déploiement est documentée
- [ ] Les instructions d'installation sont claires
- [ ] La documentation est en anglais (standard international)

---

## 📞 Support

Si vous avez des questions:
1. Consultez le PDF de standardisation Esprit
2. Contactez votre superviseur académique
3. Demandez de l'aide à votre équipe

---

## 🎯 Résumé des Actions Manuelles Requises

| Action | Priorité | Temps Estimé |
|--------|----------|--------------|
| Renommer le dépôt | 🔴 Haute | 2 min |
| Ajouter la description | 🔴 Haute | 1 min |
| Ajouter les topics | 🔴 Haute | 3 min |
| Vérifier .gitignore | 🟡 Moyenne | 2 min |
| Déployer l'application | 🟢 Basse | 30 min |
| Ajouter des badges | 🟢 Basse | 5 min |

**Temps total**: ~45 minutes

---

**Bon courage pour votre projet PI! 🚀**

*Document créé conformément aux standards Esprit School of Engineering*
