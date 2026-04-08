import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Expense } from '../models/expense.model';

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {
  private apiUrl = `${environment.apiUrl}/expenses`;

  constructor(private http: HttpClient) {}

  getExpensesByClub(clubId: number): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.apiUrl}/club/${clubId}`);
  }

  getExpensesByClubAndDateRange(
    clubId: number,
    startDate: string,
    endDate: string
  ): Observable<Expense[]> {
    return this.http.get<Expense[]>(
      `${this.apiUrl}/club/${clubId}/range?startDate=${startDate}&endDate=${endDate}`
    );
  }

  getExpenseById(id: number): Observable<Expense> {
    return this.http.get<Expense>(`${this.apiUrl}/${id}`);
  }

  getTotalExpenses(clubId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/club/${clubId}/total`);
  }

  createExpense(expense: Expense): Observable<Expense> {
    return this.http.post<Expense>(this.apiUrl, expense);
  }

  updateExpense(id: number, expense: Expense): Observable<Expense> {
    return this.http.put<Expense>(`${this.apiUrl}/${id}`, expense);
  }

  deleteExpense(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
