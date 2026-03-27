import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClubService } from '../../../core/services/club.service';
import { AuthService } from '../../../core/services/auth.service';
import { ClubCategory } from '../../../core/models/club.model';

@Component({
  selector: 'app-club-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './club-edit.component.html',
  styleUrls: ['./club-edit.component.scss']
})
export class ClubEditComponent implements OnInit {
  clubForm: FormGroup;
  clubId!: number;
  loading = false;
  saving = false;
  error: string | null = null;
  categories = Object.values(ClubCategory);
  currentUserId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private clubService: ClubService,
    private authService: AuthService
  ) {
    this.clubForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      category: ['', Validators.required],
      maxMembers: [20, [Validators.required, Validators.min(5), Validators.max(100)]]
    });
  }

  ngOnInit() {
    this.getCurrentUser();
    this.route.params.subscribe(params => {
      this.clubId = +params['id'];
      this.loadClub();
    });
  }

  getCurrentUser() {
    const user = this.authService.currentUserValue;
    if (user && user.id !== undefined && user.id !== null) {
      this.currentUserId = user.id;
    } else {
      console.error('No user found or user has no ID');
      this.error = 'User not authenticated. Please log in again.';
    }
  }

  loadClub() {
    this.loading = true;
    this.error = null;

    this.clubService.getClubById(this.clubId).subscribe({
      next: (club) => {
        this.clubForm.patchValue({
          name: club.name,
          description: club.description,
          category: club.category,
          maxMembers: club.maxMembers
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading club:', err);
        this.error = 'Failed to load club details.';
        this.loading = false;
      }
    });
  }

  onSubmit() {
    if (this.clubForm.invalid) {
      this.clubForm.markAllAsTouched();
      return;
    }

    if (!this.currentUserId) {
      this.error = 'User not authenticated. Please log in again.';
      return;
    }

    this.saving = true;
    this.error = null;

    this.clubService.updateClub(this.clubId, this.clubForm.value, this.currentUserId).subscribe({
      next: () => {
        alert('Demande de modification créée avec succès ! Elle doit être approuvée par le vice-président et le secrétaire.');
        this.router.navigate(['/dashboard/clubs', this.clubId]);
      },
      error: (err) => {
        console.error('Error creating update request:', err);
        if (err.error && err.error.message) {
          this.error = err.error.message;
        } else if (err.error && typeof err.error === 'string') {
          this.error = err.error;
        } else {
          this.error = 'Failed to create update request. Please try again.';
        }
        this.saving = false;
      }
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.clubForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }
}
