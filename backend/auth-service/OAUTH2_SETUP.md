# Configuration OAuth2 - Jungle in English

## 🔐 Providers à configurer:
1. Google
2. LinkedIn
3. GitHub
4. Discord
5. Microsoft (Outlook)

---

## 1️⃣ Google OAuth2

### Créer une application Google:
1. Va sur https://console.cloud.google.com/
2. Crée un nouveau projet "Jungle in English"
3. Active "Google+ API"
4. Va dans "Credentials" → "Create Credentials" → "OAuth 2.0 Client ID"
5. Type: Web application
6. Authorized redirect URIs:
   - `http://localhost:8081/login/oauth2/code/google`
   - `http://localhost:4200/oauth2/callback/google`

### Tu recevras:
- Client ID: `xxx.apps.googleusercontent.com`
- Client Secret: `xxx`

---

## 2️⃣ LinkedIn OAuth2

### Créer une application LinkedIn:
1. Va sur https://www.linkedin.com/developers/apps
2. Crée une nouvelle app
3. Dans "Auth" tab:
4. Redirect URLs:
   - `http://localhost:8081/login/oauth2/code/linkedin`
   - `http://localhost:4200/oauth2/callback/linkedin`

### Tu recevras:
- Client ID: `xxx`
- Client Secret: `xxx`

---

## 3️⃣ GitHub OAuth2

### Créer une OAuth App GitHub:
1. Va sur https://github.com/settings/developers
2. "New OAuth App"
3. Application name: Jungle in English
4. Homepage URL: `http://localhost:4200`
5. Authorization callback URL: `http://localhost:8081/login/oauth2/code/github`

### Tu recevras:
- Client ID: `xxx`
- Client Secret: `xxx`

---

## 4️⃣ Discord OAuth2

### Créer une application Discord:
1. Va sur https://discord.com/developers/applications
2. "New Application"
3. Dans OAuth2 → Redirects:
   - `http://localhost:8081/login/oauth2/code/discord`

### Tu recevras:
- Client ID: `xxx`
- Client Secret: `xxx`

---

## 5️⃣ Microsoft (Outlook) OAuth2

### Créer une app Microsoft:
1. Va sur https://portal.azure.com/#blade/Microsoft_AAD_RegisteredApps/ApplicationsListBlade
2. "New registration"
3. Name: Jungle in English
4. Redirect URI: `http://localhost:8081/login/oauth2/code/microsoft`

### Tu recevras:
- Client ID (Application ID): `xxx`
- Client Secret: Dans "Certificates & secrets"

---

## ⚙️ Configuration

### Variables d'environnement (.env)

Pour des raisons de sécurité, les credentials OAuth2 sont stockés dans le fichier `.env`:

```env
# Google OAuth2
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# LinkedIn OAuth2
LINKEDIN_CLIENT_ID=your-linkedin-client-id
LINKEDIN_CLIENT_SECRET=your-linkedin-client-secret

# GitHub OAuth2
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret

# Discord OAuth2
DISCORD_CLIENT_ID=your-discord-client-id
DISCORD_CLIENT_SECRET=your-discord-client-secret

# Microsoft OAuth2
MICROSOFT_CLIENT_ID=your-microsoft-client-id
MICROSOFT_CLIENT_SECRET=your-microsoft-client-secret
```

### Configuration application.yml

Le fichier `application.yml` utilise ces variables d'environnement:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: profile, email
          
          linkedin:
            client-id: ${LINKEDIN_CLIENT_ID}
            client-secret: ${LINKEDIN_CLIENT_SECRET}
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: r_liteprofile, r_emailaddress
          
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope: user:email
          
          discord:
            client-id: ${DISCORD_CLIENT_ID}
            client-secret: ${DISCORD_CLIENT_SECRET}
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: identify, email
          
          microsoft:
            client-id: ${MICROSOFT_CLIENT_ID}
            client-secret: ${MICROSOFT_CLIENT_SECRET}
            scope: openid, profile, email
        
        provider:
          linkedin:
            authorization-uri: https://www.linkedin.com/oauth/v2/authorization
            token-uri: https://www.linkedin.com/oauth/v2/accessToken
            user-info-uri: https://api.linkedin.com/v2/me
            user-name-attribute: id
          
          discord:
            authorization-uri: https://discord.com/api/oauth2/authorize
            token-uri: https://discord.com/api/oauth2/token
            user-info-uri: https://discord.com/api/users/@me
            user-name-attribute: id
```

## 📝 Prochaines étapes:

1. Crée les applications OAuth2 pour chaque provider
2. Note les Client ID et Client Secret
3. On les configurera ensemble dans le code
