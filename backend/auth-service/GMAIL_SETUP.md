# Configuration Gmail pour l'envoi d'emails

## Étapes pour configurer Gmail:

### 1. Activer l'authentification à 2 facteurs sur ton compte Gmail
- Va sur https://myaccount.google.com/security
- Active "Vérification en deux étapes"

### 2. Créer un mot de passe d'application
- Va sur https://myaccount.google.com/apppasswords
- Sélectionne "Mail" et "Autre (nom personnalisé)"
- Nomme-le "EnglishFlow Auth Service"
- Google va générer un mot de passe de 16 caractères

### 3. Configurer les variables d'environnement

Crée un fichier `.env` dans `backend/auth-service/`:

```bash
MAIL_USERNAME=ton-email@gmail.com
MAIL_PASSWORD=xxxx xxxx xxxx xxxx  # Le mot de passe d'application de 16 caractères
FRONTEND_URL=http://localhost:4200
```

### 4. Lancer le service avec les variables d'environnement

```bash
export MAIL_USERNAME=ton-email@gmail.com
export MAIL_PASSWORD="xxxx xxxx xxxx xxxx"
cd backend/auth-service
mvn spring-boot:run
```

## Alternative: Utiliser un autre service SMTP

Si tu préfères utiliser un autre service (Outlook, SendGrid, etc.), modifie `application.yml`:

### Pour Outlook:
```yaml
spring:
  mail:
    host: smtp-mail.outlook.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

### Pour SendGrid:
```yaml
spring:
  mail:
    host: smtp.sendgrid.net
    port: 587
    username: apikey
    password: ${SENDGRID_API_KEY}
```

## Test

Une fois configuré, teste en créant un nouveau compte ou en demandant un reset password!
