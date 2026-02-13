import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LogoComponent } from '../../shared/components/logo.component';

@Component({
  selector: 'app-activate',
  standalone: true,
  imports: [CommonModule, RouterModule, LogoComponent],
  templateUrl: './activate.component.html',
  styleUrls: ['./activate.component.scss']
})
export class ActivateComponent implements OnInit {
  loading = true;
  success = false;
  errorMessage = '';
  token: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParams['token'] || '';
    
    if (!this.token) {
      this.loading = false;
      this.errorMessage = 'Invalid or missing activation token';
      return;
    }

    this.activateAccount();
  }

  activateAccount(): void {
    this.authService.activateAccount(this.token).subscribe({
      next: () => {
        this.success = true;
        this.loading = false;
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (error) => {
        console.error('Activation error:', error);
        this.errorMessage = error.error?.message || 'Failed to activate account. The link may have expired.';
        this.loading = false;
      }
    });
  }
}
