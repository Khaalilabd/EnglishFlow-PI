import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { InvitationService } from '../../../core/services/invitation.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-create-tutor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-tutor.component.html',
  styleUrls: ['./create-tutor.component.scss']
})
export class CreateTutorComponent {
  inviteForm: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private invitationService: InvitationService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.inviteForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  get f() {
    return this.inviteForm.controls;
  }

  onSubmit(): void {
    if (this.inviteForm.invalid) {
      this.inviteForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const request = {
      email: this.inviteForm.value.email,
      role: 'TUTOR' as const
    };

    this.invitationService.sendInvitation(request).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = `Invitation sent successfully to ${response.email}!`;
        this.toastService.success(`Invitation sent to ${response.email}`);
        
        // Reset form
        this.inviteForm.reset();
      },
      error: (error) => {
        this.loading = false;
        console.error('Error sending invitation:', error);
        
        if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else if (error.status === 0) {
          this.errorMessage = 'Cannot connect to server. Please make sure the backend is running.';
        } else {
          this.errorMessage = 'Failed to send invitation. Please try again.';
        }
        
        this.toastService.error(this.errorMessage);
      }
    });
  }

  sendAnotherInvitation(): void {
    this.successMessage = '';
    this.inviteForm.reset();
  }

  goToTutorsList(): void {
    this.router.navigate(['/dashboard/users/tutors']);
  }

  cancel(): void {
    if (confirm('Are you sure you want to cancel?')) {
      this.router.navigate(['/dashboard/users/tutors']);
    }
  }
}
