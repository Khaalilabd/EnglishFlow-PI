const express = require('express');
const http = require('http');
const { Server } = require('socket.io');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

const server = http.createServer(app);
const io = new Server(server, {
  cors: { origin: '*', methods: ['GET', 'POST'] },
  // Increase ping timeout to avoid false disconnects
  pingTimeout: 60000,
  pingInterval: 25000
});

/**
 * rooms: Map<roomId, Map<socketId, { name, socketId, role, lessonId }>>
 *
 * Research finding: ICE candidates can arrive before the remote peer has
 * called setRemoteDescription. We buffer them per-socket and flush when
 * the target peer is ready (i.e. when they send their first offer/answer).
 *
 * iceCandidateBuffer: Map<targetSocketId, Array<{ from, candidate }>>
 */
const rooms = new Map();
const iceCandidateBuffer = new Map();

// Track active meetings by lessonId
const activeMeetings = new Map();

// ── Health / debug endpoints ──────────────────────────────────────────────────

app.get('/health', (_req, res) => res.json({ status: 'ok', rooms: rooms.size }));

app.get('/room/:roomId', (req, res) => {
  const room = rooms.get(req.params.roomId);
  if (!room) return res.json({ participants: 0, peers: [] });
  const peers = [...room.values()].map(p => ({ name: p.name, role: p.role, socketId: p.socketId }));
  res.json({ participants: room.size, peers });
});

app.get('/api/active-meeting/:lessonId', (req, res) => {
  const meeting = activeMeetings.get(req.params.lessonId);
  if (meeting) {
    res.json({ active: true, roomId: meeting.roomId, startedAt: meeting.startedAt });
  } else {
    res.json({ active: false, roomId: null, startedAt: null });
  }
});

// ── Socket.IO ─────────────────────────────────────────────────────────────────

io.on('connection', (socket) => {
  console.log(`[+] Connected: ${socket.id}`);

  // ── join-room ──────────────────────────────────────────────────────────────
  socket.on('join-room', ({ roomId, userName, role, lessonId }) => {
    if (!roomId || !userName) return;

    socket.join(roomId);

    if (!rooms.has(roomId)) rooms.set(roomId, new Map());
    rooms.get(roomId).set(socket.id, {
      name: userName,
      socketId: socket.id,
      role: role || 'student',
      lessonId: lessonId || null
    });

    // Track active meeting when tutor joins with a lessonId
    if (role === 'tutor' && lessonId) {
      activeMeetings.set(String(lessonId), {
        roomId,
        tutorSocketId: socket.id,
        startedAt: new Date().toISOString()
      });
    }

    // Send existing peers to the new joiner
    const others = [...rooms.get(roomId).values()].filter(p => p.socketId !== socket.id);
    socket.emit('room-peers', others);

    // Notify existing peers about the new joiner
    socket.to(roomId).emit('peer-joined', {
      socketId: socket.id,
      name: userName,
      role: role || 'student'
    });

    console.log(`[Room ${roomId}] ${userName} (${role}) joined — ${rooms.get(roomId).size} total`);

    // Flush any buffered ICE candidates for this socket
    if (iceCandidateBuffer.has(socket.id)) {
      const buffered = iceCandidateBuffer.get(socket.id);
      console.log(`[ICE Buffer] Flushing ${buffered.length} buffered candidates to ${socket.id}`);
      buffered.forEach(({ from, candidate }) => {
        socket.emit('ice-candidate', { from, candidate });
      });
      iceCandidateBuffer.delete(socket.id);
    }
  });

  // ── WebRTC signaling relay ─────────────────────────────────────────────────

  /**
   * offer — relay SDP offer from one peer to another.
   * Research: The server must be a pure relay. It must NOT modify the SDP.
   * Both initial offers and renegotiation offers use the same event.
   */
  // Perfect negotiation: relay description (offer or answer) between peers
  socket.on('description', ({ to, from, description }) => {
    if (!to || !description) return;
    console.log(`[Description:${description.type}] ${from || socket.id} → ${to}`);
    io.to(to).emit('description', { from: from || socket.id, description });
  });

  socket.on('offer', ({ to, offer, from, fromName }) => {
    if (!to || !offer) return;
    console.log(`[Offer] ${fromName || from} → ${to}`);
    io.to(to).emit('offer', { from: from || socket.id, fromName: fromName || '', offer });
  });

  socket.on('answer', ({ to, answer, from }) => {
    if (!to || !answer) return;
    console.log(`[Answer] ${from || socket.id} → ${to}`);
    io.to(to).emit('answer', { from: from || socket.id, answer });
  });

  /**
   * ice-candidate — relay ICE candidates between peers.
   *
   * Research finding (critical): ICE candidates can arrive at the signaling
   * server BEFORE the target peer has called setRemoteDescription. If we just
   * forward them immediately, the client will call addIceCandidate() before
   * the remote description is set, causing "Cannot add ICE candidate" errors
   * and a broken connection.
   *
   * Fix: Check if the target socket is currently connected. If not (they
   * haven't joined yet), buffer the candidate and flush it when they join.
   * If they ARE connected, forward immediately — the client-side code is
   * responsible for its own buffering after setRemoteDescription.
   */
  socket.on('ice-candidate', ({ to, candidate, from }) => {
    if (!to || !candidate) return;

    const targetSocket = io.sockets.sockets.get(to);

    if (targetSocket) {
      // Target is connected — forward immediately
      targetSocket.emit('ice-candidate', { from: from || socket.id, candidate });
    } else {
      // Target not connected yet — buffer the candidate
      if (!iceCandidateBuffer.has(to)) iceCandidateBuffer.set(to, []);
      iceCandidateBuffer.get(to).push({ from: from || socket.id, candidate });
      console.log(`[ICE Buffer] Buffered candidate for ${to} (not yet connected)`);
    }
  });

  // ── Media state broadcast ──────────────────────────────────────────────────

  socket.on('media-state', ({ roomId, audio, video, screen }) => {
    if (!roomId) return;
    socket.to(roomId).emit('peer-media-state', {
      socketId: socket.id,
      audio: !!audio,
      video: !!video,
      screen: !!screen
    });
  });

  // ── Chat ───────────────────────────────────────────────────────────────────

  socket.on('chat-message', ({ roomId, message, senderName, timestamp }) => {
    if (!roomId || !message) return;
    io.to(roomId).emit('chat-message', {
      message,
      senderName: senderName || 'Unknown',
      timestamp: timestamp || new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      socketId: socket.id
    });
  });

  // ── Tutor controls ─────────────────────────────────────────────────────────

  socket.on('mute-peer', ({ targetSocketId, roomId }) => {
    if (!targetSocketId) return;
    io.to(targetSocketId).emit('force-mute');
    if (roomId) socket.to(roomId).emit('peer-was-muted', { socketId: targetSocketId });
  });

  socket.on('kick-peer', ({ targetSocketId, roomId }) => {
    if (!targetSocketId) return;
    io.to(targetSocketId).emit('force-kick');
    if (roomId) socket.to(roomId).emit('peer-was-kicked', { socketId: targetSocketId });
  });

  // ── Disconnect ─────────────────────────────────────────────────────────────

  socket.on('disconnecting', () => {
    for (const roomId of socket.rooms) {
      if (!rooms.has(roomId)) continue;

      const peer = rooms.get(roomId).get(socket.id);
      if (!peer) continue;

      const isTutor = peer.role === 'tutor';
      const lessonId = peer.lessonId;

      // Remove from room
      rooms.get(roomId).delete(socket.id);
      if (rooms.get(roomId).size === 0) {
        rooms.delete(roomId);
        console.log(`[Room ${roomId}] Closed (empty)`);
      }

      // Clean up active meeting tracking
      if (isTutor && lessonId) {
        const meeting = activeMeetings.get(String(lessonId));
        if (meeting && meeting.tutorSocketId === socket.id) {
          activeMeetings.delete(String(lessonId));
        }
      }

      // Notify remaining peers
      if (isTutor) {
        socket.to(roomId).emit('tutor-left');
        console.log(`[Room ${roomId}] Tutor ${peer.name} left`);
      } else {
        socket.to(roomId).emit('peer-left', { socketId: socket.id });
        console.log(`[Room ${roomId}] ${peer.name} left`);
      }
    }

    // Clean up any buffered ICE candidates for this socket
    iceCandidateBuffer.delete(socket.id);
  });

  socket.on('disconnect', (reason) => {
    console.log(`[-] Disconnected: ${socket.id} (${reason})`);
  });
});

// ── Start ─────────────────────────────────────────────────────────────────────

const PORT = process.env.PORT || 3001;
server.listen(PORT, () => {
  console.log(`EnglishFlow Signaling Server running on port ${PORT}`);
});
