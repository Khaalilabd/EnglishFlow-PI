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
  email?: string;
  firstName?: string;
  lastName?: string;
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
  isActive?: boolean;
  registrationFeePaid?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8081/auth/admin/users';

  constructor(private http: HttpClient) {}

  // Get all users
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  // Get users by role
  getUsersByRole(role: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/role/${role}`);
  }

  // Get user by ID
  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  // Create user
  createUser(user: CreateUserRequest): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }

  // Update user
  updateUser(id: number, user: UpdateUserRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user);
  }

  // Delete user
  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Activate user
  activateUser(id: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}/activate`, {});
  }

  // Deactivate user
  deactivateUser(id: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}/deactivate`, {});
  }

  // Update profile photo
  updateProfilePhoto(id: number, photoBase64: string): Observable<User> {
    return this.http.patch<User>(`${this.apiUrl}/${id}/photo`, { profilePhoto: photoBase64 });
  }
}
