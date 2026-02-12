import { Component, OnInit, AfterViewInit } from '@angular/core';

declare var $: any;

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit, AfterViewInit {
  
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
