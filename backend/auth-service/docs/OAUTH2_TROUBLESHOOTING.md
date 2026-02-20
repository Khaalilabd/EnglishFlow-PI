# 🔧 OAuth2 Troubleshooting Guide

## Problème: Safari ne parvient pas à se connecter après OAuth2

### Symptômes
- L'URL contient un `#` au milieu et est dupliquée
- Exemple: `http://localhost:4200/oauth2/callback?token=...#http://localhost:4200/oauth2/callback?token=...`
- Safari affiche "safari ne parvient pas à se connecter"

### Cause
Safari peut avoir des problèmes avec les redirections OAuth2 qui contiennent des caractères spéciaux non encodés dans l'URL.

### Solution Appliquée

#### 1. Backend - Encodage de l'URL
```java
// OAuth2AuthenticationSuccessHandler.java
String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/callback")
    .queryParam("token", token)
    .queryParam("id", user.getId())
    // ... autres params
    .build()
    .encode() // ✅ Encode l'URL pour éviter les problèmes
    .toUriString();
```

#### 2. Frontend - Gestion robuste des paramètres
```typescript
// oauth2-callback.component.ts
ngOnInit(): void {
  this.route.queryParams.subscribe(params => {
    // Gestion du fragment en cas de problème de routing
    const fragment = this.route.snapshot.fragment;
    let actualParams = { ...params };
    
    // Si le fragment contient des query params, les parser
    if (fragment && fragment.includes('?')) {
      const fragmentParams = new URLSearchParams(fragment.split('?')[1]);
      fragmentParams.forEach((value, key) => {
        if (!actualParams[key]) {
          actualParams[key] = value;
        }
      });
    }
    
    // Utiliser actualParams au lieu de params
    const token = actualParams['token'];
    // ...
  });
}
```

### Vérification

#### 1. Tester la connexion OAuth2
```bash
# Démarrer le backend
cd backend/auth-service
mvn spring-boot:run

# Démarrer le frontend
cd frontend
npm start

# Ouvrir dans Safari
open -a Safari http://localhost:4200/login
```

#### 2. Vérifier les logs backend
```bash
# Chercher les logs de redirection OAuth2
tail -f backend/auth-service/logs/application.log | grep "Redirecting OAuth2"
```

#### 3. Vérifier les logs frontend (Console Safari)
```javascript
// Ouvrir la console Safari (Cmd+Option+C)
// Chercher les logs:
// "OAuth2 Callback - Received params"
// "OAuth2 Callback - User authenticated, redirecting..."
```

### Autres Solutions Possibles

#### Si le problème persiste:

1. **Vérifier la configuration CORS**
```yaml
# application.yml
spring:
  web:
    cors:
      allowed-origins: http://localhost:4200
      allowed-methods: GET, POST, PUT, DELETE, OPTIONS
      allowed-headers: "*"
```

2. **Vérifier la configuration OAuth2**
```yaml
# application.yml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
```

3. **Utiliser PathLocationStrategy au lieu de HashLocationStrategy**
```typescript
// app.config.ts
import { provideRouter, withHashLocation } from '@angular/router';

export const appConfig: ApplicationConfig = {
  providers: [
    // Ne PAS utiliser withHashLocation() si possible
    provideRouter(routes), // ✅ Utilise PathLocationStrategy par défaut
  ]
};
```

### Tests

#### Test 1: Connexion Google OAuth2
1. Aller sur http://localhost:4200/login
2. Cliquer sur "Sign in with Google"
3. Autoriser l'application
4. Vérifier la redirection vers `/oauth2/callback`
5. Vérifier la redirection finale vers `/` ou `/auth/complete-profile`

#### Test 2: Vérifier les paramètres
```javascript
// Dans la console Safari sur /oauth2/callback
console.log(window.location.href);
// Devrait afficher: http://localhost:4200/oauth2/callback?token=...&id=...
// PAS: http://localhost:4200/oauth2/callback?token=...#http://...
```

### Logs de Débogage

#### Backend
```java
log.info("Redirecting OAuth2 user {} to: {}", user.getEmail(), targetUrl);
```

#### Frontend
```typescript
console.log('OAuth2 Callback - Received params:', { 
  token: token ? 'present' : 'missing', 
  id, 
  email, 
  role, 
  profileCompleted 
});
```

### Problèmes Connus

1. **Safari bloque les cookies tiers**
   - Solution: Utiliser JWT dans localStorage au lieu de cookies

2. **URL trop longue**
   - Solution: JWT déjà utilisé (pas de données sensibles dans l'URL)

3. **Redirection multiple**
   - Solution: Utiliser `setTimeout` pour éviter les redirections trop rapides

### Support

Si le problème persiste:
1. Vérifier les logs backend et frontend
2. Tester avec un autre navigateur (Chrome, Firefox)
3. Vérifier la configuration OAuth2 dans Google Console
4. Vérifier que `app.frontend.url` est correct dans `application.yml`

---

**Dernière mise à jour:** 20 Février 2024
