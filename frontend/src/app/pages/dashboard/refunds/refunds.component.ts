import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RefundService } from '../../../core/services/refund.service';
import {
  Refund,
  RefundStatus,
  RefundFilter,
  RefundStats
} from '../../../core/models/refund.model';

@Component({
  selector: 'app-refunds',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './refunds.component.html',
  styleUrls: ['./refunds.component.scss']
})
export class RefundsComponent implements OnInit {
  refunds: Refund[] = [];
  filteredRefunds: Refund[] = [];
  stats: RefundStats | null = null;
  loading = false;
  
  // Filters
  searchTerm = '';
  statusFilter: RefundStatus | '' = '';
  itemTypeFilter = '';
  startDate = '';
  endDate = '';
  
  // Selected refund for detail view
  selectedRefund: Refund | null = null;
  showDetailModal = false;
  
  // Reject dialog
  showRejectDialog = false;
  rejectionReason = '';
  refundToReject: Refund | null = null;
  
  // Status enum for template
  RefundStatus = RefundStatus;

  constructor(private refundService: RefundService) {}

  ngOnInit(): void {
    this.loadRefunds();
    this.loadStatistics();
  }

  loadRefunds(): void {
    this.loading = true;
    
    const filter: RefundFilter = {};
    if (this.statusFilter) filter.status = this.statusFilter;
    if (this.itemTypeFilter) filter.itemType = this.itemTypeFilter;
    if (this.startDate) filter.startDate = this.startDate;
    if (this.endDate) filter.endDate = this.endDate;
    
    this.refundService.getAllRefunds(filter).subscribe({
      next: (refunds) => {
        this.refunds = refunds;
        this.applySearchFilter();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading refunds:', error);
        this.loading = false;
      }
    });
  }

  loadStatistics(): void {
    this.refundService.getStatistics().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (error) => {
        console.error('Error loading statistics:', error);
      }
    });
  }

  applyFilters(): void {
    this.loadRefunds();
  }

  applySearchFilter(): void {
    if (!this.searchTerm.trim()) {
      this.filteredRefunds = [...this.refunds];
      return;
    }
    
    const term = this.searchTerm.toLowerCase();
    this.filteredRefunds = this.refunds.filter(refund =>
      refund.orderId?.toLowerCase().includes(term) ||
      refund.studentName?.toLowerCase().includes(term) ||
      refund.studentEmail?.toLowerCase().includes(term) ||
      refund.itemName?.toLowerCase().includes(term)
    );
  }

  viewDetails(refund: Refund): void {
    this.selectedRefund = refund;
    this.showDetailModal = true;
  }

  closeDetailModal(): void {
    this.showDetailModal = false;
    this.selectedRefund = null;
  }

  approveRefund(refund: Refund): void {
    if (!confirm(`Are you sure you want to approve this refund for ${refund.amount} TND?`)) {
      return;
    }
    
    this.refundService.approveRefund(refund.id).subscribe({
      next: (updatedRefund) => {
        this.updateRefundInList(updatedRefund);
        this.loadStatistics();
        alert('Refund approved successfully!');
        if (this.selectedRefund?.id === refund.id) {
          this.selectedRefund = updatedRefund;
        }
      },
      error: (error) => {
        console.error('Error approving refund:', error);
        alert('Failed to approve refund: ' + (error.error?.message || 'Unknown error'));
      }
    });
  }

  openRejectDialog(refund: Refund): void {
    this.refundToReject = refund;
    this.rejectionReason = '';
    this.showRejectDialog = true;
  }

  closeRejectDialog(): void {
    this.showRejectDialog = false;
    this.refundToReject = null;
    this.rejectionReason = '';
  }

  confirmReject(): void {
    if (!this.refundToReject) return;
    
    if (!this.rejectionReason.trim()) {
      alert('Please provide a rejection reason');
      return;
    }
    
    this.refundService.rejectRefund(this.refundToReject.id, this.rejectionReason).subscribe({
      next: (updatedRefund) => {
        this.updateRefundInList(updatedRefund);
        this.loadStatistics();
        alert('Refund rejected successfully!');
        if (this.selectedRefund?.id === this.refundToReject!.id) {
          this.selectedRefund = updatedRefund;
        }
        this.closeRejectDialog();
      },
      error: (error) => {
        console.error('Error rejecting refund:', error);
        alert('Failed to reject refund: ' + (error.error?.message || 'Unknown error'));
      }
    });
  }

  private updateRefundInList(updatedRefund: Refund): void {
    const index = this.refunds.findIndex(r => r.id === updatedRefund.id);
    if (index !== -1) {
      this.refunds[index] = updatedRefund;
      this.applySearchFilter();
    }
  }

  getStatusClass(status: RefundStatus): string {
    const classes: Record<RefundStatus, string> = {
      [RefundStatus.PENDING]: 'bg-amber-950/40 border border-amber-800/40 text-amber-300',
      [RefundStatus.APPROVED]: 'bg-blue-950/40 border border-blue-800/40 text-blue-300',
      [RefundStatus.REJECTED]: 'bg-red-950/40 border border-red-800/40 text-red-300',
      [RefundStatus.PROCESSING]: 'bg-purple-950/40 border border-purple-800/40 text-purple-300',
      [RefundStatus.COMPLETED]: 'bg-emerald-950/40 border border-emerald-800/40 text-emerald-300',
      [RefundStatus.FAILED]: 'bg-red-950/40 border border-red-800/40 text-red-300',
      [RefundStatus.CANCELLED]: 'bg-gray-800/40 border border-gray-700/40 text-gray-400'
    };
    return classes[status] || 'bg-gray-800/40 border border-gray-700/40 text-gray-400';
  }

  getItemTypeClass(itemType: string): string {
    return itemType === 'COURSE'
      ? 'bg-teal-950/40 border border-teal-800/40 text-teal-300'
      : 'bg-indigo-950/40 border border-indigo-800/40 text-indigo-300';
  }

  canApprove(refund: Refund): boolean {
    return refund.status === RefundStatus.PENDING;
  }

  canReject(refund: Refund): boolean {
    return refund.status === RefundStatus.PENDING;
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleString('en-GB', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
