import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-sponsor-panel',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sponsor-panel.component.html',
  styleUrls: ['./sponsor-panel.component.scss']
})
export class SponsorPanelComponent implements OnInit {
  currentUser: any;

  stats = [
    { title: 'Sponsorship Level', value: '🥉 Bronze', icon: '🏆', change: 'Active', changeType: 'up' as const },
    { title: 'Students Supported', value: '120+', icon: '🎓', change: '+12 this month', changeType: 'up' as const },
    { title: 'Events Sponsored', value: '3', icon: '📅', change: 'This year', changeType: 'up' as const },
    { title: 'Contribution', value: '250 DT', icon: '💰', change: 'Total', changeType: 'up' as const }
  ];

  recentActivities = [
    { icon: '🎉', title: 'Sponsorship approved', description: 'Your sponsorship request has been approved', date: 'Today' },
    { icon: '📧', title: 'Welcome email sent', description: 'Check your inbox for details', date: 'Today' },
    { icon: '🤝', title: 'Partnership confirmed', description: 'You are now an official sponsor', date: 'Today' }
  ];

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
  }
}
