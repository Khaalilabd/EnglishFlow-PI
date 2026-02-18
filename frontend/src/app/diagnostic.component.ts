import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Component({
  selector: 'app-diagnostic',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="padding: 20px; font-family: monospace;">
      <h1>API Diagnostic Tool</h1>
      
      <div style="margin: 20px 0;">
        <h2>Configuration</h2>
        <p><strong>API URL:</strong> {{ apiUrl }}</p>
        <p><strong>Environment:</strong> {{ environment.production ? 'Production' : 'Development' }}</p>
      </div>

      <div style="margin: 20px 0;">
        <button (click)="testConnection()" style="padding: 10px 20px; font-size: 16px; cursor: pointer;">
          Test API Connection
        </button>
      </div>

      <div *ngIf="loading" style="margin: 20px 0;">
        <p>Testing connection...</p>
      </div>

      <div *ngIf="result" style="margin: 20px 0; padding: 15px; background: #f0f0f0; border-radius: 5px;">
        <h3>Result:</h3>
        <pre style="white-space: pre-wrap; word-wrap: break-word;">{{ result }}</pre>
      </div>

      <div *ngIf="error" style="margin: 20px 0; padding: 15px; background: #ffebee; border-radius: 5px; color: #c62828;">
        <h3>Error:</h3>
        <pre style="white-space: pre-wrap; word-wrap: break-word;">{{ error }}</pre>
      </div>
    </div>
  `
})
export class DiagnosticComponent {
  apiUrl = environment.apiUrl;
  environment = environment;
  loading = false;
  result: string | null = null;
  error: string | null = null;

  constructor(private http: HttpClient) {}

  testConnection() {
    this.loading = true;
    this.result = null;
    this.error = null;

    console.log('Testing connection to:', `${this.apiUrl}/courses`);

    this.http.get(`${this.apiUrl}/courses`).subscribe({
      next: (data) => {
        this.loading = false;
        this.result = JSON.stringify(data, null, 2);
        console.log('Success! Data received:', data);
      },
      error: (err) => {
        this.loading = false;
        this.error = JSON.stringify({
          message: err.message,
          status: err.status,
          statusText: err.statusText,
          url: err.url,
          error: err.error
        }, null, 2);
        console.error('Error occurred:', err);
      }
    });
  }
}
