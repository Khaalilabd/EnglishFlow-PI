import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Task, TaskStatus, CreateTaskRequest, UpdateTaskRequest } from '../models/task.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) {}

  getTasksByClubId(clubId: number, userId?: number): Observable<Task[]> {
    if (!userId) {
      console.error('TaskService: userId is required for getTasksByClubId');
      throw new Error('User ID is required');
    }
    return this.http.get<Task[]>(`${this.apiUrl}/club/${clubId}?userId=${userId}`);
  }

  getTasksByClubIdAndStatus(clubId: number, status: TaskStatus): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/club/${clubId}/status/${status}`);
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  createTask(task: CreateTaskRequest): Observable<Task> {
    if (!task.createdBy) {
      console.error('TaskService: createdBy is required for createTask');
      throw new Error('User ID is required');
    }
    return this.http.post<Task>(`${this.apiUrl}?userId=${task.createdBy}`, task);
  }

  updateTask(id: number, task: UpdateTaskRequest, userId?: number): Observable<Task> {
    if (!userId) {
      console.error('TaskService: userId is required for updateTask');
      throw new Error('User ID is required');
    }
    return this.http.put<Task>(`${this.apiUrl}/${id}?userId=${userId}`, task);
  }

  deleteTask(id: number, userId?: number): Observable<void> {
    if (!userId) {
      console.error('TaskService: userId is required for deleteTask');
      throw new Error('User ID is required');
    }
    return this.http.delete<void>(`${this.apiUrl}/${id}?userId=${userId}`);
  }

  countTasksByStatus(clubId: number, status: TaskStatus): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/club/${clubId}/count/${status}`);
  }
}
