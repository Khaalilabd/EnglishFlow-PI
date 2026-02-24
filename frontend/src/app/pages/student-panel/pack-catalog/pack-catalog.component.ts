import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PackService } from '../../../core/services/pack.service';
import { PackEnrollmentService } from '../../../core/services/pack-enrollment.service';
import { CourseCategoryService } from '../../../core/services/course-category.service';
import { AuthService } from '../../../core/services/auth.service';
import { Pack, PackStatus } from '../../../core/models/pack.model';
import { CourseCategory } from '../../../core/models/course-category.model';

@Component({
  selector: 'app-pack-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pack-catalog.component.html',
  styleUrls: ['./pack-catalog.component.scss']
})
export class PackCatalogComponent implements OnInit {
  packs: Pack[] = [];
  categories: CourseCategory[] = [];
  
  selectedCategory: string = '';
  selectedLevel: string = '';
  
  loading = false;
  enrolling = false;
  
  showEnrollModal = false;
  selectedPack: Pack | null = null;
  
  message = '';
  messageType: 'success' | 'error' = 'success';
  
  allLevels = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];

  constructor(
    private packService: PackService,
    private packEnrollmentService: PackEnrollmentService,
    private categoryService: CourseCategoryService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadAvailablePacks();
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

  loadAvailablePacks(): void {
    this.loading = true;
    this.packService.getAvailablePacks().subscribe({
      next: (packs) => {
        this.packs = packs;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading packs:', error);
        this.showMessage('Failed to load packs', 'error');
        this.loading = false;
      }
    });
  }

  onCategoryChange(): void {
    this.applyFilters();
  }

  onLevelChange(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    if (!this.selectedCategory && !this.selectedLevel) {
      this.loadAvailablePacks();
      return;
    }

    this.loading = true;
    
    if (this.selectedCategory && this.selectedLevel) {
      // Search by both category and level
      const category = this.categories.find(c => c.name === this.selectedCategory);
      if (category) {
        this.packService.searchPacks(category.name as any, this.selectedLevel).subscribe({
          next: (packs) => {
            this.packs = packs.filter(p => p.status === PackStatus.ACTIVE && p.availableSlots! > 0);
            this.loading = false;
          },
          error: (error) => {
            console.error('Error searching packs:', error);
            this.loading = false;
          }
        });
      }
    } else {
      // Load all and filter client-side
      this.packService.getAvailablePacks().subscribe({
        next: (packs) => {
          this.packs = packs.filter(p => {
            const matchesCategory = !this.selectedCategory || p.category === this.selectedCategory;
            const matchesLevel = !this.selectedLevel || p.level === this.selectedLevel;
            return matchesCategory && matchesLevel;
          });
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading packs:', error);
          this.loading = false;
        }
      });
    }
  }

  clearFilters(): void {
    this.selectedCategory = '';
    this.selectedLevel = '';
    this.loadAvailablePacks();
  }

  openEnrollModal(pack: Pack): void {
    this.selectedPack = pack;
    this.showEnrollModal = true;
  }

  closeEnrollModal(): void {
    this.showEnrollModal = false;
    this.selectedPack = null;
  }

  confirmEnrollment(): void {
    if (!this.selectedPack || !this.selectedPack.id) return;

    const currentUser = this.authService.currentUserValue;
    if (!currentUser) {
      this.showMessage('Please login to enroll', 'error');
      return;
    }

    this.enrolling = true;
    this.packEnrollmentService.enrollStudent(currentUser.id, this.selectedPack.id).subscribe({
      next: () => {
        this.showMessage('Successfully enrolled in pack!', 'success');
        this.closeEnrollModal();
        this.enrolling = false;
        
        // Refresh packs to update available slots
        this.applyFilters();
        
        // Navigate to my packs after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/user-panel/my-packs']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error enrolling:', error);
        this.showMessage(error.error?.message || 'Failed to enroll in pack', 'error');
        this.enrolling = false;
      }
    });
  }

  viewPackDetails(pack: Pack): void {
    this.router.navigate(['/user-panel/packs', pack.id]);
  }

  getCategoryIcon(categoryName: string): string {
    const category = this.categories.find(c => c.name === categoryName);
    return category?.icon || '📚';
  }

  getCategoryColor(categoryName: string): string {
    const category = this.categories.find(c => c.name === categoryName);
    return category?.color || '#3B82F6';
  }

  getEnrollmentPercentage(pack: Pack): number {
    return pack.enrollmentPercentage || 0;
  }

  getEnrollmentColor(percentage: number): string {
    if (percentage >= 80) return 'text-red-600';
    if (percentage >= 50) return 'text-orange-600';
    return 'text-green-600';
  }

  showMessage(text: string, type: 'success' | 'error'): void {
    this.message = text;
    this.messageType = type;
    setTimeout(() => {
      this.message = '';
    }, 5000);
  }
}
