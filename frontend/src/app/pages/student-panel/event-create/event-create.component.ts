import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EventService, Event } from '../../../core/services/event.service';
import { AuthService } from '../../../core/services/auth.service';
import { SponsorService } from '../../../core/services/sponsor.service';
import { MemberService, ClubWithRole } from '../../../core/services/member.service';
import { Sponsor } from '../../../core/models/sponsor.model';
import { NotificationService } from '../../../core/services/notification.service';
import { LocationSearchComponent, LocationData } from '../../../shared/components/location-search/location-search.component';

const EVENT_ALLOWED_ROLES = new Set(['PRESIDENT', 'VICE_PRESIDENT', 'EVENT_MANAGER']);

@Component({
  selector: 'app-event-create',
  standalone: true,
  imports: [CommonModule, FormsModule, LocationSearchComponent],
  templateUrl: './event-create.component.html'
})
export class EventCreateComponent implements OnInit {
  step = 1;
  loading = false;
  loadingClubs = true;
  availableSponsors: Sponsor[] = [];
  selectedSponsorIds: number[] = [];
  eligibleClubs: ClubWithRole[] = []; // clubs where user has permission

  form: Partial<Event> = {
    title: '',
    type: 'WORKSHOP',
    format: 'IN_PERSON',
    meetingLink: '',
    startDate: '',
    endDate: '',
    location: '',
    maxParticipants: 10,
    description: '',
    gallery: [],
    clubId: undefined
  };

  constructor(
    private readonly eventService: EventService,
    private readonly authService: AuthService,
    private readonly sponsorService: SponsorService,
    private readonly memberService: MemberService,
    private readonly notificationService: NotificationService,
    private readonly router: Router
  ) {}

  ngOnInit() {
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

  onImageSelected(event: globalThis.Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    if (file.size > 2 * 1024 * 1024) {
      this.notificationService.error('File too large', 'Image must be under 2MB');
      return;
    }
    const reader = new FileReader();
    reader.onload = () => { this.form.image = reader.result as string; };
    reader.readAsDataURL(file);
  }

  removeImage() {
    this.form.image = undefined;
  }

  onTypeChange(type: string) {
    if (type === 'SOCIAL') {
      this.form.format = 'IN_PERSON';
      this.form.meetingLink = '';
    }
  }

  onFormatChange(format: string) {
    if (format === 'IN_PERSON') this.form.meetingLink = '';
  }

  toggleSponsor(id: number) {
    const idx = this.selectedSponsorIds.indexOf(id);
    if (idx > -1) this.selectedSponsorIds.splice(idx, 1);
    else this.selectedSponsorIds.push(id);
  }

  isSponsorSelected(id: number) {
    return this.selectedSponsorIds.includes(id);
  }

  isStep1Valid(): boolean {
    return !!(this.form.title?.trim() && this.form.type && this.form.startDate && this.form.endDate && this.form.clubId);
  }

  nextStep() {
    if (!this.isStep1Valid()) {
      this.notificationService.error('Missing Fields', 'Please fill all required fields');
      return;
    }
    if (this.form.startDate && this.form.endDate && new Date(this.form.endDate) <= new Date(this.form.startDate)) {
      this.notificationService.error('Invalid Dates', 'End date must be after start date');
      return;
    }
    this.step = 2;
  }

  private toLocalDateTime(s: string): string {
    if (!s) return s;
    // datetime-local gives "yyyy-MM-ddTHH:mm", backend needs "yyyy-MM-ddTHH:mm:ss"
    return s.length === 16 ? s + ':00' : s;
  }

  submit() {
    if (!this.form.description?.trim()) {
      this.notificationService.error('Missing Description', 'Please enter a description');
      return;
    }
    if (this.form.format === 'IN_PERSON' && !this.form.location?.trim()) {
      this.notificationService.error('Missing Location', 'Please select a location');
      return;
    }

    const user = this.authService.currentUserValue;
    this.form.creatorId = user?.id;
    this.form.sponsorIds = this.selectedSponsorIds;

    // For ONLINE events, location is not required — send empty string to satisfy backend
    if (this.form.format === 'ONLINE' && !this.form.location?.trim()) {
      this.form.location = 'Online';
    }

    this.loading = true;

    const payload: Event = {
      ...(this.form as Event),
      startDate: this.toLocalDateTime(this.form.startDate!),
      endDate: this.toLocalDateTime(this.form.endDate!),
    };

    this.eventService.createEvent(payload).subscribe({
      next: () => {
        this.notificationService.success('Event Created', 'Your event has been submitted for approval!');
        this.router.navigate(['/user-panel/events']);
      },
      error: (err) => {
        this.notificationService.error('Error', err.error?.message || 'Failed to create event');
        this.loading = false;
      }
    });
  }

  cancel() {
    this.router.navigate(['/user-panel/events']);
  }

  getLevelBadge(level?: string): string {
    const map: Record<string, string> = { GOLD: '🥇', SILVER: '🥈', BRONZE: '🥉' };
    return map[level || ''] || '🏢';
  }
}
