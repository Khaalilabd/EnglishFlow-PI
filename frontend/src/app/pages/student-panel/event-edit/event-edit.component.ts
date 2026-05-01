import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { EventService, Event } from '../../../core/services/event.service';
import { AuthService } from '../../../core/services/auth.service';
import { SponsorService } from '../../../core/services/sponsor.service';
import { MemberService, ClubWithRole } from '../../../core/services/member.service';
import { Sponsor } from '../../../core/models/sponsor.model';
import { NotificationService } from '../../../core/services/notification.service';
import { LocationSearchComponent, LocationData } from '../../../shared/components/location-search/location-search.component';

const EVENT_ALLOWED_ROLES = new Set(['PRESIDENT', 'VICE_PRESIDENT', 'EVENT_MANAGER']);

@Component({
  selector: 'app-event-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, LocationSearchComponent],
  templateUrl: './event-edit.component.html'
})
export class EventEditComponent implements OnInit {
  step = 1;
  loading = false;
  loadingEvent = true;
  loadingClubs = true;
  availableSponsors: Sponsor[] = [];
  selectedSponsorIds: number[] = [];
  eligibleClubs: ClubWithRole[] = [];
  eventId!: number;

  form: Partial<Event> = {};

  constructor(
    private readonly eventService: EventService,
    private readonly authService: AuthService,
    private readonly sponsorService: SponsorService,
    private readonly memberService: MemberService,
    private readonly notificationService: NotificationService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.eventId = Number(this.route.snapshot.paramMap.get('id'));
    this.eventService.getEventById(this.eventId).subscribe({
      next: (event) => {
        // Block editing if event has already started or ended
        const now = new Date();
        const startDate = new Date(event.startDate);
        if (startDate <= now) {
          this.notificationService.error('Not Allowed', 'You cannot edit an event that has already started or ended.');
          this.router.navigate(['/user-panel/events', this.eventId]);
          return;
        }

        this.form = { ...event };
        if (this.form.startDate) this.form.startDate = new Date(this.form.startDate).toISOString().slice(0, 16);
        if (this.form.endDate) this.form.endDate = new Date(this.form.endDate).toISOString().slice(0, 16);
        this.selectedSponsorIds = event.sponsorIds ? [...event.sponsorIds] : [];
        this.loadingEvent = false;
      },
      error: () => { this.notificationService.error('Error', 'Event not found'); this.router.navigate(['/user-panel/events']); }
    });

    const user = this.authService.currentUserValue;
    if (user?.id) {
      this.memberService.getUserClubsWithStatus(user.id).subscribe({
        next: (clubs) => {
          this.eligibleClubs = clubs.filter(c =>
            c.status === 'APPROVED' && EVENT_ALLOWED_ROLES.has(c.userRole)
          );
          this.loadingClubs = false;
        },
        error: () => { this.loadingClubs = false; }
      });
    }

    this.sponsorService.getAllSponsors().subscribe({
      next: (s) => this.availableSponsors = s,
      error: () => {}
    });
  }

  onLocationSelected(data: LocationData) {
    this.form.location = data.address;
    this.form.latitude = data.latitude;
    this.form.longitude = data.longitude;
  }

  onTypeChange(type: string) {
    if (type === 'SOCIAL') { this.form.format = 'IN_PERSON'; this.form.meetingLink = ''; }
  }

  onFormatChange(format: string) {
    if (format === 'IN_PERSON') this.form.meetingLink = '';
  }

  toggleSponsor(id: number) {
    const idx = this.selectedSponsorIds.indexOf(id);
    if (idx > -1) this.selectedSponsorIds.splice(idx, 1);
    else this.selectedSponsorIds.push(id);
  }

  isSponsorSelected(id: number) { return this.selectedSponsorIds.includes(id); }

  isStep1Valid(): boolean {
    return !!(this.form.title?.trim() && this.form.type && this.form.startDate && this.form.endDate);
  }

  nextStep() {
    if (!this.isStep1Valid()) { this.notificationService.error('Missing Fields', 'Please fill all required fields'); return; }
    if (this.form.startDate && this.form.endDate && new Date(this.form.endDate) <= new Date(this.form.startDate)) {
      this.notificationService.error('Invalid Dates', 'End date must be after start date'); return;
    }
    this.step = 2;
  }

  submit() {
    if (!this.form.description?.trim()) { this.notificationService.error('Missing Description', 'Please enter a description'); return; }
    if (this.form.format !== 'ONLINE' && !this.form.location?.trim()) { this.notificationService.error('Missing Location', 'Please select a location'); return; }

    this.form.sponsorIds = this.selectedSponsorIds;
    this.loading = true;

    this.eventService.updateEvent(this.eventId, this.form as Event).subscribe({
      next: () => {
        this.notificationService.success('Event Updated', 'Your changes have been submitted for approval. The event is now pending review by Academic Affairs.');
        this.router.navigate(['/user-panel/events']);
      },
      error: (err) => {
        this.notificationService.error('Error', err.error?.message || 'Failed to update event');
        this.loading = false;
      }
    });
  }

  cancel() { this.router.navigate(['/user-panel/events']); }

  getLevelBadge(level?: string): string {
    const map: Record<string, string> = { GOLD: '🥇', SILVER: '🥈', BRONZE: '🥉' };
    return map[level || ''] || '🏢';
  }
}
