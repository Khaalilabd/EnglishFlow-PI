import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RefundService } from '../../../core/services/refund.service';
import { Refund } from '../../../core/models/refund.model';

@Component({
  selector: 'app-refund-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './refund-history.component.html',
  styleUrl: './refund-history.component.scss'
})
export class RefundHistoryComponent implements OnInit {
  refunds: Refund[] = [];
  loading = false;
  errorMessage: string = '';

  constructor(
    private refundService: RefundService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadRefunds();
  }

  loadRefunds(): void {
    this.loading = true;
    this.refundService.getMyRefunds().subscribe({
      next: (refunds) => {
        this.refunds = refunds;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading refunds:', error);
        this.errorMessage = 'Failed to load refund history';
        this.loading = false;
      }
    });
  }

  getStatusClass(status: string): string {
    const statusClasses: { [key: string]: string } = {
      'PENDING': 'status-pending',
      'APPROVED': 'status-approved',
      'REJECTED': 'status-rejected',
      'PROCESSING': 'status-processing',
      'COMPLETED': 'status-completed',
      'FAILED': 'status-failed',
      'CANCELLED': 'status-cancelled'
    };
    return statusClasses[status] || 'status-default';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  }

  requestNewRefund(): void {
    this.router.navigate(['/user-panel/refund-request']);
  }
}
