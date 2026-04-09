import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { trigger, state, style, transition, animate } from '@angular/animations';

import { 
  RecruitmentService, 
  ApplicationResponse, 
  ApplicationStatistics,
  MeetingPlatform,
  MeetingLinkResponse
} from '../../../core/services/recruitment.service';

interface TimeSlotOption {
  time: string;
  available: boolean;
  conflictsWith?: string;
}

interface DayOption {
  dateStr: string;
  dayName: string;
  dayNumber: string;
  monthName: string;
  isToday: boolean;
  isWeekend: boolean;
}

@Component({
  selector: 'app-recruitment-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './recruitment-dashboard.component.html',
  styleUrls: [
    './recruitment-dashboard.component.scss',
    './recruitment-dashboard-interview-modal.scss'
  ],
  animations: [
    trigger('slideDown', [
      transition(':enter', [
        style({ height: 0, opacity: 0, overflow: 'hidden' }),
        animate('300ms ease-out', style({ height: '*', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ height: 0, opacity: 0, overflow: 'hidden' }))
      ])
    ]),
    trigger('slideInRight', [
      transition(':enter', [
        style({ transform: 'translateX(100%)', opacity: 0 }),
        animate('400ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({ transform: 'translateX(0)', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ transform: 'translateX(100%)', opacity: 0 }))
      ])
    ]),
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(10px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ])
  ]
})
export class RecruitmentDashboardComponent implements OnInit {
  applications: ApplicationResponse[] = [];
  filteredApplications: ApplicationResponse[] = [];
  statistics: ApplicationStatistics | null = null;
  
  // View mode
  currentView: 'kanban' | 'timeline' | 'grid' | 'analytics' = 'kanban';
  
  // Premium features
  showAIInsights = false;
  showInterviewsPanel = false;
  upcomingInterviewsCount = 0;
  interviewFilter: 'all' | 'today' | 'week' | 'month' = 'all';
  
  selectedApplication: ApplicationResponse | null = null;
  showDetailModal = false;
  showScoreModal = false;
  showInterviewModal = false;
  showRejectModal = false;
  showNoteModal = false;
  showDocumentModal = false;
  showInterviewSuccessModal = false;
  scheduledInterviewInfo: any = null;
  isViewingExistingInterview = false;
  selectedDocument: any = null;
  documentViewerUrl: string = '';

  // Filters
  searchTerm = '';
  selectedStatus = 'ALL';
  
  // Forms
  qualificationScore = 0;
  presentationScore = 0;
  overallScore = 0;
  

  interviewDateTime = '';
  meetingLink = '';
  interviewNotes = '';
  selectedPlatform: MeetingPlatform = MeetingPlatform.GOOGLE_MEET;
  meetingTitle = '';
  durationMinutes = 60;
  availablePlatforms: { [key: string]: boolean } = {};
  generatingLink = false;
  generatedMeetingInfo: MeetingLinkResponse | null = null;
  
  // New properties for date/time selection
  selectedInterviewDate = '';
  selectedInterviewTimeSlot = '';
  interviewTimeSlots: TimeSlotOption[] = [];
  isCheckingAvailability = false;
  availableDays: DayOption[] = [];
  
  // Enum pour le template
  MeetingPlatform = MeetingPlatform;

  
  rejectionReason = '';
  noteContent = '';
  
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // Kanban columns
  columns = [
    { status: 'SUBMITTED', title: 'New Applications', color: '#F6BD60' },
    { status: 'UNDER_REVIEW', title: 'Under Review', color: '#2D5757' },
    { status: 'INTERVIEW_SCHEDULED', title: 'Interview Scheduled', color: '#3D3D60' },
    { status: 'TEST_PENDING', title: 'Test Pending', color: '#F6BD60' },
    { status: 'TEST_COMPLETED', title: 'Test Completed', color: '#2D5757' }
  ];

  constructor(
    private recruitmentService: RecruitmentService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.loadApplications();
    this.loadStatistics();
    this.loadAvailablePlatforms();
    this.loadUpcomingInterviews();
  }

  loadAvailablePlatforms(): void {
    this.recruitmentService.getAvailablePlatforms().subscribe({
      next: (platforms) => {
        this.availablePlatforms = platforms;
        console.log('Available platforms:', platforms);
      },
      error: (error) => {
        console.error('Failed to load available platforms', error);
      }
    });

  }

  loadApplications(): void {
    this.isLoading = true;
    this.recruitmentService.getAllApplications().subscribe({
      next: (data) => {
        this.applications = data;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load applications';
        this.isLoading = false;
      }
    });
  }

  loadStatistics(): void {
    this.recruitmentService.getStatistics().subscribe({
      next: (data) => {
        this.statistics = data;
      },
      error: (error) => {
        console.error('Failed to load statistics', error);
      }
    });
  }

  applyFilters(): void {
    this.filteredApplications = this.applications.filter(app => {
      const matchesSearch = !this.searchTerm || 
        app.firstName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        app.lastName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        app.email.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesStatus = this.selectedStatus === 'ALL' || app.status === this.selectedStatus;
      
      return matchesSearch && matchesStatus;
    });
  }

  getApplicationsByStatus(status: string): ApplicationResponse[] {
    return this.filteredApplications.filter(app => app.status === status);
  }

  openDetailModal(application: ApplicationResponse): void {
    this.selectedApplication = application;
    this.showDetailModal = true;
  }

  closeDetailModal(): void {
    this.showDetailModal = false;
    this.selectedApplication = null;
  }

  openScoreModal(application: ApplicationResponse): void {
    this.selectedApplication = application;
    this.qualificationScore = application.qualificationScore || 0;
    this.presentationScore = application.presentationScore || 0;
    this.overallScore = application.overallScore || 0;
    this.showScoreModal = true;
  }

  closeScoreModal(): void {
    this.showScoreModal = false;
    this.selectedApplication = null;
  }

  submitScore(): void {
    if (!this.selectedApplication) return;

    this.isLoading = true;
    const data = {
      qualificationScore: this.qualificationScore,
      presentationScore: this.presentationScore,
      overallScore: this.overallScore
    };

    this.recruitmentService.scoreApplication(this.selectedApplication.id, data).subscribe({
      next: () => {
        this.successMessage = 'Application scored successfully!';
        this.closeScoreModal();
        this.loadApplications();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Failed to score application';
        this.isLoading = false;
      }
    });
  }

  openInterviewModal(application: ApplicationResponse): void {
    this.selectedApplication = application;

    this.interviewDateTime = '';
    this.meetingLink = '';
    this.interviewNotes = '';
    this.selectedPlatform = MeetingPlatform.GOOGLE_MEET;
    this.meetingTitle = `Interview - ${application.firstName} ${application.lastName}`;
    this.durationMinutes = 60;
    this.generatedMeetingInfo = null;
    
    // Reset new variables
    this.selectedInterviewDate = '';
    this.selectedInterviewTimeSlot = '';
    this.interviewTimeSlots = [];
    this.isCheckingAvailability = false;
    
    // Generate available days
    this.availableDays = this.generateNextAvailableDays(14);
    console.log('Available days generated:', this.availableDays);

    this.showInterviewModal = true;
  }

  closeInterviewModal(): void {
    this.showInterviewModal = false;
    this.selectedApplication = null;

    this.generatedMeetingInfo = null;
  }

  getMinDateTime(): string {
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    return now.toISOString().slice(0, 16);
  }

  generateMeetingLink(): void {
    if (!this.selectedInterviewDate || !this.selectedInterviewTimeSlot) {
      this.errorMessage = "Please select date and time first";
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    this.generatingLink = true;
    
    const interviewDateTime = `${this.selectedInterviewDate}T${this.selectedInterviewTimeSlot}:00`;
    
    const request = {
      platform: this.selectedPlatform,
      interviewScheduledAt: interviewDateTime,
      title: this.meetingTitle,
      description: 'Entretien de recrutement pour le poste de tuteur',
      durationMinutes: this.durationMinutes
    };

    this.recruitmentService.generateMeetingLink(request).subscribe({
      next: (response) => {
        this.generatedMeetingInfo = response;
        this.meetingLink = response.meetingLink;
        this.generatingLink = false;
        this.successMessage = 'Lien de réunion généré avec succès!';
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Failed to generate meeting link';
        this.generatingLink = false;
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  scheduleInterview(): void {
    if (!this.selectedApplication || !this.selectedInterviewDate || !this.selectedInterviewTimeSlot) {
      this.errorMessage = 'Please select a date and time slot';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    // Validation: Si plateforme n'est pas MANUAL, on doit avoir un lien généré ou le générer
    if (this.selectedPlatform !== MeetingPlatform.MANUAL && !this.meetingLink && !this.generatedMeetingInfo) {
      this.errorMessage = 'Please generate a meeting link or enter one manually';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    this.isLoading = true;
    
    // Construct the datetime string
    const interviewDateTime = `${this.selectedInterviewDate}T${this.selectedInterviewTimeSlot}:00`;
    
    const data: any = {
      interviewScheduledAt: interviewDateTime,
      notes: this.interviewNotes
    };

    // Si une plateforme est sélectionnée et ce n'est pas MANUAL
    if (this.selectedPlatform !== MeetingPlatform.MANUAL) {
      data.platform = this.selectedPlatform;
      data.meetingTitle = this.meetingTitle;
      data.durationMinutes = this.durationMinutes;
    } else {
      // Mode manuel
      data.platform = MeetingPlatform.MANUAL;
      data.meetingLink = this.meetingLink;
    }

    this.recruitmentService.scheduleInterview(this.selectedApplication.id, data).subscribe({
      next: (response) => {
        // Stocker les informations de l'entretien planifié
        this.scheduledInterviewInfo = {
          candidateName: `${this.selectedApplication!.firstName} ${this.selectedApplication!.lastName}`,
          dateTime: this.formatSelectedDateTime(),
          meetingLink: response.interviewMeetingLink || this.meetingLink,
          platform: this.getPlatformDisplayName(this.selectedPlatform),
          duration: this.durationMinutes
        };
        
        this.isViewingExistingInterview = false;
        this.closeInterviewModal();
        this.showInterviewSuccessModal = true;
        this.loadApplications();
        this.loadUpcomingInterviews();
      },
      error: (error) => {
        this.errorMessage = error.error?.message || "Failed to schedule interview";
        this.isLoading = false;
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }
  
  closeInterviewSuccessModal(): void {
    this.showInterviewSuccessModal = false;
    this.scheduledInterviewInfo = null;
    this.isViewingExistingInterview = false;
  }
  
  copyMeetingLink(): void {
    if (this.scheduledInterviewInfo?.meetingLink) {
      navigator.clipboard.writeText(this.scheduledInterviewInfo.meetingLink).then(() => {
        this.successMessage = 'Meeting link copied to clipboard!';
        setTimeout(() => this.successMessage = '', 2000);
      });
    }
  }
  
  joinMeeting(): void {
    if (this.scheduledInterviewInfo?.meetingLink) {
      window.open(this.scheduledInterviewInfo.meetingLink, '_blank');
    }
  }
  
  joinInterviewMeeting(interview: ApplicationResponse): void {
    if (interview.interviewMeetingLink) {
      window.open(interview.interviewMeetingLink, '_blank');
    }
  }
  
  viewInterviewDetails(interview: ApplicationResponse): void {
    this.selectedApplication = interview;
    this.scheduledInterviewInfo = {
      candidateName: `${interview.firstName} ${interview.lastName}`,
      dateTime: this.formatDate(interview.interviewScheduledAt!) + ' at ' + this.formatInterviewTime(interview.interviewScheduledAt),
      meetingLink: interview.interviewMeetingLink || '',
      platform: 'Google Meet',
      duration: 60
    };
    this.isViewingExistingInterview = true;
    this.showInterviewSuccessModal = true;
  }


  isPlatformAvailable(platform: MeetingPlatform): boolean {
    return this.availablePlatforms[platform] === true;
  }

  getPlatformDisplayName(platform: MeetingPlatform): string {
    const names: { [key: string]: string } = {
      [MeetingPlatform.GOOGLE_MEET]: 'Google Meet',
      [MeetingPlatform.ZOOM]: 'Zoom',
      [MeetingPlatform.MICROSOFT_TEAMS]: 'Microsoft Teams',
      [MeetingPlatform.MANUAL]: 'Manual Link'
    };
    return names[platform] || platform;
  }


  openRejectModal(application: ApplicationResponse): void {
    this.selectedApplication = application;
    this.rejectionReason = '';
    this.showRejectModal = true;
  }

  closeRejectModal(): void {
    this.showRejectModal = false;
    this.selectedApplication = null;
  }

  rejectApplication(): void {
    if (!this.selectedApplication || !this.rejectionReason) return;

    this.isLoading = true;
    const data = { reason: this.rejectionReason };

    this.recruitmentService.rejectApplication(this.selectedApplication.id, data).subscribe({
      next: () => {
        this.successMessage = 'Application rejected';
        this.closeRejectModal();
        this.loadApplications();
        this.loadStatistics();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Failed to reject application';
        this.isLoading = false;
      }
    });
  }

  acceptApplication(application: ApplicationResponse): void {
    if (!confirm(`Accept ${application.firstName} ${application.lastName}'s application and create tutor account?`)) {
      return;
    }

    this.isLoading = true;
    this.recruitmentService.acceptApplication(application.id).subscribe({
      next: () => {
        this.successMessage = 'Application accepted! Tutor account created.';
        this.loadApplications();
        this.loadStatistics();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to accept application';
        this.isLoading = false;
      }
    });
  }

  openNoteModal(application: ApplicationResponse): void {
    this.selectedApplication = application;
    this.noteContent = '';
    this.showNoteModal = true;
  }

  closeNoteModal(): void {
    this.showNoteModal = false;
    this.selectedApplication = null;
  }

  addNote(): void {
    if (!this.selectedApplication || !this.noteContent) return;

    this.isLoading = true;
    const data = { content: this.noteContent };

    this.recruitmentService.addNote(this.selectedApplication.id, data).subscribe({
      next: () => {
        this.successMessage = 'Note added successfully!';
        this.closeNoteModal();
        this.loadApplications();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Failed to add note';
        this.isLoading = false;
      }
    });
  }

  onStatusChange(application: ApplicationResponse, event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const newStatus = selectElement.value;
    this.changeStatus(application, newStatus);
  }

  changeStatus(application: ApplicationResponse, newStatus: string): void {
    // Don't change if same status
    if (application.status === newStatus) {
      return;
    }

    this.isLoading = true;
    const data = { status: newStatus };

    this.recruitmentService.updateStatus(application.id, data).subscribe({
      next: () => {
        this.successMessage = `Status updated to ${newStatus}!`;
        this.loadApplications();
        this.loadStatistics();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = 'Failed to update status';
        this.isLoading = false;
      }
    });
  }

  changeStatusFromFinal(application: ApplicationResponse, newStatus: string): void {
    if (confirm(`Are you sure you want to move ${application.firstName} ${application.lastName} back to ${newStatus}? This will allow re-evaluation of the application.`)) {
      this.changeStatus(application, newStatus);
    }
  }

  getStatusBadgeClass(status: string): string {
    const classes: { [key: string]: string } = {
      'DRAFT': 'badge-draft',
      'SUBMITTED': 'badge-submitted',
      'UNDER_REVIEW': 'badge-review',
      'INTERVIEW_SCHEDULED': 'badge-interview',
      'TEST_PENDING': 'badge-test',
      'TEST_COMPLETED': 'badge-completed',
      'ACCEPTED': 'badge-accepted',
      'REJECTED': 'badge-rejected'
    };
    return classes[status] || 'badge-default';
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  }

  getScoreColor(score: number | undefined): string {
    if (!score) return '#999';
    if (score >= 80) return '#2D5757';
    if (score >= 60) return '#F6BD60';
    return '#C84630';
  }

  // Document viewing methods
  openDocumentModal(document: any): void {
    this.selectedDocument = document;
    this.documentViewerUrl = this.getDocumentUrl(document);
    this.showDocumentModal = true;
  }

  closeDocumentModal(): void {
    this.showDocumentModal = false;
    this.selectedDocument = null;
    this.documentViewerUrl = '';
  }

  getDocumentUrl(document: any): string {
    // Access files directly via API Gateway uploads route
    // The filePath already contains "uploads/applications/X/filename"
    return `http://localhost:8080/${document.filePath}`;
  }

  getSafeUrl(url: string): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  downloadDocument(document: any): void {
    const url = this.getDocumentUrl(document);
    window.open(url, '_blank');
  }

  getDocumentIcon(document: any): string {
    const type = document.type.toLowerCase();
    const fileType = document.fileType?.toLowerCase() || '';
    
    if (type === 'video_presentation' || fileType.includes('video')) {
      return '🎥';
    } else if (fileType.includes('pdf')) {
      return '📄';
    } else if (fileType.includes('image')) {
      return '🖼️';
    } else if (fileType.includes('word') || fileType.includes('doc')) {
      return '📝';
    }
    return '📎';
  }

  isVideoDocument(document: any): boolean {
    return document.type === 'VIDEO_PRESENTATION' || 
           document.fileType?.toLowerCase().includes('video');
  }

  isPdfDocument(document: any): boolean {
    return document.fileType?.toLowerCase().includes('pdf');
  }

  isImageDocument(document: any): boolean {
    return document.fileType?.toLowerCase().includes('image');
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  }

  getDocumentTypeName(type: string): string {
    const names: { [key: string]: string } = {
      'CV': 'Curriculum Vitae',
      'DEGREE': 'Degree Certificate',
      'CERTIFICATE': 'Teaching Certificate',
      'ID_CARD': 'ID Card',
      'VIDEO_PRESENTATION': 'Video Presentation',
      'OTHER': 'Other Document'
    };
    return names[type] || type;
  }

  // New methods for additional views
  getStatusColor(status: string): string {
    const colors: { [key: string]: string } = {
      'SUBMITTED': '#F6BD60',
      'UNDER_REVIEW': '#2D5757',
      'INTERVIEW_SCHEDULED': '#3D3D60',
      'TEST_PENDING': '#F6BD60',
      'TEST_COMPLETED': '#2D5757',
      'ACCEPTED': '#28a745',
      'REJECTED': '#C84630'
    };
    return colors[status] || '#999';
  }

  getStatusIcon(status: string): string {
    const icons: { [key: string]: string } = {
      'SUBMITTED': '📝',
      'UNDER_REVIEW': '🔍',
      'INTERVIEW_SCHEDULED': '📅',
      'TEST_PENDING': '📋',
      'TEST_COMPLETED': '✅',
      'ACCEPTED': '🎉',
      'REJECTED': '❌'
    };
    return icons[status] || '📄';
  }

  getInitials(firstName: string, lastName: string): string {
    return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
  }

  getScoreGradient(score: number): string {
    if (score >= 80) return 'linear-gradient(135deg, #2D5757 0%, #3D3D60 100%)';
    if (score >= 60) return 'linear-gradient(135deg, #F6BD60 0%, #f5b04a 100%)';
    return 'linear-gradient(135deg, #C84630 0%, #a83825 100%)';
  }

  getScoreRanges(): any[] {
    const ranges = [
      { label: '80-100 (Excellent)', min: 80, max: 100, color: '#2D5757', count: 0, percentage: 0 },
      { label: '60-79 (Good)', min: 60, max: 79, color: '#F6BD60', count: 0, percentage: 0 },
      { label: '40-59 (Average)', min: 40, max: 59, color: '#3D3D60', count: 0, percentage: 0 },
      { label: '0-39 (Poor)', min: 0, max: 39, color: '#C84630', count: 0, percentage: 0 }
    ];

    const scoredApps = this.filteredApplications.filter(app => app.overallScore);
    const total = scoredApps.length;

    ranges.forEach(range => {
      range.count = scoredApps.filter(app => 
        app.overallScore! >= range.min && app.overallScore! <= range.max
      ).length;
      range.percentage = total > 0 ? (range.count / total) * 100 : 0;
    });

    return ranges;
  }

  getRecentApplications(): ApplicationResponse[] {
    return [...this.filteredApplications]
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 10);
  }

  getActivityDescription(app: ApplicationResponse): string {
    const descriptions: { [key: string]: string } = {
      'SUBMITTED': 'Submitted application',
      'UNDER_REVIEW': 'Application under review',
      'INTERVIEW_SCHEDULED': 'Interview scheduled',
      'TEST_PENDING': 'Test pending',
      'TEST_COMPLETED': 'Test completed',
      'ACCEPTED': 'Application accepted',
      'REJECTED': 'Application rejected'
    };
    return descriptions[app.status] || 'Status updated';
  }

  // Premium Features Methods
  toggleAIInsights(): void {
    this.showAIInsights = !this.showAIInsights;
  }

  toggleInterviewsPanel(): void {
    this.showInterviewsPanel = !this.showInterviewsPanel;
    if (this.showInterviewsPanel) {
      this.loadUpcomingInterviews();
    }
  }

  loadUpcomingInterviews(): void {
    this.recruitmentService.getUpcomingInterviews().subscribe({
      next: (interviews) => {
        this.upcomingInterviewsCount = interviews.length;
      },
      error: (error) => {
        console.error('Failed to load upcoming interviews', error);
      }
    });
  }

  getFilteredInterviews(): ApplicationResponse[] {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const weekFromNow = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
    const monthFromNow = new Date(today.getTime() + 30 * 24 * 60 * 60 * 1000);

    return this.applications.filter(app => {
      if (app.status !== 'INTERVIEW_SCHEDULED' || !app.interviewScheduledAt) {
        return false;
      }

      const interviewDate = new Date(app.interviewScheduledAt);

      switch (this.interviewFilter) {
        case 'today':
          return interviewDate >= today && interviewDate < new Date(today.getTime() + 24 * 60 * 60 * 1000);
        case 'week':
          return interviewDate >= today && interviewDate < weekFromNow;
        case 'month':
          return interviewDate >= today && interviewDate < monthFromNow;
        default:
          return interviewDate >= today;
      }
    }).sort((a, b) => {
      const dateA = new Date(a.interviewScheduledAt!).getTime();
      const dateB = new Date(b.interviewScheduledAt!).getTime();
      return dateA - dateB;
    });
  }

  filterInterviewsByDate(filter: 'today' | 'week' | 'month'): void {
    this.interviewFilter = filter;
  }

  formatInterviewTime(dateString: string | undefined): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
  }

  formatInterviewDate(dateString: string | undefined): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }

  rescheduleInterview(application: ApplicationResponse): void {
    this.openInterviewModal(application);
  }

  cancelInterviewFromPanel(application: ApplicationResponse): void {
    console.log('Cancel interview called for application:', application.id);
    
    if (!confirm(`Are you sure you want to cancel the interview with ${application.firstName} ${application.lastName}?`)) {
      console.log('Cancel interview cancelled by user');
      return;
    }

    const reason = prompt('Cancellation reason (optional):');
    console.log('Cancel reason:', reason);
    
    this.isLoading = true;
    
    this.recruitmentService.cancelInterviewByApplicationId(application.id, reason || undefined).subscribe({
      next: () => {
        console.log('Interview cancelled successfully');
        this.successMessage = 'Interview cancelled successfully. Notification email sent.';
        this.loadApplications();
        this.loadUpcomingInterviews();
        this.isLoading = false;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        console.error('Failed to cancel interview:', error);
        console.error('Error details:', error.error);
        
        let errorMsg = 'Failed to cancel interview';
        if (error.status === 400) {
          errorMsg = 'No scheduled interview found for this application';
        } else if (error.error?.message) {
          errorMsg = error.error.message;
        }
        
        this.errorMessage = errorMsg;
        this.isLoading = false;
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }

  // AI Insights Methods
  getPendingReviewCount(): number {
    return this.applications.filter(app => app.status === 'SUBMITTED' || app.status === 'UNDER_REVIEW').length;
  }

  getAverageScore(): number {
    const scoredApps = this.applications.filter(app => app.overallScore);
    if (scoredApps.length === 0) return 0;
    const sum = scoredApps.reduce((acc, app) => acc + (app.overallScore || 0), 0);
    return Math.round(sum / scoredApps.length);
  }

  getAvgProcessingTime(): number {
    const processedApps = this.applications.filter(app => 
      (app.status === 'ACCEPTED' || app.status === 'REJECTED') && app.submittedAt
    );
    
    if (processedApps.length === 0) return 0;
    
    const totalDays = processedApps.reduce((acc, app) => {
      const submitted = new Date(app.submittedAt!).getTime();
      const reviewed = new Date(app.reviewedAt || app.createdAt).getTime();
      const days = Math.floor((reviewed - submitted) / (1000 * 60 * 60 * 24));
      return acc + days;
    }, 0);
    
    return Math.round(totalDays / processedApps.length);
  }

  getAcceptanceRate(): number {
    const decidedApps = this.applications.filter(app => 
      app.status === 'ACCEPTED' || app.status === 'REJECTED'
    );
    
    if (decidedApps.length === 0) return 0;
    
    const acceptedCount = decidedApps.filter(app => app.status === 'ACCEPTED').length;
    return Math.round((acceptedCount / decidedApps.length) * 100);
  }

  // New methods for date/time selection
  generateNextAvailableDays(count: number): DayOption[] {
    const days: DayOption[] = [];
    const today = new Date();
    
    for (let i = 0; i < count; i++) {
      const date = new Date(today);
      date.setDate(today.getDate() + i);
      
      const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
      const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
      
      days.push({
        dateStr: this.formatDateStr(date),
        dayName: dayNames[date.getDay()],
        dayNumber: date.getDate().toString(),
        monthName: monthNames[date.getMonth()],
        isToday: i === 0,
        isWeekend: date.getDay() === 0 || date.getDay() === 6
      });
    }
    
    console.log('Days generated:', days);
    return days;
  }

  formatDateStr(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  selectInterviewDate(dateStr: string): void {
    console.log('🗓️ Date selected:', dateStr);
    console.log('Previous selected date:', this.selectedInterviewDate);
    this.selectedInterviewDate = dateStr;
    this.selectedInterviewTimeSlot = '';
    this.interviewTimeSlots = [];
    console.log('Date selection updated, loading time slots...');
    this.loadAvailableTimeSlots();
  }

  loadAvailableTimeSlots(): void {
    if (!this.selectedInterviewDate) {
      console.log('No date selected, skipping time slots load');
      return;
    }
    
    console.log('Loading time slots for date:', this.selectedInterviewDate);
    this.isCheckingAvailability = true;
    
    const startDate = this.selectedInterviewDate;
    const endDate = this.selectedInterviewDate;
    
    this.recruitmentService.getCalendarAvailability({ startDate, endDate }).subscribe({
      next: (response) => {
        console.log('Availability response:', response);
        this.generateTimeSlots(response);
        this.isCheckingAvailability = false;
      },
      error: (error) => {
        console.error('Failed to check availability', error);
        this.generateTimeSlots(null);
        this.isCheckingAvailability = false;
      }
    });
  }

  generateTimeSlots(availability: any): void {
    console.log('Generating time slots with availability:', availability);
    const slots: TimeSlotOption[] = [];
    const startHour = 9;
    const endHour = 18;
    
    for (let hour = startHour; hour < endHour; hour++) {
      for (let minute = 0; minute < 60; minute += 30) {
        const timeStr = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
        
        let conflict: string | undefined;
        if (availability) {
          const slotStart = new Date(`${this.selectedInterviewDate}T${timeStr}:00`);
          const slotEnd = new Date(slotStart.getTime() + this.durationMinutes * 60000);
          conflict = this.checkSlotConflict(slotStart, slotEnd, availability);
        }
        
        slots.push({
          time: timeStr,
          available: !conflict,
          conflictsWith: conflict
        });
      }
    }
    
    console.log('Generated slots:', slots.length);
    this.interviewTimeSlots = slots;
  }

  checkSlotConflict(start: Date, end: Date, availability: any): string | undefined {
    if (!availability || !availability.scheduledEvents) return undefined;
    
    for (const event of availability.scheduledEvents) {
      const eventStart = new Date(event.start);
      const eventEnd = new Date(event.end);
      
      if ((start < eventEnd && end > eventStart)) {
        return event.candidateName || event.title;
      }
    }
    return undefined;
  }

  selectInterviewTimeSlot(slot: TimeSlotOption): void {
    console.log('⏰ Time slot clicked:', slot);
    if (!slot.available) {
      console.log('❌ Slot not available, ignoring click');
      return;
    }
    console.log('✅ Setting selected time slot to:', slot.time);
    this.selectedInterviewTimeSlot = slot.time;
    console.log('Selected time slot updated:', this.selectedInterviewTimeSlot);
  }

  formatSelectedDateTime(): string {
    if (!this.selectedInterviewDate || !this.selectedInterviewTimeSlot) return '';
    
    const date = new Date(this.selectedInterviewDate);
    const dayNames = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    const monthNames = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
    
    return `${dayNames[date.getDay()]}, ${monthNames[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()} at ${this.selectedInterviewTimeSlot}`;
  }
}
