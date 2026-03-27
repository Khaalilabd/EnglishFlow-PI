import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PackService } from '../../../core/services/pack.service';
import { TutorAvailabilityService } from '../../../core/services/tutor-availability.service';
import { CourseCategoryService } from '../../../core/services/course-category.service';
import { AuthService } from '../../../core/services/auth.service';
import { Pack, PackStatus } from '../../../core/models/pack.model';
import { TutorAvailability } from '../../../core/models/tutor-availability.model';
import { CourseCategory } from '../../../core/models/course-category.model';

@Component({
  selector: 'app-pack-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pack-management.component.html',
  styleUrls: ['./pack-management.component.scss']
})
export class PackManagementComponent implements OnInit {
  packs: Pack[] = [];
  filteredPacks: Pack[] = [];
  tutors: TutorAvailability[] = [];
  categories: CourseCategory[] = [];
  loading = false;
  
  // Filters
  searchTerm = '';
  selectedCategory = 'ALL';
  selectedLevel = 'ALL';
  selectedStatus = 'ALL';
  selectedTutor = 'ALL';
  
  // Pagination
  currentPage = 1;
  itemsPerPage = 10;
  
  // Delete modal
  showDeleteModal = false;
  packToDelete: Pack | null = null;
  
  // Enums
  allLevels = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];
  allStatuses = Object.values(PackStatus);

  constructor(
    private packService: PackService,
    private tutorAvailabilityService: TutorAvailabilityService,
    private courseCategoryService: CourseCategoryService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadPacks();
    this.loadTutors();
  }

  loadCategories(): void {
    this.courseCategoryService.getActiveCategories().subscribe({
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
    const currentUser = this.authService.currentUserValue;
    
    if (currentUser) {
      this.packService.getByCreatedBy(currentUser.id).subscribe({
        next: (packs) => {
          this.packs = packs;
          this.applyFilters();
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading packs:', error);
          this.loading = false;
        }
      });
    }
  }

  loadTutors(): void {
    this.tutorAvailabilityService.getTutorsWithCapacity().subscribe({
      next: (tutors) => {
        this.tutors = tutors;
      },
      error: (error) => {
        console.error('Error loading tutors:', error);
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.packs];

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(pack =>
        pack.name.toLowerCase().includes(term) ||
        pack.tutorName.toLowerCase().includes(term) ||
        pack.description?.toLowerCase().includes(term)
      );
    }

    if (this.selectedCategory !== 'ALL') {
      filtered = filtered.filter(pack => pack.category === this.selectedCategory);
    }

    if (this.selectedLevel !== 'ALL') {
      filtered = filtered.filter(pack => pack.level === this.selectedLevel);
    }

    if (this.selectedStatus !== 'ALL') {
      filtered = filtered.filter(pack => pack.status === this.selectedStatus);
    }

    if (this.selectedTutor !== 'ALL') {
      filtered = filtered.filter(pack => pack.tutorId.toString() === this.selectedTutor);
    }

    this.filteredPacks = filtered;
    this.currentPage = 1;
  }

  get paginatedPacks(): Pack[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredPacks.slice(start, end);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredPacks.length / this.itemsPerPage);
  }

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
  }

  createPack(): void {
    this.router.navigate(['/dashboard/packs/create']);
  }

  editPack(pack: Pack): void {
    this.router.navigate(['/dashboard/packs/edit', pack.id]);
  }

  viewPack(pack: Pack): void {
    this.router.navigate(['/dashboard/packs', pack.id]);
  }

  deletePack(pack: Pack): void {
    this.packToDelete = pack;
    this.showDeleteModal = true;
  }

  confirmDelete(): void {
    if (this.packToDelete && this.packToDelete.id) {
      this.packService.deletePack(this.packToDelete.id).subscribe({
        next: () => {
          this.packs = this.packs.filter(p => p.id !== this.packToDelete!.id);
          this.applyFilters();
          this.closeDeleteModal();
        },
        error: (error) => {
          console.error('Error deleting pack:', error);
          alert('Failed to delete pack');
          this.closeDeleteModal();
        }
      });
    }
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.packToDelete = null;
  }

  getStatusColor(status: PackStatus): string {
    const colors = {
      [PackStatus.DRAFT]: 'bg-gray-100 text-gray-800',
      [PackStatus.ACTIVE]: 'bg-green-100 text-green-800',
      [PackStatus.FULL]: 'bg-orange-100 text-orange-800',
      [PackStatus.CLOSED]: 'bg-red-100 text-red-800',
      [PackStatus.ARCHIVED]: 'bg-gray-100 text-gray-600'
    };
    return colors[status];
  }

  getEnrollmentColor(percentage: number): string {
    if (percentage >= 90) return 'text-red-600';
    if (percentage >= 70) return 'text-orange-600';
    return 'text-green-600';
  }

  getTotalPacks(): number {
    return this.packs.length;
  }

  getActivePacks(): number {
    return this.packs.filter(p => p.status === PackStatus.ACTIVE).length;
  }

  getFullPacks(): number {
    return this.packs.filter(p => p.status === PackStatus.FULL).length;
  }

  getTotalEnrollments(): number {
    return this.packs.reduce((sum, p) => sum + (p.currentEnrolledStudents || 0), 0);
  }

  get Math() {
    return Math;
  }
}
