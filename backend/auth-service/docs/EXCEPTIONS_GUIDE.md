# Guide des Exceptions - Auth Service

## 📋 Liste des Exceptions Personnalisées

### 1. UserNotFoundException
**Code HTTP:** 404 NOT FOUND  
**Utilisation:** Quand un utilisateur n'est pas trouvé

```java
throw new UserNotFoundException(userId);
throw new UserNotFoundException("email", email);
```

### 2. InvalidTokenException
**Code HTTP:** 401 UNAUTHORIZED  
**Utilisation:** Token JWT invalide ou malformé

```java
throw new InvalidTokenException("JWT", "Token signature invalid");
```

### 3. TokenExpiredException
**Code HTTP:** 401 UNAUTHORIZED  
**Utilisation:** Token expiré

```java
throw new TokenExpiredException("Activation");
throw new TokenExpiredException("Password reset");
```

### 4. AccountNotActivatedException
**Code HTTP:** 403 FORBIDDEN  
**Utilisation:** Compte non activé

```java
throw new AccountNotActivatedException(email);
```

### 5. RateLimitExceededException
**Code HTTP:** 429 TOO MANY REQUESTS  
**Utilisation:** Limite de requêtes dépassée

```java
throw new RateLimitExceededException("login", 900); // 900 seconds
```

### 6. InvitationExpiredException
**Code HTTP:** 410 GONE  
**Utilisation:** Invitation expirée

```java
throw new InvitationExpiredException();
```

### 7. InvitationAlreadyUsedException
**Code HTTP:** 409 CONFLICT  
**Utilisation:** Invitation déjà utilisée

```java
throw new InvitationAlreadyUsedException();
```

### 8. EmailAlreadyExistsException
**Code HTTP:** 409 CONFLICT  
**Utilisation:** Email déjà enregistré

```java
throw new EmailAlreadyExistsException(email);
```

### 9. InvalidCredentialsException
**Code HTTP:** 401 UNAUTHORIZED  
**Utilisation:** Identifiants incorrects

```java
throw new InvalidCredentialsException();
```

### 10. RecaptchaVerificationException
**Code HTTP:** 400 BAD REQUEST  
**Utilisation:** Échec vérification reCAPTCHA

```java
throw new RecaptchaVerificationException();
```

### 11. SessionNotFoundException
**Code HTTP:** 404 NOT FOUND  
**Utilisation:** Session introuvable

```java
throw new SessionNotFoundException(sessionId);
```

### 12. UnauthorizedSessionAccessException
**Code HTTP:** 403 FORBIDDEN  
**Utilisation:** Accès non autorisé à une session

```java
throw new UnauthorizedSessionAccessException();
```

### 13. FileStorageException
**Code HTTP:** 500 INTERNAL SERVER ERROR  
**Utilisation:** Erreur stockage fichier

```java
throw new FileStorageException("Failed to store file", cause);
```

---

## 🔄 Migration du Code Existant

### Avant (RuntimeException générique)
```java
if (!userRepository.existsById(userId)) {
    throw new RuntimeException("User not found");
}
```

### Après (Exception personnalisée)
```java
if (!userRepository.existsById(userId)) {
    throw new UserNotFoundException(userId);
}
```

---

## 📝 Format de Réponse

```json
{
  "timestamp": "2024-02-20T10:30:00",
  "status": 404,
  "error": "User Not Found",
  "message": "User not found with ID: 123",
  "path": "/auth/users/123"
}
```
