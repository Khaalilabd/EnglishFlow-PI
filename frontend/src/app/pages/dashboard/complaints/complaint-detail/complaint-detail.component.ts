import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ComplaintService, ComplaintWithUser, ComplaintWorkflow } from '../../../../core/services/complaint.service';
import { AuthService } from '../../../../core/services/auth.service';
import Swal from 'sweetalert2';

interface ComplaintMessage {
  id?: number;
  author: string;
  authorRole: string;
  content: string;
  timestamp: Date;
  isAdmin: boolean;
}

@Component({
  selector: 'app-complaint-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complaint-detail.component.html',
  styleUrls: ['./complaint-detail.component.css']
})
export class ComplaintDetailComponent implements OnInit {
  complaint: ComplaintWithUser | null = null;
  complaintHistory: ComplaintWorkflow[] = [];
  messages: ComplaintMessage[] = [];
  
  isLoading = false;
  showActionModal = false;
  
  // Action modal
  selectedAction: string = '';
  actionComment: string = '';
  rejectReason: string = '';
  
  // New message
  newMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private complaintService: ComplaintService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadComplaintDetails(+id);
    }
  }

  loadComplaintDetails(id: number): void {
    this.isLoading = true;
    
    this.complaintService.getComplaintById(id).subscribe({
      next: (data: any) => {
        this.complaint = data;
        
        // Calculate daysSinceCreation if not present
        if (this.complaint && this.complaint.createdAt && !this.complaint.daysSinceCreation) {
          const createdDate = new Date(this.complaint.createdAt);
          const now = new Date();
          const diffTime = Math.abs(now.getTime() - createdDate.getTime());
          const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
          this.complaint.daysSinceCreation = diffDays;
        }
        
        this.loadHistory(id);
        this.loadMessages(id);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading complaint:', error);
        Swal.fire('Error', 'Failed to load complaint details', 'error');
        this.isLoading = false;
      }
    });
  }

  loadHistory(id: number): void {
    this.complaintService.getComplaintHistory(id).subscribe({
      next: (history) => {
        this.complaintHistory = history;
      },
      error: (error) => {
        console.error('Error loading history:', error);
      }
    });
  }

  loadMessages(id: number): void {
    this.complaintService.getMessages(id).subscribe({
      next: (messages) => {
        this.messages = messages;
      },
      error: (error) => {
        console.error('Error loading messages:', error);
      }
    });
  }

  sendMessage(): void {
    if (!this.newMessage.trim() || !this.complaint) return;

    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return;

    const messageData = {
      authorId: currentUser.id,
      authorRole: 'ACADEMIC_OFFICE_AFFAIR',
      content: this.newMessage
    };

    this.complaintService.sendMessage(this.complaint.id, messageData).subscribe({
      next: (message) => {
        this.messages.push(message);
        this.newMessage = '';
      },
      error: (error) => {
        console.error('Error sending message:', error);
        Swal.fire('Error', 'Failed to send message', 'error');
      }
    });
  }

  openActionModal(): void {
    this.selectedAction = '';  // Reset selection when opening modal
    this.actionComment = '';
    this.rejectReason = '';
    this.showActionModal = true;
  }

  executeAction(): void {
    console.log('🔍 executeAction called');
    console.log('🔍 selectedAction:', this.selectedAction);
    console.log('🔍 complaint:', this.complaint);
    
    if (!this.complaint) {
      console.error('❌ No complaint');
      return;
    }

    const currentUser = this.authService.currentUserValue;
    if (!currentUser) {
      console.error('❌ No current user');
      return;
    }

    console.log('✅ Current user:', currentUser);

    let newStatus = this.complaint.status;
    let comment = this.actionComment;

    switch (this.selectedAction) {
      case 'REQUEST_INFO':
        comment = `Admin requested more information: ${this.actionComment}`;
        break;
      case 'FORWARD_TUTOR':
        comment = 'Complaint forwarded to tutor';
        break;
      case 'ESCALATE':
        newStatus = 'IN_PROGRESS';
        comment = 'Priority escalated';
        break;
      case 'IN_PROGRESS':
        newStatus = 'IN_PROGRESS';
        comment = this.actionComment || 'Complaint marked as in progress';
        break;
      case 'RESOLVE':
        newStatus = 'RESOLVED';
        comment = this.actionComment || 'Admin marked as resolved';
        break;
      case 'REJECT':
        if (!this.rejectReason.trim()) {
          Swal.fire('Error', 'Rejection reason is required', 'error');
          return;
        }
        newStatus = 'REJECTED';
        comment = `Rejected: ${this.rejectReason}`;
        break;
      default:
        console.error('❌ Unknown action:', this.selectedAction);
        Swal.fire('Error', 'Please select an action', 'error');
        return;
    }

    console.log('📊 Action data:', { newStatus, comment });

    const data = {
      status: newStatus,
      actorId: currentUser.id,
      actorRole: 'ACADEMIC_OFFICE_AFFAIR',
      response: this.actionComment,
      comment: comment
    };

    console.log('🚀 Sending data:', data);

    this.complaintService.updateComplaintStatus(this.complaint.id, data).subscribe({
      next: () => {
        console.log('✅ Action executed successfully');
        this.showActionModal = false;
        // Reload data first, then show success message
        this.loadComplaintDetails(this.complaint!.id);
        setTimeout(() => {
          Swal.fire('Success', 'Action executed successfully', 'success');
        }, 300);
      },
      error: (error) => {
        console.error('❌ Error executing action:', error);
        console.log('📊 Error details:', error);
        
        // Even if there's an error, reload the data to check if the action was actually executed
        this.showActionModal = false;
        this.loadComplaintDetails(this.complaint!.id);
        
        // Wait a bit to see if the data was actually updated
        setTimeout(() => {
          // If the status changed, it means the action was successful despite the error
          if (this.complaint && this.complaint.status === newStatus) {
            console.log('✅ Action was actually successful despite error response');
            Swal.fire('Success', 'Action executed successfully', 'success');
          } else {
            // Only show error if the action truly failed
            Swal.fire('Error', 'Failed to execute action', 'error');
          }
        }, 1000);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard/complaints']);
  }

  getPriorityClass(priority: string): string {
    const classes: any = {
      'URGENT': 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
      'HIGH': 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-200',
      'MEDIUM': 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
    };
    return classes[priority] || 'bg-gray-100 text-gray-800';
  }

  getStatusClass(status: string): string {
    const classes: any = {
      'OPEN': 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
      'IN_PROGRESS': 'bg-indigo-100 text-indigo-800 dark:bg-indigo-900 dark:text-indigo-200',
      'RESOLVED': 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
      'PENDING_STUDENT_CONFIRMATION': 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200',
      'REJECTED': 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-200'
    };
    return classes[status] || 'bg-gray-100 text-gray-800';
  }

  getSeverityBadge(priority: string): { text: string; class: string } {
    const badges: any = {
      'MEDIUM': { text: 'Medium Severity', class: 'bg-yellow-500' },
      'HIGH': { text: 'High Severity', class: 'bg-orange-500' },
      'URGENT': { text: 'Urgent Severity', class: 'bg-red-500' }
    };
    return badges[priority] || badges['MEDIUM'];
  }

  getDaysOpenBadge(days: number): { text: string; class: string } {
    if (days >= 5) {
      return { text: `${days} days - Overdue`, class: 'bg-red-500' };
    } else if (days >= 2) {
      return { text: `${days} days - Delayed`, class: 'bg-orange-500' };
    }
    return { text: `${days} days waiting`, class: 'bg-blue-500' };
  }

  closeModal(): void {
    this.showActionModal = false;
    this.selectedAction = '';
    this.actionComment = '';
    this.rejectReason = '';
  }
}
