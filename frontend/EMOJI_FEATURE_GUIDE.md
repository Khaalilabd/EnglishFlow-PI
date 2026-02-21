# Guide d'Utilisation des Emojis - Frontend

## Vue d'ensemble

Le système de messagerie supporte maintenant l'envoi d'emojis de deux manières:

1. **Emojis dans le texte**: Ajoutés directement dans le message texte
2. **Messages emoji dédiés**: Emojis envoyés comme messages indépendants (réactions rapides)

## Fonctionnalités Implémentées

### 1. Picker d'Emojis Amélioré

Le picker d'emojis a été divisé en deux sections:

#### Réactions Rapides
- 6 emojis populaires: 👍 ❤️ 😂 😮 😢 🙏
- Cliquer sur un emoji l'envoie immédiatement comme message EMOJI
- Idéal pour les réponses rapides

#### Tous les Emojis
- 100 emojis populaires organisés en grille
- Cliquer sur un emoji l'insère dans le champ de texte
- Permet de composer des messages avec plusieurs emojis

### 2. Types de Messages

```typescript
export enum MessageType {
  TEXT = 'TEXT',      // Message texte standard
  FILE = 'FILE',      // Fichier attaché
  IMAGE = 'IMAGE',    // Image
  EMOJI = 'EMOJI'     // Emoji dédié (nouveau)
}
```

### 3. Affichage des Messages Emoji

Les messages de type EMOJI sont affichés différemment:
- Taille plus grande (48px)
- Pas de bulle de message
- Animation de rebond à l'apparition
- Effet de zoom au survol

## Utilisation

### Envoyer un Emoji comme Message

```typescript
// Dans le composant
sendEmojiAsMessage(emoji: string): void {
  if (!this.selectedConversation) return;
  
  const emojiCode = this.getEmojiUnicode(emoji);
  const request: SendMessageRequest = {
    content: emoji,
    messageType: MessageType.EMOJI,
    emojiCode: emojiCode
  };
  
  this.webSocketService.sendMessage(this.selectedConversation.id, request);
  this.showEmojiPicker = false;
}
```

### Insérer un Emoji dans le Texte

```typescript
insertEmoji(emoji: string): void {
  this.newMessage += emoji;
  this.showEmojiPicker = false;
  this.messageInput.nativeElement.focus();
}
```

### Conversion Unicode

```typescript
getEmojiUnicode(emoji: string): string {
  const codePoint = emoji.codePointAt(0);
  return codePoint ? `U+${codePoint.toString(16).toUpperCase()}` : emoji;
}
```

## Structure des Données

### Message avec Emoji

```typescript
interface Message {
  id: number;
  conversationId: number;
  senderId: number;
  senderName: string;
  senderAvatar?: string;
  content: string;           // L'emoji en UTF-8
  messageType: MessageType;  // 'EMOJI'
  emojiCode?: string;        // Code Unicode (ex: 'U+1F44D')
  isEdited: boolean;
  createdAt: Date;
  updatedAt: Date;
  readBy?: MessageReadStatus[];
  reactions?: ReactionSummary[];
}
```

### Requête d'Envoi

```typescript
interface SendMessageRequest {
  content: string;           // L'emoji ou le texte
  messageType: MessageType;  // Type du message
  emojiCode?: string;        // Code Unicode pour les emojis
  fileUrl?: string;
  fileName?: string;
  fileSize?: number;
}
```

## Styles CSS

### Message Emoji

```scss
.message.emoji-message {
  max-width: auto;
  
  .message-bubble.emoji-bubble {
    background: transparent !important;
    border: none !important;
    box-shadow: none !important;
    padding: 4px 8px !important;
    
    &:hover {
      transform: scale(1.1);
    }
  }
  
  .emoji-large {
    font-size: 48px;
    line-height: 1;
    display: block;
    animation: bounce 0.5s ease;
  }
}
```

### Réactions Rapides

```scss
.quick-emoji-item {
  width: 100%;
  aspect-ratio: 1;
  border: 2px solid $border-color;
  background: white;
  font-size: 28px;
  cursor: pointer;
  border-radius: 12px;
  
  &:hover {
    background: $primary;
    border-color: $primary;
    transform: scale(1.15);
    box-shadow: 0 4px 12px rgba($primary, 0.3);
  }
}
```

## Template HTML

### Affichage des Messages

```html
<div *ngFor="let msg of messages" 
     class="message"
     [class.sent]="msg.senderId === currentUserId"
     [class.received]="msg.senderId !== currentUserId"
     [class.emoji-message]="msg.messageType === 'EMOJI'">
  <div class="message-bubble" [class.emoji-bubble]="msg.messageType === 'EMOJI'">
    <div class="message-content">
      <p *ngIf="msg.messageType !== 'EMOJI'">{{ msg.content }}</p>
      <span *ngIf="msg.messageType === 'EMOJI'" class="emoji-large">{{ msg.content }}</span>
      <span class="message-time" *ngIf="msg.messageType !== 'EMOJI'">
        {{ formatMessageTime(msg.createdAt) }}
      </span>
    </div>
  </div>
</div>
```

### Picker d'Emojis

```html
<div class="emoji-picker-wrapper">
  <!-- Réactions Rapides -->
  <div class="quick-reactions">
    <span class="section-label">Réactions rapides</span>
    <div class="quick-emoji-grid">
      <button *ngFor="let emoji of quickReactionEmojis" 
              class="quick-emoji-item" 
              (click)="sendEmojiAsMessage(emoji)">
        {{ emoji }}
      </button>
    </div>
  </div>
  
  <!-- Tous les Emojis -->
  <div class="all-emojis">
    <span class="section-label">Tous les emojis</span>
    <div class="emoji-grid">
      <button *ngFor="let emoji of popularEmojis" 
              class="emoji-item" 
              (click)="insertEmoji(emoji)">
        {{ emoji }}
      </button>
    </div>
  </div>
</div>
```

## Animations

### Bounce (apparition des emojis)

```scss
@keyframes bounce {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-6px);
  }
}
```

### Slide In Up (messages)

```scss
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
```

## Emojis Disponibles

### Réactions Rapides (6)
- 👍 Pouce levé
- ❤️ Coeur
- 😂 Rire
- 😮 Surprise
- 😢 Triste
- 🙏 Prière

### Emojis Populaires (100)
Organisés en catégories:
- Visages et émotions (50)
- Gestes et mains (20)
- Coeurs et symboles (20)
- Étoiles et célébrations (10)

## Bonnes Pratiques

1. **Utiliser les réactions rapides** pour les réponses immédiates
2. **Insérer des emojis dans le texte** pour enrichir les messages
3. **Limiter les messages emoji** aux réactions vraiment rapides
4. **Combiner texte et emojis** pour une meilleure communication

## Prochaines Améliorations

- [ ] Recherche d'emojis par mot-clé
- [ ] Emojis récemment utilisés
- [ ] Emojis personnalisés
- [ ] Catégories d'emojis
- [ ] Skin tones pour les emojis de personnes
- [ ] Animations personnalisées par emoji
- [ ] Support des emoji composés (flags, etc.)

## Compatibilité

- ✅ Chrome/Edge (dernières versions)
- ✅ Firefox (dernières versions)
- ✅ Safari (dernières versions)
- ✅ Mobile (iOS/Android)
- ✅ Support UTF-8 complet

## Dépendances

Aucune dépendance externe requise. Le système utilise:
- Emojis natifs UTF-8
- CSS Grid pour la mise en page
- Animations CSS natives
- TypeScript pour la logique
