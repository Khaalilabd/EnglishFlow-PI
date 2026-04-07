# Sponsors Service

Service de gestion des sponsors pour la plateforme Jungle in English.

## Fonctionnalités

- Gestion CRUD des sponsors
- **Calcul automatique du niveau** selon le montant de contribution
- Système de **cache Caffeine** pour optimiser les performances
- **Gestion d'erreurs personnalisées** avec GlobalExceptionHandler
- **Validation des données** avec Jakarta Validation
- **MapStruct** pour le mapping DTO ↔ Entity

## Niveaux de Sponsors (Automatiques)

Les niveaux sont calculés automatiquement selon le montant de contribution:
- **BRONZE**: 0-499 DT
- **SILVER**: 500-999 DT  
- **GOLD**: 1000+ DT

> Note: Le niveau PARTNER a été retiré. Les niveaux sont maintenant calculés automatiquement.

## Optimisations Appliquées

### Backend
- **MapStruct**: Mapping automatique DTO ↔ Entity (plus de méthodes manuelles)
- **Caffeine Cache**: Cache en mémoire (10 min, max 200 entrées)
  - `sponsors`: Liste complète
  - `sponsorById`: Sponsors individuels
  - `sponsorsByLevel`: Sponsors par niveau
- **Exceptions personnalisées**: 
  - `SponsorNotFoundException`
  - `GlobalExceptionHandler` pour gestion centralisée
- **Validation**: Annotations Jakarta sur SponsorDTO
  - `@NotBlank` pour name
  - `@Email` pour contactEmail
  - `@NotNull` et `@Min(0)` pour contributionAmount
- **Transactions optimisées**: `@Transactional(readOnly = true)` pour les lectures

### Frontend
- **Cache client**: BehaviorSubject avec TTL de 5 minutes
- **Force refresh**: Option `forceRefresh` pour recharger après modifications
- **Logs optimisés**: Distinction entre appels API et cache
- **Auto-invalidation**: Cache invalidé automatiquement après create/update/delete

## API Endpoints

- `GET /sponsors` - Liste tous les sponsors (cached)
- `GET /sponsors/{id}` - Détails d'un sponsor (cached)
- `GET /sponsors/level/{level}` - Sponsors par niveau (cached)
- `POST /sponsors` - Créer un sponsor (invalide cache)
- `PUT /sponsors/{id}` - Modifier un sponsor (invalide cache)
- `DELETE /sponsors/{id}` - Supprimer un sponsor (invalide cache)

## Configuration

Variables d'environnement (.env):
```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sponsors_db
DB_USERNAME=postgres
DB_PASSWORD=password
EUREKA_SERVER_URL=http://localhost:8761/eureka
```

## Démarrage

```bash
# Installer les dépendances et compiler
mvn clean install

# Démarrer le service
mvn spring-boot:run
```

Le service démarre sur le port 8089 par défaut.

## Base de données

Le service utilise PostgreSQL. Créer la base de données :

```sql
CREATE DATABASE sponsors_db;
```

Les tables seront créées automatiquement au démarrage avec JPA.

## Dépendances Principales

- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL
- MapStruct 1.5.5
- Caffeine Cache
- Lombok
- Spring Cloud Eureka Client
