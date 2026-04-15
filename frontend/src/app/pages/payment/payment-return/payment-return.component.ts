import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentService } from '../../../core/services/payment.service';
import { Payment } from '../../../core/models/payment.model';

@Component({
  selector: 'app-payment-return',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-gray-950 flex items-center justify-center p-6">
      <div class="bg-gray-900 rounded-3xl border border-gray-800 p-10 max-w-md w-full text-center shadow-2xl">

        <!-- Loading -->
        <ng-container *ngIf="loading">
          <div class="w-16 h-16 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mx-auto mb-6"></div>
          <p class="text-white text-lg font-semibold">Verifying your payment...</p>
        </ng-container>

        <!-- Success -->
        <ng-container *ngIf="!loading && payment?.status === 'SUCCESS'">
          <div class="w-20 h-20 bg-emerald-500/20 rounded-full flex items-center justify-center mx-auto mb-6">
            <svg class="w-10 h-10 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
            </svg>
          </div>
          <h1 class="text-2xl font-bold text-white mb-2">Payment Successful!</h1>
          <p class="text-gray-400 mb-1">You are now enrolled in</p>
          <p class="text-teal-400 font-semibold text-lg mb-6">{{payment?.itemName}}</p>
          <p class="text-gray-500 text-sm mb-8">Amount paid: <span class="text-white font-semibold">{{payment?.amount | number:'1.2-2'}} TND</span></p>
          <button (click)="goToDashboard()" class="w-full py-3 bg-teal-600 hover:bg-teal-500 text-white rounded-xl font-semibold transition-all">
            Go to My Dashboard
          </button>
        </ng-container>

        <!-- Failed / Cancelled -->
        <ng-container *ngIf="!loading && payment?.status !== 'SUCCESS' && payment">
          <div class="w-20 h-20 bg-red-500/20 rounded-full flex items-center justify-center mx-auto mb-6">
            <svg class="w-10 h-10 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </div>
          <h1 class="text-2xl font-bold text-white mb-2">Payment {{cancelled ? 'Cancelled' : 'Failed'}}</h1>
          <p class="text-gray-400 mb-8">Your payment was not completed. No charges were made.</p>
          <button (click)="goBack()" class="w-full py-3 bg-gray-700 hover:bg-gray-600 text-white rounded-xl font-semibold transition-all">
            Go Back
          </button>
        </ng-container>

        <!-- Error -->
        <ng-container *ngIf="!loading && !payment">
          <p class="text-red-400">Could not verify payment. Please contact support.</p>
        </ng-container>

      </div>
    </div>
  `
})
export class PaymentReturnComponent implements OnInit {
  loading = true;
  payment: Payment | null = null;
  cancelled = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService
  ) {}

  ngOnInit(): void {
    const orderId = this.route.snapshot.queryParamMap.get('orderId');
    this.cancelled = this.route.snapshot.url.some(s => s.path === 'cancel');

    if (!orderId) { this.loading = false; return; }

    this.paymentService.verify(orderId).subscribe({
      next: (p) => { this.payment = p; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  goToDashboard(): void {
    const type = this.payment?.itemType;
    if (type === 'PACK') this.router.navigate(['/user-panel/my-packs']);
    else this.router.navigate(['/user-panel/my-courses']);
  }

  goBack(): void {
    this.router.navigate(['/user-panel/pack-catalog']);
  }
}
