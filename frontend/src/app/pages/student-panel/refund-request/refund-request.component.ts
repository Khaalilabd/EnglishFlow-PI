import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PaymentService } from '../../../core/services/payment.service';
import { RefundService } from '../../../core/services/refund.service';
import { AuthService } from '../../../core/services/auth.service';
import { LessonProgressService } from '../../../core/services/lesson-progress.service';
import { Payment } from '../../../core/models/payment.model';
import { CreateRefundRequest } from '../../../core/models/refund.model';
import { CourseProgressSummary } from '../../../core/models/lesson-progress.model';

interface PaymentWithProgress extends Payment {
  progressPercentage?: number;
  progressLoading?: boolean;
  progressError?: boolean;
}

@Component({
  selector: 'app-refund-request',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './refund-request.component.html',
  styleUrl: './refund-request.component.scss'
})
export class RefundRequestComponent implements OnInit {
  payments: PaymentWithProgress[] = [];
  selectedPayment: PaymentWithProgress | null = null;
  refundReason: string = '';
  loading = false;
  showConfirmDialog = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private paymentService: PaymentService,
    private refundService: RefundService,
    private authService: AuthService,
    private lessonProgressService: LessonProgressService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadSuccessfulPayments();
  }

  loadSuccessfulPayments(): void {
    this.loading = true;
    const currentUser = this.authService.currentUserValue;
    const studentId = currentUser?.id;

    if (!studentId) {
      this.errorMessage = 'User not authenticated';
      this.loading = false;
      return;
    }

    this.paymentService.getMyPayments(studentId).subscribe({
      next: (payments) => {
        // Filter only successful payments
        this.payments = payments.filter(p => p.status === 'SUCCESS');
        
        // Load progress for each course payment
        this.loadProgressForPayments(studentId);
        
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading payments:', error);
        this.errorMessage = 'Failed to load payments';
        this.loading = false;
      }
    });
  }

  loadProgressForPayments(studentId: number): void {
    // Load progress for each COURSE payment
    this.payments.forEach(payment => {
      if (payment.itemType === 'COURSE') {
        payment.progressLoading = true;
        this.lessonProgressService.getCourseProgressSummary(studentId, payment.itemId).subscribe({
          next: (summary: CourseProgressSummary) => {
            payment.progressPercentage = summary.progressPercentage;
            payment.progressLoading = false;
          },
          error: (error) => {
            console.error(`Error loading progress for course ${payment.itemId}:`, error);
            payment.progressError = true;
            payment.progressLoading = false;
          }
        });
      } else if (payment.itemType === 'PACK') {
        // For packs, we'll show a note that progress will be checked on submission
        payment.progressPercentage = undefined;
      }
    });
  }

  selectPayment(payment: PaymentWithProgress): void {
    this.selectedPayment = payment;
    this.refundReason = '';
    this.errorMessage = '';
    this.successMessage = '';
  }

  isEligible(payment: PaymentWithProgress): boolean {
    // Check if payment is within 7-day window
    const paymentDate = new Date(payment.createdAt);
    const now = new Date();
    const daysDiff = Math.floor((now.getTime() - paymentDate.getTime()) / (1000 * 60 * 60 * 24));
    const withinTimeWindow = daysDiff <= 7;
    
    // Check progress threshold for courses (30%)
    if (payment.itemType === 'COURSE' && payment.progressPercentage !== undefined) {
      return withinTimeWindow && payment.progressPercentage <= 30;
    }
    
    // For packs or courses without progress data, only check time window
    return withinTimeWindow;
  }

  getEligibilityMessage(payment: PaymentWithProgress): string {
    const paymentDate = new Date(payment.createdAt);
    const now = new Date();
    const daysDiff = Math.floor((now.getTime() - paymentDate.getTime()) / (1000 * 60 * 60 * 24));
    const withinTimeWindow = daysDiff <= 7;
    
    if (!withinTimeWindow) {
      return 'Refund window expired (7-day limit)';
    }
    
    if (payment.itemType === 'COURSE') {
      if (payment.progressLoading) {
        return 'Checking eligibility...';
      }
      if (payment.progressError) {
        return 'Unable to check progress. You can still request a refund.';
      }
      if (payment.progressPercentage !== undefined) {
        if (payment.progressPercentage > 30) {
          return `Course progress (${payment.progressPercentage.toFixed(0)}%) exceeds 30% threshold`;
        }
        return `Eligible for refund (${this.getDaysRemaining(payment)} days remaining, ${payment.progressPercentage.toFixed(0)}% progress)`;
      }
    }
    
    if (payment.itemType === 'PACK') {
      return `Eligible for refund (${this.getDaysRemaining(payment)} days remaining, pack progress will be verified)`;
    }
    
    return `Eligible for refund (${this.getDaysRemaining(payment)} days remaining)`;
  }

  getDaysRemaining(payment: PaymentWithProgress): number {
    const paymentDate = new Date(payment.createdAt);
    const now = new Date();
    const daysDiff = Math.floor((now.getTime() - paymentDate.getTime()) / (1000 * 60 * 60 * 24));
    return Math.max(0, 7 - daysDiff);
  }

  openConfirmDialog(): void {
    if (!this.refundReason.trim()) {
      this.errorMessage = 'Please provide a reason for the refund request';
      return;
    }
    this.showConfirmDialog = true;
  }

  closeConfirmDialog(): void {
    this.showConfirmDialog = false;
  }

  submitRefundRequest(): void {
    if (!this.selectedPayment) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request: CreateRefundRequest = {
      paymentId: this.selectedPayment.id,
      reason: this.refundReason
    };

    this.refundService.createRefundRequest(request).subscribe({
      next: (refund) => {
        this.loading = false;
        this.showConfirmDialog = false;
        this.successMessage = 'Refund request submitted successfully! You will receive an email confirmation shortly.';
        this.selectedPayment = null;
        this.refundReason = '';
        // Reload payments to reflect the change
        setTimeout(() => {
          this.router.navigate(['/user-panel/refund-history']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error submitting refund request:', error);
        this.loading = false;
        this.showConfirmDialog = false;
        this.errorMessage = error.error?.message || 'Failed to submit refund request. Please try again.';
      }
    });
  }

  backToList(): void {
    this.selectedPayment = null;
    this.refundReason = '';
    this.errorMessage = '';
    this.successMessage = '';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  }
}
