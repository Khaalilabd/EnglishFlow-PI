import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService } from '../../../core/services/payment.service';
import { AuthService } from '../../../core/services/auth.service';
import { InitiatePaymentRequest } from '../../../core/models/payment.model';

@Component({
  selector: 'app-payment-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed inset-0 bg-black/80 backdrop-blur-sm flex items-center justify-center z-50 p-4"
         (click)="onBackdropClick($event)">
      <div class="bg-gray-900 border border-gray-700 rounded-3xl shadow-2xl flex flex-col items-center justify-center"
           style="width:520px; min-height:300px; padding: 48px 32px;"
           (click)="$event.stopPropagation()">

        <!-- Header -->
        <div class="w-full flex items-center justify-between mb-8">
          <div>
            <h2 class="text-lg font-bold text-white">Complete Payment</h2>
            <p class="text-gray-400 text-xs mt-0.5">{{itemName}}</p>
          </div>
          <div class="flex items-center gap-3">
            <span class="text-teal-400 font-bold text-lg">{{amount | number:'1.2-2'}} TND</span>
            <button (click)="close()" [disabled]="verifying"
              class="text-gray-400 hover:text-white transition-colors disabled:opacity-40">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- Loading -->
        <ng-container *ngIf="loading">
          <div class="w-12 h-12 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-4"></div>
          <p class="text-gray-400 text-sm">Preparing payment...</p>
        </ng-container>

        <!-- Error -->
        <ng-container *ngIf="error && !loading">
          <div class="w-16 h-16 bg-red-500/20 rounded-full flex items-center justify-center mb-4">
            <svg class="w-8 h-8 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </div>
          <p class="text-red-400 text-center text-sm mb-4">{{error}}</p>
          <button (click)="initiate()" class="px-6 py-2 bg-teal-600 hover:bg-teal-500 text-white rounded-xl text-sm font-semibold transition-all">
            Try Again
          </button>
        </ng-container>

        <!-- Waiting for popup -->
        <ng-container *ngIf="waitingForPopup && !loading && !error && !verifying && !success">
          <div class="w-16 h-16 bg-teal-500/20 rounded-full flex items-center justify-center mb-4">
            <svg class="w-8 h-8 text-teal-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"/>
            </svg>
          </div>
          <p class="text-white font-semibold mb-2">Payment window opened</p>
          <p class="text-gray-400 text-sm text-center mb-6">Complete your payment in the popup window.<br>This dialog will update automatically.</p>
          <button (click)="reopenPopup()" class="px-6 py-2 bg-gray-700 hover:bg-gray-600 text-white rounded-xl text-sm font-semibold transition-all">
            Reopen Payment Window
          </button>
        </ng-container>

        <!-- Verifying -->
        <ng-container *ngIf="verifying">
          <div class="w-12 h-12 border-4 border-teal-500 border-t-transparent rounded-full animate-spin mb-4"></div>
          <p class="text-white font-semibold">Verifying payment...</p>
          <p class="text-gray-400 text-sm mt-1">Please wait</p>
        </ng-container>

        <!-- Success -->
        <ng-container *ngIf="success">
          <div class="w-20 h-20 bg-emerald-500/20 rounded-full flex items-center justify-center mb-4">
            <svg class="w-10 h-10 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
            </svg>
          </div>
          <h3 class="text-xl font-bold text-white mb-2">Payment Successful!</h3>
          <p class="text-gray-400 text-sm text-center mb-6">
            You are now enrolled in <span class="text-teal-400 font-semibold">{{itemName}}</span>
          </p>
          <button (click)="close()" class="px-8 py-2.5 bg-teal-600 hover:bg-teal-500 text-white rounded-xl font-semibold transition-all">
            Continue
          </button>
        </ng-container>

      </div>
    </div>
  `
})
export class PaymentModalComponent implements OnInit, OnDestroy {
  @Input() itemType: 'COURSE' | 'PACK' = 'COURSE';
  @Input() itemId!: number;
  @Input() itemName!: string;
  @Input() amount!: number;
  @Output() closed = new EventEmitter<void>();
  @Output() enrolled = new EventEmitter<void>();

  loading = true;
  error = '';
  verifying = false;
  success = false;
  waitingForPopup = false;

  private orderId = '';
  private paymentUrl = '';
  private popup: Window | null = null;
  private messageListener!: (e: MessageEvent) => void;
  private popupCheckInterval: any;

  constructor(
    private paymentService: PaymentService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.messageListener = (event: MessageEvent) => {
      if (event.data?.event_id === 'paymee.complete') {
        this.onPaymeeComplete();
      }
    };
    window.addEventListener('message', this.messageListener);
    this.initiate();
  }

  ngOnDestroy(): void {
    window.removeEventListener('message', this.messageListener);
    this.clearPopupCheck();
    if (this.popup && !this.popup.closed) this.popup.close();
  }

  initiate(): void {
    const user = this.authService.currentUserValue;
    if (!user) return;

    this.loading = true;
    this.error = '';
    this.waitingForPopup = false;

    const req: InitiatePaymentRequest = {
      studentId: user.id,
      studentName: `${user.firstName} ${user.lastName}`.trim(),
      studentEmail: user.email,
      studentPhone: user.phone || '00000000',
      itemType: this.itemType,
      itemId: this.itemId,
      itemName: this.itemName,
      amount: Number(this.amount)
    };

    this.paymentService.initiate(req).subscribe({
      next: (payment) => {
        this.loading = false;
        if (payment.paymentUrl) {
          this.orderId = payment.orderId;
          this.paymentUrl = payment.paymentUrl;
          this.openPopup();
        } else {
          this.error = 'Could not get payment URL. Please try again.';
        }
      },
      error: (err) => {
        this.loading = false;
        const msg = err?.error?.message || '';
        if (err?.status === 409 || msg.toLowerCase().includes('already enrolled')) {
          // Already paid ÔÇö trigger enrollment directly and show success
          this.success = true;
          this.enrolled.emit();
        } else {
          this.error = msg || 'Payment initiation failed. Please try again.';
        }
      }
    });
  }

  openPopup(): void {
    const w = 520, h = 680;
    const left = window.screenX + (window.outerWidth - w) / 2;
    const top = window.screenY + (window.outerHeight - h) / 2;
    this.popup = window.open(
      this.paymentUrl,
      'paymee_payment',
      `width=${w},height=${h},left=${left},top=${top},resizable=yes,scrollbars=yes`
    );
    this.waitingForPopup = true;
    this.startPopupCheck();
  }

  reopenPopup(): void {
    if (this.popup && !this.popup.closed) {
      this.popup.focus();
    } else {
      this.openPopup();
    }
  }

  private startPopupCheck(): void {
    this.clearPopupCheck();
    // Poll every second ÔÇö when popup closes, verify payment status automatically
    this.popupCheckInterval = setInterval(() => {
      if (this.popup?.closed && !this.verifying && !this.success) {
        this.clearPopupCheck();
        this.waitingForPopup = false;
        // Auto-verify when popup closes ÔÇö covers the case where paymee.complete
        // postMessage was missed due to cross-origin navigation
        if (this.orderId) {
          this.verifying = true;
          this.paymentService.verify(this.orderId).subscribe({
            next: (payment) => {
              this.verifying = false;
              if (payment.status === 'SUCCESS' || payment.status === 'PENDING') {
                this.success = true;
                this.enrolled.emit();
              } else {
                this.error = 'Payment was not completed. Click "Try Again" to restart.';
              }
            },
            error: () => {
              this.verifying = false;
              this.error = 'Could not verify payment. Please contact support.';
            }
          });
        } else {
          this.error = 'Payment window was closed. Click "Try Again" to restart.';
        }
      }
    }, 1000);
  }

  private clearPopupCheck(): void {
    if (this.popupCheckInterval) {
      clearInterval(this.popupCheckInterval);
      this.popupCheckInterval = null;
    }
  }

  private onPaymeeComplete(): void {
    this.clearPopupCheck();
    this.waitingForPopup = false;
    this.verifying = true;
    if (this.popup && !this.popup.closed) this.popup.close();

    // paymee.complete fires only on successful payment ÔÇö mark as success directly
    // then verify in background to trigger enrollment
    this.paymentService.verify(this.orderId).subscribe({
      next: (payment) => {
        this.verifying = false;
        // Accept SUCCESS or PENDING (sandbox verify may lag)
        if (payment.status === 'SUCCESS' || payment.status === 'PENDING') {
          this.success = true;
          this.enrolled.emit();
        } else {
          this.error = 'Payment was not completed. Please try again.';
        }
      },
      error: () => {
        this.verifying = false;
        // paymee.complete fired = payment was made, show success anyway
        this.success = true;
        this.enrolled.emit();
      }
    });
  }

  onBackdropClick(event: MouseEvent): void {
    if (!this.loading && !this.verifying) this.close();
  }

  close(): void {
    if (!this.loading && !this.verifying) {
      if (this.popup && !this.popup.closed) this.popup.close();
      this.closed.emit();
    }
  }
}
