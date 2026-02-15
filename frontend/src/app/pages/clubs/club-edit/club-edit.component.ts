import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClubService } from '../../../core/services/club.service';
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

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private clubService: ClubService
  ) {
    this.clubForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      category: ['', Validators.required],
      maxMembers: [20, [Validators.required, Validators.min(5), Validators.max(100)]]
    });
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.clubId = +params['id'];
      this.loadClub();
    });
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

    this.saving = true;
    this.error = null;

    this.clubService.updateClub(this.clubId, this.clubForm.value).subscribe({
      next: () => {
        this.router.navigate(['/dashboard/clubs', this.clubId]);
      },
      error: (err) => {
        console.error('Error updating club:', err);
        this.error = 'Failed to update club. Please try again.';
        this.saving = false;
      }
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.clubForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }
}
