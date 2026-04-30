import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { io, Socket } from 'socket.io-client';
import { OnlineLessonService } from '../../../core/services/online-lesson.service';
import { AuthService } from '../../../core/services/auth.service';

/**
 * Tutor Meeting Component - Clean Architecture
 * 
 * This component handles the tutor side of WebRTC video meetings.
 * Features: Camera, Audio, Screen Share, Multi-peer support, Clean UI
 */

interface Peer {
  socketId: string;
  name: string;
  role: string;
  connection?: RTCPeerConnection;
  stream?: MediaStream;
  iceBuffer?: RTCIceCandidate[];
  mediaState?: {
    audio: boolean;
    video: boolean;
    screen: boolean;
  };
}

@Component({
  selector: 'app-instant-meeting',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './instant-meeting.component.html',
  styleUrls: ['./instant-meeting.component.scss']
})
export class InstantMeetingComponent implements OnInit, OnDestroy, AfterViewChecked {
  // View References
  @ViewChild('localVideo') localVideoRef!: ElementRef<HTMLVideoElement>;

  // State Management
  roomId: string = '';
  meetingStarted: boolean = false;
  error: string = '';
  inviteLink: string = '';
  lessonId: number | null = null;

  // Media State
  localStream: MediaStream | null = null;
  cameraStream: MediaStream | null = null; // Separate camera stream for PiP
  audioEnabled: boolean = true;
  videoEnabled: boolean = true;
  screenSharing: boolean = false;
  screenTrack: MediaStreamTrack | null = null;
  originalVideoTrack: MediaStreamTrack | null = null;

  // WebRTC
  socket: Socket | null = null;
  peers: Map<string, Peer> = new Map();
  
  // ICE Configuration
  private readonly iceServers = [
    { urls: 'stun:stun.l.google.com:19302' },
    { urls: 'stun:stun1.l.google.com:19302' }
  ];

  // Flags
  private makingOffer: boolean = false;
  private negotiationTimeout: any = null;
  private noCameraMode: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private onlineLessonService: OnlineLessonService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Get or generate room ID
    this.roomId = this.route.snapshot.paramMap.get('roomId') || this.generateRoomId();
    
    // Get lessonId from query params
    this.route.queryParams.subscribe(params => {
      if (params['lessonId']) {
        this.lessonId = +params['lessonId'];
      }
    });
    
    // Check for no-camera mode
    const urlParams = new URLSearchParams(window.location.search);
    this.noCameraMode = urlParams.has('noCamera');

    // Generate invite link
    this.inviteLink = `${window.location.origin}/join/${this.roomId}`;
  }

  ngAfterViewChecked(): void {
    this.attachVideoStreams();
  }

  ngOnDestroy(): void {
    this.cleanup();
  }

  /**
   * Start the meeting
   */
  async startMeeting(): Promise<void> {
    try {
      // Get user media
      if (this.noCameraMode) {
        this.localStream = await navigator.mediaDevices.getUserMedia({
          video: false,
          audio: true
        });
        this.videoEnabled = false;
      } else {
        // Try HD video first, fallback to basic, then audio-only
        try {
          this.localStream = await navigator.mediaDevices.getUserMedia({
            video: { width: 1280, height: 720 },
            audio: true
          });
        } catch (hdError) {
          console.warn('HD video failed, trying basic video', hdError);
          try {
            this.localStream = await navigator.mediaDevices.getUserMedia({
              video: true,
              audio: true
            });
          } catch (basicError) {
            console.warn('Basic video failed, trying audio-only', basicError);
            this.localStream = await navigator.mediaDevices.getUserMedia({
              video: false,
              audio: true
            });
            this.videoEnabled = false;
            this.error = 'Camera unavailable - audio only mode';
          }
        }
      }

      // Store original video track for screen share
      const videoTrack = this.localStream.getVideoTracks()[0];
      if (videoTrack) {
        this.originalVideoTrack = videoTrack;
      }

      // Clone camera stream for PiP during screen share
      this.cameraStream = this.localStream.clone();

      // Create meeting session in database if lessonId is provided
      if (this.lessonId) {
        const currentUser = this.authService.currentUserValue;
        if (currentUser) {
          this.onlineLessonService.createMeetingSession(
            this.lessonId,
            this.roomId,
            this.inviteLink,
            currentUser.id
          ).subscribe({
            next: (session) => {
              console.log('Meeting session created:', session);
            },
            error: (err) => {
              console.error('Failed to create meeting session:', err);
              // Continue anyway - don't block the meeting
            }
          });
        }
      }

      // Connect to signaling server
      this.connectToSignalingServer();
      
      // Update UI
      this.meetingStarted = true;
      
    } catch (err: any) {
      console.error('Failed to start meeting:', err);
      
      if (err.name === 'NotReadableError' || err.name === 'AbortError') {
        this.error = 'Camera in use - use ?noCamera=true for audio only';
      } else if (err.name === 'NotAllowedError') {
        this.error = 'Camera/microphone permission denied';
      } else {
        this.error = 'Failed to access camera/microphone';
      }
    }
  }

  /**
   * Connect to Socket.IO signaling server
   */
  private connectToSignalingServer(): void {
    this.socket = io('http://localhost:3001', {
      transports: ['websocket'],
      reconnection: true
    });

    // Connection events
    this.socket.on('connect', () => {
      console.log('Connected to signaling server');
      this.socket!.emit('join-room', {
        roomId: this.roomId,
        userName: 'Tutor',
        role: 'tutor'
      });
    });

    // Room events
    this.socket.on('room-peers', (peers: Peer[]) => {
      console.log('Existing peers in room:', peers);
      // Create connections for existing peers
      peers.forEach(peer => {
        if (peer.socketId !== this.socket!.id) {
          this.createPeerConnection(peer.socketId, peer.name);
        }
      });
    });

    this.socket.on('peer-joined', async (peer: Peer) => {
      console.log('New peer joined:', peer);
      
      // Create peer connection and send offer
      await this.createPeerConnection(peer.socketId, peer.name);
      await this.createAndSendOffer(peer.socketId);
    });

    // WebRTC signaling
    this.socket.on('description', async ({ from, description }: { from: string; description: RTCSessionDescriptionInit }) => {
      // Ignore own descriptions
      if (from === this.socket!.id) return;

      console.log('Received description:', description.type, 'from', from);
      
      try {
        let peer = this.peers.get(from);
        
        if (!peer) {
          peer = await this.createPeerConnection(from, 'Student');
        }

        const pc = peer.connection!;
        
        // Set remote description
        await pc.setRemoteDescription(description);
        
        // Flush buffered ICE candidates
        if (peer.iceBuffer && peer.iceBuffer.length > 0) {
          console.log(`Flushing ${peer.iceBuffer.length} buffered ICE candidates`);
          for (const candidate of peer.iceBuffer) {
            await pc.addIceCandidate(candidate);
          }
          peer.iceBuffer = [];
        }

        // If it's an offer, create and send answer (shouldn't happen for tutor, but handle it)
        if (description.type === 'offer') {
          const answer = await pc.createAnswer();
          await pc.setLocalDescription(answer);
          
          this.socket!.emit('description', {
            to: from,
            from: this.socket!.id,
            description: pc.localDescription
          });
        }
      } catch (err) {
        console.error('Error handling description:', err);
      }
    });

    this.socket.on('ice-candidate', async ({ from, candidate }: { from: string; candidate: RTCIceCandidateInit }) => {
      if (from === this.socket!.id) return;

      const peer = this.peers.get(from);
      if (!peer || !peer.connection) {
        console.warn('Received ICE candidate for unknown peer:', from);
        return;
      }

      try {
        const pc = peer.connection;
        
        // Buffer candidates if remote description not set yet
        if (!pc.remoteDescription) {
          if (!peer.iceBuffer) peer.iceBuffer = [];
          peer.iceBuffer.push(new RTCIceCandidate(candidate));
          console.log('Buffered ICE candidate (no remote description yet)');
        } else {
          await pc.addIceCandidate(new RTCIceCandidate(candidate));
        }
      } catch (err) {
        console.error('Error adding ICE candidate:', err);
      }
    });

    // Peer left
    this.socket.on('peer-left', ({ socketId }: { socketId: string }) => {
      this.removePeer(socketId);
    });

    // Disconnect
    this.socket.on('disconnect', () => {
      console.log('Disconnected from signaling server');
    });
  }

  /**
   * Create RTCPeerConnection for a peer
   */
  private async createPeerConnection(peerId: string, peerName: string): Promise<Peer> {
    const pc = new RTCPeerConnection({ iceServers: this.iceServers });
    
    const peer: Peer = {
      socketId: peerId,
      name: peerName,
      role: 'student',
      connection: pc,
      stream: new MediaStream(),
      iceBuffer: [],
      mediaState: { audio: true, video: true, screen: false }
    };

    this.peers.set(peerId, peer);

    // Add local tracks
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => {
        pc.addTrack(track, this.localStream!);
      });
    }

    // Handle incoming tracks
    pc.ontrack = (event) => {
      console.log('Received remote track:', event.track.kind);
      if (peer.stream) {
        peer.stream.addTrack(event.track);
      }
    };

    // Handle ICE candidates
    pc.onicecandidate = (event) => {
      if (event.candidate) {
        this.socket!.emit('ice-candidate', {
          to: peerId,
          candidate: event.candidate
        });
      }
    };

    // Handle negotiation needed (debounced)
    pc.onnegotiationneeded = async () => {
      if (this.negotiationTimeout) {
        clearTimeout(this.negotiationTimeout);
      }
      
      this.negotiationTimeout = setTimeout(async () => {
        if (!this.makingOffer) {
          await this.createAndSendOffer(peerId);
        }
      }, 150);
    };

    // Handle connection state
    pc.onconnectionstatechange = () => {
      console.log('Connection state:', pc.connectionState, 'for peer', peerId);
      
      if (pc.connectionState === 'failed' || pc.connectionState === 'disconnected') {
        setTimeout(() => {
          if (pc.connectionState === 'disconnected') {
            pc.restartIce();
          }
        }, 5000);
      }
    };

    return peer;
  }

  /**
   * Create and send offer to peer
   */
  private async createAndSendOffer(peerId: string): Promise<void> {
    const peer = this.peers.get(peerId);
    if (!peer || !peer.connection) return;

    try {
      this.makingOffer = true;
      const pc = peer.connection;
      
      const offer = await pc.createOffer();
      await pc.setLocalDescription(offer);
      
      this.socket!.emit('description', {
        to: peerId,
        from: this.socket!.id,
        description: pc.localDescription
      });
      
      console.log('Sent offer to', peerId);
    } catch (err) {
      console.error('Error creating offer:', err);
    } finally {
      this.makingOffer = false;
    }
  }

  /**
   * Toggle audio on/off
   */
  toggleAudio(): void {
    if (this.localStream) {
      const audioTrack = this.localStream.getAudioTracks()[0];
      if (audioTrack) {
        audioTrack.enabled = !audioTrack.enabled;
        this.audioEnabled = audioTrack.enabled;
      }
    }
  }

  /**
   * Toggle video on/off
   */
  toggleVideo(): void {
    if (this.localStream) {
      const videoTrack = this.localStream.getVideoTracks()[0];
      if (videoTrack) {
        videoTrack.enabled = !videoTrack.enabled;
        this.videoEnabled = videoTrack.enabled;
      }
    }
  }

  /**
   * Start screen sharing
   */
  async startScreenShare(): Promise<void> {
    try {
      // Get screen share WITHOUT audio to prevent echo
      const screenStream = await navigator.mediaDevices.getDisplayMedia({
        video: true,
        audio: false  // CRITICAL: No audio to prevent echo
      });

      this.screenTrack = screenStream.getVideoTracks()[0];
      
      if (!this.screenTrack) {
        throw new Error('No screen track available');
      }

      // Handle screen share stop (user clicks browser's stop button)
      this.screenTrack.onended = () => {
        this.stopScreenShare();
      };

      // Replace video track in all peer connections
      this.peers.forEach(peer => {
        if (peer.connection) {
          const sender = peer.connection.getSenders().find(s => s.track?.kind === 'video');
          if (sender && this.screenTrack) {
            sender.replaceTrack(this.screenTrack);
          }
        }
      });

      // Replace in local stream for display
      if (this.localStream && this.originalVideoTrack) {
        this.localStream.removeTrack(this.originalVideoTrack);
        this.localStream.addTrack(this.screenTrack);
      }

      this.screenSharing = true;
      
    } catch (err) {
      console.error('Failed to start screen share:', err);
      this.error = 'Failed to start screen sharing';
    }
  }

  /**
   * Stop screen sharing
   */
  stopScreenShare(): void {
    if (!this.screenTrack) return;

    // Stop screen track
    this.screenTrack.stop();

    // Replace back to camera track in all peer connections
    this.peers.forEach(peer => {
      if (peer.connection && this.originalVideoTrack) {
        const sender = peer.connection.getSenders().find(s => s.track?.kind === 'video');
        if (sender) {
          sender.replaceTrack(this.originalVideoTrack);
        }
      }
    });

    // Replace in local stream
    if (this.localStream && this.originalVideoTrack) {
      this.localStream.removeTrack(this.screenTrack);
      this.localStream.addTrack(this.originalVideoTrack);
    }

    this.screenTrack = null;
    this.screenSharing = false;
  }

  /**
   * Copy invite link to clipboard
   */
  copyInviteLink(): void {
    navigator.clipboard.writeText(this.inviteLink).then(() => {
      alert('Invite link copied to clipboard!');
    }).catch(err => {
      console.error('Failed to copy:', err);
    });
  }

  /**
   * End meeting for all participants
   */
  endMeeting(): void {
    if (confirm('Are you sure you want to end the meeting for all participants?')) {
      // End meeting session in database if lessonId is provided
      if (this.lessonId) {
        this.onlineLessonService.endMeetingSession(this.lessonId).subscribe({
          next: () => {
            console.log('Meeting session ended in database');
          },
          error: (err) => {
            console.error('Failed to end meeting session:', err);
          }
        });
      }
      
      this.cleanup();
      this.router.navigate(['/tutor-panel']);
    }
  }

  /**
   * Attach streams to video elements
   */
  private attachVideoStreams(): void {
    // Attach local stream (main video or screen share)
    if (this.localVideoRef && this.localStream) {
      const video = this.localVideoRef.nativeElement;
      if (video.srcObject !== this.localStream) {
        video.srcObject = this.localStream;
        video.muted = true;
        video.play().catch(err => console.warn('Local play failed:', err));
      }
    }
  }

  /**
   * Attach camera PiP stream to video element
   */
  attachCameraPiP(videoElement: HTMLVideoElement): void {
    if (videoElement && this.cameraStream) {
      if (videoElement.srcObject !== this.cameraStream) {
        videoElement.srcObject = this.cameraStream;
        videoElement.muted = true;
        videoElement.play().catch(err => console.warn('Camera PiP play failed:', err));
      }
    }
  }

  /**
   * Get array of peers for template iteration
   */
  getPeersArray(): Peer[] {
    return Array.from(this.peers.values());
  }

  /**
   * Get initials from name (first letter of first and last name)
   */
  getInitials(name: string): string {
    if (!name) return '?';
    const parts = name.trim().split(' ');
    if (parts.length === 1) {
      return parts[0].charAt(0).toUpperCase();
    }
    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
  }

  /**
   * Get color for avatar based on name
   */
  getAvatarColor(name: string): string {
    const colors = [
      '#667eea', '#764ba2', '#f093fb', '#4facfe',
      '#43e97b', '#fa709a', '#fee140', '#30cfd0',
      '#a8edea', '#fed6e3', '#c471f5', '#fa71cd'
    ];
    
    if (!name) return colors[0];
    
    // Generate consistent color based on name
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
      hash = name.codePointAt(i)! + ((hash << 5) - hash);
    }
    
    return colors[Math.abs(hash) % colors.length];
  }

  /**
   * Check if peer has an active video track
   */
  hasActiveVideoTrack(peer: Peer): boolean {
    if (!peer.stream) return false;
    
    const videoTracks = peer.stream.getVideoTracks();
    if (videoTracks.length === 0) return false;
    
    // Check if at least one video track is enabled and not ended
    return videoTracks.some(track => track.enabled && track.readyState === 'live');
  }

  /**
   * Remove a peer connection
   */
  private removePeer(peerId: string): void {
    const peer = this.peers.get(peerId);
    if (peer) {
      if (peer.connection) {
        peer.connection.close();
      }
      if (peer.stream) {
        peer.stream.getTracks().forEach(track => track.stop());
      }
      this.peers.delete(peerId);
    }
  }

  /**
   * Cleanup all resources
   */
  private cleanup(): void {
    // Stop screen share if active
    if (this.screenSharing) {
      this.stopScreenShare();
    }

    // Stop all local tracks
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => track.stop());
      this.localStream = null;
    }

    // Close all peer connections
    this.peers.forEach(peer => {
      if (peer.connection) peer.connection.close();
      if (peer.stream) peer.stream.getTracks().forEach(track => track.stop());
    });
    this.peers.clear();

    // Disconnect socket
    if (this.socket) {
      this.socket.disconnect();
      this.socket = null;
    }
  }

  /**
   * Generate random room ID
   */
  private generateRoomId(): string {
    return Math.random().toString(36).substring(2, 10);
  }
}
