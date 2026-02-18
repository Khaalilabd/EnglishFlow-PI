import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  cin?: string;
  profilePhoto?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  bio?: string;
  englishLevel?: string;
  yearsOfExperience?: number;
  role: string;
  isActive: boolean;
  registrationFeePaid: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface UserDetails {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  image?: string;
}

export interface CreateUserRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  cin?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  bio?: string;
  englishLevel?: string;
  yearsOfExperience?: number;
  role: string;
}

export interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
  profilePhoto?: string;
  dateOfBirth?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  bio?: string;
  englishLevel?: string;
  yearsOfExperience?: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  getUsersByRole(role: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/admin/users/role/${role}`);
  }

  createUser(userData: CreateUserRequest): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/admin/users`, userData);
  }

  updateUser(userId: number, userData: UpdateUserRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${userId}`, userData);
  }

  activateUser(userId: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/admin/users/${userId}/activate`, {});
  }

  deactivateUser(userId: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/admin/users/${userId}/deactivate`, {});
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/admin/users/${userId}`);
  }

  // New methods for club member details
  getUserById(userId: number): Observable<UserDetails> {
    return this.http.get<UserDetails>(`${this.apiUrl}/users/${userId}`);
  }

  getUsersByIds(userIds: number[]): Observable<UserDetails[]> {
    return this.http.post<UserDetails[]>(`${this.apiUrl}/users/batch`, { userIds });
  }
}
