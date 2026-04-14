import { Component, OnInit, OnDestroy, AfterViewChecked, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { OnlineLessonService } from '../../../core/services/online-lesson.service';
import { io, Socket } from 'socket.io-client';

interface Participant {
  socketId: string;
  name: string;
  role: 'tutor' | 'student';
  pc: RTCPeerConnection;
  stream: MediaStream;           // single combined stream per peer
  isMicMuted: boolean;
  isCameraOff: boolean;
  isScreenSharing: boolean;
  // Perfect negotiation state
  makingOffer: boolean;
  ignoreOffer: boolean;
  isSettingRemoteAnswerPending: boolean;
}

interface ChatMessage {
  senderName: string;
  message: string;
  timestamp: string;
  isMine: boolean;
}

const ICE_SERVERS = [
  { urls: 'stun:stun.l.google.com:19302' },
  { urls: 'stun:stun1.l.google.com:19302' }
];

@Component({
  selector: 'app-instant-meeting',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './instant-meeting.component.html'
})
export class InstantMeetingComponent implements OnInit, OnDestroy, AfterViewChecked {
  roomId = '';
  inviteLink = '';
  copied = false;
  currentUser: any = null;
  userName = '';
  lessonId: number | null = null;

  socket!: Socket;
  localStream: MediaStream | null = null;
  localScreenStream: MediaStream | null = null;

  participants: Map<string, Participant> = new Map();

  audioEnabled = true;
  videoEnabled = false;
  isScreenSharing = false;
  isDeafened = false;

  showChat = false;
  showParticipants = false;
  chatMessages: ChatMessage[] = [];
  chatInput = '';
  unreadCount = 0;
  openMenuId: string | null = null;

  connected = false;
  connecting = true;
  error = '';
  duration = 0;
  private durationInterval: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private onlineLessonService: OnlineLessonService,
    private cdr: ChangeDetectorRef,
    private zone: NgZone
  ) {}

  async ngOnInit(): Promise<void> {
    this.roomId = this.route.snapshot.paramMap.get('roomId') || this.generateRoomId();
    this.inviteLink = `${window.location.origin}/join/${this.roomId}`;

    const lessonIdParam = this.route.snapshot.queryParamMap.get('lessonId');
    if (lessonIdParam) this.lessonId = parseInt(lessonIdParam, 10);

    this.currentUser = this.authService.currentUserValue;
    this.userName = this.currentUser
      ? `${this.currentUser.firstName} ${this.currentUser.lastName}`.trim()
      : 'Tutor';

    try {
      this.localStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
      this.videoEnabled = true;
    } catch {
      try {
        this.localStream = await navigator.mediaDevices.getUserMedia({ video: false, audio: true });
        this.videoEnabled = false;
      } catch {
        this.error = 'Camera/microphone access denied. Allow access in your browser and refresh.';
        this.connecting = false;
        return;
      }
    }

    this.audioEnabled = true;

    // Attach local stream to all data-local-cam elements
    this.attachLocalVideo();

    this.connectSocket();
    this.startDurationTimer();

    if (this.lessonId && this.currentUser) this.createMeetingSession();
  }

  // ── Local video attachment ─────────────────────────────────────────────────

  private attachLocalVideo(): void {
    requestAnimationFrame(() => {
      document.querySelectorAll<HTMLVideoElement>('video[data-local-cam]').forEach(el => {
        if (el.srcObject !== this.localStream) {
          el.muted = true;
          el.srcObject = this.localStream;
          el.play().catch(() => {});
        }
      });
    });
  }

  // Runs after every Angular render — attaches any streams not yet attached
  ngAfterViewChecked(): void {
    // Local camera
    if (this.localStream) {
      document.querySelectorAll<HTMLVideoElement>('video[data-local-cam]').forEach(el => {
        if (el.srcObject !== this.localStream) {
          el.muted = true;
          el.srcObject = this.localStream;
          el.play().catch(() => {});
        }
      });
    }
    // Remote streams
    this.participants.forEach((p, socketId) => {
      if (p.stream && p.stream.getTracks().length > 0) {
        const el = document.getElementById(`cam-${socketId}`) as HTMLVideoElement;
        if (el && el.srcObject !== p.stream) {
          el.muted = this.isDeafened;
          el.srcObject = p.stream;
          el.play().catch(() => {});
        }
      }
    });
  }

  // ── Socket ─────────────────────────────────────────────────────────────────

  private connectSocket(): void {
    this.socket = io('http://localhost:3001', { transports: ['websocket', 'polling'] });

    this.socket.on('connect', () => {
      this.zone.run(() => {
        this.connected = true;
        this.connecting = false;
        this.socket.emit('join-room', {
          roomId: this.roomId,
          userName: this.userName,
          role: 'tutor',
          lessonId: this.lessonId
        });
        this.cdr.markForCheck();
      });
    });

    this.socket.on('connect_error', () => {
      this.zone.run(() => {
        this.error = 'Cannot connect to signaling server on port 3001.';
        this.connecting = false;
        this.cdr.markForCheck();
      });
    });

    // Existing peers — we are polite (we joined first as host, they join after)
    this.socket.on('room-peers', async (peers: any[]) => {
      for (const p of peers) {
        this.getOrCreateParticipant(p.socketId, p.name, p.role || 'student');
      }
    });

    // New peer joined — create PC for them (they will send offer as impolite)
    this.socket.on('peer-joined', ({ socketId, name, role }: any) => {
      this.getOrCreateParticipant(socketId, name, role || 'student');
    });

    // Perfect negotiation: handle incoming description (offer or answer)
    this.socket.on('description', async ({ from, description }: any) => {
      const p = this.getOrCreateParticipant(from, '', 'student');
      const pc = p.pc;
      const polite = true; // tutor is always polite

      try {
        const readyForOffer =
          !p.makingOffer &&
          (pc.signalingState === 'stable' || p.isSettingRemoteAnswerPending);
        const offerCollision = description.type === 'offer' && !readyForOffer;

        p.ignoreOffer = !polite && offerCollision;
        if (p.ignoreOffer) return;

        p.isSettingRemoteAnswerPending = description.type === 'answer';
        await pc.setRemoteDescription(description);
        p.isSettingRemoteAnswerPending = false;

        if (description.type === 'offer') {
          await pc.setLocalDescription();
          this.socket.emit('description', {
            to: from,
            from: this.socket.id,
            description: pc.localDescription
          });
        }
      } catch (e) {
        console.error('[Perfect Negotiation] description error:', e);
      }
    });

    // Perfect negotiation: handle incoming ICE candidate
    this.socket.on('ice-candidate', async ({ from, candidate }: any) => {
      const p = this.participants.get(from);
      if (!p) return;
      try {
        await p.pc.addIceCandidate(candidate ? new RTCIceCandidate(candidate) : undefined);
      } catch (e) {
        if (!p.ignoreOffer) console.error('[ICE] addIceCandidate error:', e);
      }
    });

    this.socket.on('peer-left', ({ socketId }: any) => {
      this.zone.run(() => this.removeParticipant(socketId));
    });

    this.socket.on('peer-media-state', ({ socketId, audio, video, screen }: any) => {
      this.zone.run(() => {
        const p = this.participants.get(socketId);
        if (!p) return;
        p.isMicMuted = !audio;
        p.isCameraOff = !video;
        p.isScreenSharing = screen;
        this.cdr.markForCheck();
      });
    });

    this.socket.on('force-mute', () => {
      this.zone.run(() => {
        this.audioEnabled = false;
        this.localStream?.getAudioTracks().forEach(t => t.enabled = false);
        this.cdr.markForCheck();
      });
    });

    this.socket.on('chat-message', ({ message, senderName, timestamp, socketId }: any) => {
      this.zone.run(() => {
        const isMine = socketId === this.socket.id;
        this.chatMessages.push({ message, senderName, timestamp, isMine });
        if (!this.showChat && !isMine) this.unreadCount++;
        this.cdr.markForCheck();
      });
    });
  }

  // ── Peer Connection (Perfect Negotiation) ─────────────────────────────────

  private getOrCreateParticipant(socketId: string, name: string, role: 'tutor' | 'student'): Participant {
    if (this.participants.has(socketId)) return this.participants.get(socketId)!;

    const pc = new RTCPeerConnection({ iceServers: ICE_SERVERS });
    const stream = new MediaStream();

    const participant: Participant = {
      socketId, name: name || 'Unknown', role,
      pc, stream,
      isMicMuted: false, isCameraOff: true, isScreenSharing: false,
      makingOffer: false, ignoreOffer: false, isSettingRemoteAnswerPending: false
    };

    this.participants.set(socketId, participant);

    // Add local tracks
    this.localStream?.getTracks().forEach(t => pc.addTrack(t, this.localStream!));

    // Perfect negotiation: onnegotiationneeded
    // Tutor is polite — only renegotiate (for screen share etc), not initial offer
    pc.onnegotiationneeded = async () => {
      if (pc.signalingState !== 'stable') return;
      // Only send offer if we already have a remote description (renegotiation)
      // Initial connection is driven by the student (impolite peer)
      if (!pc.currentRemoteDescription) return;
      try {
        participant.makingOffer = true;
        await pc.setLocalDescription();
        this.socket.emit('description', {
          to: socketId,
          from: this.socket.id,
          description: pc.localDescription
        });
      } catch (e) {
        console.error('[Perfect Negotiation] onnegotiationneeded error:', e);
      } finally {
        participant.makingOffer = false;
      }
    };

    // Handle incoming tracks — attach directly in ontrack, don't rely on onunmute
    // Chrome doesn't reliably fire onunmute for video tracks
    pc.ontrack = ({ track, streams }) => {
      this.zone.run(() => {
        stream.addTrack(track);
        if (track.kind === 'video') participant.isCameraOff = false;

        // Attach immediately
        const el = document.getElementById(`cam-${socketId}`) as HTMLVideoElement;
        if (el) {
          el.muted = this.isDeafened;
          el.srcObject = stream;
          el.play().catch(() => {});
        }
        this.cdr.markForCheck();
      });
    };

    pc.onicecandidate = ({ candidate }) => {
      this.socket.emit('ice-candidate', {
        to: socketId,
        from: this.socket.id,
        candidate
      });
    };

    pc.onconnectionstatechange = () => {
      console.log(`[WebRTC] ${name} → ${pc.connectionState}`);
      if (pc.connectionState === 'failed') pc.restartIce();
    };

    this.zone.run(() => this.cdr.markForCheck());
    return participant;
  }

  private removeParticipant(socketId: string): void {
    this.participants.get(socketId)?.pc.close();
    this.participants.delete(socketId);
    this.cdr.markForCheck();
  }

  // ── Controls ───────────────────────────────────────────────────────────────

  toggleAudio(): void {
    this.audioEnabled = !this.audioEnabled;
    this.localStream?.getAudioTracks().forEach(t => t.enabled = this.audioEnabled);
    this.emitMediaState();
  }

  toggleVideo(): void {
    this.videoEnabled = !this.videoEnabled;
    this.localStream?.getVideoTracks().forEach(t => t.enabled = this.videoEnabled);
    if (this.videoEnabled) requestAnimationFrame(() => this.attachLocalVideo());
    this.emitMediaState();
  }

  toggleDeafen(): void {
    this.isDeafened = !this.isDeafened;
    this.participants.forEach(p => {
      const el = document.getElementById(`cam-${p.socketId}`) as HTMLVideoElement;
      if (el) el.muted = this.isDeafened;
    });
  }

  async toggleScreenShare(): Promise<void> {
    if (this.isScreenSharing) await this.stopScreenShare();
    else await this.startScreenShare();
  }

  private async startScreenShare(): Promise<void> {
    try {
      this.localScreenStream = await (navigator.mediaDevices as any).getDisplayMedia({ video: true, audio: false });
      this.isScreenSharing = true;
      this.participants.forEach(p => {
        this.localScreenStream!.getTracks().forEach(t => p.pc.addTrack(t, this.localScreenStream!));
      });
      this.localScreenStream!.getVideoTracks()[0].onended = () => this.stopScreenShare();
      this.emitMediaState();
      this.cdr.markForCheck();
    } catch (e) { console.error('Screen share failed:', e); }
  }

  private async stopScreenShare(): Promise<void> {
    if (!this.localScreenStream) return;
    this.participants.forEach(p => {
      p.pc.getSenders().forEach(s => {
        if (this.localScreenStream!.getTracks().includes(s.track!)) p.pc.removeTrack(s);
      });
    });
    this.localScreenStream.getTracks().forEach(t => t.stop());
    this.localScreenStream = null;
    this.isScreenSharing = false;
    this.emitMediaState();
    this.cdr.markForCheck();
  }

  private emitMediaState(): void {
    this.socket?.emit('media-state', {
      roomId: this.roomId,
      audio: this.audioEnabled,
      video: this.videoEnabled,
      screen: this.isScreenSharing
    });
  }

  // ── Chat ───────────────────────────────────────────────────────────────────

  toggleChat(): void {
    this.showChat = !this.showChat;
    if (this.showChat) { this.unreadCount = 0; this.showParticipants = false; }
  }

  toggleParticipants(): void {
    this.showParticipants = !this.showParticipants;
    if (this.showParticipants) this.showChat = false;
  }

  sendChat(): void {
    if (!this.chatInput.trim()) return;
    this.socket.emit('chat-message', {
      roomId: this.roomId,
      message: this.chatInput.trim(),
      senderName: this.userName,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    });
    this.chatInput = '';
  }

  mutePeer(socketId: string): void {
    this.socket?.emit('mute-peer', { targetSocketId: socketId, roomId: this.roomId });
    const p = this.participants.get(socketId);
    if (p) { p.isMicMuted = true; this.cdr.markForCheck(); }
  }

  kickPeer(socketId: string): void {
    if (!confirm('Remove this participant?')) return;
    this.socket?.emit('kick-peer', { targetSocketId: socketId, roomId: this.roomId });
    this.removeParticipant(socketId);
  }

  copyInviteLink(): void {
    navigator.clipboard.writeText(this.inviteLink).then(() => {
      this.copied = true;
      setTimeout(() => this.copied = false, 2500);
    });
  }

  endMeeting(): void {
    const doEnd = () => { this.cleanup(); this.router.navigate(['/tutor-panel']); };
    if (this.lessonId) {
      this.onlineLessonService.endMeetingSession(this.lessonId).subscribe({ next: doEnd, error: doEnd });
    } else doEnd();
  }

  private cleanup(): void {
    clearInterval(this.durationInterval);
    this.localStream?.getTracks().forEach(t => t.stop());
    this.localScreenStream?.getTracks().forEach(t => t.stop());
    this.participants.forEach(p => p.pc.close());
    this.participants.clear();
    this.socket?.disconnect();
  }

  // ── Helpers ────────────────────────────────────────────────────────────────

  private generateRoomId(): string { return 'ef-' + Math.random().toString(36).substring(2, 10); }

  private startDurationTimer(): void {
    this.durationInterval = setInterval(() => { this.duration++; this.cdr.markForCheck(); }, 1000);
  }

  get formattedDuration(): string {
    const h = Math.floor(this.duration / 3600);
    const m = Math.floor((this.duration % 3600) / 60);
    const s = this.duration % 60;
    if (h > 0) return `${h}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
    return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
  }

  get participantsArray(): Participant[] { return [...this.participants.values()]; }
  get anyoneSharing(): boolean { return this.isScreenSharing || this.participantsArray.some(p => p.isScreenSharing); }
  get sharingParticipant(): Participant | null { return this.participantsArray.find(p => p.isScreenSharing) || null; }
  get sidebarParticipants(): Participant[] { return this.participantsArray.filter(p => !p.isScreenSharing).slice(0, 3); }

  getFirstLetter(name: string): string { return (name?.trim()[0] || '?').toUpperCase(); }
  getInitials(name: string): string { return name?.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2) || '?'; }

  private createMeetingSession(): void {
    if (!this.lessonId || !this.currentUser) return;
    this.onlineLessonService.createMeetingSession(
      this.lessonId, this.roomId, this.inviteLink, this.currentUser.id
    ).subscribe({ error: e => console.error('Error creating meeting session:', e) });
  }

  ngOnDestroy(): void { this.cleanup(); }
}
