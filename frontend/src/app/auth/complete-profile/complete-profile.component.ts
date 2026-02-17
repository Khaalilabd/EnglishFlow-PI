import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-complete-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './complete-profile.component.html',
  styleUrls: ['./complete-profile.component.scss']
})
export class CompleteProfileComponent implements OnInit {
  token: string = '';
  userId: string = '';
  email: string = '';
  firstName: string = '';
  lastName: string = '';

  profileForm = {
    phone: '',
    cin: '',
    dateOfBirth: '',
    address: '',
    city: '',
    postalCode: '',
    bio: '',
    englishLevel: ''
  };

  englishLevels = ['Beginner', 'Intermediate', 'Advanced'];
  loading = false;
  error = '';
  success = false;

  private apiUrl = 'http://localhost:8081/auth';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit() {
    // Récupérer les paramètres URL
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
      this.userId = params['userId'] || '';
      this.email = params['email'] || '';
      this.firstName = params['firstName'] || '';
      this.lastName = params['lastName'] || '';

      // Stocker le token
      if (this.token) {
        localStorage.setItem('authToken', this.token);
        localStorage.setItem('userId', this.userId);
        localStorage.setItem('userEmail', this.email);
        localStorage.setItem('firstName', this.firstName);
        localStorage.setItem('lastName', this.lastName);
      }

      // Vérifier que tous les paramètres sont présents
      if (!this.token || !this.userId) {
        this.error = 'Missing required parameters. Redirecting to login...';
        setTimeout(() => {
          this.router.navigate(['/auth/login']);
        }, 2000);
      }
    });
  }

  onSubmit() {
    // Validation
    if (!this.profileForm.phone || !this.profileForm.cin) {
      this.error = 'Phone and CIN are required';
      return;
    }

    this.loading = true;
    this.error = '';

    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    this.http.post(
      `${this.apiUrl}/complete-profile/${this.userId}`,
      this.profileForm,
      { headers }
    ).subscribe({
      next: (response: any) => {
        console.log('Profile completed:', response);
        this.success = true;
        
        // Marquer le profil comme complété dans localStorage
        localStorage.setItem('profileCompleted', 'true');
        
        // Rediriger vers le landing page (home) après 1.5 secondes
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 1500);
      },
      error: (error) => {
        console.error('Error completing profile:', error);
        this.error = error.error?.message || 'Failed to complete profile. Please try again.';
        this.loading = false;
      }
    });
  }

  get bioLength(): number {
    return this.profileForm.bio.length;
  }
}
