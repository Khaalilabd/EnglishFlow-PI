import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { Sponsor, CreateSponsorRequest } from '../models/sponsor.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SponsorService {
  private apiUrl = `${environment.apiUrl}/sponsors`;
  
  // Cache for sponsors list
  private sponsorsCache$ = new BehaviorSubject<Sponsor[] | null>(null);
  private cacheTimestamp: number = 0;
  private readonly CACHE_DURATION = 5 * 60 * 1000; // 5 minutes

  constructor(private http: HttpClient) {}

  getAllSponsors(forceRefresh: boolean = false): Observable<Sponsor[]> {
    const now = Date.now();
    const isCacheValid = this.sponsorsCache$.value && (now - this.cacheTimestamp) < this.CACHE_DURATION;
    
    if (!forceRefresh && isCacheValid) {
      return new Observable(observer => {
        observer.next(this.sponsorsCache$.value!);
        observer.complete();
      });
    }
    
    return this.http.get<Sponsor[]>(`${this.apiUrl}/approved`).pipe(
      tap(sponsors => {
        this.sponsorsCache$.next(sponsors);
        this.cacheTimestamp = now;
      })
    );
  }

  getSponsorById(id: number): Observable<Sponsor> {
    return this.http.get<Sponsor>(`${this.apiUrl}/${id}`);
  }

  getSponsorsByLevel(level: string): Observable<Sponsor[]> {
    return this.http.get<Sponsor[]>(`${this.apiUrl}/level/${level}`);
  }

  createSponsor(sponsor: CreateSponsorRequest): Observable<Sponsor> {
    return this.http.post<Sponsor>(this.apiUrl, sponsor).pipe(
      tap(() => this.invalidateCache())
    );
  }

  getSponsorsByUser(userId: number): Observable<Sponsor[]> {
    return this.http.get<Sponsor[]>(`${this.apiUrl}/user/${userId}`);
  }

  createClubSponsorRequest(request: {
    name: string; description?: string; logo?: string; website?: string;
    contactEmail?: string; contactPhone?: string;
    contributionAmount: number; userId: number;
    applicantFirstName: string; applicantLastName: string;
    clubId: number; clubName: string;
  }): Observable<Sponsor> {
    return this.http.post<Sponsor>(this.apiUrl, request).pipe(
      tap(() => this.invalidateCache())
    );
  }

  updateSponsor(id: number, sponsor: CreateSponsorRequest): Observable<Sponsor> {
    return this.http.put<Sponsor>(`${this.apiUrl}/${id}`, sponsor).pipe(
      tap(() => this.invalidateCache())
    );
  }

  deleteSponsor(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.invalidateCache())
    );
  }
  
  // Invalidate cache when data changes
  private invalidateCache(): void {
    console.log('🗑️ Invalidating sponsors cache');
    this.sponsorsCache$.next(null);
    this.cacheTimestamp = 0;
  }
  
  // Get cached sponsors as observable
  get sponsors$(): Observable<Sponsor[] | null> {
    return this.sponsorsCache$.asObservable();
  }
}
