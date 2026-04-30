import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { FrontofficeUserDropdownComponent } from '../../shared/components/frontoffice-user-dropdown.component';
import { FrontofficeNotificationDropdownComponent } from '../../shared/components/frontoffice-notification-dropdown.component';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-become-sponsor',
  standalone: true,
  imports: [CommonModule, RouterModule, FrontofficeUserDropdownComponent, FrontofficeNotificationDropdownComponent],
  templateUrl: './become-sponsor.component.html',
  styleUrls: ['./become-sponsor.component.scss']
})
export class BecomeSponsorComponent {
  mobileMenuOpen = false;
  isAuthenticated$;

  constructor(
    private readonly router: Router,
    public readonly authService: AuthService
  ) {
    this.isAuthenticated$ = this.authService.currentUser$.pipe(
      map(user => !!user)
    );
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  applyNow(): void {
    this.router.navigate(['/apply-sponsor']);
  }

  scrollToForm(): void {
    this.router.navigate(['/apply-sponsor']);
  }
}
