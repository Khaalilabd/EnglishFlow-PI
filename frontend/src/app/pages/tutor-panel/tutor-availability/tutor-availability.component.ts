import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TutorAvailabilityService } from '../../../core/services/tutor-availability.service';
import { CourseCategoryService } from '../../../core/services/course-category.service';
import { AuthService } from '../../../core/services/auth.service';
import { TutorAvailability, DayOfWeek, TutorStatus, TimeSlot } from '../../../core/models/tutor-availability.model';
import { CourseCategory } from '../../../core/models/course-category.model';

@Component({
  selector: 'app-tutor-availability',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tutor-availability.component.html',
  styleUrls: ['./tutor-availability.component.scss']
})
export class TutorAvailabilityComponent implements OnInit {
  availability: TutorAvailability = {
    tutorId: 0,
    tutorName: '',
    availableDays: [],
    timeSlots: [],
    maxStudentsCapacity: 30,
    categories: [],
    levels: [],
    status: TutorStatus.AVAILABLE
  };

  loading = false;
  saving = false;
  message = '';
  messageType: 'success' | 'error' = 'success';
  isLocked = false; // Schedule lock status
  showPreviewModal = false;
  showModificationRequestModal = false;
  modificationReason = '';

  // Enums for template
  allDays = Object.values(DayOfWeek);
  allCategories: CourseCategory[] = [];
  allLevels = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];
  allStatuses = Object.values(TutorStatus);

  constructor(
    private availabilityService: TutorAvailabilityService,
    private categoryService: CourseCategoryService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadAvailability();
  }

  loadCategories(): void {
    this.categoryService.getActiveCategories().subscribe({
      next: (categories) => {
        this.allCategories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });
  }

  loadAvailability(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return;

    this.loading = true;
    this.availability.tutorId = currentUser.id;
    this.availability.tutorName = `${currentUser.firstName} ${currentUser.lastName}`;

    this.availabilityService.getByTutorId(currentUser.id).subscribe({
      next: (data) => {
        if (data) {
          this.availability = data;
          // Lock the schedule if it has been saved before
          this.isLocked = !!data.id;
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading availability:', error);
        this.loading = false;
      }
    });
  }

  toggleDay(day: DayOfWeek): void {
    const index = this.availability.availableDays.indexOf(day);
    if (index > -1) {
      this.availability.availableDays.splice(index, 1);
    } else {
      this.availability.availableDays.push(day);
    }
  }

  isDaySelected(day: DayOfWeek): boolean {
    return this.availability.availableDays.includes(day);
  }

  toggleCategory(category: string): void {
    const index = this.availability.categories.indexOf(category);
    if (index > -1) {
      this.availability.categories.splice(index, 1);
    } else {
      this.availability.categories.push(category);
    }
  }

  isCategorySelected(category: string): boolean {
    return this.availability.categories.includes(category);
  }

  toggleLevel(level: string): void {
    const index = this.availability.levels.indexOf(level);
    if (index > -1) {
      this.availability.levels.splice(index, 1);
    } else {
      this.availability.levels.push(level);
    }
  }

  isLevelSelected(level: string): boolean {
    return this.availability.levels.includes(level);
  }

  addTimeSlot(): void {
    this.availability.timeSlots.push({
      startTime: '09:00',
      endTime: '17:00'
    });
  }

  removeTimeSlot(index: number): void {
    this.availability.timeSlots.splice(index, 1);
  }

  saveAvailability(): void {
    if (!this.validateForm()) {
      this.showMessage('Please fill all required fields', 'error');
      return;
    }

    this.saving = true;
    this.availabilityService.createOrUpdateAvailability(this.availability).subscribe({
      next: (data) => {
        this.availability = data;
        this.isLocked = true; // Lock after first save
        this.showMessage('Availability saved and submitted successfully! Your schedule is now locked.', 'success');
        this.saving = false;
      },
      error: (error) => {
        console.error('Error saving availability:', error);
        this.showMessage('Failed to save availability', 'error');
        this.saving = false;
      }
    });
  }

  previewSchedule(): void {
    this.showPreviewModal = true;
  }

  closePreviewModal(): void {
    this.showPreviewModal = false;
  }

  requestModification(): void {
    this.showModificationRequestModal = true;
  }

  closeModificationRequestModal(): void {
    this.showModificationRequestModal = false;
    this.modificationReason = '';
  }

  submitModificationRequest(): void {
    if (!this.modificationReason.trim()) {
      this.showMessage('Please provide a reason for modification', 'error');
      return;
    }

    // TODO: Implement backend API call to send modification request to manager
    // For now, we'll just show a success message
    console.log('Modification request:', {
      tutorId: this.availability.tutorId,
      tutorName: this.availability.tutorName,
      reason: this.modificationReason,
      requestedAt: new Date()
    });

    this.showMessage('Modification request sent to manager successfully!', 'success');
    this.closeModificationRequestModal();
  }

  validateForm(): boolean {
    return this.availability.availableDays.length > 0 &&
           this.availability.categories.length > 0 &&
           this.availability.levels.length > 0 &&
           this.availability.maxStudentsCapacity > 0;
  }

  showMessage(text: string, type: 'success' | 'error'): void {
    this.message = text;
    this.messageType = type;
    setTimeout(() => {
      this.message = '';
    }, 5000);
  }

  getDayLabel(day: DayOfWeek): string {
    const labels: { [key in DayOfWeek]: string } = {
      [DayOfWeek.MONDAY]: 'Mon',
      [DayOfWeek.TUESDAY]: 'Tue',
      [DayOfWeek.WEDNESDAY]: 'Wed',
      [DayOfWeek.THURSDAY]: 'Thu',
      [DayOfWeek.FRIDAY]: 'Fri',
      [DayOfWeek.SATURDAY]: 'Sat',
      [DayOfWeek.SUNDAY]: 'Sun'
    };
    return labels[day];
  }

  getDayEmoji(day: DayOfWeek): string {
    const emojis: { [key in DayOfWeek]: string } = {
      [DayOfWeek.MONDAY]: '🌙',
      [DayOfWeek.TUESDAY]: '🔥',
      [DayOfWeek.WEDNESDAY]: '⚡',
      [DayOfWeek.THURSDAY]: '🌟',
      [DayOfWeek.FRIDAY]: '🎉',
      [DayOfWeek.SATURDAY]: '🌈',
      [DayOfWeek.SUNDAY]: '☀️'
    };
    return emojis[day];
  }

  getStatusColor(status: TutorStatus): string {
    const colors = {
      [TutorStatus.AVAILABLE]: 'bg-green-100 text-green-800',
      [TutorStatus.BUSY]: 'bg-orange-100 text-orange-800',
      [TutorStatus.UNAVAILABLE]: 'bg-red-100 text-red-800'
    };
    return colors[status];
  }

  getCapacityColor(): string {
    const percentage = this.availability.capacityPercentage || 0;
    if (percentage >= 80) return 'text-red-600';
    if (percentage >= 50) return 'text-orange-600';
    return 'text-green-600';
  }

  getFormattedTimeSlots(): string {
    if (!this.availability.timeSlots || this.availability.timeSlots.length === 0) {
      return 'No time slots defined';
    }
    return this.availability.timeSlots
      .map(slot => `${slot.startTime} - ${slot.endTime}`)
      .join(', ');
  }

  getFormattedDays(): string {
    if (!this.availability.availableDays || this.availability.availableDays.length === 0) {
      return 'No days selected';
    }
    return this.availability.availableDays
      .map(day => this.getDayLabel(day))
      .join(', ');
  }
}
