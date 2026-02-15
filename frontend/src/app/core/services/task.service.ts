import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskStatus, CreateTaskRequest, UpdateTaskRequest } from '../models/task.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = 'http://localhost:8084/api/tasks';

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
    return this.http.post<Task>(this.apiUrl, task);
  }

  updateTask(id: number, task: UpdateTaskRequest): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  countTasksByStatus(clubId: number, status: TaskStatus): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/club/${clubId}/count/${status}`);
  }
}
