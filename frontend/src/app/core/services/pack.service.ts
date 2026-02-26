import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pack, PackStatus } from '../models/pack.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PackService {
  private apiUrl = `${environment.apiUrl}/packs`;

  constructor(private http: HttpClient) {}

  createPack(pack: Pack): Observable<Pack> {
    return this.http.post<Pack>(this.apiUrl, pack);
  }

  updatePack(id: number, pack: Pack): Observable<Pack> {
    return this.http.put<Pack>(`${this.apiUrl}/${id}`, pack);
  }

  getById(id: number): Observable<Pack> {
    return this.http.get<Pack>(`${this.apiUrl}/${id}`);
  }

  getAllPacks(): Observable<Pack[]> {
    return this.http.get<Pack[]>(this.apiUrl);
  }

  getByTutorId(tutorId: number): Observable<Pack[]> {
    return this.http.get<Pack[]>(`${this.apiUrl}/tutor/${tutorId}`);
  }

  getByStatus(status: PackStatus): Observable<Pack[]> {
    return this.http.get<Pack[]>(`${this.apiUrl}/status/${status}`);
  }

  searchPacks(category: string, level: string): Observable<Pack[]> {
    const params = new HttpParams()
      .set('category', category)
      .set('level', level);
    return this.http.get<Pack[]>(`${this.apiUrl}/search`, { params });
  }

  getAvailablePacks(category?: string, level?: string): Observable<Pack[]> {
    let params = new HttpParams();
    if (category) params = params.set('category', category);
    if (level) params = params.set('level', level);
    return this.http.get<Pack[]>(`${this.apiUrl}/available`, { params });
  }

  getByCreatedBy(academicId: number): Observable<Pack[]> {
    return this.http.get<Pack[]>(`${this.apiUrl}/academic/${academicId}`);
  }

  deletePack(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
