import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ClubService } from '../../../core/services/club.service';
import { ClubCategory } from '../../../core/models/club.model';

@Component({
  selector: 'app-club-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './club-create.component.html',
  styleUrls: ['./club-create.component.scss']
})
export class ClubCreateComponent {
  clubForm: FormGroup;
  loading = false;
  error: string | null = null;
  categories = Object.values(ClubCategory);

  constructor(
    private fb: FormBuilder,
    private clubService: ClubService,
    private router: Router
  ) {
    this.clubForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      category: ['', Validators.required],
      maxMembers: [20, [Validators.required, Validators.min(5), Validators.max(100)]]
    });
  }

  onSubmit() {
    if (this.clubForm.invalid) {
      this.clubForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    this.clubService.createClub(this.clubForm.value).subscribe({
      next: (club) => {
        this.router.navigate(['/dashboard/clubs', club.id]);
      },
      error: (err) => {
        console.error('Error creating club:', err);
        this.error = 'Failed to create club. Please try again.';
        this.loading = false;
      }
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.clubForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }
}
