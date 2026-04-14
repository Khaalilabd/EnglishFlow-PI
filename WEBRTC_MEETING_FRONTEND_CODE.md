# WebRTC Meeting System - Frontend Code

This document contains the frontend Angular components and services for the WebRTC meeting functionality.

## Table of Contents
1. [Student Meeting Component](#student-meeting-component)
2. [Tutor Meeting Component](#tutor-meeting-component)
3. [Online Lesson Service](#online-lesson-service)

---

## Student Meeting Component

### File: `frontend/src/app/pages/meeting-join/meeting-join.component.ts`

```typescript
import { Component, OnInit, OnDestroy, AfterViewChecked, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { io, Socket } from 'socket.io-client';

interface RemotePeer {
  socketId: string;
  name: string;
  role: string;
  pc: RTCPeerConnection;
  stream: MediaStream;
  audioEnabled: boolean;
  videoEnabled: boolean;
  screenSharing: boolean;
  pendingCandidates: RTCIceCandidateInit[];
  remoteDescSet: boolean;
}

const ICE = [
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
