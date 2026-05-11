import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { PackService } from '../../core/services/pack.service';
import { CourseCategoryService } from '../../core/services/course-category.service';
import { EventService, Event as ClubEvent } from '../../core/services/event.service';
import { ClubService } from '../../core/services/club.service';
import { UserService, User } from '../../core/services/user.service';
import { Pack, PackStatus } from '../../core/models/pack.model';
import { CourseCategory } from '../../core/models/course-category.model';
import { Club } from '../../core/models/club.model';
import { FrontofficeUserDropdownComponent } from '../../shared/components/frontoffice-user-dropdown.component';
import { FrontofficeNotificationDropdownComponent } from '../../shared/components/frontoffice-notification-dropdown.component';
import { map } from 'rxjs/operators';

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
  isAuthenticated$;
  currentUser$;
  activeSection = 'home';
  
  packs: Pack[] = [];
  categories: CourseCategory[] = [];
  selectedCategory: string = '';
  loading = false;
  
  // Events
  upcomingEvents: ClubEvent[] = [];
  loadingEvents = false;
  
  // Clubs
  clubs: Club[] = [];
  loadingClubs = false;
  
  // Tutors
  tutors: User[] = [];
  loadingTutors = false;
  allTutors: User[] = []; // Tous les tuteurs
  currentTutorPage = 0;
  tutorsPerPage = 4;
  
  // Animated Stats
  studentsCount = 0;
  teachersCount = 0;
  successRate = 0;
  private statsAnimated = false;
  
  constructor(
    public authService: AuthService,
    private packService: PackService,
    private categoryService: CourseCategoryService,
    private eventService: EventService,
    private clubService: ClubService,
    private userService: UserService
  ) {
    this.isAuthenticated$ = this.authService.currentUser$.pipe(
      map(user => !!user)
    );
    this.currentUser$ = this.authService.currentUser$;
  }

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
    
    // Charger toutes les données
    this.loadCategories();
    this.loadPacks();
    this.loadUpcomingEvents();
    this.loadClubs();
    this.loadTutors();
    
    // Ajouter le listener de scroll pour activer les sections
    this.setupScrollListener();
    
    // Démarrer l'animation des stats après un court délai
    setTimeout(() => {
      this.animateStats();
      this.statsAnimated = true;
    }, 800);
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

  setupScrollListener(): void {
    window.addEventListener('scroll', () => {
      const sections = ['home', 'services', 'courses', 'clubs', 'team', 'events', 'contact'];
      const scrollPosition = window.scrollY + 150; // Offset pour la navbar
      
      for (const sectionId of sections) {
        const section = document.getElementById(sectionId);
        if (section) {
          const sectionTop = section.offsetTop;
          const sectionHeight = section.offsetHeight;
          
          if (scrollPosition >= sectionTop && scrollPosition < sectionTop + sectionHeight) {
            this.activeSection = sectionId;
            break;
          }
        }
      }
      
      // Animate stats when hero section is visible
      if (!this.statsAnimated) {
        const heroSection = document.getElementById('home');
        if (heroSection) {
          const heroTop = heroSection.offsetTop;
          const heroHeight = heroSection.offsetHeight;
          
          if (scrollPosition >= heroTop && scrollPosition < heroTop + heroHeight) {
            this.animateStats();
            this.statsAnimated = true;
          }
        }
      }
    });
  }

  animateStats(): void {
    // Animate Students Count to 500
    this.animateValue('studentsCount', 0, 500, 2000);
    
    // Animate Teachers Count to 50
    this.animateValue('teachersCount', 0, 50, 2000);
    
    // Animate Success Rate to 95
    this.animateValue('successRate', 0, 95, 2000);
  }

  animateValue(property: 'studentsCount' | 'teachersCount' | 'successRate', start: number, end: number, duration: number): void {
    const startTime = performance.now();
    
    const animate = (currentTime: number) => {
      const elapsed = currentTime - startTime;
      const progress = Math.min(elapsed / duration, 1);
      
      // Easing function for smooth animation
      const easeOutQuart = 1 - Math.pow(1 - progress, 4);
      
      this[property] = Math.floor(start + (end - start) * easeOutQuart);
      
      if (progress < 1) {
        requestAnimationFrame(animate);
      } else {
        this[property] = end;
      }
    };
    
    requestAnimationFrame(animate);
  }

  loadCategories(): void {
    this.categoryService.getActiveCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });
  }

  loadPacks(): void {
    this.loading = true;
    this.packService.getAvailablePacks().subscribe({
      next: (packs) => {
        this.packs = packs.filter(p => p.status === PackStatus.ACTIVE);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading packs:', error);
        this.loading = false;
      }
    });
  }

  filterByCategory(categoryName: string): void {
    this.selectedCategory = categoryName;
    
    if (!categoryName) {
      this.loadPacks();
      return;
    }
    
    this.loading = true;
    this.packService.getAvailablePacks().subscribe({
      next: (packs) => {
        this.packs = packs.filter(p => 
          p.status === PackStatus.ACTIVE && 
          p.category === categoryName
        );
        this.loading = false;
      },
      error: (error) => {
        console.error('Error filtering packs:', error);
        this.loading = false;
      }
    });
  }

  getCategoryIcon(categoryName: string): string {
    const category = this.categories.find(c => c.name === categoryName);
    return category?.icon || '📚';
  }

  getCategoryColor(categoryName: string): string {
    const category = this.categories.find(c => c.name === categoryName);
    return category?.color || '#3B82F6';
  }

  getPackCountByCategory(categoryName: string): number {
    // Count packs for a specific category from all loaded packs
    return this.packs.filter(p => p.category === categoryName).length;
  }

  getEnrollmentPercentage(pack: Pack): number {
    if (!pack.maxStudents || pack.maxStudents === 0) return 0;
    const enrolled = pack.maxStudents - (pack.availableSlots || 0);
    return Math.round((enrolled / pack.maxStudents) * 100);
  }

  loadUpcomingEvents(): void {
    this.loadingEvents = true;
    this.eventService.getAllEvents().subscribe({
      next: (events: ClubEvent[]) => {
        // Filter approved and upcoming events
        const now = new Date();
        this.upcomingEvents = events
          .filter((event: ClubEvent) => {
            // Only show approved events
            if (event.status !== 'APPROVED') return false;
            
            // Only show upcoming events (events that haven't ended yet)
            const endDate = event.endDate ? new Date(event.endDate) : new Date(event.startDate);
            return endDate >= now;
          })
          .sort((a: ClubEvent, b: ClubEvent) => {
            const dateA = new Date(a.startDate);
            const dateB = new Date(b.startDate);
            return dateA.getTime() - dateB.getTime();
          })
          .slice(0, 3); // Show only 3 upcoming events
        this.loadingEvents = false;
      },
      error: (error: any) => {
        console.error('Error loading events:', error);
        this.loadingEvents = false;
      }
    });
  }

  loadClubs(): void {
    this.loadingClubs = true;
    this.clubService.getApprovedClubs().subscribe({
      next: (clubs) => {
        this.clubs = clubs.slice(0, 4); // Show only 4 clubs
        this.loadingClubs = false;
      },
      error: (error) => {
        console.error('Error loading clubs:', error);
        this.loadingClubs = false;
      }
    });
  }

  loadTutors(): void {
    this.loadingTutors = true;
    console.log('🔍 Loading tutors from public endpoint...');
    
    this.userService.getPublicTutors().subscribe({
      next: (tutors) => {
        console.log('✅ Tutors received:', tutors);
        console.log('📊 Number of tutors:', tutors.length);
        this.allTutors = tutors;
        this.updateDisplayedTutors();
        this.loadingTutors = false;
      },
      error: (error) => {
        console.error('❌ Error loading tutors:', error);
        this.loadingTutors = false;
        this.tutors = [];
      }
    });
  }

  updateDisplayedTutors(): void {
    const start = this.currentTutorPage * this.tutorsPerPage;
    const end = start + this.tutorsPerPage;
    this.tutors = this.allTutors.slice(start, end);
  }

  nextTutorPage(): void {
    if ((this.currentTutorPage + 1) * this.tutorsPerPage < this.allTutors.length) {
      this.currentTutorPage++;
      this.updateDisplayedTutors();
    }
  }

  previousTutorPage(): void {
    if (this.currentTutorPage > 0) {
      this.currentTutorPage--;
      this.updateDisplayedTutors();
    }
  }

  get hasNextTutorPage(): boolean {
    return (this.currentTutorPage + 1) * this.tutorsPerPage < this.allTutors.length;
  }

  get hasPreviousTutorPage(): boolean {
    return this.currentTutorPage > 0;
  }

  get totalTutorPages(): number {
    return Math.ceil(this.allTutors.length / this.tutorsPerPage);
  }

  getTutorPhotoUrl(photoUrl: string | null | undefined): string {
    if (!photoUrl) {
      return 'assets/images/member-01.jpg'; // Default image
    }
    
    if (photoUrl.startsWith('http')) {
      return photoUrl;
    }
    
    return `http://localhost:8081${photoUrl}`;
  }

  formatEventDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric'
    });
  }

  getEventDuration(event: ClubEvent): string {
    if (!event.endDate) return 'TBD';
    
    const start = new Date(event.startDate);
    const end = new Date(event.endDate);
    const diffMs = end.getTime() - start.getTime();
    const diffHours = Math.round(diffMs / (1000 * 60 * 60));
    
    if (diffHours < 1) {
      const diffMinutes = Math.round(diffMs / (1000 * 60));
      return `${diffMinutes} Minutes`;
    }
    
    return `${diffHours} Hour${diffHours > 1 ? 's' : ''}`;
  }

  getEventTypeLabel(type: string): string {
    const labels: { [key: string]: string } = {
      'WORKSHOP': 'Workshop',
      'SEMINAR': 'Seminar',
      'SOCIAL': 'Social Event',
      'COMPETITION': 'Competition',
      'CONFERENCE': 'Conference'
    };
    return labels[type] || type;
  }
}
