import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-activation-pending',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './activation-pending.component.html',
  styleUrls: ['./activation-pending.component.scss']
})
export class ActivationPendingComponent implements OnInit {
  email = '';
  firstName = '';
  activationType: 'email' | 'admin' = 'email'; // 'email' pour activation par email, 'admin' pour activation par admin

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.email = params['email'] || '';
      this.firstName = params['firstName'] || '';
      this.activationType = params['type'] || 'email'; // Par défaut: activation par email
    });
  }

  goToHome(): void {
    this.router.navigate(['/']);
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
