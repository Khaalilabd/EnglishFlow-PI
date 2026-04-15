import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../../../core/services/payment.service';
import { Payment, PaymentStats } from '../../../core/models/payment.model';

@Component({
  selector: 'app-payment-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-management.component.html'
})
export class PaymentManagementComponent implements OnInit {
  payments: Payment[] = [];
  filtered: Payment[] = [];
  stats: PaymentStats | null = null;
  loading = true;

  searchTerm = '';
  statusFilter = '';
  typeFilter = '';

  constructor(private paymentService: PaymentService) {}

  ngOnInit(): void {
    this.loadStats();
    this.loadPayments();
  }

  loadStats(): void {
    this.paymentService.getStats().subscribe({
      next: (s) => this.stats = s,
      error: () => {}
    });
  }

  loadPayments(): void {
    this.loading = true;
    this.paymentService.getAllPayments().subscribe({
      next: (p) => {
        this.payments = p;
        this.applyFilters();
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  applyFilters(): void {
    this.filtered = this.payments.filter(p => {
      const matchSearch = !this.searchTerm ||
        p.studentName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        p.studentEmail.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        p.itemName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        p.orderId.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchStatus = !this.statusFilter || p.status === this.statusFilter;
      const matchType = !this.typeFilter || p.itemType === this.typeFilter;
      return matchSearch && matchStatus && matchType;
    });
  }

  statusClass(status: string): string {
    switch (status) {
      case 'SUCCESS':   return 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30';
      case 'PENDING':   return 'bg-amber-500/20 text-amber-400 border border-amber-500/30';
      case 'FAILED':    return 'bg-red-500/20 text-red-400 border border-red-500/30';
      case 'CANCELLED': return 'bg-gray-500/20 text-gray-400 border border-gray-500/30';
      default:          return 'bg-gray-700 text-gray-300';
    }
  }

  typeClass(type: string): string {
    return type === 'PACK'
      ? 'bg-purple-500/20 text-purple-400 border border-purple-500/30'
      : 'bg-blue-500/20 text-blue-400 border border-blue-500/30';
  }
}
