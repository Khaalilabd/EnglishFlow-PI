# Fonctionnalité : Messages Vocaux

## Vue d'ensemble
Implémentation complète des messages vocaux avec enregistrement, visualisation des ondes sonores et lecture audio.

## Fonctionnalités

### 1. Enregistrement audio
- Accès au microphone via l'API `MediaRecorder`
- Enregistrement en format WebM
- Limite de 5 minutes par message
- Compteur de temps en temps réel
- Animation des ondes sonores pendant l'enregistrement

### 2. Interface utilisateur
- **Bouton micro** : Apparaît quand le champ de texte est vide
- **Mode enregistrement** : 
  - Indicateur rouge pulsant
  - Compteur de temps
  - Visualisation des ondes sonores animées
  - Boutons Annuler et Arrêter
- **Mode preview** :
  - Lecteur audio HTML5
  - Durée affichée
  - Boutons Annuler et Envoyer

### 3. Affichage des messages vocaux
- Bouton play circulaire vert
- Visualisation des ondes sonores (8 barres animées)
- Durée du message
- Lecteur audio intégré (caché)

## Backend

### Modèle de données
- **Message.java** : Ajout du champ `voiceDuration` (Integer, en secondes)
- **MessageDTO.java** : Ajout du champ `voiceDuration`
- **SendMessageRequest.java** : Ajout du champ `voiceDuration`
- **MessageType** : Ajout de l'enum `VOICE`

### Stockage
- Les fichiers audio sont stockés dans `uploads/messages/`
- Format : `voice-{timestamp}.webm`
- Upload via l'endpoint existant `/api/messaging/upload`

## Frontend

### Composant TypeScript
**Nouvelles propriétés :**
- `isRecording`: État d'enregistrement
- `recordingTime`: Durée en secondes
- `mediaRecorder`: Instance MediaRecorder
- `audioChunks`: Chunks audio enregistrés
- `audioBlob`: Blob audio final
- `audioUrl`: URL blob pour la preview

**Nouvelles méthodes :**
- `startRecording()`: Démarre l'enregistrement
- `stopRecording()`: Arrête l'enregistrement
- `cancelRecording()`: Annule et nettoie
- `sendVoiceMessage()`: Upload et envoi du message vocal
- `formatRecordingTime()`: Formate la durée (mm:ss)
- `formatVoiceDuration()`: Formate la durée des messages

### Template HTML
**3 modes de footer :**
1. **Normal** : Emoji, Fichier, Input, Micro/Envoyer
2. **Enregistrement** : Indicateur, Ondes, Annuler, Arrêter
3. **Preview** : Annuler, Lecteur audio, Envoyer

**Affichage des messages vocaux :**
- Bouton play
- Ondes sonores animées
- Durée
- Lecteur audio caché

### Styles SCSS
**Animations :**
- `voiceWave`: Animation des barres d'ondes
- `recordingPulse`: Pulsation du point rouge
- `recordingWave`: Animation pendant l'enregistrement

**Composants stylisés :**
- `.voice-message`: Container du message vocal
- `.voice-play-btn`: Bouton play circulaire
- `.voice-waveform`: Visualisation des ondes
- `.footer-recording`: Interface d'enregistrement
- `.footer-audio-preview`: Interface de preview

## Utilisation

### Enregistrer un message vocal
1. Cliquer sur le bouton micro (🎤)
2. Autoriser l'accès au microphone
3. Parler (max 5 minutes)
4. Cliquer sur Arrêter (⏹)
5. Écouter la preview (optionnel)
6. Cliquer sur Envoyer (➤)

### Écouter un message vocal
1. Cliquer sur le bouton play (▶)
2. Le lecteur audio se lance automatiquement

## Permissions
L'application demande l'accès au microphone via :
```javascript
navigator.mediaDevices.getUserMedia({ audio: true })
```

L'utilisateur doit autoriser l'accès dans son navigateur.

## Formats supportés
- **Enregistrement** : WebM (format natif du navigateur)
- **Lecture** : Tous les formats supportés par HTML5 audio

## Limitations
- Durée maximale : 5 minutes (300 secondes)
- Format : WebM uniquement
- Nécessite HTTPS en production (pour getUserMedia)

## Fichiers modifiés

### Backend
- `Message.java` : Ajout du champ voiceDuration
- `MessageDTO.java` : Ajout du champ voiceDuration
- `SendMessageRequest.java` : Ajout du champ voiceDuration
- `MessagingService.java` : Gestion du voiceDuration

### Frontend
- `message.model.ts` : Ajout de VOICE dans MessageType et voiceDuration
- `messaging-container.component.ts` : Logique d'enregistrement et lecture
- `messaging-container.component.html` : Interface d'enregistrement et affichage
- `messaging-container.component.scss` : Styles et animations

## Améliorations futures
1. **Visualisation en temps réel** : Analyser le niveau audio pendant l'enregistrement
2. **Compression** : Compresser l'audio avant l'upload
3. **Formats multiples** : Support MP3, OGG, etc.
4. **Vitesse de lecture** : Permettre 1.5x, 2x
5. **Transcription** : Convertir l'audio en texte (Speech-to-Text)
6. **Effets** : Filtres audio, réduction de bruit
