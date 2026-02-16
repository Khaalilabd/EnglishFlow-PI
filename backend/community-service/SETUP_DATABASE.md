# Configuration de la base de données Community Service

## Étape 1: Créer la base de données

### Option 1: Via psql (ligne de commande)

```bash
# Se connecter à PostgreSQL
psql -U root -h localhost

# Créer la base de données
CREATE DATABASE englishflow_community;

# Se connecter à la base
\c englishflow_community

# Vérifier
\l
```

### Option 2: Via script SQL

```bash
psql -U root -h localhost -f create-database.sql
```

### Option 3: Via pgAdmin ou autre GUI
1. Ouvrir pgAdmin
2. Clic droit sur "Databases"
3. Create > Database
4. Nom: `englishflow_community`
5. Owner: `root`
6. Save

## Étape 2: Vérifier la configuration

Vérifiez que les credentials dans `application.yml` correspondent à votre configuration PostgreSQL:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/englishflow_community
    username: root
    password: root
```

## Étape 3: Démarrer le service

```bash
cd backend/community-service
mvn spring-boot:run
```

ou

```bash
./run.bat
```

## Étape 4: Vérifier la création des tables

Les tables seront créées automatiquement par Hibernate (ddl-auto: update).

Vérifier dans psql:
```bash
psql -U root -d englishflow_community

# Lister les tables
\dt

# Vous devriez voir:
# - categories
# - sub_categories
# - topics
# - posts
```

## Étape 5: Initialiser les catégories

Une fois les tables créées, exécutez le script d'initialisation:

```bash
psql -U root -d englishflow_community -f init-categories.sql
```

## Vérification

```bash
# Vérifier les catégories
psql -U root -d englishflow_community -c "SELECT * FROM categories;"

# Vérifier les sous-catégories
psql -U root -d englishflow_community -c "SELECT * FROM sub_categories;"
```

## Troubleshooting

### Erreur: "database does not exist"
- Créez la base de données manuellement (voir Étape 1)

### Erreur: "password authentication failed"
- Vérifiez les credentials dans `application.yml`
- Vérifiez que l'utilisateur `root` existe dans PostgreSQL

### Les tables ne sont pas créées
- Vérifiez que le service démarre sans erreur
- Vérifiez les logs: `mvn spring-boot:run`
- Vérifiez que `ddl-auto: update` est bien configuré

### Erreur de connexion
- Vérifiez que PostgreSQL est démarré
- Vérifiez le port (5432 par défaut)
- Vérifiez que PostgreSQL accepte les connexions locales
