import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { InitiatePaymentRequest, Payment, PaymentStats } from '../models/payment.model';

export interface MembershipRequestPayment {
  id: number;
  clubId: number;
  clubName: string;
  registrationFee?: number;
  userId: number;
  status: string; // PENDING | APPROVED | REJECTED
  paymentMethod?: string;
  paymentToken?: string;
  paymentConfirmedAt?: string;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {

  private readonly base = `${environment.apiUrl}/payments`;
  private readonly clubsBase = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  initiate(req: InitiatePaymentRequest): Observable<Payment> {
    return this.http.post<Payment>(`${this.base}/initiate`, req);
  }

  verify(orderId: string): Observable<Payment> {
    return this.http.get<Payment>(`${this.base}/verify/${orderId}`);
  }

  getMyPayments(studentId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.base}/student/${studentId}`);
  }

  getAllPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(this.base);
  }

  getStats(): Observable<PaymentStats> {
    return this.http.get<PaymentStats>(`${this.base}/stats`);
  }

  getMembershipRequest(requestId: number): Observable<MembershipRequestPayment> {
    return this.http.get<MembershipRequestPayment>(`${this.clubsBase}/membership-requests/${requestId}`);
  }

  confirmPayment(requestId: number, paymentMethod: string, paymentToken: string): Observable<MembershipRequestPayment> {
    return this.http.post<MembershipRequestPayment>(`${this.clubsBase}/membership-requests/${requestId}/confirm-payment`, {
      paymentMethod,
      paymentToken
    });
  }

  initKonnectPayment(requestId: number, amount: number, firstName: string, email: string): Observable<{ payUrl: string }> {
    return this.http.post<{ payUrl: string }>(`${this.clubsBase}/membership-requests/${requestId}/init-konnect-payment`, {
      amount,
      firstName,
      email
    });
  }
}
