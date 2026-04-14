import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface EventParticipantPayment {
  id: number;
  eventId: number;
  eventTitle: string;
  participationFee: number;
  userId: number;
  paymentStatus: string; // PAYMENT_PENDING | PAID
  paymentMethod?: string;
  paymentToken?: string;
  paymentConfirmedAt?: string;
  paymentDeadline?: string;
}

@Injectable({ providedIn: 'root' })
export class EventPaymentService {
  private apiUrl = `${environment.apiUrl}/events`;

  constructor(private http: HttpClient) {}

  getParticipant(participantId: number): Observable<EventParticipantPayment> {
    return this.http.get<EventParticipantPayment>(`${this.apiUrl}/participants/${participantId}`);
  }

  confirmPayment(participantId: number, paymentMethod: string, paymentToken: string): Observable<EventParticipantPayment> {
    return this.http.post<EventParticipantPayment>(`${this.apiUrl}/participants/${participantId}/confirm-payment`, {
      paymentMethod,
      paymentToken
    });
  }

  initKonnectPayment(participantId: number, amount: number, firstName: string, email: string): Observable<{ payUrl: string }> {
    return this.http.post<{ payUrl: string }>(`${this.apiUrl}/participants/${participantId}/init-konnect-payment`, {
      amount,
      firstName,
      email
    });
  }

  getTotalPayments(eventId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${eventId}/total-payments`);
  }

  getTotalPaymentsByClub(clubId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/club/${clubId}/total-payments`);
  }
}
