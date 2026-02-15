import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { FrontofficeUserDropdownComponent } from '../../shared/components/frontoffice-user-dropdown.component';
import { FrontofficeNotificationDropdownComponent } from '../../shared/components/frontoffice-notification-dropdown.component';

declare var $: any;

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FrontofficeUserDropdownComponent, FrontofficeNotificationDropdownComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit, AfterViewInit {
  mobileMenuOpen = false;
  
  constructor(public authService: AuthService) {}

  get userPanelLabel(): string {
    const user = this.authService.currentUserValue;
    if (user?.role === 'TUTOR') {
      return 'Tutor Panel';
    }
    return 'User Panel';
  }

  get userPanelRoute(): string {
    const user = this.authService.currentUserValue;
    if (user?.role === 'TUTOR') {
      return '/tutor-panel';
    }
    return '/user-panel';
  }

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }
  
  ngOnInit() {
    // Cacher le preloader après le chargement
    setTimeout(() => {
      const preloader = document.querySelector('.js-preloader');
      if (preloader) {
        preloader.classList.add('loaded');
      }
    }, 500);
  }

  ngAfterViewInit() {
    // Initialiser les scripts jQuery après le chargement de la vue
    if (typeof $ !== 'undefined') {
      // Réinitialiser les carousels et autres plugins
      setTimeout(() => {
        if ($('.owl-banner').length) {
          $('.owl-banner').owlCarousel({
            items: 1,
            loop: true,
            dots: true,
            nav: false,
            autoplay: true,
            autoplayTimeout: 5000,
            autoplayHoverPause: true
          });
        }
        
        if ($('.owl-testimonials').length) {
          $('.owl-testimonials').owlCarousel({
            items: 1,
            loop: true,
            dots: true,
            nav: false,
            autoplay: true,
            autoplayTimeout: 5000
          });
        }
      }, 100);
    }
  }
}
