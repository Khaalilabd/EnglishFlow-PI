# WebRTC Meeting System - Complete Code Summary

This document provides a complete overview of all files responsible for the WebRTC video/audio meeting functionality in the EnglishFlow application.

## ✅ CRITICAL BUG FIXES COMPLETED (2026-04-14)

All critical WebRTC bugs have been fixed in both student and tutor components:

### Fixed Issues:
1. ✅ Duplicate signaling events removed (unified `description` event)
2. ✅ STUN servers configured (Google's public STUN)
3. ✅ Two-pass getUserMedia (HD video → basic video → audio-only)
4. ✅ Remote video track handling fixed (single MediaStream per peer)
5. ✅ ICE candidate buffering with 30-second TTL
6. ✅ Perfect negotiation pattern implemented
7. ✅ ICE connection state tracking per peer
8. ✅ Reconnection flow with 5-second delay + restartIce()
9. ✅ Screen share track cleanup with proper replaceTrack()
10. ✅ Negotiationneeded debouncing (150ms)
11. ✅ Media state sync on join
12. ✅ Proper cleanup on disconnect (all tracks stopped, connections closed)

### Components Fixed:
- ✅ **Signaling Server** (`backend/webrtc-signaling/server.js`)
- ✅ **Student Component** (`frontend/src/app/pages/meeting-join/meeting-join.component.ts`)
- ✅ **Tutor Component** (`frontend/src/app/pages/tutor-panel/instant-meeting/instant-meeting.component.ts`)

**Status**: Video and screen share should now work correctly. Audio was already working.

## 📁 File Structure

```
EnglishFlow/
├── backend/
│   ├── webrtc-signaling/
│   │   └── server.js                                    # Socket.IO signaling server
│   └── courses-service/src/main/java/com/englishflow/courses/
│       ├── controller/
│       │   ├── OnlineMeetingSessionController.java      # Meeting session REST API
│       │   └── OnlineLessonController.java              # Online lesson REST API
│       └── service/
│           └── OnlineLessonService.java                 # Business logic
└── frontend/src/app/
    ├── pages/
    │   ├── meeting-join/
    │   │   ├── meeting-join.component.ts                # Student meeting UI
    │   │   └── meeting-join.component.html              # Student meeting template
    │   └── tutor-panel/instant-meeting/
    │       ├── instant-meeting.component.ts             # Tutor meeting UI
    │       └── instant-meeting.component.html           # Tutor meeting template
    └── core/services/
        └── online-lesson.service.ts                     # HTTP service for API calls
```

## 🏗️ Architecture Overview

### Technology Stack
- **WebRTC**: Peer-to-peer video/audio streaming
- **Socket.IO**: Real-time signaling (offer/answer/ICE candidates)
- **Node.js + Express**: Signaling server (port 3001)
- **Spring Boot**: Backend REST API for session management
- **Angular**: Frontend components
- **STUN Servers**: Google's public STUN for NAT traversal

### Communication Flow

```
┌─────────────┐                    ┌──────────────┐                    ┌─────────────┐
│   Tutor     │                    │  Signaling   │                    │   Student   │
│  (Browser)  │                    │   Server     │                    │  (Browser)  │
└──────┬──────┘                    └──────┬───────┘                    └──────┬──────┘
       │                                  │                                   │
       │  1. Create Meeting               │                                   │
       ├─────────────────────────────────>│                                   │
       │  (join-room: tutor)              │                                   │
       │                                  │                                   │
       │                                  │  2. Join Meeting                  │
       │                                  │<──────────────────────────────────┤
       │                                  │  (join-room: student)             │
       │                                  │                                   │
       │  3. peer-joined event            │                                   │
       │<─────────────────────────────────┤                                   │
       │                                  │                                   │
       │  4. Create Offer                 │                                   │
       │  (RTCPeerConnection)             │                                   │
       │                                  │                                   │
       │  5. Send Offer                   │                                   │
       ├─────────────────────────────────>│                                   │
       │                                  │  6. Forward Offer                 │
       │                                  ├──────────────────────────────────>│
       │                                  │                                   │
       │                                  │  7. Create Answer                 │
       │                                  │  (RTCPeerConnection)              │
       │                                  │                                   │
       │                                  │  8. Send Answer                   │
       │                                  │<──────────────────────────────────┤
       │  9. Forward Answer               │                                   │
       │<─────────────────────────────────┤                                   │
       │                                  │                                   │
       │  10. Exchange ICE Candidates     │  11. Exchange ICE Candidates      │
       │<────────────────────────────────>│<─────────────────────────────────>│
       │                                  │                                   │
       │  12. P2P Connection Established  │                                   │
       │<═══════════════════════════════════════════════════════════════════>│
       │         (Direct video/audio stream)                                  │
       │                                                                      │
```

## 📝 Key Files and Their Responsibilities

### 1. Backend - Signaling Server
**File**: `backend/webrtc-signaling/server.js`

**Purpose**: Socket.IO server that relays WebRTC signaling messages between peers

**Key Features**:
- Room management (join/leave)
- Offer/Answer relay
- ICE candidate buffering and relay
- Media state broadcasting
- Chat message relay
- Tutor controls (mute/kick)
- Active meeting tracking

**Socket Events**:
- `join-room` - User joins a meeting room
- `offer` - Relay SDP offer
- `answer` - Relay SDP answer
- `ice-candidate` - Relay ICE candidates (with buffering)
- `media-state` - Broadcast audio/video/screen state
- `chat-message` - Relay chat messages
- `mute-peer` - Tutor mutes a student
- `kick-peer` - Tutor removes a student
- `peer-joined` - Notify when new peer joins
- `peer-left` - Notify when peer leaves
- `tutor-left` - Notify when tutor ends meeting

### 2. Backend - Java Controllers

#### OnlineMeetingSessionController.java
**Purpose**: REST API for meeting session lifecycle management

**Endpoints**:
- `POST /api/meeting-sessions` - Create new meeting session
- `GET /api/meeting-sessions/lesson/{lessonId}` - Get active session by lesson
- `GET /api/meeting-sessions/room/{roomId}` - Get active session by room
- `DELETE /api/meeting-sessions/lesson/{lessonId}` - End session by lesson
- `DELETE /api/meeting-sessions/room/{roomId}` - End session by room

#### OnlineLessonController.java
**Purpose**: REST API for online lesson configuration and scheduling

**Endpoints**:
- `POST /online-lessons/configure` - Configure online lesson
- `POST /online-lessons/generate-sessions` - Generate recurring sessions
- `GET /online-lessons/student/{studentId}/upcoming` - Get upcoming sessions
- `POST /online-lessons/sessions/{sessionId}/attendance` - Record attendance
- `POST /online-lessons/{lessonId}/assign-time-slot` - Assign time slot
- `GET /online-lessons/{lessonId}/time-assignment` - Get time assignment
- `DELETE /online-lessons/{lessonId}/time-assignment` - Remove time assignment

### 3. Backend - Java Services

#### OnlineLessonService.java
**Purpose**: Business logic for online lessons and sessions

**Key Methods**:
- `configureOnlineLesson()` - Set up online lesson with schedules
- `generateSessionsForNextWeeks()` - Auto-generate recurring sessions
- `getUpcomingSessionsForStudent()` - Fetch student's upcoming sessions
- `recordAttendance()` - Track student attendance with percentage
- `assignTimeSlot()` - Assign tutor availability slot to lesson
- `getTimeAssignment()` - Retrieve lesson time assignment
- `removeTimeAssignment()` - Delete time assignment

### 4. Frontend - Student Component

**File**: `frontend/src/app/pages/meeting-join/meeting-join.component.ts`

**Purpose**: Student's meeting interface (receives offers, sends answers)

**Key Features**:
- Join meeting with display name
- Receive WebRTC offer from tutor
- Create and send answer
- Handle ICE candidates with buffering
- Toggle audio/video/screen share
- Display remote video streams
- Leave meeting

**WebRTC Flow (Student Side)**:
1. Get user media (camera/microphone)
2. Connect to signaling server
3. Receive offer from tutor
4. Create RTCPeerConnection
5. Set remote description (offer)
6. Create answer
7. Set local description (answer)
8. Send answer to tutor
9. Exchange ICE candidates
10. Display remote streams

### 5. Frontend - Tutor Component

**File**: `frontend/src/app/pages/tutor-panel/instant-meeting/instant-meeting.component.ts`

**Purpose**: Tutor's meeting interface (creates offers, receives answers)

**Key Features**:
- Create meeting with unique room ID
- Generate and share invite link
- Create WebRTC offer for each joining student
- Receive answers from students
- Handle ICE candidates with buffering
- Toggle audio/video/screen share
- Chat functionality
- Participant management (mute/kick)
- Meeting duration timer
- End meeting for all participants

**WebRTC Flow (Tutor Side)**:
1. Get user media (camera/microphone)
2. Connect to signaling server
3. Wait for students to join
4. For each student:
   - Create RTCPeerConnection
   - Create offer
   - Set local description (offer)
   - Send offer to student
   - Receive answer from student
   - Set remote description (answer)
   - Exchange ICE candidates
5. Display all remote streams

### 6. Frontend - Service

**File**: `frontend/src/app/core/services/online-lesson.service.ts`

**Purpose**: HTTP service for API communication

**Key Methods**:
- `createMeetingSession()` - POST to create session
- `getActiveMeetingSession()` - GET active session
- `endMeetingSession()` - DELETE to end session
- `getAvailableSlots()` - GET tutor availability
- `assignTimeSlot()` - POST to assign time slot
- `getTimeAssignment()` - GET lesson time assignment
- `removeTimeAssignment()` - DELETE time assignment

## 🔑 Key Concepts

### ICE Candidate Buffering
**Problem**: ICE candidates can arrive before `setRemoteDescription()` is called, causing "Cannot add ICE candidate" errors.

**Solution**: 
- Server-side: Buffer candidates for peers not yet connected
- Client-side: Buffer candidates until remote description is set
- Flush buffered candidates after `setRemoteDescription()` completes

### Perfect Negotiation Pattern
The code implements a simplified version where:
- Tutor is always the "offerer" (polite peer)
- Student is always the "answerer" (impolite peer)
- This avoids collision scenarios in offer/answer exchange

### Media Stream Management
- Local streams: Camera, microphone, screen share
- Remote streams: One per connected peer
- Track management: Add/remove tracks dynamically
- State broadcasting: Notify peers of media state changes

### STUN Servers
Uses Google's public STUN servers for NAT traversal:
- `stun:stun.l.google.com:19302`
- `stun:stun1.l.google.com:19302`

## 🚀 How to Use

### Starting the Signaling Server
```bash
cd backend/webrtc-signaling
npm install
node server.js
# Server runs on port 3001
```

### Tutor Creates Meeting
1. Navigate to `/tutor-panel/instant-meeting/:roomId`
2. System generates unique room ID
3. Tutor gets invite link
4. Share link with students

### Student Joins Meeting
1. Click invite link or navigate to `/join/:roomId`
2. Enter display name
3. Grant camera/microphone permissions
4. Join meeting

### During Meeting
- Toggle audio/video/screen share
- Chat with participants (tutor only)
- Mute/kick participants (tutor only)
- View all participants
- End meeting (tutor) or leave (student)

## 🔧 Configuration

### Signaling Server Port
Default: `3001`
Change in: `backend/webrtc-signaling/server.js`

### Frontend Socket Connection
Default: `http://localhost:3001`
Change in:
- `frontend/src/app/pages/meeting-join/meeting-join.component.ts`
- `frontend/src/app/pages/tutor-panel/instant-meeting/instant-meeting.component.ts`

### STUN Servers
Default: Google's public STUN
Change in both components:
```typescript
const ICE = [
  { urls: 'stun:your-stun-server.com:port' }
];
```

## 📊 Data Flow

### Meeting Creation
```
Tutor Component → OnlineLessonService → Spring Boot API → Database
                                                         ↓
                                              Meeting Session Created
```

### WebRTC Connection
```
Tutor → Socket.IO → Student
  ↓                    ↓
Offer              Receive Offer
  ↓                    ↓
Send via Socket    Create Answer
  ↓                    ↓
                   Send via Socket
  ↓                    ↓
Receive Answer     ICE Exchange
  ↓                    ↓
P2P Connection Established
```

### Media Streaming
```
Tutor Camera/Mic → RTCPeerConnection → Student Browser
Student Camera/Mic → RTCPeerConnection → Tutor Browser
```

## 🐛 Common Issues & Solutions

### Issue: "Cannot add ICE candidate"
**Cause**: ICE candidate added before `setRemoteDescription()`
**Solution**: Buffer candidates until remote description is set

### Issue: Video not displaying
**Cause**: Video element not attached to stream
**Solution**: Use `ngAfterViewChecked()` to attach streams to video elements

### Issue: Connection fails
**Cause**: STUN server unreachable or firewall blocking
**Solution**: Check STUN server availability, consider TURN server for restrictive networks

### Issue: Audio echo
**Cause**: Local video element not muted
**Solution**: Always set `muted=true` on local video elements

## 📚 Additional Resources

- [WebRTC API Documentation](https://developer.mozilla.org/en-US/docs/Web/API/WebRTC_API)
- [Socket.IO Documentation](https://socket.io/docs/v4/)
- [RTCPeerConnection](https://developer.mozilla.org/en-US/docs/Web/API/RTCPeerConnection)
- [Perfect Negotiation Pattern](https://developer.mozilla.org/en-US/docs/Web/API/WebRTC_API/Perfect_negotiation)

---

## 📄 Complete Code Files

For the complete source code of each file, see:
- `WEBRTC_MEETING_CODE_COMPLETE.md` - Backend code (signaling server + Java)
- Individual component files in the repository

---

**Last Updated**: 2026-04-14
**Version**: 1.0
**Author**: EnglishFlow Development Team
