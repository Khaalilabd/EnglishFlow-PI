import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentService, MembershipRequestPayment } from '../../../core/services/payment.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-club-payment',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './club-payment.component.html',
  styleUrls: ['./club-payment.component.scss']
})
export class ClubPaymentComponent implements OnInit {
  request: MembershipRequestPayment | null = null;
  loading = true;
  processing = false;
  error: string | null = null;
  paymentStatus: string | null = null; // 'success' | 'failed' | null
  selectedMethod: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    const requestId = Number(this.route.snapshot.paramMap.get('requestId'));
    this.paymentStatus = this.route.snapshot.queryParamMap.get('status');
    const method = this.route.snapshot.queryParamMap.get('method');

    if (this.paymentStatus === 'success' && method) {
      // Retour depuis la passerelle de paiement — confirmer le paiement
      const paymentToken = this.route.snapshot.queryParamMap.get('payment_ref') 
        || this.route.snapshot.queryParamMap.get('payment_id') 
        || 'TOKEN_' + Date.now();
      this.confirmPayment(requestId, method, paymentToken);
    } else {
      this.loadRequest(requestId);
    }
  }

  loadRequest(requestId: number) {
    this.paymentService.getMembershipRequest(requestId).subscribe({
      next: (req) => {
        this.request = req;
        this.loading = false;
        if (req.status === 'APPROVED') {
          this.paymentStatus = 'already_paid';
        }
      },
      error: () => {
        this.error = 'Demande introuvable.';
        this.loading = false;
      }
    });
  }

  confirmPayment(requestId: number, method: string, token: string) {
    this.paymentService.confirmPayment(requestId, method, token).subscribe({
      next: () => {
        this.paymentStatus = 'confirmed';
        this.loading = false;
      },
      error: () => {
        this.error = 'Erreur lors de la confirmation du paiement.';
        this.loading = false;
      }
    });
  }

  payWithKonnect() {
    if (!this.request) return;
    this.processing = true;
    const user = this.authService.currentUserValue;
    const amount = this.request.registrationFee ?? 0;

    this.paymentService.initKonnectPayment(
      this.request.id,
      amount,
      user?.firstName || 'Student',
      user?.email || ''
    ).subscribe({
      next: (res) => {
        window.location.href = res.payUrl;
      },
      error: () => {
        this.error = 'Erreur lors de l\'initialisation du paiement Konnect.';
        this.processing = false;
      }
    });
  }

  simulatePayment() {
    if (!this.request) return;
    this.processing = true;
    const token = 'DEV_SIM_' + Date.now();
    this.confirmPayment(this.request.id, 'SIMULATION', token);
  }

  goToClubs() {
    this.router.navigate(['/user-panel/clubs']);
  }
}
