import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule, FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { PhoneInputComponent } from '../../shared/components/phone-input/phone-input.component';
import { CustomValidators } from '../../shared/validators/custom-validators';

@Component({
  selector: 'app-complete-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, PhoneInputComponent],
  templateUrl: './complete-profile.component.html',
  styleUrls: ['./complete-profile.component.scss']
})
export class CompleteProfileComponent implements OnInit {
  token: string = '';
  userId: string = '';
  email: string = '';
  firstName: string = '';
  lastName: string = '';
  profileForm!: FormGroup;
  maxDate: string;

  englishLevels = ['Beginner', 'Elementary', 'Intermediate', 'Upper Intermediate', 'Advanced', 'Proficient'];
  loading = false;
  error = '';
  success = false;

  private apiUrl = 'http://localhost:8080/api/auth'; // Via API Gateway

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private fb: FormBuilder
  ) {
    // Set max date to today
    const today = new Date();
    this.maxDate = today.toISOString().split('T')[0];
    
    this.profileForm = this.fb.group({
      phone: ['', CustomValidators.phoneValidator()],
      cin: ['', [Validators.required, CustomValidators.cinValidator(), Validators.minLength(5), Validators.maxLength(20)]],
      dateOfBirth: ['', [Validators.required, CustomValidators.minAgeValidator(13)]],
      address: [''],
      city: [''],
      postalCode: ['', CustomValidators.postalCodeValidator()],
      bio: ['', Validators.maxLength(500)],
      englishLevel: ['', Validators.required]
    });
  }

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
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      this.error = 'Please fill in all required fields correctly';
      return;
    }

    this.loading = true;
    this.error = '';

    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    this.http.post(
      `${this.apiUrl}/complete-profile/${this.userId}`,
      this.profileForm.value,
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

  get f() {
    return this.profileForm.controls;
  }

  get bioLength(): number {
    return this.profileForm.get('bio')?.value?.length || 0;
  }
}
