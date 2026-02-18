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

  private apiUrl = 'http://localhost:8080/api/auth'; // Via API Gateway

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

      // Stocker les données utilisateur dans le format attendu par AuthService
      if (this.token && this.userId) {
        const userData = {
          token: this.token,
          type: 'Bearer',
          id: parseInt(this.userId),
          email: this.email,
          firstName: this.firstName,
          lastName: this.lastName,
          role: 'STUDENT', // Par défaut pour les nouveaux utilisateurs
          profileCompleted: false
        };
        
        localStorage.setItem('currentUser', JSON.stringify(userData));
        localStorage.setItem('token', this.token);
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
        
        // Mettre à jour le currentUser avec profileCompleted = true
        const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');
        currentUser.profileCompleted = true;
        localStorage.setItem('currentUser', JSON.stringify(currentUser));
        
        // Rediriger vers la home page après 1.5 secondes
        setTimeout(() => {
          // Recharger la page pour que le AuthService détecte l'utilisateur connecté
          window.location.href = '/';
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
