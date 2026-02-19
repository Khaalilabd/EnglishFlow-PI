import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService, User, CreateUserRequest, UpdateUserRequest } from '../../../core/services/user.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-academic-affairs',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './academic-affairs.component.html',
  styleUrl: './academic-affairs.component.scss'
})
export class AcademicAffairsComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  loading = false;
  searchTerm = '';
  selectedStatus = 'ALL';
  
  showCreateModal = false;
  showEditModal = false;
  showViewModal = false;
  
  createForm!: FormGroup;
  editForm!: FormGroup;
  
  selectedUser: User | null = null;
  
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    private toastService: ToastService,
    private router: Router
  ) {
    this.initForms();
  }

  ngOnInit(): void {
    this.loadAcademicAffairs();
  }

  initForms(): void {
    this.createForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      phone: [''],
      cin: [''],
      dateOfBirth: [''],
      address: [''],
      city: [''],
      postalCode: ['']
    });

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
      isActive: [true],
      registrationFeePaid: [false]
    });
  }

  loadAcademicAffairs(): void {
    this.loading = true;
    this.userService.getUsersByRole('ACADEMIC_OFFICE_AFFAIR').subscribe({
      next: (data) => {
        this.users = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading academic affairs:', error);
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.users];

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(user =>
        user.email.toLowerCase().includes(term) ||
        user.firstName.toLowerCase().includes(term) ||
        user.lastName.toLowerCase().includes(term) ||
        (user.cin && user.cin.toLowerCase().includes(term))
      );
    }

    if (this.selectedStatus === 'ACTIVE') {
      filtered = filtered.filter(user => user.isActive);
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

  openCreateModal(): void {
    this.createForm.reset();
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
    this.createForm.reset();
  }

  createAcademicAffair(): void {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }

    // Clean up the form data - remove empty strings
    const formValue = this.createForm.value;
    const userData: CreateUserRequest = {
      email: formValue.email,
      password: formValue.password,
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      role: 'ACADEMIC_OFFICE_AFFAIR'
    };

    // Add optional fields only if they have values
    if (formValue.phone && formValue.phone.trim()) userData.phone = formValue.phone.trim();
    if (formValue.cin && formValue.cin.trim()) userData.cin = formValue.cin.trim();
    if (formValue.dateOfBirth) userData.dateOfBirth = formValue.dateOfBirth;
    if (formValue.address && formValue.address.trim()) userData.address = formValue.address.trim();
    if (formValue.city && formValue.city.trim()) userData.city = formValue.city.trim();
    if (formValue.postalCode && formValue.postalCode.trim()) userData.postalCode = formValue.postalCode.trim();

    console.log('📤 Sending create request:', userData);

    this.userService.createUser(userData).subscribe({
      next: (newUser) => {
        console.log('✅ User created:', newUser);
        this.users.push(newUser);
        this.applyFilters();
        this.closeCreateModal();
        this.toastService.success('Academic Affairs staff created successfully!');
      },
      error: (error) => {
        console.error('❌ Error creating academic affairs:', error);
        console.error('Error details:', error.error);
        this.toastService.error(error.error?.message || 'Failed to create academic affairs staff. Please try again.');
      }
    });
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

  updateAcademicAffair(): void {
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
        this.toastService.success('Academic Affairs staff updated successfully!');
      },
      error: (error) => {
        console.error('Error updating academic affairs:', error);
        this.toastService.error('Failed to update academic affairs staff. Please try again.');
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

  activateUser(user: User): void {
    this.userService.activateUser(user.id).subscribe({
      next: (updatedUser) => {
        const index = this.users.findIndex(u => u.id === user.id);
        if (index !== -1) {
          this.users[index] = updatedUser;
        }
        this.applyFilters();
        this.toastService.success(`${user.firstName} ${user.lastName} has been activated successfully!`);
      },
      error: (error) => {
        console.error('Error activating academic affairs:', error);
        this.toastService.error('Failed to activate academic affairs staff.');
      }
    });
  }

  deactivateUser(user: User): void {
    if (confirm(`Are you sure you want to deactivate ${user.firstName} ${user.lastName}?`)) {
      this.userService.deactivateUser(user.id).subscribe({
        next: (updatedUser) => {
          const index = this.users.findIndex(u => u.id === user.id);
          if (index !== -1) {
            this.users[index] = updatedUser;
          }
          this.applyFilters();
          this.toastService.success(`${user.firstName} ${user.lastName} has been deactivated.`);
        },
        error: (error) => {
          console.error('Error deactivating academic affairs:', error);
          this.toastService.error('Failed to deactivate academic affairs staff.');
        }
      });
    }
  }

  deleteUser(user: User): void {
    if (confirm(`Are you sure you want to delete ${user.firstName} ${user.lastName}? This action cannot be undone.`)) {
      this.userService.deleteUser(user.id).subscribe({
        next: () => {
          this.users = this.users.filter(u => u.id !== user.id);
          this.applyFilters();
          this.toastService.success(`${user.firstName} ${user.lastName} has been deleted.`);
        },
        error: (error) => {
          console.error('Error deleting academic affairs:', error);
          this.toastService.error('Failed to delete academic affairs staff.');
        }
      });
    }
  }

  getActiveCount(): number {
    return this.users.filter(u => u.isActive).length;
  }

  getUserInitials(user: User): string {
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  }

  goToInvitePage(): void {
    this.router.navigate(['/dashboard/users/academic-affairs/create']);
  }

  get Math() {
    return Math;
  }
}
