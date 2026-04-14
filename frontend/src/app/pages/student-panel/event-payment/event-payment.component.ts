import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { EventPaymentService, EventParticipantPayment } from '../../../core/services/event-payment.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-event-payment',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './event-payment.component.html'
})
export class EventPaymentComponent implements OnInit {
  participant: EventParticipantPayment | null = null;
  loading = true;
  processing = false;
  error: string | null = null;
  paymentStatus: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventPaymentService: EventPaymentService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    const participantId = Number(this.route.snapshot.paramMap.get('participantId'));
    this.paymentStatus = this.route.snapshot.queryParamMap.get('status');
    const method = this.route.snapshot.queryParamMap.get('method');

    if (this.paymentStatus === 'success' && method) {
      const paymentToken = this.route.snapshot.queryParamMap.get('payment_ref')
        || this.route.snapshot.queryParamMap.get('payment_id')
        || 'TOKEN_' + Date.now();
      this.confirmPayment(participantId, method, paymentToken);
    } else {
      this.loadParticipant(participantId);
    }
  }

  loadParticipant(participantId: number) {
    this.eventPaymentService.getParticipant(participantId).subscribe({
      next: (p) => {
        this.participant = p;
        this.loading = false;
        if (p.paymentStatus === 'PAID') {
          this.paymentStatus = 'already_paid';
        }
      },
      error: () => {
        this.error = 'Participant not found.';
        this.loading = false;
      }
    });
  }

  confirmPayment(participantId: number, method: string, token: string) {
    this.eventPaymentService.confirmPayment(participantId, method, token).subscribe({
      next: () => {
        this.paymentStatus = 'confirmed';
        this.loading = false;
      },
      error: () => {
        this.error = 'Error confirming payment.';
        this.loading = false;
      }
    });
  }

  payWithKonnect() {
    if (!this.participant) return;
    this.processing = true;
    const user = this.authService.currentUserValue;

    this.eventPaymentService.initKonnectPayment(
      this.participant.id,
      this.participant.participationFee,
      user?.firstName || 'Student',
      user?.email || ''
    ).subscribe({
      next: (res) => { window.location.href = res.payUrl; },
      error: () => {
        this.error = 'Error initializing payment.';
        this.processing = false;
      }
    });
  }

  goToEvents() {
    this.router.navigate(['/user-panel/events']);
  }
}
