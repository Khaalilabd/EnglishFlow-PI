# Gamification Service

Service de gamification pour EnglishFlow - Gestion des niveaux, badges, points XP et récompenses.

## Configuration

### Base de données

1. Créer la base de données PostgreSQL:
```sql
CREATE DATABASE englishflow_gamification;
```

2. Initialiser les badges par défaut:
```bash
psql -U postgres -d englishflow_gamification -f init-badges.sql
```

### Variables d'environnement

Copier `.env.example` vers `.env` et configurer:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=englishflow_gamification
DB_USERNAME=postgres
DB_PASSWORD=123456
JWT_SECRET=mySecretKeyForJWTTokenGenerationAndValidation123456789
```

## Démarrage

### Compilation
```bash
mvn clean compile
```

### Lancement
```bash
mvn spring-boot:run
```

Le service démarre sur le port **8086**.

## Endpoints API

### Niveau utilisateur

- `GET /gamification/users/{userId}/level` - Obtenir le niveau d'un utilisateur
- `POST /gamification/users/{userId}/initialize` - Initialiser le niveau d'un nouvel utilisateur
- `POST /gamification/users/{userId}/xp` - Ajouter de l'XP
- `POST /gamification/users/{userId}/coins` - Ajouter des Jungle Coins
- `POST /gamification/users/{userId}/coins/spend` - Dépenser des Jungle Coins

### Badges

- `GET /gamification/users/{userId}/badges` - Obtenir tous les badges d'un utilisateur
- `GET /gamification/users/{userId}/badges/new` - Obtenir les nouveaux badges non vus
- `POST /gamification/users/{userId}/badges` - Attribuer un badge
- `POST /gamification/users/{userId}/badges/mark-seen` - Marquer les badges comme vus

### Achats

- `POST /gamification/users/{userId}/purchase` - Enregistrer un achat (pour le système de fidélité)

### Health Check

- `GET /gamification/health` - Vérifier l'état du service

## Système de gamification

### Niveaux d'anglais (Assessment)

Les utilisateurs progressent à travers 6 niveaux basés sur l'XP:

| Niveau | XP Requis | Icon |
|--------|-----------|------|
| A1     | 0         | 🌱   |
| A2     | 1,000     | 🌿   |
| B1     | 2,500     | 🌳   |
| B2     | 5,000     | 🎋   |
| C1     | 8,000     | 🌲   |
| C2     | 12,000    | 🌴   |

### Jungle Coins

Monnaie virtuelle gagnée par:
- Montée de niveau
- Obtention de badges
- Complétion d'activités
- Achats (cashback)

Utilisable pour:
- Débloquer du contenu premium
- Acheter des avatars/thèmes
- Réductions sur les cours

### Paliers de fidélité

Basés sur le montant total dépensé:

| Palier   | Dépense | Réduction | Icon |
|----------|---------|-----------|------|
| Bronze   | 0€      | 0%        | 🥉   |
| Silver   | 500€    | 5%        | 🥈   |
| Gold     | 1,500€  | 10%       | 🥇   |
| Platinum | 3,000€  | 15%       | 💎   |

### Badges

20 badges par défaut répartis en 5 catégories:
- **Achievement** (5): Progression générale
- **Streak** (4): Séries de connexion
- **Quiz** (3): Performance aux quiz
- **Social** (3): Interactions sociales
- **Special** (5): Événements spéciaux

Chaque badge rapporte des Jungle Coins en récompense.

## Intégration

### Frontend

Le service est utilisé par le frontend Angular via `GamificationService`:

```typescript
// Charger le niveau utilisateur
this.gamificationService.getUserLevel(userId).subscribe(level => {
  console.log('Level:', level.assessmentLevel);
  console.log('XP:', level.totalXP);
  console.log('Coins:', level.jungleCoins);
});

// Charger les badges
this.gamificationService.getUserBadges(userId).subscribe(badges => {
  console.log('Badges:', badges);
});
```

### Autres services

Les autres services peuvent appeler les endpoints pour:
- Attribuer de l'XP après une activité
- Déclencher l'attribution de badges
- Enregistrer des achats pour le système de fidélité

## Architecture

```
gamification-service/
├── entity/          # Entités JPA
│   ├── UserLevel.java
│   ├── Badge.java
│   ├── UserBadge.java
│   ├── EnglishLevel.java
│   ├── LoyaltyTier.java
│   ├── BadgeType.java
│   └── BadgeRarity.java
├── repository/      # Repositories Spring Data
├── service/         # Logique métier
├── controller/      # Endpoints REST
└── dto/            # Data Transfer Objects
```

## Tests

```bash
mvn test
```

## Monitoring

Le service expose des endpoints Actuator pour le monitoring:
- `/actuator/health` - État de santé
- `/actuator/metrics` - Métriques
- `/actuator/info` - Informations

## Notes

- Le service utilise JWT pour l'authentification
- Les badges sont initialisés automatiquement au démarrage via `BadgeInitializationService`
- Les niveaux sont calculés automatiquement en fonction de l'XP
- Les séries (streaks) sont mises à jour à chaque activité
