import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CourseCategory } from '../models/course-category.model';

@Injectable({
  providedIn: 'root'
})
export class CourseCategoryService {
  private apiUrl = `${environment.apiUrl}/categories`;

  constructor(private http: HttpClient) {}

  createCategory(category: CourseCategory): Observable<CourseCategory> {
    return this.http.post<CourseCategory>(this.apiUrl, category);
  }

  updateCategory(id: number, category: CourseCategory): Observable<CourseCategory> {
    return this.http.put<CourseCategory>(`${this.apiUrl}/${id}`, category);
  }

  getById(id: number): Observable<CourseCategory> {
    return this.http.get<CourseCategory>(`${this.apiUrl}/${id}`);
  }

  getAllCategories(): Observable<CourseCategory[]> {
    return this.http.get<CourseCategory[]>(this.apiUrl);
  }

  getActiveCategories(): Observable<CourseCategory[]> {
    return this.http.get<CourseCategory[]>(`${this.apiUrl}/active`);
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleActive(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/toggle-active`, null);
  }

  updateDisplayOrder(id: number, order: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/order?order=${order}`, null);
  }
}
