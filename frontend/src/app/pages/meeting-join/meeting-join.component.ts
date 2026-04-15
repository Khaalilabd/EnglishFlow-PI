import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { io, Socket } from 'socket.io-client';

/**
 * Student Meeting Component - Clean Architecture
 * 
 * This component handles the student side of WebRTC video meetings.
 * Features: Camera, Audio, Screen Share viewing, Clean UI
 */

interface Peer {
  socketId: string;
  name: string;
  role: string;
  connection?: RTCPeerConnection;
  stream?: MediaStream;
  iceBuffer?: RTCIceCandidate[];
}

@Component({
  selector: 'app-meeting-join',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './meeting-join.component.html',
  styleUrls: ['./meeting-join.component.scss']
})
export class MeetingJoinComponent implements OnInit, OnDestroy, AfterViewChecked {
  // View References
  @ViewChild('localVideo') localVideoRef!: ElementRef<HTMLVideoElement>;
  @ViewChild('remoteVideo') remoteVideoRef!: ElementRef<HTMLVideoElement>;
  @ViewChild('previewVideo') previewVideoRef!: ElementRef<HTMLVideoElement>;

  // State Management
  roomId: string = '';
  userName: string = '';
  joined: boolean = false;
  showPreview: boolean = true;
  error: string = '';
  connectionStatus: string = 'disconnected';

  // Media State
  localStream: MediaStream | null = null;
  previewStream: MediaStream | null = null;
  audioEnabled: boolean = true;
  videoEnabled: boolean = true;

  // WebRTC
  socket: Socket | null = null;
  peers: Map<string, Peer> = new Map();
  
  // ICE Configuration
  private readonly iceServers = [
    { urls: 'stun:stun.l.google.com:19302' },
    { urls: 'stun:stun1.l.google.com:19302' }
  ];

  // Flags
  private noCameraMode: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Get room ID from route
    this.roomId = this.route.snapshot.paramMap.get('roomId') || '';
    
    // Check for no-camera mode (useful for testing)
    const urlParams = new URLSearchParams(window.location.search);
    this.noCameraMode = urlParams.has('noCamera');

    if (!this.roomId) {
      this.error = 'Invalid room ID';
      return;
    }

    // Start camera preview
    this.startPreview();
  }

  ngAfterViewChecked(): void {
    // Attach streams to video elements
    this.attachVideoStreams();
  }

  ngOnDestroy(): void {
    this.cleanup();
  }

  /**
   * Start camera preview before joining
   */
  async startPreview(): Promise<void> {
    try {
      if (this.noCameraMode) {
        // Audio only mode
        this.previewStream = await navigator.mediaDevices.getUserMedia({
          video: false,
          audio: true
        });
        this.videoEnabled = false;
      } else {
        // Try HD video first, fallback to basic, then audio-only
        try {
          this.previewStream = await navigator.mediaDevices.getUserMedia({
            video: { width: 1280, height: 720 },
            audio: true
          });
        } catch (hdError) {
          console.warn('HD video failed, trying basic video', hdError);
          try {
            this.previewStream = await navigator.mediaDevices.getUserMedia({
              video: true,
              audio: true
            });
          } catch (basicError) {
            console.warn('Basic video failed, trying audio-only', basicError);
            this.previewStream = await navigator.mediaDevices.getUserMedia({
              video: false,
              audio: true
            });
            this.videoEnabled = false;
            this.error = 'Camera unavailable - audio only mode';
          }
        }
      }
    } catch (err: any) {
      console.error('Failed to get user media:', err);
      
      // Handle specific errors
      if (err.name === 'NotReadableError' || err.name === 'AbortError') {
        this.error = 'Camera in use by another app - use ?noCamera=true for audio only';
      } else if (err.name === 'NotAllowedError') {
        this.error = 'Camera/microphone permission denied';
      } else {
        this.error = 'Failed to access camera/microphone';
      }
    }
  }

  /**
   * Join the meeting room
   */
  async joinMeeting(): Promise<void> {
    if (!this.userName.trim()) {
      this.error = 'Please enter your name';
      return;
    }

    try {
      // Reuse preview stream to avoid "device in use" error
      if (this.previewStream) {
        this.localStream = this.previewStream;
        this.previewStream = null;
      } else {
        // Fallback: get media if preview failed
        this.localStream = await navigator.mediaDevices.getUserMedia({
          video: !this.noCameraMode,
          audio: true
        });
      }

      // Connect to signaling server
      this.connectToSignalingServer();
      
      // Update UI
      this.showPreview = false;
      this.joined = true;
      this.connectionStatus = 'connecting';
      
    } catch (err) {
      console.error('Failed to join meeting:', err);
      this.error = 'Failed to join meeting';
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
        userName: this.userName,
        role: 'student'
      });
    });

    // Room events
    this.socket.on('room-peers', (peers: Peer[]) => {
      console.log('Existing peers in room:', peers);
      // Tutor should initiate connection
    });

    this.socket.on('peer-joined', (peer: Peer) => {
      console.log('Peer joined:', peer);
    });

    // WebRTC signaling
    this.socket.on('description', async ({ from, description }: { from: string; description: RTCSessionDescriptionInit }) => {
      // Ignore own descriptions
      if (from === this.socket!.id) return;

      console.log('Received description:', description.type, 'from', from);
      
      try {
        let peer = this.peers.get(from);
        
        if (!peer) {
          peer = await this.createPeerConnection(from);
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

        // If it's an offer, create and send answer
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

    this.socket.on('tutor-left', () => {
      this.error = 'Tutor has ended the meeting';
      setTimeout(() => this.leaveMeeting(), 3000);
    });

    // Disconnect
    this.socket.on('disconnect', () => {
      console.log('Disconnected from signaling server');
      this.connectionStatus = 'disconnected';
    });
  }

  /**
   * Create RTCPeerConnection for a peer
   */
  private async createPeerConnection(peerId: string): Promise<Peer> {
    const pc = new RTCPeerConnection({ iceServers: this.iceServers });
    
    const peer: Peer = {
      socketId: peerId,
      name: 'Tutor',
      role: 'tutor',
      connection: pc,
      stream: new MediaStream(),
      iceBuffer: []
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
      this.connectionStatus = 'connected';
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

    // Handle connection state
    pc.onconnectionstatechange = () => {
      console.log('Connection state:', pc.connectionState);
      this.connectionStatus = pc.connectionState;
      
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
   * Leave the meeting
   */
  leaveMeeting(): void {
    this.cleanup();
    this.router.navigate(['/']);
  }

  /**
   * Attach streams to video elements
   */
  private attachVideoStreams(): void {
    // Attach preview stream
    if (this.previewVideoRef && this.previewStream) {
      const video = this.previewVideoRef.nativeElement;
      if (video.srcObject !== this.previewStream) {
        video.srcObject = this.previewStream;
        video.muted = true;
        video.play().catch(err => console.warn('Preview play failed:', err));
      }
    }

    // Attach local stream
    if (this.localVideoRef && this.localStream) {
      const video = this.localVideoRef.nativeElement;
      if (video.srcObject !== this.localStream) {
        video.srcObject = this.localStream;
        video.muted = true;
        video.play().catch(err => console.warn('Local play failed:', err));
      }
    }

    // Attach remote stream (first peer only for now)
    if (this.remoteVideoRef && this.peers.size > 0) {
      const firstPeer = Array.from(this.peers.values())[0];
      if (firstPeer.stream) {
        const video = this.remoteVideoRef.nativeElement;
        if (video.srcObject !== firstPeer.stream) {
          video.srcObject = firstPeer.stream;
          video.play().catch(err => console.warn('Remote play failed:', err));
        }
      }
    }
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
    // Stop all local tracks
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => track.stop());
      this.localStream = null;
    }
    
    if (this.previewStream) {
      this.previewStream.getTracks().forEach(track => track.stop());
      this.previewStream = null;
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
}
