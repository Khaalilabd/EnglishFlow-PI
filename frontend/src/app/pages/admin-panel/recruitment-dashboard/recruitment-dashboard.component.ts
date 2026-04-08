import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { 
  RecruitmentService, 
  ApplicationResponse, 
  ApplicationStatistics,
  MeetingPlatform,
  MeetingLinkResponse
} from '../../../core/services/recruitment.service';

@Component({
  selector: 'app-recruitment-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recruitment-dashboard.component.html',
  styleUrls: ['./recruitment-dashboard.component.scss']
})
export class RecruitmentDashboardComponent implements OnInit {
  applications: ApplicationResponse[] = [];
  filteredApplications: ApplicationResponse[] = [];
  statistics: ApplicationStatistics | null = null;
  
  selectedApplication: ApplicationResponse | null = null;
  showDetailModal = false;
  showScoreModal = false;
  showInterviewModal = false;
  showRejectModal = false;
  showNoteModal = false;
  showDocumentModal = false;
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
    if (!this.interviewDateTime) {
      this.errorMessage = "Veuillez sélectionner la date et l'heure d'abord";
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    this.generatingLink = true;
    
    const request = {
      platform: this.selectedPlatform,
      interviewScheduledAt: this.interviewDateTime + ':00',
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
        this.errorMessage = 'Échec de la génération du lien de réunion';
        this.generatingLink = false;
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
  }

  scheduleInterview(): void {
    if (!this.selectedApplication || !this.interviewDateTime) {
      this.errorMessage = 'Veuillez remplir tous les champs obligatoires';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    // Validation: Si plateforme n'est pas MANUAL, on doit avoir un lien généré ou le générer
    if (this.selectedPlatform !== MeetingPlatform.MANUAL && !this.meetingLink) {
      this.errorMessage = 'Veuillez générer un lien de réunion ou en saisir un manuellement';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    this.isLoading = true;
    
    const data: any = {
      interviewScheduledAt: this.interviewDateTime + ':00',
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
      next: () => {
        this.successMessage = "Entretien planifié avec succès!";
        this.closeInterviewModal();
        this.loadApplications();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || "Échec de la planification de l'entretien";
        this.isLoading = false;
        setTimeout(() => this.errorMessage = '', 3000);
      }
    });
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
}
