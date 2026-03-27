import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface FileUploadResponse {
  filename: string;
  originalFilename: string;
  url: string;
  contentType: string;
  size: string;
}

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {
  private apiUrl = `${environment.apiUrl}/files`;

  constructor(private http: HttpClient) {}

  uploadFile(file: File): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<FileUploadResponse>(`${this.apiUrl}/upload`, formData);
  }

  uploadFileWithProgress(file: File): Observable<{ progress: number; response?: FileUploadResponse }> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<FileUploadResponse>(`${this.apiUrl}/upload`, formData, {
      reportProgress: true,
      observe: 'events'
    }).pipe(
      map((event: HttpEvent<any>) => {
        if (event.type === HttpEventType.UploadProgress) {
          const progress = event.total ? Math.round((100 * event.loaded) / event.total) : 0;
          return { progress };
        } else if (event.type === HttpEventType.Response) {
          return { progress: 100, response: event.body };
        }
        return { progress: 0 };
      })
    );
  }

  deleteFile(filename: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${filename}`);
  }

  getFileUrl(filename: string): string {
    return `${environment.apiUrl}/files/${filename}`;
  }
}
