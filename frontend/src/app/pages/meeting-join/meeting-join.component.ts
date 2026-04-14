import { Component, OnInit, OnDestroy, AfterViewChecked, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { io, Socket } from 'socket.io-client';

interface RemotePeer {
  socketId: string;
  name: string;
  role: 'tutor' | 'student';
  pc: RTCPeerConnection;
  stream: MediaStream;
  audioEnabled: boolean;
  videoEnabled: boolean;
  screenSharing: boolean;
  // Perfect negotiation state
  makingOffer: boolean;
  ignoreOffer: boolean;
  isSettingRemoteAnswerPending: boolean;
}

const ICE_SERVERS = [
  { urls: 'stun:stun.l.google.com:19302' },
  { urls: 'stun:stun1.l.google.com:19302' }
];

@Component({
  selector: 'app-meeting-join',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './meeting-join.component.html'
})
export class MeetingJoinComponent implements OnInit, OnDestroy, AfterViewChecked {
  roomId = '';
  displayName = '';
  joined = false;
  joining = false;
  error = '';

  socket!: Socket;
  localStream: MediaStream | null = null;
  localScreenStream: MediaStream | null = null;

  peers: Map<string, RemotePeer> = new Map();

  audioEnabled = true;
  videoEnabled = true;
  screenSharing = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private zone: NgZone
  ) {}

  ngOnInit(): void {
    this.roomId = this.route.snapshot.paramMap.get('roomId') || '';
    const user = this.authService.currentUserValue;
    if (user) this.displayName = `${user.firstName} ${user.lastName}`.trim();
  }

  // ── Join ───────────────────────────────────────────────────────────────────

  async joinMeeting(): Promise<void> {
    if (!this.displayName.trim()) return;
    this.joining = true;
    this.error = '';

    try {
      this.localStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
      this.videoEnabled = true;
      this.audioEnabled = true;
    } catch {
      try {
        this.localStream = await navigator.mediaDevices.getUserMedia({ video: false, audio: true });
        this.videoEnabled = false;
        this.audioEnabled = true;
        this.error = 'Joined with audio only (camera unavailable)';
      } catch {
        this.videoEnabled = false;
        this.audioEnabled = false;
        this.error = 'Joined without media. You can still see and hear others.';
      }
    }

    this.joined = true;
    this.joining = false;

    // Attach local video
    requestAnimationFrame(() => this.attachLocalVideo());
    this.connectSocket();
  }

  private attachLocalVideo(): void {
    document.querySelectorAll<HTMLVideoElement>('video[data-local-cam]').forEach(el => {
      if (el.srcObject !== this.localStream) {
        el.muted = true;
        el.srcObject = this.localStream;
        el.play().catch(() => {});
      }
    });
  }

  ngAfterViewChecked(): void {
    // Attach local camera
    if (this.localStream) {
      document.querySelectorAll<HTMLVideoElement>('video[data-local-cam]').forEach(el => {
        if (el.srcObject !== this.localStream) {
          el.muted = true;
          el.srcObject = this.localStream;
          el.play().catch(() => {});
        }
      });
    }
    // Attach remote streams
    this.peers.forEach((peer, socketId) => {
      if (peer.stream && peer.stream.getTracks().length > 0) {
        const el = document.getElementById(`cam-${socketId}`) as HTMLVideoElement;
        if (el && el.srcObject !== peer.stream) {
          el.muted = false;
          el.srcObject = peer.stream;
          el.play().catch(() => {});
        }
      }
    });
  }

  // ── Socket ─────────────────────────────────────────────────────────────────

  private connectSocket(): void {
    this.socket = io('http://localhost:3001', { transports: ['websocket', 'polling'] });

    this.socket.on('connect', () => {
      console.log('✅ Connected to signaling server');
      this.socket.emit('join-room', {
        roomId: this.roomId,
        userName: this.displayName,
        role: 'student'
      });
    });

    // Existing peers in room
    this.socket.on('room-peers', (peers: any[]) => {
      peers.forEach(p => this.getOrCreatePeer(p.socketId, p.name, p.role));
    });

    // New peer joined
    this.socket.on('peer-joined', ({ socketId, name, role }: any) => {
      this.getOrCreatePeer(socketId, name, role);
    });

    // Perfect negotiation: incoming description
    this.socket.on('description', async ({ from, description }: any) => {
      const peer = this.getOrCreatePeer(from, '', 'tutor');
      const pc = peer.pc;
      const polite = false; // student is impolite (tutor drives negotiation)

      try {
        const readyForOffer =
          !peer.makingOffer &&
          (pc.signalingState === 'stable' || peer.isSettingRemoteAnswerPending);
        const offerCollision = description.type === 'offer' && !readyForOffer;

        peer.ignoreOffer = !polite && offerCollision;
        if (peer.ignoreOffer) return;

        peer.isSettingRemoteAnswerPending = description.type === 'answer';
        await pc.setRemoteDescription(description);
        peer.isSettingRemoteAnswerPending = false;

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

    // Perfect negotiation: incoming ICE candidate
    this.socket.on('ice-candidate', async ({ from, candidate }: any) => {
      const peer = this.peers.get(from);
      if (!peer) return;
      try {
        await peer.pc.addIceCandidate(candidate ? new RTCIceCandidate(candidate) : undefined);
      } catch (e) {
        if (!peer.ignoreOffer) console.error('[ICE] addIceCandidate error:', e);
      }
    });

    this.socket.on('peer-left', ({ socketId }: any) => {
      this.zone.run(() => this.removePeer(socketId));
    });

    this.socket.on('peer-media-state', ({ socketId, audio, video, screen }: any) => {
      this.zone.run(() => {
        const peer = this.peers.get(socketId);
        if (!peer) return;
        peer.audioEnabled = audio;
        peer.videoEnabled = video;
        peer.screenSharing = screen;
        this.cdr.detectChanges();
      });
    });

    this.socket.on('tutor-left', () => {
      this.zone.run(() => {
        this.error = 'The tutor has ended the meeting';
        setTimeout(() => this.router.navigate(['/student-panel']), 3000);
      });
    });
  }

  // ── Peer Connection (Perfect Negotiation) ─────────────────────────────────

  private getOrCreatePeer(socketId: string, name: string, role: 'tutor' | 'student'): RemotePeer {
    if (this.peers.has(socketId)) return this.peers.get(socketId)!;

    const pc = new RTCPeerConnection({ iceServers: ICE_SERVERS });
    const stream = new MediaStream();

    const peer: RemotePeer = {
      socketId, name: name || 'Unknown', role,
      pc, stream,
      audioEnabled: true, videoEnabled: false, screenSharing: false,
      makingOffer: false, ignoreOffer: false, isSettingRemoteAnswerPending: false
    };

    this.peers.set(socketId, peer);

    // Add local tracks
    this.localStream?.getTracks().forEach(t => pc.addTrack(t, this.localStream!));

    // Perfect negotiation: onnegotiationneeded
    // Student is impolite — always initiates the offer
    pc.onnegotiationneeded = async () => {
      if (pc.signalingState !== 'stable') return;
      try {
        peer.makingOffer = true;
        await pc.setLocalDescription();
        this.socket.emit('description', {
          to: socketId,
          from: this.socket.id,
          description: pc.localDescription
        });
      } catch (e) {
        console.error('[Perfect Negotiation] onnegotiationneeded error:', e);
      } finally {
        peer.makingOffer = false;
      }
    };

    // Handle incoming tracks — attach directly, don't rely on onunmute
    pc.ontrack = ({ track, streams }) => {
      this.zone.run(() => {
        stream.addTrack(track);
        if (track.kind === 'video') peer.videoEnabled = true;

        const el = document.getElementById(`cam-${socketId}`) as HTMLVideoElement;
        if (el) {
          el.muted = false;
          el.srcObject = stream;
          el.play().catch(() => {});
        }
        this.cdr.detectChanges();
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

    this.zone.run(() => this.cdr.detectChanges());
    return peer;
  }

  private removePeer(socketId: string): void {
    this.peers.get(socketId)?.pc.close();
    this.peers.delete(socketId);
    this.cdr.detectChanges();
  }

  // ── Controls ───────────────────────────────────────────────────────────────

  toggleAudio(): void {
    this.audioEnabled = !this.audioEnabled;
    this.localStream?.getAudioTracks().forEach(t => t.enabled = this.audioEnabled);
    this.broadcastMediaState();
  }

  toggleVideo(): void {
    this.videoEnabled = !this.videoEnabled;
    this.localStream?.getVideoTracks().forEach(t => t.enabled = this.videoEnabled);
    if (this.videoEnabled) requestAnimationFrame(() => this.attachLocalVideo());
    this.broadcastMediaState();
  }

  async toggleScreenShare(): Promise<void> {
    if (this.screenSharing) this.stopScreenShare();
    else await this.startScreenShare();
  }

  private async startScreenShare(): Promise<void> {
    try {
      this.localScreenStream = await (navigator.mediaDevices as any).getDisplayMedia({ video: true, audio: false });
      this.screenSharing = true;
      this.peers.forEach(peer => {
        this.localScreenStream!.getTracks().forEach(t => peer.pc.addTrack(t, this.localScreenStream!));
      });
      this.localScreenStream!.getVideoTracks()[0].onended = () => this.stopScreenShare();
      this.broadcastMediaState();
      this.cdr.detectChanges();
    } catch (e) { console.error('Screen share error:', e); }
  }

  private stopScreenShare(): void {
    if (!this.localScreenStream) return;
    this.peers.forEach(peer => {
      peer.pc.getSenders().forEach(s => {
        if (this.localScreenStream!.getTracks().includes(s.track!)) peer.pc.removeTrack(s);
      });
    });
    this.localScreenStream.getTracks().forEach(t => t.stop());
    this.localScreenStream = null;
    this.screenSharing = false;
    this.broadcastMediaState();
    this.cdr.detectChanges();
  }

  private broadcastMediaState(): void {
    this.socket?.emit('media-state', {
      roomId: this.roomId,
      audio: this.audioEnabled,
      video: this.videoEnabled,
      screen: this.screenSharing
    });
  }

  leaveMeeting(): void {
    this.localStream?.getTracks().forEach(t => t.stop());
    this.localScreenStream?.getTracks().forEach(t => t.stop());
    this.peers.forEach(p => p.pc.close());
    this.peers.clear();
    this.socket?.disconnect();
    this.router.navigate(['/student-panel']);
  }

  // ── Getters ────────────────────────────────────────────────────────────────

  get peersArray(): RemotePeer[] { return Array.from(this.peers.values()); }
  get anyoneSharing(): boolean { return this.screenSharing || this.peersArray.some(p => p.screenSharing); }
  get sharingPeer(): RemotePeer | null { return this.peersArray.find(p => p.screenSharing) || null; }

  getInitials(name: string): string { return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2) || '?'; }
  getFirstLetter(name: string): string { return (name?.trim()[0] || '?').toUpperCase(); }

  ngOnDestroy(): void { this.leaveMeeting(); }
}
