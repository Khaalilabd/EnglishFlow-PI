import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface MembershipRequestPayment {
  id: number;
  clubId: number;
  clubName: string;
  userId: number;
  status: string;
  registrationFee?: number;
  paymentMethod?: string;
  paymentToken?: string;
  paymentConfirmedAt?: string;
}

export interface KonnectPaymentResponse {
  payUrl: string;
  paymentRef: string;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private clubServiceUrl = `${environment.apiUrl}/membership-requests`;
  private konnectApiUrl = 'https://api.preprod.konnect.network/api/v2/payments/init-payment';

  constructor(private http: HttpClient) {}

  getMembershipRequest(requestId: number): Observable<MembershipRequestPayment> {
    return this.http.get<MembershipRequestPayment>(`${this.clubServiceUrl}/${requestId}`);
  }

  confirmPayment(requestId: number, paymentMethod: string, paymentToken: string): Observable<MembershipRequestPayment> {
    return this.http.post<MembershipRequestPayment>(`${this.clubServiceUrl}/${requestId}/confirm-payment`, {
      paymentMethod,
      paymentToken
    });
  }

  // Initier un paiement Konnect via le backend (évite les problèmes CORS)
  initKonnectPayment(requestId: number, amount: number, firstName: string, email: string): Observable<{ payUrl: string }> {
    return this.http.post<{ payUrl: string }>(`${this.clubServiceUrl}/${requestId}/init-konnect-payment`, {
      amount,
      firstName,
      email
    });
  }
}
