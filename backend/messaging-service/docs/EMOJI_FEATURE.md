# Fonctionnalité d'Envoi d'Emojis

## Vue d'ensemble

Le service de messagerie supporte maintenant l'envoi d'emojis en tant que type de message distinct. Les emojis peuvent être envoyés de deux manières:

1. **Emojis dans le texte**: Inclus directement dans le contenu d'un message TEXT
2. **Messages emoji dédiés**: Messages de type EMOJI avec un code emoji spécifique

## Types de Messages

```java
public enum MessageType {
    TEXT,   // Message texte standard (peut contenir des emojis)
    FILE,   // Fichier attaché
    IMAGE,  // Image
    EMOJI   // Emoji dédié (nouveau)
}
```

## Envoi d'un Message Emoji

### Via REST API

**Endpoint**: `POST /messaging/conversations/{conversationId}/messages`

**Exemple de requête**:

```json
{
  "content": "👍",
  "messageType": "EMOJI",
  "emojiCode": "U+1F44D"
}
```

**Champs**:
- `content`: L'emoji en UTF-8 (optionnel si emojiCode est fourni)
- `messageType`: Doit être "EMOJI"
- `emojiCode`: Code Unicode de l'emoji (ex: "U+1F44D" pour 👍) ou l'emoji natif

### Via WebSocket

**Destination**: `/app/chat/{conversationId}`

**Exemple de payload**:

```json
{
  "content": "❤️",
  "messageType": "EMOJI",
  "emojiCode": "U+2764"
}
```

## Validation

Pour les messages de type EMOJI:
- Le champ `emojiCode` est **obligatoire**
- Le champ `content` est optionnel
- La longueur maximale de `emojiCode` est de 50 caractères

Pour les messages de type TEXT:
- Le champ `content` est **obligatoire**
- Peut contenir des emojis UTF-8 directement dans le texte
- Longueur maximale: 5000 caractères

## Formats de Code Emoji Supportés

1. **Unicode avec préfixe U+**: `U+1F600`, `U+1F44D`
2. **Emoji natif UTF-8**: `😀`, `👍`, `❤️`
3. **Code hexadécimal**: `1F600`, `1F44D`
4. **Shortcode**: `:smile:`, `:thumbsup:` (si implémenté côté client)

## Exemples d'Utilisation

### Exemple 1: Emoji simple

```bash
curl -X POST http://localhost:8084/messaging/conversations/1/messages \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "👍",
    "messageType": "EMOJI",
    "emojiCode": "U+1F44D"
  }'
```

### Exemple 2: Texte avec emojis

```bash
curl -X POST http://localhost:8084/messaging/conversations/1/messages \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Super travail! 🎉👏",
    "messageType": "TEXT"
  }'
```

### Exemple 3: WebSocket avec emoji

```javascript
// Connexion WebSocket
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect(headers, () => {
  // Envoyer un emoji
  stompClient.send('/app/chat/1', {}, JSON.stringify({
    content: '❤️',
    messageType: 'EMOJI',
    emojiCode: 'U+2764'
  }));
});
```

## Réponse API

La réponse inclut le nouveau champ `emojiCode`:

```json
{
  "id": 123,
  "conversationId": 1,
  "senderId": 42,
  "senderName": "John Doe",
  "senderAvatar": "https://...",
  "content": "👍",
  "messageType": "EMOJI",
  "emojiCode": "U+1F44D",
  "isEdited": false,
  "createdAt": "2026-02-21T10:30:00",
  "updatedAt": "2026-02-21T10:30:00",
  "readBy": [],
  "reactions": []
}
```

## Migration Base de Données

La migration SQL ajoute automatiquement:
- Colonne `emoji_code` (VARCHAR(50), nullable)
- Index sur `emoji_code` pour les recherches optimisées
- Support du type EMOJI dans l'enum message_type

**Fichier**: `src/main/resources/db/migration/V2__add_emoji_support.sql`

## Différence entre Messages EMOJI et Réactions

- **Messages EMOJI**: Messages complets de type emoji, apparaissent dans le fil de conversation
- **Réactions**: Réactions rapides à un message existant (déjà implémenté via MessageReaction)

## Bonnes Pratiques

1. **Utiliser le type EMOJI** pour les réponses rapides (👍, ❤️, 😂)
2. **Utiliser le type TEXT** pour les messages contenant du texte et des emojis
3. **Toujours fournir emojiCode** pour les messages EMOJI pour faciliter le filtrage et les statistiques
4. **Valider les emojis côté client** avant l'envoi pour une meilleure UX

## Compatibilité

- ✅ UTF-8 natif supporté
- ✅ Codes Unicode supportés
- ✅ Compatible avec tous les navigateurs modernes
- ✅ Support mobile (iOS/Android)

## Prochaines Améliorations

- [ ] Picker d'emojis côté frontend
- [ ] Statistiques sur les emojis les plus utilisés
- [ ] Suggestions d'emojis basées sur le contexte
- [ ] Support des emojis personnalisés
- [ ] Recherche de messages par emoji
