import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { LiveSessionService, ChatMessage, Poll, Question, HandRaise, WhiteboardEvent } from '../../../core/services/live-session.service';
import { WebRTCService, RemoteParticipant } from '../../../core/services/webrtc.service';
import { AuthService } from '../../../core/services/auth.service';
import { EventService } from '../../../core/services/event.service';
import { MemberService } from '../../../core/services/member.service';
import { SponsorService } from '../../../core/services/sponsor.service';
import { Sponsor } from '../../../core/models/sponsor.model';

type Tab = 'chat' | 'qa' | 'poll' | 'hands' | 'whiteboard';

interface FloatingReaction { id: number; emoji: string; x: number; }

@Component({
  selector: 'app-live-session',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './live-session.component.html',
  styleUrls: ['./live-session.component.scss']
})
export class LiveSessionComponent implements OnInit, OnDestroy, AfterViewChecked {

  @ViewChild('chatContainer') chatContainer!: ElementRef;
  @ViewChild('whiteboardCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('localVideo') localVideoRef!: ElementRef<HTMLVideoElement>;
  @ViewChild('screenVideo') screenVideoRef!: ElementRef<HTMLVideoElement>;

  eventId!: number;
  eventTitle = '';
  eventClubId: number | null = null;
  activeTab: Tab = 'chat';
  loading = true;
  isModerator = false;
  showVideo = false;

  myRank = '';
  moderatorTransferMessage = '';
  isGhost = false;
  returnTo: string | null = null;

  // Club sponsors
  clubSponsors: Sponsor[] = [];

  // Video/Audio
  localStream: MediaStream | null = null;
  screenStream: MediaStream | null = null;
  cameraStream: MediaStream | null = null;
  remoteScreenSharerId: number | null = null;
  participants: RemoteParticipant[] = [];
  connectedUsers: import('../../../core/services/live-session.service').ConnectedUser[] = [];
  showParticipants = false;

  currentUserId!: number;
  currentUserName!: string;
  myProfilePhoto: string | null = null;

  getPhotoUrl(photo: string | null | undefined): string | null {
    if (!photo) return null;
    if (photo.startsWith('http')) return photo;
    return `http://localhost:8081${photo}`;
  }

  // Chat
  messages: ChatMessage[] = [];
  chatInput = '';
  targetLang = '';
  private shouldScrollChat = false;
  unreadChatCount = 0;

  // Poll
  poll: Poll | null = null;
  newPollQuestion = '';
  newPollOptions = ['', ''];
  newPollMultiple = false;
  showCreatePoll = false;
  hasNewPoll = false;

  // Q&A
  questions: Question[] = [];
  questionInput = '';
  questionAnonymous = false;
  unreadQaCount = 0;

  // Hand raise
  handQueue: HandRaise[] = [];
  myHandRaised = false;

  // Reactions
  floatingReactions: FloatingReaction[] = [];
  private reactionCounter = 0;
  showReactionPicker = false;
  activeReactionCategory = 0;

  reactionCategories = [
    {
      label: 'Applause',
      icon: '👏',
      emojis: ['👏', '🙌', '👐', '🤲', '🫶'],
      sound: 'applause'
    },
    {
      label: 'Love',
      icon: '❤️',
      emojis: ['❤️', '🥰', '😍', '💕', '💖', '💯'],
      sound: 'pop'
    },
    {
      label: 'Funny',
      icon: '😂',
      emojis: ['😂', '🤣', '😆', '😹', '🤭'],
      sound: 'boing'
    },
    {
      label: 'Hype',
      icon: '🔥',
      emojis: ['🔥', '🎉', '🎊', '⚡', '💥', '🚀'],
      sound: 'whoosh'
    },
    {
      label: 'Agree',
      icon: '👍',
      emojis: ['👍', '✅', '💪', '🤝', '👌', '✨'],
      sound: 'ding'
    },
    {
      label: 'Surprise',
      icon: '😮',
      emojis: ['😮', '😱', '🤯', '😲', '🫢'],
      sound: 'gasp'
    }
  ];

  // Quick bar = first emoji of each category
  get reactionEmojis(): string[] {
    return this.reactionCategories.map(c => c.emojis[0]);
  }

  private audioCtx: AudioContext | null = null;

  private getAudioCtx(): AudioContext {
    if (!this.audioCtx) this.audioCtx = new AudioContext();
    return this.audioCtx;
  }

  playReactionSound(soundType: string): void {
    try {
      const ctx = this.getAudioCtx();
      if (ctx.state === 'suspended') ctx.resume();
      switch (soundType) {
        case 'applause': this.playApplause(ctx); break;
        case 'pop':      this.playPop(ctx);      break;
        case 'boing':    this.playBoing(ctx);     break;
        case 'whoosh':   this.playWhoosh(ctx);    break;
        case 'ding':     this.playDing(ctx);      break;
        case 'gasp':     this.playGasp(ctx);      break;
      }
    } catch { /* audio blocked */ }
  }

  // ── Shared reverb convolution ─────────────────────────────────
  private createReverb(ctx: AudioContext, duration = 0.4, decay = 2): ConvolverNode {
    const len = ctx.sampleRate * duration;
    const buf = ctx.createBuffer(2, len, ctx.sampleRate);
    for (let c = 0; c < 2; c++) {
      const d = buf.getChannelData(c);
      for (let i = 0; i < len; i++)
        d[i] = (Math.random() * 2 - 1) * Math.pow(1 - i / len, decay);
    }
    const conv = ctx.createConvolver();
    conv.buffer = buf;
    return conv;
  }

  // ── APPLAUSE — crowd clapping with room reverb ────────────────
  private playApplause(ctx: AudioContext): void {
    const master = ctx.createGain();
    master.gain.setValueAtTime(0, ctx.currentTime);
    master.gain.linearRampToValueAtTime(0.55, ctx.currentTime + 0.15);
    master.gain.setValueAtTime(0.55, ctx.currentTime + 0.7);
    master.gain.linearRampToValueAtTime(0, ctx.currentTime + 1.2);

    const reverb = this.createReverb(ctx, 0.8, 3);
    const reverbGain = ctx.createGain();
    reverbGain.gain.value = 0.35;

    // Multiple noise layers at different frequencies = crowd texture
    [800, 1600, 3200, 6400].forEach((freq, idx) => {
      const buf = ctx.createBuffer(1, ctx.sampleRate * 1.2, ctx.sampleRate);
      const data = buf.getChannelData(0);
      // Rhythmic amplitude modulation = clapping rhythm
      for (let i = 0; i < data.length; i++) {
        const t = i / ctx.sampleRate;
        const rhythm = Math.abs(Math.sin(Math.PI * t * 4.5)) * 0.7 + 0.3;
        data[i] = (Math.random() * 2 - 1) * rhythm;
      }
      const src = ctx.createBufferSource();
      src.buffer = buf;
      const filter = ctx.createBiquadFilter();
      filter.type = 'bandpass';
      filter.frequency.value = freq;
      filter.Q.value = 0.8;
      const layerGain = ctx.createGain();
      layerGain.gain.value = 0.25 - idx * 0.04;
      src.connect(filter);
      filter.connect(layerGain);
      layerGain.connect(master);
      layerGain.connect(reverb);
      src.start(ctx.currentTime);
    });

    reverb.connect(reverbGain);
    reverbGain.connect(master);
    master.connect(ctx.destination);
  }

  // ── POP — realistic bubble/kiss pop ──────────────────────────
  private playPop(ctx: AudioContext): void {
    const master = ctx.createGain();
    master.connect(ctx.destination);

    // Body: pitched noise burst
    const buf = ctx.createBuffer(1, ctx.sampleRate * 0.05, ctx.sampleRate);
    const data = buf.getChannelData(0);
    for (let i = 0; i < data.length; i++) {
      const env = Math.pow(1 - i / data.length, 3);
      data[i] = (Math.random() * 2 - 1) * env;
    }
    const src = ctx.createBufferSource();
    src.buffer = buf;
    const bodyFilter = ctx.createBiquadFilter();
    bodyFilter.type = 'bandpass';
    bodyFilter.frequency.value = 900;
    bodyFilter.Q.value = 3;
    const bodyGain = ctx.createGain();
    bodyGain.gain.value = 0.6;
    src.connect(bodyFilter); bodyFilter.connect(bodyGain); bodyGain.connect(master);
    src.start(ctx.currentTime);

    // Tone: descending pitch
    const osc = ctx.createOscillator();
    osc.type = 'sine';
    osc.frequency.setValueAtTime(1200, ctx.currentTime);
    osc.frequency.exponentialRampToValueAtTime(180, ctx.currentTime + 0.12);
    const oscGain = ctx.createGain();
    oscGain.gain.setValueAtTime(0.45, ctx.currentTime);
    oscGain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.12);
    osc.connect(oscGain); oscGain.connect(master);
    osc.start(); osc.stop(ctx.currentTime + 0.12);

    // Click transient
    const clickBuf = ctx.createBuffer(1, 256, ctx.sampleRate);
    const cd = clickBuf.getChannelData(0);
    for (let i = 0; i < 256; i++) cd[i] = (Math.random() * 2 - 1) * Math.pow(1 - i / 256, 8);
    const click = ctx.createBufferSource();
    click.buffer = clickBuf;
    const clickGain = ctx.createGain();
    clickGain.gain.value = 0.8;
    click.connect(clickGain); clickGain.connect(master);
    click.start(ctx.currentTime);
  }

  // ── BOING — cartoon spring with FM synthesis ──────────────────
  private playBoing(ctx: AudioContext): void {
    const master = ctx.createGain();
    master.gain.setValueAtTime(0.5, ctx.currentTime);
    master.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.7);
    master.connect(ctx.destination);

    // FM carrier
    const carrier = ctx.createOscillator();
    carrier.type = 'sine';
    carrier.frequency.setValueAtTime(80, ctx.currentTime);
    carrier.frequency.exponentialRampToValueAtTime(900, ctx.currentTime + 0.08);
    carrier.frequency.exponentialRampToValueAtTime(220, ctx.currentTime + 0.7);

    // FM modulator for metallic texture
    const modulator = ctx.createOscillator();
    modulator.type = 'sine';
    modulator.frequency.setValueAtTime(60, ctx.currentTime);
    modulator.frequency.exponentialRampToValueAtTime(700, ctx.currentTime + 0.08);
    modulator.frequency.exponentialRampToValueAtTime(180, ctx.currentTime + 0.7);
    const modGain = ctx.createGain();
    modGain.gain.setValueAtTime(300, ctx.currentTime);
    modGain.gain.exponentialRampToValueAtTime(10, ctx.currentTime + 0.7);
    modulator.connect(modGain);
    modGain.connect(carrier.frequency);

    carrier.connect(master);
    carrier.start(); carrier.stop(ctx.currentTime + 0.7);
    modulator.start(); modulator.stop(ctx.currentTime + 0.7);

    // Harmonic overtone
    const harm = ctx.createOscillator();
    harm.type = 'triangle';
    harm.frequency.setValueAtTime(160, ctx.currentTime);
    harm.frequency.exponentialRampToValueAtTime(1800, ctx.currentTime + 0.08);
    harm.frequency.exponentialRampToValueAtTime(440, ctx.currentTime + 0.7);
    const harmGain = ctx.createGain();
    harmGain.gain.setValueAtTime(0.15, ctx.currentTime);
    harmGain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.5);
    harm.connect(harmGain); harmGain.connect(master);
    harm.start(); harm.stop(ctx.currentTime + 0.5);
  }

  // ── WHOOSH — fire/rocket with filtered noise sweep ────────────
  private playWhoosh(ctx: AudioContext): void {
    const master = ctx.createGain();
    master.gain.setValueAtTime(0, ctx.currentTime);
    master.gain.linearRampToValueAtTime(0.5, ctx.currentTime + 0.05);
    master.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.55);
    master.connect(ctx.destination);

    const reverb = this.createReverb(ctx, 0.3, 4);
    const rvGain = ctx.createGain(); rvGain.gain.value = 0.2;
    reverb.connect(rvGain); rvGain.connect(ctx.destination);

    // Two noise layers: low rumble + high hiss
    [[200, 600, 0.4], [1500, 8000, 0.25]].forEach(([fStart, fEnd, vol]) => {
      const buf = ctx.createBuffer(1, ctx.sampleRate * 0.55, ctx.sampleRate);
      const d = buf.getChannelData(0);
      for (let i = 0; i < d.length; i++) d[i] = Math.random() * 2 - 1;
      const src = ctx.createBufferSource();
      src.buffer = buf;
      const filter = ctx.createBiquadFilter();
      filter.type = 'bandpass';
      filter.frequency.setValueAtTime(fStart, ctx.currentTime);
      filter.frequency.exponentialRampToValueAtTime(fEnd, ctx.currentTime + 0.3);
      filter.frequency.exponentialRampToValueAtTime(fStart * 0.5, ctx.currentTime + 0.55);
      filter.Q.value = 1.5;
      const g = ctx.createGain(); g.gain.value = vol as number;
      src.connect(filter); filter.connect(g);
      g.connect(master); g.connect(reverb);
      src.start(ctx.currentTime);
    });

    // Pitch tone for "rocket" feel
    const osc = ctx.createOscillator();
    osc.type = 'sawtooth';
    osc.frequency.setValueAtTime(60, ctx.currentTime);
    osc.frequency.exponentialRampToValueAtTime(400, ctx.currentTime + 0.2);
    osc.frequency.exponentialRampToValueAtTime(80, ctx.currentTime + 0.55);
    const oscGain = ctx.createGain();
    oscGain.gain.setValueAtTime(0.12, ctx.currentTime);
    oscGain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.55);
    const oscFilter = ctx.createBiquadFilter();
    oscFilter.type = 'lowpass'; oscFilter.frequency.value = 400;
    osc.connect(oscFilter); oscFilter.connect(oscGain); oscGain.connect(master);
    osc.start(); osc.stop(ctx.currentTime + 0.55);
  }

  // ── DING — real bell with exponential decay + harmonics ───────
  private playDing(ctx: AudioContext): void {
    const reverb = this.createReverb(ctx, 1.2, 4);
    const rvGain = ctx.createGain(); rvGain.gain.value = 0.3;
    reverb.connect(rvGain); rvGain.connect(ctx.destination);

    // Bell partials: fundamental + inharmonic overtones (real bell ratios)
    const partials = [
      { freq: 880,  gain: 0.5,  decay: 1.8 },
      { freq: 1100, gain: 0.3,  decay: 1.2 },
      { freq: 1760, gain: 0.2,  decay: 0.9 },
      { freq: 2200, gain: 0.12, decay: 0.6 },
      { freq: 3080, gain: 0.07, decay: 0.4 },
      { freq: 4400, gain: 0.04, decay: 0.25 },
    ];

    partials.forEach(({ freq, gain, decay }) => {
      const osc = ctx.createOscillator();
      osc.type = 'sine';
      osc.frequency.value = freq;
      const g = ctx.createGain();
      // Sharp attack, long exponential decay = bell
      g.gain.setValueAtTime(gain, ctx.currentTime);
      g.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + decay);
      osc.connect(g);
      g.connect(ctx.destination);
      g.connect(reverb);
      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + decay);
    });

    // Strike noise (mallet hit)
    const strikeBuf = ctx.createBuffer(1, ctx.sampleRate * 0.015, ctx.sampleRate);
    const sd = strikeBuf.getChannelData(0);
    for (let i = 0; i < sd.length; i++) sd[i] = (Math.random() * 2 - 1) * Math.pow(1 - i / sd.length, 2);
    const strike = ctx.createBufferSource();
    strike.buffer = strikeBuf;
    const strikeFilter = ctx.createBiquadFilter();
    strikeFilter.type = 'highpass'; strikeFilter.frequency.value = 2000;
    const strikeGain = ctx.createGain(); strikeGain.gain.value = 0.4;
    strike.connect(strikeFilter); strikeFilter.connect(strikeGain); strikeGain.connect(ctx.destination);
    strike.start(ctx.currentTime);
  }

  // ── GASP — crowd "wow" with formant synthesis ─────────────────
  private playGasp(ctx: AudioContext): void {
    const master = ctx.createGain();
    master.gain.setValueAtTime(0, ctx.currentTime);
    master.gain.linearRampToValueAtTime(0.4, ctx.currentTime + 0.08);
    master.gain.setValueAtTime(0.4, ctx.currentTime + 0.25);
    master.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.6);
    master.connect(ctx.destination);

    // Voiced source: buzz oscillator
    const buzz = ctx.createOscillator();
    buzz.type = 'sawtooth';
    buzz.frequency.setValueAtTime(130, ctx.currentTime);
    buzz.frequency.linearRampToValueAtTime(180, ctx.currentTime + 0.15);
    buzz.frequency.linearRampToValueAtTime(140, ctx.currentTime + 0.6);

    // Formant filters simulating "ooh" → "aah" vowel transition
    const formants = [
      { f: 400,  q: 8,  g: 0.8 },   // F1
      { f: 1000, q: 10, g: 0.5 },   // F2
      { f: 2500, q: 12, g: 0.2 },   // F3
    ];
    formants.forEach(({ f, q, g }) => {
      const filter = ctx.createBiquadFilter();
      filter.type = 'bandpass';
      filter.frequency.setValueAtTime(f * 0.7, ctx.currentTime);
      filter.frequency.linearRampToValueAtTime(f, ctx.currentTime + 0.2);
      filter.Q.value = q;
      const fGain = ctx.createGain(); fGain.gain.value = g;
      buzz.connect(filter); filter.connect(fGain); fGain.connect(master);
    });

    // Breath noise layer
    const noiseBuf = ctx.createBuffer(1, ctx.sampleRate * 0.6, ctx.sampleRate);
    const nd = noiseBuf.getChannelData(0);
    for (let i = 0; i < nd.length; i++) nd[i] = Math.random() * 2 - 1;
    const noiseSrc = ctx.createBufferSource();
    noiseSrc.buffer = noiseBuf;
    const noiseFilter = ctx.createBiquadFilter();
    noiseFilter.type = 'bandpass'; noiseFilter.frequency.value = 3000; noiseFilter.Q.value = 2;
    const noiseGain = ctx.createGain(); noiseGain.gain.value = 0.06;
    noiseSrc.connect(noiseFilter); noiseFilter.connect(noiseGain); noiseGain.connect(master);
    noiseSrc.start(ctx.currentTime);

    buzz.connect(ctx.createGain()); // prevent direct output
    buzz.start(ctx.currentTime); buzz.stop(ctx.currentTime + 0.6);
  }

  tabs = [
    { id: 'chat',       icon: '💬', label: 'Chat' },
    { id: 'qa',         icon: '❓', label: 'Q&A' },
    { id: 'poll',       icon: '📊', label: 'Poll' },
    { id: 'hands',      icon: '✋', label: 'Hands' },
    { id: 'whiteboard', icon: '🎨', label: 'Board' }
  ];

  // Grid layout (Google Meet style)
  // Roles that are invisible in the session (ghost observers)
  private readonly GHOST_ROLES = ['ACADEMIC_OFFICE_AFFAIR', 'SPONSOR'];

  // Visible users in the grid (excludes ghost roles)
  get visibleUsers() {
    return this.connectedUsers.filter(u => !this.GHOST_ROLES.includes(u.systemRole ?? ''));
  }

  // Ghost observers — only moderator can see them in the participants list
  get ghostObservers() {
    return this.connectedUsers.filter(u => this.GHOST_ROLES.includes(u.systemRole ?? ''));
  }

  get gridClass(): string {
    const total = this.participants.length + this.visibleUsers.length + 1;
    if (total === 1) return 'grid-cols-1';
    if (total <= 4) return 'grid-cols-2';
    if (total <= 6) return 'grid-cols-3';
    return 'grid-cols-4';
  }

  isInParticipants(userId: number): boolean {
    return this.participants.some(p => p.userId === userId);
  }

  // Whiteboard
  private drawing = false;
  private lastX = 0;
  private lastY = 0;
  wbColor = '#2D5757';
  wbStroke = 3;
  wbTool: 'draw' | 'erase' = 'draw';

  // Track if we need to reassign screen share video after view update
  private pendingScreenShareAssign = false;

  private subs = new Subscription();
  private hasLeft = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    public readonly liveService: LiveSessionService,
    public readonly webrtc: WebRTCService,
    private readonly authService: AuthService,
    private readonly eventService: EventService,
    private readonly memberService: MemberService,
    private readonly sponsorService: SponsorService
  ) {}

  async ngOnInit() {
    this.eventId = Number(this.route.snapshot.paramMap.get('id'));
    this.isGhost = this.route.snapshot.queryParamMap.get('ghost') === 'true';
    this.returnTo = this.route.snapshot.queryParamMap.get('returnTo') || null;
    const isGhost = this.isGhost;
    const user = this.authService.currentUserValue;
    this.currentUserId = user?.id!;
    this.currentUserName = `${user?.firstName || ''} ${user?.lastName || ''}`.trim();
    this.myProfilePhoto = user?.profilePhoto ? this.getPhotoUrl(user.profilePhoto) : null;

    // First determine rank, then connect with rank included in JOIN message
    this.eventService.getEventById(this.eventId).subscribe(async e => {
      this.eventTitle = e.title;
      this.eventClubId = e.clubId ?? null;

      // Load club sponsors if event belongs to a club
      if (e.clubId) {
        this.sponsorService.getAllSponsors().subscribe({
          next: (allSponsors) => {
            // Find users who have a club sponsorship for this club
            const clubSponsorUserIds = new Set(allSponsors
              .filter(s => s.clubId === e.clubId && s.status === 'APPROVED')
              .map(s => s.userId)
              .filter((id): id is number => !!id));

            // Get their platform sponsor entry (no clubId) — that's where logo/name is stored
            const platformSponsors = allSponsors.filter(s =>
              !s.clubId &&
              s.status === 'APPROVED' &&
              s.userId != null &&
              clubSponsorUserIds.has(s.userId as number)
            );

            // Fallback: if no platform entry found, use the club entries directly
            this.clubSponsors = platformSponsors.length > 0
              ? platformSponsors
              : allSponsors.filter(s => s.clubId === e.clubId && s.status === 'APPROVED');
          },
          error: () => {}
        });
      }

      if (!isGhost) {
        // Only CREATOR and EVENT_MANAGER can moderate
        if (e.creatorId === this.currentUserId) {
          this.myRank = 'CREATOR';
          this.isModerator = true;
        } else if (e.clubId) {
          const clubId = e.clubId;
          await new Promise<void>(resolve => {
            this.memberService.getUserMembershipInClub(clubId, this.currentUserId).subscribe({
              next: (membership) => {
                if (membership?.rank === 'EVENT_MANAGER') {
                  this.myRank = 'EVENT_MANAGER';
                  this.isModerator = true;
                }
                resolve();
              },
              error: () => resolve()
            });
          });
        }
      }

      // Ghost mode: connect without broadcasting presence (invisible observer)
      // Moderators broadcast as MODERATOR, ghost users (ACADEMIC_MANAGER, SPONSOR) broadcast their real role
      const systemRole = this.isModerator ? 'MODERATOR' : (user?.role ?? 'STUDENT');
      const profilePhoto = this.myProfilePhoto ?? undefined;
      await this.liveService.connect(this.eventId, this.currentUserId, this.currentUserName, systemRole, profilePhoto);
      this.loading = false;

      // Subscribe WebRTC signaling
      this.webrtc.subscribeSignaling(
        this.liveService.stompClient!,
        this.eventId,
        this.currentUserId,
        this.currentUserName
      );
    }); // end getEventById subscribe

    // Setup all reactive subscriptions
    this.subs.add(this.liveService.messages$.subscribe(m => {
      const prevCount = this.messages.length;
      this.messages = m.filter(msg => !msg.isSystem);
      this.shouldScrollChat = true;
      if (this.activeTab !== 'chat' && this.messages.length > prevCount) {
        this.unreadChatCount += this.messages.length - prevCount;
      }
    }));
    this.subs.add(this.liveService.poll$.subscribe(p => {
      const hadPoll = !!this.poll;
      this.poll = p;
      // Show dot if new poll created and not on poll tab
      if (p && !hadPoll && this.activeTab !== 'poll') {
        this.hasNewPoll = true;
      }
    }));
    this.subs.add(this.liveService.questions$.subscribe(q => {
      const prevCount = this.questions.length;
      this.questions = q;
      // Increment unread Q&A count when moderator posts a new question and user is not on qa tab
      if (q.length > prevCount && this.activeTab !== 'qa') {
        this.unreadQaCount += q.length - prevCount;
      }
    }));
    this.subs.add(this.liveService.handQueue$.subscribe(q => {
      this.handQueue = q;
      this.myHandRaised = q.some(h => h.userId === this.currentUserId);
    }));
    this.subs.add(this.liveService.reactions$.subscribe(r => {
      if (r) {
        this.spawnReaction(r.emoji);
        // Play sound for incoming reactions from others
        if (r.userId !== this.currentUserId) {
          const cat = this.reactionCategories.find(c => c.emojis.includes(r.emoji));
          if (cat) this.playReactionSound(cat.sound);
        }
      }
    }));
    this.subs.add(this.liveService.whiteboard$.subscribe(e => {
      if (!e) return;
      if (e.type === 'TAB_SWITCH' && e.text) {
        this.activeTab = e.text as Tab;
      } else if (e.type === 'SCREEN_SHARE') {
        // e.text = userId of sharer, or '' if stopped
        const sharerId = e.text ? Number(e.text) : null;
        this.webrtc.remoteScreenSharerId$.next(sharerId);
      } else {
        this.applyRemoteStroke(e);
      }
    }));

    // WebRTC streams
    this.subs.add(this.webrtc.localStream$.subscribe(s => {
      this.localStream = s;
      if (s && !this.webrtc.screenSharing) {
        this.cameraStream = s;
      }
      if (s && this.localVideoRef?.nativeElement) {
        this.localVideoRef.nativeElement.srcObject = s;
      }
    }));
    this.subs.add(this.webrtc.screenStream$.subscribe(s => {
      this.screenStream = s;
      // Assign screen stream to the screen video element
      setTimeout(() => {
        if (s && this.screenVideoRef?.nativeElement) {
          this.screenVideoRef.nativeElement.srcObject = s;
          this.screenVideoRef.nativeElement.play().catch(() => {});
        }
        // Restore camera to local video when screen share stops
        if (!s && this.cameraStream && this.localVideoRef?.nativeElement) {
          this.localVideoRef.nativeElement.srcObject = this.cameraStream;
        }
      }, 100);
    }));
    this.subs.add(this.webrtc.remoteScreenSharerId$.subscribe(id => {
      this.remoteScreenSharerId = id;
      if (id) {
        // Mark that we need to assign the stream once the DOM re-renders
        this.pendingScreenShareAssign = true;
      }
    }));
    this.subs.add(this.webrtc.participants$.subscribe(p => {
      this.participants = p;
      if (this.remoteScreenSharerId) {
        this.pendingScreenShareAssign = true;
      }
    }));
    this.subs.add(this.liveService.connectedUsers$.subscribe(u => {
      this.connectedUsers = u;
    }));
  }

  ngAfterViewChecked() {
    if (this.shouldScrollChat) {
      this.scrollChatToBottom();
      this.shouldScrollChat = false;
    }
    if (this.pendingScreenShareAssign && this.remoteScreenSharerId) {
      const sharer = this.participants.find(x => x.userId === this.remoteScreenSharerId);
      if (sharer?.stream) {
        const el = document.getElementById(`screen-share-video`) as HTMLVideoElement | null;
        if (el && el.srcObject !== sharer.stream) {
          el.srcObject = sharer.stream;
          el.play().catch(() => {});
          this.pendingScreenShareAssign = false;
        }
      }
    }
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
    this.webrtc.leave();
    // Only disconnect if leave() wasn't already called (avoids double disconnect)
    if (!this.hasLeft) {
      if (this.isGhost) {
        this.liveService.disconnect();
      } else {
        this.liveService.disconnect(this.currentUserId, this.currentUserName);
      }
    }
  }

  // ── CHAT ──────────────────────────────────────────────────────

  sendMessage() {
    if (!this.chatInput.trim()) return;
    this.liveService.sendMessage({
      senderId: this.currentUserId,
      senderName: this.currentUserName,
      content: this.chatInput.trim(),
      targetLang: this.targetLang || undefined
    });
    this.chatInput = '';
  }

  moderateMessage(msg: ChatMessage) {
    if (!this.isModerator || !msg.id) return;
    this.liveService.moderateMessage(this.eventId, msg.id).subscribe();
  }

  private scrollChatToBottom() {
    try {
      this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
    } catch {}
  }

  // ── POLL ──────────────────────────────────────────────────────

  addPollOption() { this.newPollOptions.push(''); }
  removePollOption(i: number) {
    if (this.newPollOptions.length > 2) this.newPollOptions.splice(i, 1);
  }

  submitCreatePoll() {
    const opts = this.newPollOptions.filter(o => o.trim());
    if (!this.newPollQuestion.trim() || opts.length < 2) return;
    this.liveService.createPoll(this.eventId, this.newPollQuestion, opts, this.newPollMultiple)
      .subscribe(() => {
        this.showCreatePoll = false;
        this.newPollQuestion = '';
        this.newPollOptions = ['', ''];
      });
  }

  vote(optionId: number) {
    if (!this.poll) return;
    this.liveService.vote(this.poll.id, optionId, this.currentUserId);
  }

  getPollPercent(option: { voteCount: number }): number {
    if (!this.poll) return 0;
    const total = this.poll.options.reduce((s, o) => s + o.voteCount, 0);
    return total === 0 ? 0 : Math.round((option.voteCount / total) * 100);
  }

  getTotalVotes(): number {
    return this.poll?.options.reduce((s, o) => s + o.voteCount, 0) ?? 0;
  }

  // ── Q&A ───────────────────────────────────────────────────────

  // Moderator posts a question; participants reply
  qaNewQuestion = '';           // moderator input
  qaReplyInput = '';            // participant reply input
  qaActiveQuestionId: number | null = null;  // which question participant is replying to

  submitModeratorQuestion() {
    if (!this.qaNewQuestion.trim()) return;
    this.liveService.askQuestion(
      this.currentUserId, this.currentUserName,
      this.qaNewQuestion.trim(), false
    );
    this.qaNewQuestion = '';
  }

  submitReply(questionId: number) {
    if (!this.qaReplyInput.trim()) return;
    // Encode reply as "[R:questionId] text" so we can distinguish replies from questions
    this.liveService.askQuestion(
      this.currentUserId, this.currentUserName,
      `[R:${questionId}] ${this.qaReplyInput.trim()}`, false
    );
    this.qaReplyInput = '';
    this.qaActiveQuestionId = null;
  }

  isReply(q: Question): boolean {
    return q.text.startsWith('[R:');
  }

  getReplyTargetId(q: Question): number | null {
    const m = q.text.match(/^\[R:(\d+)\]/);
    return m ? Number(m[1]) : null;
  }

  getReplyText(q: Question): string {
    return q.text.replace(/^\[R:\d+\]\s*/, '');
  }

  getRepliesFor(questionId: number): Question[] {
    return this.questions.filter(q => this.getReplyTargetId(q) === questionId);
  }

  get rootQuestions(): Question[] {
    return this.questions
      .filter(q => !this.isReply(q))
      .sort((a, b) => {
        // Active (not answered) first, then by creation order
        if (a.answered !== b.answered) return a.answered ? 1 : -1;
        return a.id - b.id;
      });
  }

  markAnswered(q: Question) {
    this.liveService.markAnswered(q.id);
  }

  // ── Q&A HELPERS (kept for compatibility) ──────────────────────

  get pendingQuestions(): Question[] {
    return this.rootQuestions.filter(q => !q.answered);
  }

  get answeredQuestions(): Question[] {
    return this.rootQuestions.filter(q => q.answered);
  }

  isMyQuestion(q: Question): boolean {
    return q.authorId === this.currentUserId;
  }

  // old submitQuestion kept for safety — now unused
  submitQuestion() {
    if (!this.questionInput.trim()) return;
    this.liveService.askQuestion(this.currentUserId, this.currentUserName,
      this.questionInput.trim(), this.questionAnonymous);
    this.questionInput = '';
  }

  upvote(q: Question) {
    this.liveService.upvoteQuestion(q.id, this.currentUserId);
  }

  toggleHand() {
    if (this.myHandRaised) {
      this.liveService.dismissHand(this.currentUserId);
    } else {
      this.liveService.raiseHand(this.currentUserId, this.currentUserName);
    }
  }

  dismissHand(userId: number) {
    this.liveService.dismissHand(userId);
  }

  // ── REACTIONS ─────────────────────────────────────────────────

  sendReaction(emoji: string) {
    // Find which category this emoji belongs to and play its sound
    const cat = this.reactionCategories.find(c => c.emojis.includes(emoji));
    if (cat) this.playReactionSound(cat.sound);
    this.liveService.sendReaction(this.currentUserId, this.currentUserName, emoji);
    this.showReactionPicker = false;
  }

  private spawnReaction(emoji: string) {
    const id = this.reactionCounter++;
    const x = 10 + Math.random() * 80;
    this.floatingReactions.push({ id, emoji, x });
    setTimeout(() => {
      this.floatingReactions = this.floatingReactions.filter(r => r.id !== id);
    }, 2500);
  }

  getSharerName(): string {
    if (!this.remoteScreenSharerId) return '';
    return this.participants.find(p => p.userId === this.remoteScreenSharerId)?.userName ?? 'Moderator';
  }

  // ── TAB SWITCH ────────────────────────────────────────────────

  switchTab(tabId: string): void {
    this.activeTab = tabId as Tab;
    // Reset unread badges when switching to the tab
    if (tabId === 'chat') this.unreadChatCount = 0;
    if (tabId === 'poll') this.hasNewPoll = false;
    if (tabId === 'qa') this.unreadQaCount = 0;
    // Moderator switching to whiteboard forces all participants to follow
    if (this.isModerator && tabId === 'whiteboard') {
      this.liveService.broadcastTabSwitch('whiteboard');
    }
  }

  // ── WHITEBOARD ────────────────────────────────────────────────

  onCanvasMouseDown(e: MouseEvent) {
    this.drawing = true;
    const rect = this.canvasRef.nativeElement.getBoundingClientRect();
    this.lastX = e.clientX - rect.left;
    this.lastY = e.clientY - rect.top;
  }

  onCanvasMouseMove(e: MouseEvent) {
    if (!this.drawing) return;
    const rect = this.canvasRef.nativeElement.getBoundingClientRect();
    const x2 = e.clientX - rect.left;
    const y2 = e.clientY - rect.top;

    const event: WhiteboardEvent = {
      userId: this.currentUserId,
      type: this.wbTool === 'erase' ? 'ERASE' : 'DRAW',
      x: this.lastX, y: this.lastY, x2, y2,
      color: this.wbTool === 'erase' ? '#ffffff' : this.wbColor,
      strokeWidth: this.wbTool === 'erase' ? 20 : this.wbStroke
    };

    this.drawStroke(event);
    this.liveService.sendWhiteboardEvent(event);
    this.lastX = x2;
    this.lastY = y2;
  }

  onCanvasMouseUp() { this.drawing = false; }

  clearWhiteboard() {
    const canvas = this.canvasRef.nativeElement;
    const ctx = canvas.getContext('2d')!;
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    this.liveService.sendWhiteboardEvent({
      userId: this.currentUserId, type: 'CLEAR', x: 0, y: 0
    });
  }

  private drawStroke(e: WhiteboardEvent) {
    const ctx = this.canvasRef?.nativeElement.getContext('2d');
    if (!ctx) return;
    ctx.beginPath();
    ctx.moveTo(e.x, e.y);
    ctx.lineTo(e.x2 ?? e.x, e.y2 ?? e.y);
    ctx.strokeStyle = e.color || '#2D5757';
    ctx.lineWidth = e.strokeWidth || 3;
    ctx.lineCap = 'round';
    ctx.stroke();
  }

  private applyRemoteStroke(e: WhiteboardEvent) {
    if (e.userId === this.currentUserId) return; // already drawn locally
    if (e.type === 'CLEAR') {
      const canvas = this.canvasRef?.nativeElement;
      if (canvas) canvas.getContext('2d')?.clearRect(0, 0, canvas.width, canvas.height);
    } else {
      this.drawStroke(e);
    }
  }

  // ── VIDEO ─────────────────────────────────────────────────────

  async toggleScreenShare(): Promise<void> {
    if (this.webrtc.screenSharing) {
      await this.webrtc.stopScreenShare();
      this.liveService.broadcastScreenShare(false, this.currentUserId);
    } else {
      // Always ensure we have a peer connection with all participants before sharing
      if (!this.webrtc.localStream$.value) {
        await this.webrtc.joinScreenShareOnly(
          this.liveService.stompClient!,
          this.eventId,
          this.currentUserId,
          this.currentUserName,
          this.myProfilePhoto ?? undefined
        );
      }
      await this.webrtc.startScreenShare();
      if (this.webrtc.screenSharing) {
        this.liveService.broadcastScreenShare(true, this.currentUserId);
      }
    }
  }

  async toggleVideoPanel(): Promise<void> {
    this.showVideo = !this.showVideo;
    if (this.showVideo && !this.localStream) {
      await this.webrtc.join(
        this.liveService.stompClient!,
        this.eventId,
        this.currentUserId,
        this.currentUserName,
        this.myProfilePhoto ?? undefined
      );
    }
  }

  setRemoteVideo(el: HTMLVideoElement, stream: MediaStream | null): void {
    if (el && stream) el.srcObject = stream;
  }

  leave() {
    if (this.hasLeft) return;
    this.hasLeft = true;

    // Cleanup: disconnect from WebRTC and WebSocket
    try {
      this.webrtc.leave();
    } catch (e) {
      console.error('[LiveSession] Error leaving WebRTC:', e);
    }

    try {
      if (this.isGhost) {
        this.liveService.disconnect();
      } else {
        this.liveService.disconnect(this.currentUserId, this.currentUserName);
      }
    } catch (e) {
      console.error('[LiveSession] Error disconnecting from live service:', e);
    }

    // Determine redirect destination
    const user = this.authService.currentUserValue;
    const role = user?.role ?? 'STUDENT';
    let destination = '/user-panel/events';

    if (this.isGhost) {
      // Ghost mode observers
      if (role === 'SPONSOR') {
        destination = this.eventClubId 
          ? `/sponsor-panel/clubs/${this.eventClubId}` 
          : '/sponsor-panel/my-impact';
      } else if (role === 'ACADEMIC_OFFICE_AFFAIR') {
        destination = this.returnTo || '/dashboard/events/manage';
      } else {
        destination = this.returnTo || '/dashboard';
      }
    } else {
      // Normal participants and moderators
      destination = `/user-panel/events/${this.eventId}`;
    }

    console.log('[LiveSession] Leaving session, redirecting to:', destination);
    
    // Force full page reload to ensure clean state
    setTimeout(() => {
      globalThis.location.href = destination;
    }, 100);
  }


}
