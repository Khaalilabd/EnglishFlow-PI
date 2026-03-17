import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { FrontofficeNotificationDropdownComponent } from '../../shared/components/frontoffice-notification-dropdown.component';
import { FrontofficeUserDropdownComponent } from '../../shared/components/frontoffice-user-dropdown.component';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-careers',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule,
    FrontofficeNotificationDropdownComponent,
    FrontofficeUserDropdownComponent
  ],
  templateUrl: './careers.component.html',
  styleUrls: ['./careers.component.scss']
})
export class CareersComponent {
  mobileMenuOpen = false;
  isAuthenticated$;

  benefits = [
    { icon: '💰', title: 'Competitive Pay', description: 'Earn competitive rates for your teaching expertise' },
    { icon: '⏰', title: 'Flexible Schedule', description: 'Set your own availability and work hours' },
    { icon: '🌍', title: 'Work Remotely', description: 'Teach from anywhere in the world' },
    { icon: '📚', title: 'Professional Growth', description: 'Access to training and development resources' },
    { icon: '👥', title: 'Global Community', description: 'Join a community of passionate educators' },
    { icon: '🎯', title: 'Impact Lives', description: 'Help students achieve their English learning goals' }
  ];

  requirements = [
    'Native or near-native English proficiency (C1/C2 level)',
    'Bachelor\'s degree (preferably in Education, English, or related field)',
    'Teaching certification (TEFL, TESOL, CELTA, or equivalent)',
    'Minimum 2 years of teaching experience',
    'Reliable internet connection and quiet teaching environment',
    'Passion for teaching and helping students succeed'
  ];

  testimonials = [
    {
      name: 'Sarah Johnson',
      role: 'English Tutor',
      photo: '👩‍🏫',
      quote: 'Teaching at Jungle in English has been incredibly rewarding. The platform is easy to use and the students are motivated!'
    },
    {
      name: 'Michael Chen',
      role: 'IELTS Specialist',
      photo: '👨‍🏫',
      quote: 'I love the flexibility and the supportive community. It\'s the best decision I\'ve made in my teaching career.'
    },
    {
      name: 'Emma Williams',
      role: 'Business English Tutor',
      photo: '👩‍💼',
      quote: 'The professional development opportunities and competitive pay make this an excellent platform for tutors.'
    }
  ];

  constructor(
    private router: Router,
    private authService: AuthService
  ) {
    this.isAuthenticated$ = this.authService.currentUser$.pipe(
      map(user => !!user)
    );
  }

  toggleMobileMenu(): void {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  applyNow(): void {
    this.router.navigate(['/apply-tutor']);
  }
}
