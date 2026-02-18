import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskStatus, CreateTaskRequest, UpdateTaskRequest } from '../models/task.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}

  getTasksByClubId(clubId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/club/${clubId}`);
  }

  getTasksByClubIdAndStatus(clubId: number, status: TaskStatus): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/club/${clubId}/status/${status}`);
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  createTask(task: CreateTaskRequest): Observable<Task> {
    // Add userId as query parameter
    const userId = task.createdBy || 0;
    return this.http.post<Task>(`${this.apiUrl}?userId=${userId}`, task);
  }

  updateTask(id: number, task: UpdateTaskRequest, userId?: number): Observable<Task> {
    const userIdParam = userId || 0;
    return this.http.put<Task>(`${this.apiUrl}/${id}?userId=${userIdParam}`, task);
  }

  deleteTask(id: number, userId?: number): Observable<void> {
    const userIdParam = userId || 0;
    return this.http.delete<void>(`${this.apiUrl}/${id}?userId=${userIdParam}`);
  }

  countTasksByStatus(clubId: number, status: TaskStatus): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/club/${clubId}/count/${status}`);
  }
}
