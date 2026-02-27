import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ComplaintService } from '../../../core/services/complaint.service';
import { AuthService } from '../../../core/services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-complaints',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complaints.component.html',
  styleUrls: ['./complaints.component.css']
})
export class ComplaintsComponent implements OnInit, OnDestroy {
  // Workflow steps
  currentStep = 0;
  
  // Form data
  complaintForm = {
    category: '',
    courseType: '',
    difficulty: '',
    issueType: '',
    sessionCount: null as number | null,
    subject: '',
    description: ''
  };
  
  // Categories
  categories = [
    { 
      value: 'PEDAGOGICAL', 
      label: 'Pedagogical Issue',
      description: 'I don\'t understand the course',
      icon: 'fa-book-open',
      color: 'text-blue-600',
      bgColor: 'bg-blue-50'
    },
    { 
      value: 'TUTOR_BEHAVIOR', 
      label: 'Tutor Issue',
      description: 'Behavior or teaching method',
      icon: 'fa-chalkboard-teacher',
      color: 'text-purple-600',
      bgColor: 'bg-purple-50'
    },
    { 
      value: 'SCHEDULE', 
      label: 'Schedule Issue',
      description: 'Absence or schedule change',
      icon: 'fa-calendar-times',
      color: 'text-orange-600',
      bgColor: 'bg-orange-50'
    },
    { 
      value: 'TECHNICAL', 
      label: 'Technical Issue',
      description: 'Platform problem',
      icon: 'fa-laptop-code',
      color: 'text-red-600',
      bgColor: 'bg-red-50'
    },
    { 
      value: 'ADMINISTRATIVE', 
      label: 'Administrative Issue',
      description: 'Payment or registration',
      icon: 'fa-file-invoice-dollar',
      color: 'text-green-600',
      bgColor: 'bg-green-50'
    }
  ];
  
  // Dynamic options based on category
  courseTypes = ['Speaking', 'Grammar', 'Listening', 'Writing', 'Vocabulary'];
  
  difficulties = [
    'Vocabulary too complex',
    'Difficult oral comprehension',
    'Teacher speaks too fast',
    'Lack of practical exercises',
    'Unclear explanations'
  ];
  
  tutorIssues = [
    'Frequent delays',
    'Unjustified absences',
    'Poor explanation',
    'Inappropriate behavior',
    'Lack of interaction'
  ];
  
  technicalIssues = [
    'Cannot login',
    'Video not loading',
    'Audio not working',
    'Platform blocked',
    'Payment error'
  ];
  
  // Complaints list
  complaints: any[] = [];
  isLoading = false;
  isSubmitting = false;
  
  // Suggested solutions
  suggestedSolutions: any[] = [];
  
  // Polling interval for checking new responses
  private pollingInterval: any;

  constructor(
    private complaintService: ComplaintService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadComplaints();
    this.startPolling();
  }

  ngOnDestroy(): void {
    this.stopPolling();
  }

  private startPolling(): void {
    // Check for new responses every 10 seconds
    this.pollingInterval = setInterval(() => {
      this.loadComplaintsQuietly();
    }, 10000);
  }

  private stopPolling(): void {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }

  private loadComplaintsQuietly(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !currentUser.id) return;

    this.complaintService.getMyComplaints(currentUser.id).subscribe({
      next: (data) => {
        // Check if there are new responses
        const hasNewResponses = this.checkForNewResponses(data);
        this.complaints = data;
        
        if (hasNewResponses) {
          // Show a subtle notification
          this.showNewResponseNotification();
        }
      },
      error: (error) => {
        console.error('Error loading complaints:', error);
      }
    });
  }

  private checkForNewResponses(newComplaints: any[]): boolean {
    if (this.complaints.length === 0) return false;
    
    for (let newComplaint of newComplaints) {
      const oldComplaint = this.complaints.find(c => c.id === newComplaint.id);
      if (oldComplaint && !oldComplaint.response && newComplaint.response) {
        return true;
      }
      if (oldComplaint && oldComplaint.response !== newComplaint.response) {
        return true;
      }
    }
    return false;
  }

  private showNewResponseNotification(): void {
    // Show a toast notification
    const toast = Swal.mixin({
      toast: true,
      position: 'top-end',
      showConfirmButton: false,
      timer: 3000,
      timerProgressBar: true,
    });
    
    toast.fire({
      icon: 'info',
      title: 'New response received!'
    });
  }

  private loadComplaints(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !currentUser.id) return;

    this.isLoading = true;
    this.complaintService.getMyComplaints(currentUser.id).subscribe({
      next: (data) => {
        this.complaints = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading complaints:', error);
        this.isLoading = false;
      }
    });
  }

  // Navigate steps
  nextStep(): void {
    if (this.currentStep < 3) {
      this.currentStep++;
      
      // Skip step 2 (suggested solutions) if not PEDAGOGICAL
      if (this.currentStep === 2 && this.complaintForm.category !== 'PEDAGOGICAL') {
        this.currentStep = 3; // Skip directly to step 3
      }
      
      // Check for suggested solutions at step 2 for PEDAGOGICAL
      if (this.currentStep === 2 && this.complaintForm.category === 'PEDAGOGICAL') {
        this.loadSuggestedSolutions();
      }
    }
  }

  previousStep(): void {
    if (this.currentStep > 0) {
      this.currentStep--;
      
      // Skip step 2 when going back if not PEDAGOGICAL
      if (this.currentStep === 2 && this.complaintForm.category !== 'PEDAGOGICAL') {
        this.currentStep = 1; // Go back to step 1
      }
    }
  }

  selectCategory(category: string): void {
    this.complaintForm.category = category;
    this.nextStep();
  }

  // Load suggested solutions for pedagogical issues
  loadSuggestedSolutions(): void {
    this.suggestedSolutions = [
      {
        title: 'Explanatory Video',
        description: 'Watch this video that explains the concept in detail',
        link: '#',
        icon: 'fa-video',
        color: 'text-red-600'
      },
      {
        title: 'Additional Exercises',
        description: 'Practice with these interactive exercises',
        link: '#',
        icon: 'fa-pen-fancy',
        color: 'text-blue-600'
      },
      {
        title: 'Review PDF',
        description: 'Download this comprehensive review guide',
        link: '#',
        icon: 'fa-file-pdf',
        color: 'text-orange-600'
      }
    ];
  }

  skipSolutions(): void {
    this.nextStep();
  }

  // Submit complaint
  submitComplaint(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !currentUser.id) {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Please login to submit a complaint',
        confirmButtonColor: '#F59E0B'
      });
      return;
    }

    this.isSubmitting = true;

    const complaintData = {
      userId: currentUser.id,
      category: this.complaintForm.category,
      subject: this.complaintForm.subject,
      description: this.complaintForm.description,
      courseType: this.complaintForm.courseType,
      difficulty: this.complaintForm.difficulty,
      issueType: this.complaintForm.issueType,
      sessionCount: this.complaintForm.sessionCount
    };

    this.complaintService.createComplaint(complaintData).subscribe({
      next: (response: any) => {
        this.complaints.unshift(response);
        this.resetForm();
        this.isSubmitting = false;
        
        Swal.fire({
          icon: 'success',
          title: 'Complaint Sent!',
          html: `Your complaint has been sent to <strong>${this.getTargetRole(response.targetRole || 'MANAGER', response.category)}</strong><br>
                 Priority: <strong>${this.getPriorityLabel(response.priority || 'MEDIUM')}</strong>`,
          confirmButtonColor: '#F59E0B',
          timer: 4000,
          timerProgressBar: true
        });
      },
      error: (error) => {
        console.error('Error submitting complaint:', error);
        this.isSubmitting = false;
        
        Swal.fire({
          icon: 'error',
          title: 'Error!',
          text: 'Unable to send complaint. Please try again.',
          confirmButtonColor: '#F59E0B'
        });
      }
    });
  }

  resetForm(): void {
    this.currentStep = 0;
    this.complaintForm = {
      category: '',
      courseType: '',
      difficulty: '',
      issueType: '',
      sessionCount: null,
      subject: '',
      description: ''
    };
    this.suggestedSolutions = [];
  }

  getTargetRole(role: string, category?: string): string {
    // If category is PEDAGOGICAL, show "Your Tutor"
    if (category === 'PEDAGOGICAL') {
      return 'Your Tutor';
    }
    
    // For all other categories, show "Academic Office Affair"
    return 'Academic Office Affair';
  }

  getPriorityLabel(priority: string): string {
    const priorities: any = {
      'LOW': 'Low',
      'MEDIUM': 'Medium',
      'HIGH': 'High',
      'URGENT': 'Urgent'
    };
    return priorities[priority] || priority;
  }

  getStatusClass(status: string): string {
    const classes: any = {
      'OPEN': 'bg-blue-100 text-blue-700',
      'IN_PROGRESS': 'bg-indigo-100 text-indigo-700',
      'RESOLVED': 'bg-green-100 text-green-700',
      'REJECTED': 'bg-red-100 text-red-700'
    };
    return classes[status] || 'bg-gray-100 text-gray-700';
  }

  getPriorityClass(priority: string): string {
    const classes: any = {
      'MEDIUM': 'bg-yellow-100 text-yellow-700',
      'HIGH': 'bg-orange-100 text-orange-700',
      'URGENT': 'bg-red-100 text-red-700'
    };
    return classes[priority] || 'bg-gray-100 text-gray-700';
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      day: 'numeric',
      month: 'long', 
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getTimeAgo(dateString?: string): string {
    if (!dateString) return '';
    
    const now = new Date();
    const date = new Date(dateString);
    const diffMs = now.getTime() - date.getTime();
    const diffSecs = Math.floor(diffMs / 1000);
    const diffMins = Math.floor(diffSecs / 60);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);
    
    if (diffSecs < 60) {
      return 'just now';
    } else if (diffMins < 60) {
      return `${diffMins} minute${diffMins > 1 ? 's' : ''} ago`;
    } else if (diffHours < 24) {
      return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    } else if (diffDays < 30) {
      return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    } else {
      return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    }
  }

  trackByComplaintId(index: number, complaint: any): number {
    return complaint.id || index;
  }

  getResponderInfo(complaint: any): string {
    if (!complaint.responderRole) return 'ACADEMIC_OFFICE_AFFAIR';
    
    // If responderName is available, use it
    if (complaint.responderName) {
      return `${complaint.responderName}`;
    }
    
    return complaint.responderRole;
  }

  // Navigate to complaint detail page
  viewComplaintDetails(id: number): void {
    this.router.navigate(['/user-panel/complaints', id]);
  }

  editComplaint(id: number): void {
    this.router.navigate(['/user-panel/complaints/edit', id]);
  }

  deleteComplaint(id: number): void {
    Swal.fire({
      title: 'Are you sure?',
      text: "You won't be able to revert this!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#F59E0B',
      cancelButtonColor: '#6B7280',
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.complaintService.deleteComplaint(id).subscribe({
          next: () => {
            this.complaints = this.complaints.filter(c => c.id !== id);
            
            Swal.fire({
              icon: 'success',
              title: 'Deleted!',
              text: 'Your complaint has been deleted.',
              confirmButtonColor: '#F59E0B',
              timer: 2000,
              timerProgressBar: true,
              showConfirmButton: false
            });
          },
          error: (error) => {
            console.error('Error deleting complaint:', error);
            
            Swal.fire({
              icon: 'error',
              title: 'Error!',
              text: 'Unable to delete complaint.',
              confirmButtonColor: '#F59E0B'
            });
          }
        });
      }
    });
  }
}
