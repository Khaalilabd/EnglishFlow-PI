import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { switchMap, map } from 'rxjs/operators';
import { of } from 'rxjs';
import { EventService, Event } from '../../../core/services/event.service';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { MemberService } from '../../../core/services/member.service';
import { EventFeedbackService, EventFeedback, EventFeedbackStats } from '../../../core/services/event-feedback.service';
import { NotificationService } from '../../../core/services/notification.service';
import { StarRatingComponent } from '../../../shared/components/star-rating/star-rating.component';
import { LocationMapComponent } from '../../../shared/components/location-map/location-map.component';

@Component({
  selector: 'app-event-details',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, StarRatingComponent, LocationMapComponent],
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.scss']
})
export class EventDetailsComponent implements OnInit, OnDestroy {
  event: Event | null = null;
  loading = true;
  currentUserId: number | null = null;
  isAdmin = false;
  isAcademicManager = false;
  canViewSponsors = false;
  canViewParticipants = false;
  canJoinLive = false;
  eventStatus: 'upcoming' | 'ongoing' | 'ended' = 'upcoming'; // New permission flag
  myEventIds: Set<number> = new Set();

  // Countdown
  countdown = { days: 0, hours: 0, minutes: 0, seconds: 0 };
  private countdownInterval: any;

  // Feedback
  feedbackStats: EventFeedbackStats | null = null;
  userFeedback: EventFeedback | null = null;
  hasGivenFeedback = false;
  submittingFeedback = false;
  feedbackForm: EventFeedback = { eventId: 0, userId: 0, rating: 0, comment: '', anonymous: false };

  // Feedback comments modal
  showFeedbackCommentsModal = false;
  feedbackComments: EventFeedback[] = [];
  loadingFeedbackComments = false;

  // Active tab
  private _activeTab: 'overview' | 'participants' | 'feedbacks' = 'overview';
  
  get activeTab() {
    return this._activeTab;
  }
  
  set activeTab(value: 'overview' | 'participants' | 'feedbacks') {
    this._activeTab = value;
    if (value === 'participants' && this.event?.id && !this.loadingParticipants) {
      this.loadParticipants();
    }
  }

  // Participants
  eventParticipants: any[] = [];
  loadingParticipants = false;

  // Old modal (kept for compatibility)
  showParticipantsModal = false;

  // Gallery modal
  selectedGalleryImage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventService: EventService,
    private authService: AuthService,
    private userService: UserService,
    private memberService: MemberService,
    private feedbackService: EventFeedbackService,
    private notificationService: NotificationService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const user = this.authService.currentUserValue;
    if (user?.id) {
      this.currentUserId = user.id;
      this.isAdmin = user.role === 'ADMIN';
      this.isAcademicManager = user.role === 'ACADEMIC_OFFICE_AFFAIR' || user.role === 'ADMIN';
      this.checkSponsorViewPermission();
    }

    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadEvent(id);
  }

  ngOnDestroy() {
    if (this.countdownInterval) clearInterval(this.countdownInterval);
  }

  loadEvent(id: number) {
    this.loading = true;
    this.eventService.getEventById(id).subscribe({
      next: (event) => {
        this.event = event;
        console.log('Event loaded:', event);
        console.log('Creator ID:', event.creatorId);
        console.log('Current User ID:', this.currentUserId);
        console.log('Is Creator:', this.isCreator());
        this.loading = false;
        this.updateEventStatus();
        this.startCountdown();
        this.loadFeedbackData();
        this.loadMyEvents();
        this.checkParticipantsViewPermission();
        this.checkLiveSessionPermission();
      },
      error: () => {
        this.notificationService.error('Error', 'Event not found');
        this.router.navigate(['/user-panel/events']);
      }
    });
  }

  loadMyEvents() {
    if (!this.currentUserId) return;
    this.eventService.getUserEvents(this.currentUserId).subscribe({
      next: (participants) => {
        participants.forEach(p => this.myEventIds.add(p.eventId));
        console.log('loadMyEvents → myEventIds:', Array.from(this.myEventIds));
        this.updateEventStatus();
        // Re-check live permission after registration status is known
        this.checkLiveSessionPermission();
      },
      error: () => {}
    });
  }

  // ── Countdown ──────────────────────────────────────────────
  startCountdown() {
    if (this.countdownInterval) clearInterval(this.countdownInterval);
    this.updateCountdown();
    this.countdownInterval = setInterval(() => this.updateCountdown(), 1000);
  }

  updateCountdown() {
    if (!this.event) return;
    this.updateEventStatus();
    const start = new Date(this.event.startDate).getTime();
    const dist = start - Date.now();
    if (dist <= 0) { clearInterval(this.countdownInterval); return; }
    this.countdown = {
      days: Math.floor(dist / 86400000),
      hours: Math.floor((dist % 86400000) / 3600000),
      minutes: Math.floor((dist % 3600000) / 60000),
      seconds: Math.floor((dist % 60000) / 1000)
    };
    if (this.eventStatus === 'ongoing') {
      this.checkLiveSessionPermission();
    }
  }

  // ── Status helpers ─────────────────────────────────────────
  getStatus(): 'upcoming' | 'ongoing' | 'ended' {
    if (!this.event) return 'upcoming';
    const now = Date.now();
    const start = new Date(this.event.startDate).getTime();
    const end = this.event.endDate ? new Date(this.event.endDate).getTime() : start;
    if (now >= end) return 'ended';
    if (now >= start) return 'ongoing';
    return 'upcoming';
  }

  private updateEventStatus() {
    this.eventStatus = this.getStatus();
    this.cdr.detectChanges();
  }

  isStarted() {
    return Date.now() >= new Date(this.event?.startDate || 0).getTime();
  }
  isCreator() { return this.event?.creatorId === this.currentUserId; }
  isRegistered() { return !!this.event?.id && this.myEventIds.has(this.event.id); }
  isFull() { return (this.event?.currentParticipants || 0) >= (this.event?.maxParticipants || 0); }

  getEventIcon(type: string) {
    return { WORKSHOP: '🛠️', SEMINAR: '📚', SOCIAL: '🎉' }[type] || '📅';
  }

  formatDate(d: string) {
    return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
  }

  fillPercent() {
    if (!this.event?.maxParticipants) return 0;
    return Math.min(100, ((this.event.currentParticipants || 0) / this.event.maxParticipants) * 100);
  }

  // ── Actions ────────────────────────────────────────────────
  join() {
    if (!this.currentUserId || !this.event?.id) return;
    this.eventService.joinEvent(this.event.id, this.currentUserId).subscribe({
      next: (participant: any) => {
        if (participant?.paymentStatus === 'PAYMENT_PENDING' && participant?.id) {
          this.notificationService.info('Payment Required', 'Please complete the payment to confirm your registration.');
          this.router.navigate(['/user-panel/event-payment', participant.id]);
          return;
        }
        this.notificationService.success('Joined!', 'You have joined the event.');
        this.myEventIds.add(this.event!.id!);
        this.event!.currentParticipants = (this.event!.currentParticipants || 0) + 1;
      },
      error: (err) => this.notificationService.error('Error', err.error?.message || 'Failed to join')
    });
  }

  leave() {
    if (!this.currentUserId || !this.event?.id) return;
    if (!confirm('Leave this event?')) return;
    this.eventService.leaveEvent(this.event.id, this.currentUserId).subscribe({
      next: () => {
        this.notificationService.success('Left', 'You have left the event.');
        this.myEventIds.delete(this.event!.id!);
        this.event!.currentParticipants = Math.max(0, (this.event!.currentParticipants || 1) - 1);
      },
      error: () => this.notificationService.error('Error', 'Failed to leave event')
    });
  }

  delete() {
    if (!this.event?.id) return;
    if (!confirm('Delete this event?')) return;
    this.eventService.deleteEvent(this.event.id).subscribe({
      next: () => { this.notificationService.success('Deleted', 'Event deleted.'); this.router.navigate(['/user-panel/events']); },
      error: () => this.notificationService.error('Error', 'Failed to delete event')
    });
  }

  // ── Participants ───────────────────────────────────────────
  loadParticipants() {
    if (!this.event?.id) return;
    this.loadingParticipants = true;
    this.eventService.getEventParticipants(this.event.id).subscribe({
      next: (participants) => { 
        this.eventParticipants = participants; 
        this.loadingParticipants = false; 
      },
      error: (err) => { 
        console.error('Error loading participants:', err);
        this.loadingParticipants = false; 
      }
    });
  }

  openParticipants() {
    this.activeTab = 'participants';
  }

  // ── Gallery modal ──────────────────────────────────────────
  openImage(img: string) { this.selectedGalleryImage = img; }

  // ── Sponsor permission ─────────────────────────────────────
  checkSponsorViewPermission() {
    // Academic manager can see everything
    if (this.isAcademicManager) { this.canViewSponsors = true; return; }
    if (!this.currentUserId) return;
    this.memberService.getMembersByUser(this.currentUserId).subscribe({
      next: (members) => {
        const privileged = ['PRESIDENT', 'VICE_PRESIDENT', 'TREASURER', 'EVENT_MANAGER', 'PARTNERSHIP_MANAGER'];
        this.canViewSponsors = members.some(m => privileged.includes(m.rank));
      },
      error: () => {}
    });
  }

  // ── Participants permission ────────────────────────────────
  checkParticipantsViewPermission() {
    // Academic manager can always view participants
    if (this.isAcademicManager) { this.canViewParticipants = true; return; }

    // Creator can always view participants
    if (this.isCreator()) {
      this.canViewParticipants = true;
      return;
    }

    // If event is not linked to a club, only creator can view
    if (!this.event?.clubId || !this.currentUserId) {
      this.canViewParticipants = false;
      return;
    }

    // Check if user is a member of the club with appropriate permissions
    this.memberService.getMembersByUser(this.currentUserId).subscribe({
      next: (members) => {
        const clubMember = members.find(m => m.clubId === this.event?.clubId);
        if (!clubMember) {
          this.canViewParticipants = false;
          return;
        }

        // Roles that can view participants
        const privilegedRoles = [
          'PRESIDENT', 
          'VICE_PRESIDENT', 
          'SECRETARY', 
          'EVENT_MANAGER',
          'COMMUNICATION_MANAGER'
        ];
        
        this.canViewParticipants = privilegedRoles.includes(clubMember.rank);
      },
      error: () => {
        this.canViewParticipants = false;
      }
    });
  }

  // ── Navigation ─────────────────────────────────────────────
  goBack() {
    this.router.navigate(['/user-panel/events']);
  }

  // ── Live Session permission ────────────────────────────────
  checkLiveSessionPermission() {
    console.log('checkLiveSessionPermission → isAcademicManager:', this.isAcademicManager, 'isCreator:', this.isCreator(), 'isRegistered:', this.isRegistered(), 'myEventIds:', Array.from(this.myEventIds), 'eventId:', this.event?.id);
    // Academic manager always has access
    if (this.isAcademicManager) { this.canJoinLive = true; this.cdr.detectChanges(); return; }
    // Creator always has access
    if (this.isCreator()) { this.canJoinLive = true; this.cdr.detectChanges(); return; }
    // Registered participants have access
    if (this.isRegistered()) { this.canJoinLive = true; this.cdr.detectChanges(); return; }

    // Club members with privileged roles have access
    if (!this.event?.clubId || !this.currentUserId) {
      this.canJoinLive = false;
      this.cdr.detectChanges();
      return;
    }
    this.memberService.getMembersByUser(this.currentUserId).subscribe({
      next: (members) => {
        const clubMember = members.find(m => m.clubId === this.event?.clubId);
        if (!clubMember) { this.canJoinLive = false; this.cdr.detectChanges(); return; }
        const privilegedRoles = ['PRESIDENT', 'VICE_PRESIDENT', 'SECRETARY', 'EVENT_MANAGER', 'COMMUNICATION_MANAGER'];
        this.canJoinLive = privilegedRoles.includes(clubMember.rank);
        this.cdr.detectChanges();
      },
      error: () => { this.canJoinLive = false; this.cdr.detectChanges(); }
    });
  }
  // ── Feedback ───────────────────────────────────────────────
  showFeedbackSection() {
    if (!this.event?.endDate) return false;
    return new Date(this.event.endDate) < new Date();
  }

  showFeedbackForm() {
    if (!this.event || !this.currentUserId) return false;
    return new Date(this.event.endDate!) < new Date() && this.isRegistered() && !this.isCreator() && !this.hasGivenFeedback;
  }

  showFeedbackStats() {
    if (!this.feedbackStats) return false;
    return this.isAcademicManager || this.isCreator() || this.feedbackStats.totalFeedbacks >= 3;
  }

  loadFeedbackData() {
    if (!this.event?.id) return;
    this.feedbackService.getEventFeedbackStats(this.event.id).subscribe({
      next: (s) => this.feedbackStats = s, error: () => {}
    });
    if (this.currentUserId) {
      this.feedbackService.hasUserGivenFeedback(this.event.id, this.currentUserId).subscribe({
        next: (has) => {
          this.hasGivenFeedback = has;
          if (has) {
            this.feedbackService.getUserFeedback(this.event!.id!, this.currentUserId!).subscribe({
              next: (f) => this.userFeedback = f, error: () => {}
            });
          }
        }, error: () => {}
      });
    }
  }

  setRating(r: number) { this.feedbackForm.rating = r; }

  submitFeedback() {
    if (!this.event || !this.currentUserId || this.feedbackForm.rating === 0) return;
    this.submittingFeedback = true;
    this.feedbackService.createFeedback({
      eventId: this.event.id!, userId: this.currentUserId,
      rating: this.feedbackForm.rating, comment: this.feedbackForm.comment || '', anonymous: this.feedbackForm.anonymous
    }).subscribe({
      next: () => {
        this.notificationService.success('Feedback Submitted', 'Thank you! 🎉');
        this.hasGivenFeedback = true;
        this.submittingFeedback = false;
        this.feedbackForm = { eventId: 0, userId: 0, rating: 0, comment: '', anonymous: false };
        this.loadFeedbackData();
      },
      error: () => { this.notificationService.error('Error', 'Failed to submit feedback'); this.submittingFeedback = false; }
    });
  }

  getDistributionPct(r: number) {
    if (!this.feedbackStats?.totalFeedbacks) return 0;
    return ((this.feedbackStats.ratingDistribution[r] || 0) / this.feedbackStats.totalFeedbacks) * 100;
  }

  openFeedbackComments() {
    if (!this.event?.id) return;
    this.showFeedbackCommentsModal = true;
    this.loadingFeedbackComments = true;
    this.feedbackService.getEventFeedbacks(this.event.id).pipe(
      switchMap((feedbacks: EventFeedback[]) => {
        const ids = feedbacks.filter(f => !f.anonymous).map(f => f.userId).filter((v, i, a) => a.indexOf(v) === i);
        if (!ids.length) return of({ feedbacks, users: [] });
        return this.userService.getUsersByIds(ids).pipe(map(users => ({ feedbacks, users })));
      })
    ).subscribe({
      next: ({ feedbacks, users }) => {
        const map = new Map(users.map((u: any) => [u.id, u]));
        this.feedbackComments = feedbacks.map((f: EventFeedback) => {
          if (f.anonymous) return f;
          const u: any = map.get(f.userId);
          return { ...f, userFirstName: u?.firstName || '', userLastName: u?.lastName || '' };
        });
        this.loadingFeedbackComments = false;
      },
      error: () => { this.loadingFeedbackComments = false; }
    });
  }
}
