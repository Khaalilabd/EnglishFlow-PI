# Comment lancer Auth Service

## Option 1: Avec variables d'environnement (Recommandé pour production)

```bash
# 1. Configure JAVA_HOME
export JAVA_HOME=/path/to/java17

# 2. Exporte les variables d'environnement
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD="your-app-password"
export GOOGLE_CLIENT_ID=your-google-client-id
export GOOGLE_CLIENT_SECRET=your-google-client-secret
export FRONTEND_URL=http://localhost:4200

# 3. Lance le service
cd backend/auth-service
mvn spring-boot:run
```

## Option 2: Avec fichier .env (Développement local)

```bash
# 1. Crée le fichier .env
cp .env.example .env

# 2. Édite .env avec tes credentials

# 3. Charge les variables
export $(cat .env | xargs)

# 4. Lance le service
mvn spring-boot:run
```

## Option 3: Avec IntelliJ IDEA

1. Ouvre le projet dans IntelliJ
2. Va dans Run > Edit Configurations
3. Ajoute les variables d'environnement dans "Environment variables"
4. Lance AuthServiceApplication

## Sur un autre PC

1. Clone le repo
2. Demande le fichier `.env` à l'équipe (ne pas commit ce fichier!)
3. Suis l'Option 2 ci-dessus

## Vérification

Le service est lancé quand tu vois:
```
Started AuthServiceApplication in X seconds
```

Teste: http://localhost:8081/actuator/health
