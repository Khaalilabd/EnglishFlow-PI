import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ComplaintService, ComplaintWorkflow } from '../../../../core/services/complaint.service';
import { AuthService } from '../../../../core/services/auth.service';
import Swal from 'sweetalert2';
import { interval, Subscription } from 'rxjs';

interface ComplaintMessage {
  id?: number;
  author: string;
  authorRole: string;
  content: string;
  timestamp: Date;
  isStudent: boolean;
}

@Component({
  selector: 'app-student-complaint-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complaint-detail.component.html',
  styleUrls: ['./complaint-detail.component.css']
})
export class StudentComplaintDetailComponent implements OnInit, OnDestroy {
  complaint: any = null;
  complaintHistory: ComplaintWorkflow[] = [];
  messages: ComplaintMessage[] = [];
  
  isLoading = false;
  newMessage: string = '';
  
  private pollingSubscription?: Subscription;

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
      this.startPolling(+id);
    }
  }

  ngOnDestroy(): void {
    this.stopPolling();
  }

  startPolling(id: number): void {
    // Poll for new messages every 5 seconds
    this.pollingSubscription = interval(5000).subscribe(() => {
      this.loadMessages(id, true);
    });
  }

  stopPolling(): void {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
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

  loadMessages(id: number, silent: boolean = false): void {
    this.complaintService.getMessages(id).subscribe({
      next: (messages) => {
        this.messages = messages;
      },
      error: (error) => {
        if (!silent) {
          console.error('Error loading messages:', error);
        }
      }
    });
  }

  sendMessage(): void {
    if (!this.newMessage.trim() || !this.complaint) return;

    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return;

    const messageData = {
      authorId: currentUser.id,
      authorRole: 'STUDENT',
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

  goBack(): void {
    this.router.navigate(['/user-panel/complaints']);
  }

  getPriorityClass(priority: string): string {
    const classes: any = {
      'CRITICAL': 'bg-red-100 text-red-800',
      'HIGH': 'bg-orange-100 text-orange-800',
      'MEDIUM': 'bg-yellow-100 text-yellow-800',
      'LOW': 'bg-gray-100 text-gray-800'
    };
    return classes[priority] || 'bg-gray-100 text-gray-800';
  }

  getStatusClass(status: string): string {
    const classes: any = {
      'OPEN': 'bg-blue-100 text-blue-800',
      'NOTED': 'bg-teal-100 text-teal-800',
      'IN_PROGRESS': 'bg-indigo-100 text-indigo-800',
      'RESOLVED': 'bg-green-100 text-green-800',
      'REJECTED': 'bg-gray-100 text-gray-800'
    };
    return classes[status] || 'bg-gray-100 text-gray-800';
  }

  formatDate(dateString?: string | Date): string {
    if (!dateString) return 'N/A';
    const date = typeof dateString === 'string' ? new Date(dateString) : dateString;
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getTargetRole(role: string): string {
    const roles: any = {
      'TUTOR': 'Tutor',
      'ACADEMIC_OFFICE_AFFAIR': 'Academic Office',
      'SUPPORT': 'Technical Support',
      'MANAGER': 'Manager'
    };
    return roles[role] || role;
  }
}
