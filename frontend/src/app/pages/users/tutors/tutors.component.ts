import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserService, User, UpdateUserRequest } from '../../../core/services/user.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-tutors',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './tutors.component.html',
  styleUrls: ['./tutors.component.scss']
})
export class TutorsComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  loading = false;
  searchTerm = '';
  selectedStatus = 'ALL';
  
  showEditModal = false;
  showViewModal = false;
  editForm!: FormGroup;
  selectedUser: User | null = null;
  
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    private toastService: ToastService
  ) {
    this.initForms();
  }

  ngOnInit(): void {
    this.loadTutors();
  }

  initForms(): void {
    this.editForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phone: [''],
      cin: [''],
      dateOfBirth: [''],
      address: [''],
      city: [''],
      postalCode: [''],
      yearsOfExperience: [''],
      bio: [''],
      isActive: [true],
      registrationFeePaid: [false]
    });
  }

  loadTutors(): void {
    this.loading = true;
    console.log('🔍 Loading tutors from API...');
    this.userService.getUsersByRole('TUTOR').subscribe({
      next: (data) => {
        console.log('✅ Tutors received:', data);
        console.log('📊 Number of tutors:', data.length);
        this.users = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Error loading tutors:', error);
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          url: error.url
        });
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.users];

    // Search filter
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(user =>
        user.email.toLowerCase().includes(term) ||
        user.firstName.toLowerCase().includes(term) ||
        user.lastName.toLowerCase().includes(term) ||
        (user.cin && user.cin.toLowerCase().includes(term))
      );
    }

    // Status filter
    if (this.selectedStatus === 'ACTIVE') {
      filtered = filtered.filter(user => user.isActive);
    } else if (this.selectedStatus === 'PENDING') {
      filtered = filtered.filter(user => !user.isActive);
    } else if (this.selectedStatus === 'INACTIVE') {
      filtered = filtered.filter(user => !user.isActive);
    }

    this.filteredUsers = filtered;
    this.totalPages = Math.ceil(this.filteredUsers.length / this.itemsPerPage);
    this.currentPage = 1;
  }

  get paginatedUsers(): User[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredUsers.slice(start, end);
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

  get pageNumbers(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  activateUser(user: User): void {
    this.userService.activateUser(user.id).subscribe({
      next: (updatedUser: User) => {
        const index = this.users.findIndex(u => u.id === user.id);
        if (index !== -1) {
          this.users[index] = updatedUser;
        }
        this.applyFilters();
        this.toastService.success(`${user.firstName} ${user.lastName} has been activated successfully!`);
      },
      error: (error: any) => {
        console.error('Error activating tutor:', error);
        this.toastService.error('Failed to activate tutor. Please try again.');
      }
    });
  }

  deactivateUser(user: User): void {
    if (confirm(`Are you sure you want to deactivate ${user.firstName} ${user.lastName}?`)) {
      this.userService.deactivateUser(user.id).subscribe({
        next: (updatedUser: User) => {
          const index = this.users.findIndex(u => u.id === user.id);
          if (index !== -1) {
            this.users[index] = updatedUser;
          }
          this.applyFilters();
          this.toastService.success(`${user.firstName} ${user.lastName} has been deactivated.`);
        },
        error: (error: any) => {
          console.error('Error deactivating tutor:', error);
          this.toastService.error('Failed to deactivate tutor. Please try again.');
        }
      });
    }
  }

  deleteUser(user: User): void {
    if (confirm(`Are you sure you want to delete ${user.firstName} ${user.lastName}?`)) {
      this.userService.deleteUser(user.id).subscribe({
        next: () => {
          this.users = this.users.filter(u => u.id !== user.id);
          this.applyFilters();
          this.toastService.success(`${user.firstName} ${user.lastName} has been deleted.`);
        },
        error: (error) => {
          console.error('Error deleting user:', error);
          this.toastService.error('Failed to delete tutor. Please try again.');
        }
      });
    }
  }

  editUser(user: User): void {
    this.openEditModal(user);
  }

  viewUser(user: User): void {
    this.openViewModal(user);
  }

  openEditModal(user: User): void {
    this.selectedUser = user;
    this.editForm.patchValue({
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      phone: user.phone || '',
      cin: user.cin || '',
      dateOfBirth: user.dateOfBirth || '',
      address: user.address || '',
      city: user.city || '',
      postalCode: user.postalCode || '',
      yearsOfExperience: user.yearsOfExperience || '',
      bio: user.bio || '',
      isActive: user.isActive,
      registrationFeePaid: user.registrationFeePaid
    });
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedUser = null;
    this.editForm.reset();
  }

  updateTutor(): void {
    if (this.editForm.invalid || !this.selectedUser) {
      this.editForm.markAllAsTouched();
      return;
    }

    const updateData: UpdateUserRequest = this.editForm.value;

    this.userService.updateUser(this.selectedUser.id, updateData).subscribe({
      next: (updatedUser) => {
        const index = this.users.findIndex(u => u.id === updatedUser.id);
        if (index !== -1) {
          this.users[index] = updatedUser;
          this.applyFilters();
        }
        this.closeEditModal();
        this.toastService.success('Tutor updated successfully!');
      },
      error: (error) => {
        console.error('Error updating tutor:', error);
        this.toastService.error('Failed to update tutor. Please try again.');
      }
    });
  }

  openViewModal(user: User): void {
    this.selectedUser = user;
    this.showViewModal = true;
  }

  closeViewModal(): void {
    this.showViewModal = false;
    this.selectedUser = null;
  }

  getUserInitials(user: User): string {
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  }

  getActiveCount(): number {
    return this.users.filter(u => u.isActive).length;
  }

  getAverageExperience(): number {
    if (this.users.length === 0) return 0;
    const tutorsWithExperience = this.users.filter(u => u.yearsOfExperience && u.yearsOfExperience > 0);
    if (tutorsWithExperience.length === 0) return 0;
    const total = tutorsWithExperience.reduce((sum, u) => sum + (u.yearsOfExperience || 0), 0);
    return Math.round(total / tutorsWithExperience.length);
  }

  get Math() {
    return Math;
  }
}
