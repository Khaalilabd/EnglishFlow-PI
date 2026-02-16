import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Category {
  id: number;
  name: string;
  description: string;
  icon: string;
  color: string;
  subCategories: SubCategory[];
}

export interface SubCategory {
  id: number;
  name: string;
  description: string;
  categoryId: number;
}

export interface Topic {
  id: number;
  title: string;
  content: string;
  userId: number;
  userName: string;
  subCategoryId: number;
  viewsCount: number;
  isPinned: boolean;
  isLocked: boolean;
  postsCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface Post {
  id: number;
  content: string;
  userId: number;
  userName: string;
  topicId: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTopicRequest {
  subCategoryId: number;
  title: string;
  content: string;
  userId: number;
  userName: string;
}

export interface CreatePostRequest {
  topicId: number;
  content: string;
  userId: number;
  userName: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class ForumService {
  private apiUrl = 'http://localhost:8082/api/community';

  constructor(private http: HttpClient) {}

  // Categories
  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/categories`);
  }

  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.apiUrl}/categories/${id}`);
  }

  initializeCategories(): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/categories/initialize`, {});
  }

  // Topics
  createTopic(request: CreateTopicRequest): Observable<Topic> {
    return this.http.post<Topic>(`${this.apiUrl}/topics`, request);
  }

  getTopicsBySubCategory(subCategoryId: number, page: number = 0, size: number = 20): Observable<PageResponse<Topic>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Topic>>(`${this.apiUrl}/topics/subcategory/${subCategoryId}`, { params });
  }

  getTopicById(id: number): Observable<Topic> {
    return this.http.get<Topic>(`${this.apiUrl}/topics/${id}`);
  }

  updateTopic(id: number, request: CreateTopicRequest): Observable<Topic> {
    return this.http.put<Topic>(`${this.apiUrl}/topics/${id}`, request);
  }

  deleteTopic(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/topics/${id}`);
  }

  // Posts
  createPost(request: CreatePostRequest): Observable<Post> {
    return this.http.post<Post>(`${this.apiUrl}/posts`, request);
  }

  getPostsByTopic(topicId: number, page: number = 0, size: number = 20): Observable<PageResponse<Post>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Post>>(`${this.apiUrl}/posts/topic/${topicId}`, { params });
  }

  updatePost(id: number, request: CreatePostRequest): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/posts/${id}`, request);
  }

  deletePost(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/posts/${id}`);
  }
}
