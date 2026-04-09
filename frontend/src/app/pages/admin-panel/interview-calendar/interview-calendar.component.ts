import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions, EventClickArg, DateSelectArg, EventInput } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';

import {
  RecruitmentService,
  CalendarAvailabilityRequest,
  CalendarAvailabilityResponse,
  CalendarEventResponse,
  ApplicationResponse,
  MeetingPlatform
} from '../../../core/services/recruitment.service';

interface TimeSlotOption {
  time: string;
  available: boolean;
  conflictsWith?: string;
}

@Component({
  selector: 'app-interview-calendar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, FullCalendarModule],
  templateUrl: './interview-calendar.component.html',
  styleUrls: ['./interview-calendar.component.scss']
})
export class InterviewCalendarComponent implements OnInit {
  calendarOptions = signal<CalendarOptions>({
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    initialView: 'timeGridWeek',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay'
    },
    editable: false,
    selectable: true,
    selectMirror: true,
    dayMaxEvents: true,
    weekends: true,
    slotMinTime: '08:00:00',
    slotMaxTime: '20:00:00',
    height: 'auto',
    nowIndicator: true,
    businessHours: {
      daysOfWeek: [1, 2, 3, 4, 5],
      startTime: '09:00',
      endTime: '18:00'
    },
    select: this.handleDateSelect.bind(this),
    eventClick: this.handleEventClick.bind(this),
    datesSet: this.handleDatesSet.bind(this)
  });

  events: CalendarEventResponse[] = [];
  applications: ApplicationResponse[] = [];
  availabilityData: CalendarAvailabilityResponse | null = null;
  
  showScheduleModal = false;
  showEventDetailModal = false;
  showAvailabilityCheck = false;
  selectedEvent: CalendarEventResponse | null = null;
  selectedDate: Date | null = null;
  
  selectedApplication: ApplicationResponse | null = null;
  selectedDateStr = '';
  selectedTimeSlot = '';
  availableTimeSlots: TimeSlotOption[] = [];
  durationMinutes = 60;
  interviewNotes = '';
  selectedPlatform: MeetingPlatform = MeetingPlatform.GOOGLE_MEET;
  
  isLoading = false;
  isCheckingAvailability = false;
  errorMessage = '';
  successMessage = '';
  
  upcomingCount = 0;
  todayCount = 0;
  thisWeekCount = 0;

  MeetingPlatform = MeetingPlatform;

  constructor(private recruitmentService: RecruitmentService) {}

  ngOnInit(): void {
    this.loadUpcomingInterviews();
    this.loadApplications();
  }

  loadUpcomingInterviews(): void {
    this.isLoading = true;
    this.recruitmentService.getUpcomingInterviews().subscribe({
      next: (events) => {
        this.events = events;
        this.updateCalendarEvents();
        this.calculateStats();
        this.isLoading = false;
      },
      error: (error) => {
        this.showError('Failed to load interviews');
        this.isLoading = false;
      }
    });
  }

  loadApplications(): void {
    this.recruitmentService.getApplicationsByStatus('UNDER_REVIEW').subscribe({
      next: (apps) => {
        this.applications = apps;
      },
      error: (error) => console.error('Failed to load applications', error)
    });
  }

  updateCalendarEvents(): void {
    const calendarEvents: EventInput[] = this.events.map(event => ({
      id: event.scheduleId?.toString() || event.googleEventId,
      title: event.candidateName || event.title,
      start: event.start,
      end: event.end,
      backgroundColor: this.getEventColor(event),
      borderColor: this.getEventBorderColor(event),
      extendedProps: event
    }));

    this.calendarOptions.update(options => ({
      ...options,
      events: calendarEvents
    }));
  }

  getEventColor(event: CalendarEventResponse): string {
    if (event.status === 'CANCELLED') return '#ef4444';
    if (event.status === 'COMPLETED') return '#10b981';
    return '#3b82f6';
  }

  getEventBorderColor(event: CalendarEventResponse): string {
    if (event.source === 'GOOGLE_CALENDAR') return '#8b5cf6';
    return this.getEventColor(event);
  }

  handleDateSelect(selectInfo: DateSelectArg): void {
    this.selectedDate = selectInfo.start;
    this.selectedDateStr = this.formatDate(selectInfo.start);
    this.showScheduleModal = true;
    this.checkAvailabilityForDate(selectInfo.start);
  }

  checkAvailabilityForDate(date: Date): void {
    this.isCheckingAvailability = true;
    this.showAvailabilityCheck = true;
    
    const startDate = this.formatDate(date);
    const endDate = this.formatDate(new Date(date.getTime() + 24 * 60 * 60 * 1000));

    const request: CalendarAvailabilityRequest = {
      startDate,
      endDate
    };

    this.recruitmentService.getCalendarAvailability(request).subscribe({
      next: (response) => {
        this.availabilityData = response;
        this.generateTimeSlots(date, response);
        this.isCheckingAvailability = false;
      },
      error: (error) => {
        this.showError('Failed to check availability');
        this.isCheckingAvailability = false;
      }
    });
  }

  generateTimeSlots(date: Date, availability: CalendarAvailabilityResponse): void {
    const slots: TimeSlotOption[] = [];
    const startHour = 9;
    const endHour = 18;
    
    for (let hour = startHour; hour < endHour; hour++) {
      for (let minute = 0; minute < 60; minute += 30) {
        const timeStr = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
        const slotStart = new Date(date);
        slotStart.setHours(hour, minute, 0, 0);
        const slotEnd = new Date(slotStart.getTime() + this.durationMinutes * 60000);

        const conflict = this.checkSlotConflict(slotStart, slotEnd, availability);
        
        slots.push({
          time: timeStr,
          available: !conflict,
          conflictsWith: conflict
        });
      }
    }
    
    this.availableTimeSlots = slots;
  }

  checkSlotConflict(start: Date, end: Date, availability: CalendarAvailabilityResponse): string | undefined {
    for (const event of availability.scheduledEvents) {
      const eventStart = new Date(event.start);
      const eventEnd = new Date(event.end);
      
      if ((start < eventEnd && end > eventStart)) {
        return event.candidateName || event.title;
      }
    }
    return undefined;
  }

  onTimeSlotSelected(slot: TimeSlotOption): void {
    if (!slot.available) {
      this.showError(`This time slot conflicts with: ${slot.conflictsWith}`);
      return;
    }
    this.selectedTimeSlot = slot.time;
  }

  scheduleInterview(): void {
    if (!this.selectedApplication || !this.selectedDateStr || !this.selectedTimeSlot) {
      this.showError('Please select an application, date, and time slot');
      return;
    }

    this.isLoading = true;
    const dateTimeStr = `${this.selectedDateStr}T${this.selectedTimeSlot}:00`;
    
    const data: any = {
      interviewScheduledAt: dateTimeStr,
      platform: this.selectedPlatform,
      meetingTitle: `Interview - ${this.selectedApplication.firstName} ${this.selectedApplication.lastName}`,
      durationMinutes: this.durationMinutes,
      notes: this.interviewNotes
    };

    this.recruitmentService.scheduleInterview(this.selectedApplication.id, data).subscribe({
      next: () => {
        this.showSuccess('Interview scheduled successfully!');
        this.closeScheduleModal();
        this.loadUpcomingInterviews();
      },
      error: (error) => {
        this.showError(error.error?.message || 'Failed to schedule interview');
        this.isLoading = false;
      }
    });
  }

  handleEventClick(clickInfo: EventClickArg): void {
    this.selectedEvent = clickInfo.event.extendedProps as CalendarEventResponse;
    this.showEventDetailModal = true;
  }

  handleDatesSet(dateInfo: any): void {
    // Optionnel : charger les événements pour la période visible
  }

  cancelInterview(): void {
    if (!this.selectedEvent?.scheduleId) return;
    if (!confirm('Are you sure you want to cancel this interview?')) return;

    const reason = prompt('Cancellation reason (optional):');
    
    this.isLoading = true;
    this.recruitmentService.cancelInterview(this.selectedEvent.scheduleId, reason || undefined).subscribe({
      next: () => {
        this.showSuccess('Interview cancelled successfully');
        this.closeEventDetailModal();
        this.loadUpcomingInterviews();
      },
      error: () => {
        this.showError('Failed to cancel interview');
        this.isLoading = false;
      }
    });
  }

  closeScheduleModal(): void {
    this.showScheduleModal = false;
    this.showAvailabilityCheck = false;
    this.selectedApplication = null;
    this.selectedDate = null;
    this.selectedDateStr = '';
    this.selectedTimeSlot = '';
    this.availableTimeSlots = [];
    this.durationMinutes = 60;
    this.interviewNotes = '';
    this.isLoading = false;
  }

  closeEventDetailModal(): void {
    this.showEventDetailModal = false;
    this.selectedEvent = null;
    this.isLoading = false;
  }

  calculateStats(): void {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const weekEnd = new Date(today);
    weekEnd.setDate(weekEnd.getDate() + 7);

    this.upcomingCount = this.events.filter(e => e.status === 'SCHEDULED').length;
    this.todayCount = this.events.filter(e => {
      const eventDate = new Date(e.start);
      return eventDate >= today && eventDate < new Date(today.getTime() + 24 * 60 * 60 * 1000);
    }).length;
    this.thisWeekCount = this.events.filter(e => {
      const eventDate = new Date(e.start);
      return eventDate >= today && eventDate < weekEnd;
    }).length;
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  formatDateTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  openMeetingLink(link: string): void {
    window.open(link, '_blank');
  }

  showError(message: string): void {
    this.errorMessage = message;
    setTimeout(() => this.errorMessage = '', 5000);
  }

  showSuccess(message: string): void {
    this.successMessage = message;
    setTimeout(() => this.successMessage = '', 3000);
  }
}
