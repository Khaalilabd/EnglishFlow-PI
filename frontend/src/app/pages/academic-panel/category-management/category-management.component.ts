import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CourseCategoryService } from '../../../core/services/course-category.service';
import { AuthService } from '../../../core/services/auth.service';
import { CourseCategory } from '../../../core/models/course-category.model';

@Component({
  selector: 'app-category-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './category-management.component.html',
  styleUrls: ['./category-management.component.scss']
})
export class CategoryManagementComponent implements OnInit {
  categories: CourseCategory[] = [];
  filteredCategories: CourseCategory[] = [];
  
  loading = false;
  showModal = false;
  showDeleteModal = false;
  isEditMode = false;
  
  currentCategory: CourseCategory = this.getEmptyCategory();
  categoryToDelete: CourseCategory | null = null;
  
  searchTerm = '';
  filterActive: string = 'all';
  
  message = '';
  messageType: 'success' | 'error' = 'success';

  // Icon options
  iconOptions = [
    '📚', '✏️', '🗣️', '💼', '📖', '✍️', '👂', '🎯', 
    '🌟', '🔤', '📝', '🎓', '💡', '🚀', '🎨', '🔊'
  ];

  // Color options
  colorOptions = [
    '#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6',
    '#EC4899', '#14B8A6', '#F97316', '#6366F1', '#84CC16'
  ];

  constructor(
    private categoryService: CourseCategoryService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.categoryService.getAllCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
        this.showMessage('Failed to load categories', 'error');
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredCategories = this.categories.filter(cat => {
      const matchesSearch = cat.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                           (cat.description && cat.description.toLowerCase().includes(this.searchTerm.toLowerCase()));
      const matchesActive = this.filterActive === 'all' || 
                           (this.filterActive === 'active' && cat.active) ||
                           (this.filterActive === 'inactive' && !cat.active);
      return matchesSearch && matchesActive;
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.currentCategory = this.getEmptyCategory();
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.currentCategory.createdBy = currentUser.id;
    }
    this.showModal = true;
  }

  openEditModal(category: CourseCategory): void {
    this.isEditMode = true;
    this.currentCategory = { ...category };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.currentCategory = this.getEmptyCategory();
  }

  saveCategory(): void {
    if (!this.validateCategory()) {
      this.showMessage('Please fill all required fields', 'error');
      return;
    }

    const saveObservable = this.isEditMode && this.currentCategory.id
      ? this.categoryService.updateCategory(this.currentCategory.id, this.currentCategory)
      : this.categoryService.createCategory(this.currentCategory);

    saveObservable.subscribe({
      next: () => {
        this.showMessage(`Category ${this.isEditMode ? 'updated' : 'created'} successfully!`, 'success');
        this.closeModal();
        this.loadCategories();
      },
      error: (error) => {
        console.error('Error saving category:', error);
        this.showMessage(error.error?.message || 'Failed to save category', 'error');
      }
    });
  }

  validateCategory(): boolean {
    return this.currentCategory.name.trim() !== '';
  }

  openDeleteModal(category: CourseCategory): void {
    this.categoryToDelete = category;
    this.showDeleteModal = true;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.categoryToDelete = null;
  }

  confirmDelete(): void {
    if (this.categoryToDelete && this.categoryToDelete.id) {
      this.categoryService.deleteCategory(this.categoryToDelete.id).subscribe({
        next: () => {
          this.showMessage('Category deleted successfully!', 'success');
          this.closeDeleteModal();
          this.loadCategories();
        },
        error: (error) => {
          console.error('Error deleting category:', error);
          this.showMessage('Failed to delete category', 'error');
        }
      });
    }
  }

  toggleActive(category: CourseCategory): void {
    if (category.id) {
      this.categoryService.toggleActive(category.id).subscribe({
        next: () => {
          this.showMessage(`Category ${category.active ? 'deactivated' : 'activated'} successfully!`, 'success');
          this.loadCategories();
        },
        error: (error) => {
          console.error('Error toggling category:', error);
          this.showMessage('Failed to update category status', 'error');
        }
      });
    }
  }

  moveUp(category: CourseCategory, index: number): void {
    if (index > 0 && category.id) {
      const newOrder = (this.filteredCategories[index - 1].displayOrder || 0) - 1;
      this.categoryService.updateDisplayOrder(category.id, newOrder).subscribe({
        next: () => this.loadCategories(),
        error: (error) => console.error('Error updating order:', error)
      });
    }
  }

  moveDown(category: CourseCategory, index: number): void {
    if (index < this.filteredCategories.length - 1 && category.id) {
      const newOrder = (this.filteredCategories[index + 1].displayOrder || 0) + 1;
      this.categoryService.updateDisplayOrder(category.id, newOrder).subscribe({
        next: () => this.loadCategories(),
        error: (error) => console.error('Error updating order:', error)
      });
    }
  }

  getEmptyCategory(): CourseCategory {
    return {
      name: '',
      description: '',
      icon: '📚',
      color: '#3B82F6',
      active: true,
      displayOrder: 0
    };
  }

  showMessage(text: string, type: 'success' | 'error'): void {
    this.message = text;
    this.messageType = type;
    setTimeout(() => {
      this.message = '';
    }, 5000);
  }
}
