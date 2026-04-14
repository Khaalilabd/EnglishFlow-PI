import { Injectable, NgZone } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';

export interface RemoteParticipant {
  userId: number;
  userName: string;
  stream: MediaStream | null;
  audioEnabled: boolean;
  videoEnabled: boolean;
  profilePhoto?: string;
}

interface WebRTCSignal {
  eventId?: number;
  fromUserId: number;
  toUserId?: number;
  type: 'offer' | 'answer' | 'ice-candidate' | 'join' | 'leave';
  sdp?: string;
  candidate?: string;
  sdpMid?: string;
  sdpMLineIndex?: number;
  userName?: string;
  profilePhoto?: string;
}

const ICE_SERVERS: RTCConfiguration = {
  iceServers: [
    { urls: 'stun:stun.l.google.com:19302' },
    { urls: 'stun:stun1.l.google.com:19302' }
  ]
};

@Injectable({ providedIn: 'root' })
export class WebRTCService {

  private client: Client | null = null;
  private eventId!: number;
  private currentUserId!: number;
  private _userName = '';
  private subscribed = false;

  localStream$ = new BehaviorSubject<MediaStream | null>(null);
  participants$ = new BehaviorSubject<RemoteParticipant[]>([]);

  audioEnabled = true;
  videoEnabled = true;

  private peers = new Map<number, RTCPeerConnection>();

  constructor(private zone: NgZone) {}

  // ── SUBSCRIBE EARLY ───────────────────────────────────────────
  // Call at session connect so we receive signals before enabling camera

  subscribeSignaling(client: Client, eventId: number, userId: number, userName: string): void {
    if (this.subscribed) return;
    this.client = client;
    this.eventId = eventId;
    this.currentUserId = userId;
    this._userName = userName;
    this.subscribed = true;

    const personalTopic = `/topic/session/${eventId}/webrtc/user/${userId}`;
    const broadcastTopic = `/topic/session/${eventId}/webrtc`;
    console.log(`[WebRTC] subscribing to ${personalTopic}`);
    console.log(`[WebRTC] subscribing to ${broadcastTopic}`);

    this.client.subscribe(
      personalTopic,
      (msg: IMessage) => {
        console.log(`[WebRTC] personal message received:`, msg.body);
        this.zone.run(() => this.handleSignal(JSON.parse(msg.body)));
      }
    );

    this.client.subscribe(
      broadcastTopic,
      (msg: IMessage) => {
        console.log(`[WebRTC] broadcast message received:`, msg.body);
        this.zone.run(() => this.handleSignal(JSON.parse(msg.body)));
      }
    );
  }

  // ── JOIN WITH CAMERA ──────────────────────────────────────────

  async join(client: Client, eventId: number, userId: number, userName: string, profilePhoto?: string): Promise<void> {
    this.subscribeSignaling(client, eventId, userId, userName);

    const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });

    // Mute audio and video by default on join
    stream.getAudioTracks().forEach(t => t.enabled = false);
    stream.getVideoTracks().forEach(t => t.enabled = false);
    this.audioEnabled = false;
    this.videoEnabled = false;

    this.localStream$.next(stream);
    console.log(`[WebRTC] got local stream, tracks: ${stream.getTracks().map(t => t.kind).join(',')}`);

    // Add tracks to any existing peer connections
    this.peers.forEach((pc, peerId) => {
      stream.getTracks().forEach(t => {
        const senders = pc.getSenders();
        if (!senders.find(s => s.track?.kind === t.kind)) {
          pc.addTrack(t, stream);
          console.log(`[WebRTC] added ${t.kind} track to existing peer ${peerId}`);
        }
      });
    });

    console.log(`[WebRTC] sending join broadcast`);
    this.send({ type: 'join', userName, profilePhoto });
  }

  // ── JOIN FOR SCREEN SHARE (no camera) ────────────────────────

  async joinScreenShareOnly(client: Client, eventId: number, userId: number, userName: string, profilePhoto?: string): Promise<void> {
    this.subscribeSignaling(client, eventId, userId, userName);
    // Broadcast join so participants create peer connections with us
    // We don't request camera — screen track will be added in startScreenShare
    console.log(`[WebRTC] sending join broadcast (screen share only)`);
    this.send({ type: 'join', userName, profilePhoto });
    // Small delay to let participants respond with offers
    await new Promise(resolve => setTimeout(resolve, 500));
  }

  // ── SIGNAL HANDLING ───────────────────────────────────────────

  private async handleSignal(signal: WebRTCSignal): Promise<void> {
    console.log(`[WebRTC] received signal type=${signal.type} from=${signal.fromUserId} to=${signal.toUserId} me=${this.currentUserId}`);
    if (signal.fromUserId === this.currentUserId) return;

    switch (signal.type) {
      case 'join':
        console.log(`[WebRTC] peer ${signal.fromUserId} joined, creating offer`);
        await this.createOffer(signal.fromUserId, signal.userName || 'Unknown', signal.profilePhoto);
        break;
      case 'offer':
        console.log(`[WebRTC] got offer from ${signal.fromUserId}`);
        await this.handleOffer(signal);
        break;
      case 'answer':
        console.log(`[WebRTC] got answer from ${signal.fromUserId}`);
        await this.handleAnswer(signal);
        break;
      case 'ice-candidate':
        await this.handleIceCandidate(signal);
        break;
      case 'leave':
        this.removePeer(signal.fromUserId);
        break;
    }
  }

  private async createOffer(toUserId: number, userName: string, profilePhoto?: string): Promise<void> {
    const pc = this.createPeerConnection(toUserId, userName, profilePhoto);
    const offer = await pc.createOffer();
    await pc.setLocalDescription(offer);
    this.send({ type: 'offer', toUserId, sdp: offer.sdp });
  }

  private async handleOffer(signal: WebRTCSignal): Promise<void> {
    const pc = this.createPeerConnection(signal.fromUserId, signal.userName || 'Unknown', signal.profilePhoto);
    await pc.setRemoteDescription(new RTCSessionDescription({ type: 'offer', sdp: signal.sdp }));
    const answer = await pc.createAnswer();
    await pc.setLocalDescription(answer);
    this.send({ type: 'answer', toUserId: signal.fromUserId, sdp: answer.sdp });
  }

  private async handleAnswer(signal: WebRTCSignal): Promise<void> {
    const pc = this.peers.get(signal.fromUserId);
    if (pc) await pc.setRemoteDescription(new RTCSessionDescription({ type: 'answer', sdp: signal.sdp }));
  }

  private async handleIceCandidate(signal: WebRTCSignal): Promise<void> {
    const pc = this.peers.get(signal.fromUserId);
    if (pc && signal.candidate) {
      await pc.addIceCandidate(new RTCIceCandidate({
        candidate: signal.candidate,
        sdpMid: signal.sdpMid ?? null,
        sdpMLineIndex: signal.sdpMLineIndex ?? null
      }));
    }
  }

  // ── PEER CONNECTION ───────────────────────────────────────────

  private createPeerConnection(userId: number, userName: string, profilePhoto?: string): RTCPeerConnection {
    if (this.peers.has(userId)) return this.peers.get(userId)!;

    const pc = new RTCPeerConnection(ICE_SERVERS);
    this.peers.set(userId, pc);

    // Add local tracks if camera is already on
    const local = this.localStream$.value;
    if (local) local.getTracks().forEach(t => pc.addTrack(t, local));

    pc.onicecandidate = (e) => {
      if (e.candidate) {
        this.send({
          type: 'ice-candidate',
          toUserId: userId,
          candidate: e.candidate.candidate,
          sdpMid: e.candidate.sdpMid ?? undefined,
          sdpMLineIndex: e.candidate.sdpMLineIndex ?? undefined
        });
      }
    };

    pc.ontrack = (e) => {
      const stream = e.streams[0];
      console.log(`[WebRTC] ontrack from ${userId}, streams: ${e.streams.length}, tracks: ${e.track.kind}`);
      if (!stream) return;
      // Run inside Angular zone so change detection fires automatically
      this.zone.run(() => {
        this.upsertParticipant(userId, userName, stream, profilePhoto);
        this.assignVideoStream(userId, stream, 0);
      });
    };

    pc.onconnectionstatechange = () => {
      console.log(`[WebRTC] connection state with ${userId}: ${pc.connectionState}`);
      this.zone.run(() => {
        if (pc.connectionState === 'disconnected' || pc.connectionState === 'failed') {
          this.removePeer(userId);
        }
      });
    };

    this.upsertParticipant(userId, userName, null, profilePhoto);
    return pc;
  }

  private assignVideoStream(userId: number, stream: MediaStream, attempt: number): void {
    const el = document.getElementById(`video-${userId}`) as HTMLVideoElement | null;
    if (el) {
      el.srcObject = stream;
      el.play().catch(() => {});
      console.log(`[WebRTC] assigned srcObject to video-${userId} on attempt ${attempt}`);
    } else if (attempt < 30) {
      // First attempts: wait longer for Angular to render
      const delay = attempt < 3 ? 300 : 200;
      setTimeout(() => this.assignVideoStream(userId, stream, attempt + 1), delay);
    } else {
      console.warn(`[WebRTC] could not find video-${userId} after ${attempt} attempts`);
    }
  }

  private upsertParticipant(userId: number, userName: string, stream: MediaStream | null, profilePhoto?: string): void {
    const current = this.participants$.value;
    const idx = current.findIndex(p => p.userId === userId);
    if (idx >= 0) {
      // Create new object to trigger Angular change detection
      const updated = [...current];
      updated[idx] = { ...current[idx], stream: stream ?? current[idx].stream, profilePhoto: profilePhoto ?? current[idx].profilePhoto };
      this.participants$.next(updated);
    } else {
      this.participants$.next([...current, { userId, userName, stream, audioEnabled: true, videoEnabled: true, profilePhoto }]);
    }
  }

  private removePeer(userId: number): void {
    this.peers.get(userId)?.close();
    this.peers.delete(userId);
    this.participants$.next(this.participants$.value.filter(p => p.userId !== userId));
  }

  // ── SCREEN SHARE ──────────────────────────────────────────────

  screenSharing = false;
  private screenStream: MediaStream | null = null;
  screenStream$ = new BehaviorSubject<MediaStream | null>(null);
  // userId of the participant currently sharing screen (null = nobody)
  remoteScreenSharerId$ = new BehaviorSubject<number | null>(null);

  async startScreenShare(): Promise<void> {
    try {
      const screen = await (navigator.mediaDevices as any).getDisplayMedia({ video: true, audio: false });
      this.screenStream = screen;
      this.screenSharing = true;

      const videoTrack = screen.getVideoTracks()[0];

      // For each peer: if a video sender exists, replace track; otherwise add track and renegotiate
      const renegotiatePromises: Promise<void>[] = [];
      this.peers.forEach((pc, peerId) => {
        const sender = pc.getSenders().find(s => s.track?.kind === 'video');
        if (sender) {
          sender.replaceTrack(videoTrack);
        } else {
          // No video sender yet — add track and renegotiate
          pc.addTrack(videoTrack, screen);
          renegotiatePromises.push(this.renegotiate(pc, peerId));
        }
      });
      await Promise.all(renegotiatePromises);

      // Expose screen stream for local preview
      this.zone.run(() => {
        this.screenStream$.next(screen);
        this.localStream$.next(screen);
      });

      // Stop screen share when user clicks browser's "Stop sharing"
      videoTrack.onended = () => this.stopScreenShare();
    } catch {
      this.screenSharing = false;
    }
  }

  private async renegotiate(pc: RTCPeerConnection, peerId: number): Promise<void> {
    try {
      const offer = await pc.createOffer();
      await pc.setLocalDescription(offer);
      this.send({ type: 'offer', toUserId: peerId, sdp: offer.sdp });
    } catch (e) {
      console.warn(`[WebRTC] renegotiate failed for peer ${peerId}`, e);
    }
  }

  async stopScreenShare(): Promise<void> {
    this.screenStream?.getTracks().forEach(t => t.stop());
    this.screenStream = null;
    this.screenSharing = false;

    // Restore camera stream
    const cam = this.localStream$.value;
    if (cam) {
      const camVideo = cam.getVideoTracks()[0];
      if (camVideo) {
        this.peers.forEach(pc => {
          const sender = pc.getSenders().find(s => s.track?.kind === 'video');
          if (sender) sender.replaceTrack(camVideo);
        });
      }
    }
    this.zone.run(() => this.screenStream$.next(null));
  }

  // ── CONTROLS ──────────────────────────────────────────────────

  toggleAudio(): void {
    const stream = this.localStream$.value;
    if (!stream) return;
    this.audioEnabled = !this.audioEnabled;
    stream.getAudioTracks().forEach(t => t.enabled = this.audioEnabled);
  }

  toggleVideo(): void {
    const stream = this.localStream$.value;
    if (!stream) return;
    this.videoEnabled = !this.videoEnabled;
    stream.getVideoTracks().forEach(t => t.enabled = this.videoEnabled);
  }

  // ── CLEANUP ───────────────────────────────────────────────────

  leave(): void {
    this.send({ type: 'leave' });
    this.peers.forEach(pc => pc.close());
    this.peers.clear();
    this.localStream$.value?.getTracks().forEach(t => t.stop());
    this.localStream$.next(null);
    this.participants$.next([]);
    this.subscribed = false;
  }

  // ── HELPERS ───────────────────────────────────────────────────

  private send(signal: Partial<WebRTCSignal>): void {
    this.client?.publish({
      destination: `/app/session/${this.eventId}/webrtc`,
      body: JSON.stringify({ ...signal, fromUserId: this.currentUserId, eventId: this.eventId })
    });
  }
}
