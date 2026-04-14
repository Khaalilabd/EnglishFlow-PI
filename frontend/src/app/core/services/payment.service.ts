import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { InitiatePaymentRequest, Payment, PaymentStats } from '../models/payment.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {

  private readonly base = `${environment.apiUrl}/payments`;

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
}
