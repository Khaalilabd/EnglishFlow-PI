import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService, User, CreateUserRequest, UpdateUserRequest } from '../../../core/services/user.service';
import { ToastService } from '../../../core/services/toast.service';
import { UserRoleBadgeComponent } from '../../../shared/components/user-role-badge/user-role-badge.component';
import { ConfirmationDialogComponent, ConfirmationConfig } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { SkeletonLoaderComponent } from '../../../shared/components/skeleton-loader/skeleton-loader.component';
import { UserPreferencesService } from '../../../core/services/user-preferences.service';

@Component({
  selector: 'app-students',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, UserRoleBadgeComponent, ConfirmationDialogComponent, SkeletonLoaderComponent],
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.scss']
})
export class StudentsComponent implements OnInit, OnDestroy {
  @ViewChild(ConfirmationDialogComponent) confirmDialog!: ConfirmationDialogComponent;
  
  users: User[] = [];
  filteredUsers: User[] = [];
  loading = false;
  searchTerm = '';
  private searchDebounceTimer: any;
  isSearching = false;
  selectedStatus = 'ALL';
  selectedEnglishLevel = 'ALL';
  selectedPaymentStatus = 'ALL';
  showAdvancedFilters = false;
  
  // Modals
  showCreateModal = false;
  showEditModal = false;
  showViewModal = false;
  showImportModal = false;
  
  // Forms
  createForm!: FormGroup;
  editForm!: FormGroup;
  
  // Selected user
  selectedUser: User | null = null;
  
  // Pagination
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  // Sorting
  sortField: 'firstName' | 'lastName' | 'email' | 'englishLevel' | 'registrationFeePaid' = 'firstName';
  sortDirection: 'asc' | 'desc' = 'asc';

  // Bulk actions
  selectedUserIds: Set<number> = new Set();
  selectAll = false;

  // View mode
  viewMode: 'comfortable' | 'compact' | 'spacious' = 'comfortable';

  // English levels
  englishLevels = [
    { value: 'A1', label: 'A1 - Beginner' },
    { value: 'A2', label: 'A2 - Elementary' },
    { value: 'B1', label: 'B1 - Intermediate' },
    { value: 'B2', label: 'B2 - Upper Intermediate' },
    { value: 'C1', label: 'C1 - Advanced' },
    { value: 'C2', label: 'C2 - Proficient' }
  ];

  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    private toastService: ToastService,
    private preferencesService: UserPreferencesService
  ) {
    this.initForms();
  }

  ngOnInit(): void {
    this.loadPreferences();
    this.loadStudents();
    this.setupKeyboardShortcuts();
  }

  ngOnDestroy(): void {
    // Cleanup keyboard shortcuts
    document.removeEventListener('keydown', this.handleKeyboardShortcut);
    // Cleanup debounce timer
    if (this.searchDebounceTimer) {
      clearTimeout(this.searchDebounceTimer);
    }
  }

  private handleKeyboardShortcut = (event: KeyboardEvent): void => {
    // Ctrl+N or Cmd+N: New student
    if ((event.ctrlKey || event.metaKey) && event.key === 'n') {
      event.preventDefault();
      this.openCreateModal();
    }
    // Ctrl+F or Cmd+F: Focus search
    if ((event.ctrlKey || event.metaKey) && event.key === 'f') {
      event.preventDefault();
      const searchInput = document.querySelector('.search-input') as HTMLInputElement;
      if (searchInput) searchInput.focus();
    }
    // Escape: Close modals
    if (event.key === 'Escape') {
      if (this.showCreateModal) this.closeCreateModal();
      if (this.showEditModal) this.closeEditModal();
      if (this.showViewModal) this.closeViewModal();
    }
    // Ctrl+E or Cmd+E: Export
    if ((event.ctrlKey || event.metaKey) && event.key === 'e') {
      event.preventDefault();
      this.exportToCSV();
    }
  };

  private setupKeyboardShortcuts(): void {
    document.addEventListener('keydown', this.handleKeyboardShortcut);
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
      postalCode: [''],
      englishLevel: ['A1']
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
      englishLevel: [''],
      isActive: [true],
      registrationFeePaid: [false]
    });
  }

  loadStudents(): void {
    this.loading = true;
    this.userService.getUsersByRole('STUDENT').subscribe({
      next: (data) => {
        this.users = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading students:', error);
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
    } else if (this.selectedStatus === 'INACTIVE') {
      filtered = filtered.filter(user => !user.isActive);
    }

    // English level filter
    if (this.selectedEnglishLevel !== 'ALL') {
      filtered = filtered.filter(user => user.englishLevel === this.selectedEnglishLevel);
    }

    // Payment status filter
    if (this.selectedPaymentStatus === 'PAID') {
      filtered = filtered.filter(user => user.registrationFeePaid);
    } else if (this.selectedPaymentStatus === 'UNPAID') {
      filtered = filtered.filter(user => !user.registrationFeePaid);
    }

    // Apply sorting
    filtered.sort((a, b) => {
      let aValue: any = a[this.sortField];
      let bValue: any = b[this.sortField];
      
      // Handle null/undefined values
      if (aValue === null || aValue === undefined) aValue = '';
      if (bValue === null || bValue === undefined) bValue = '';
      
      // Convert to lowercase for string comparison
      if (typeof aValue === 'string') aValue = aValue.toLowerCase();
      if (typeof bValue === 'string') bValue = bValue.toLowerCase();
      
      if (aValue < bValue) return this.sortDirection === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });

    this.filteredUsers = filtered;
    this.totalPages = Math.ceil(this.filteredUsers.length / this.itemsPerPage);
    this.currentPage = 1;
    this.savePreferences();
  }

  get paginatedUsers(): User[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredUsers.slice(start, end);
  }

  // Pagination methods
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

  // CRUD Operations
  openCreateModal(): void {
    this.createForm.reset({
      englishLevel: 'A1'
    });
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
    this.createForm.reset();
  }

  createStudent(): void {
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
      role: 'STUDENT'
    };

    // Add optional fields only if they have values
    if (formValue.phone && formValue.phone.trim()) userData.phone = formValue.phone.trim();
    if (formValue.cin && formValue.cin.trim()) userData.cin = formValue.cin.trim();
    if (formValue.dateOfBirth) userData.dateOfBirth = formValue.dateOfBirth;
    if (formValue.address && formValue.address.trim()) userData.address = formValue.address.trim();
    if (formValue.city && formValue.city.trim()) userData.city = formValue.city.trim();
    if (formValue.postalCode && formValue.postalCode.trim()) userData.postalCode = formValue.postalCode.trim();
    if (formValue.englishLevel) userData.englishLevel = formValue.englishLevel;

    this.userService.createUser(userData).subscribe({
      next: (newUser) => {
        this.users.push(newUser);
        this.applyFilters();
        this.closeCreateModal();
        this.toastService.success('Student created successfully!');
      },
      error: (error) => {
        console.error('Error creating student:', error);
        this.toastService.error(error.error?.message || 'Failed to create student. Please try again.');
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
      englishLevel: user.englishLevel || '',
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

  updateStudent(): void {
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
        this.toastService.success('Student updated successfully!');
      },
      error: (error) => {
        console.error('Error updating student:', error);
        this.toastService.error('Failed to update student. Please try again.');
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
    console.log('🟢 Activating user:', user.id, user.email);
    this.userService.activateUser(user.id).subscribe({
      next: (updatedUser) => {
        console.log('✅ User activated:', updatedUser);
        const index = this.users.findIndex(u => u.id === user.id);
        if (index !== -1) {
          this.users[index] = updatedUser;
        }
        this.applyFilters();
        this.toastService.success(`${user.firstName} ${user.lastName} has been activated successfully!`);
      },
      error: (error) => {
        console.error('❌ Error activating student:', error);
        this.toastService.error('Failed to activate student. Please try again.');
      }
    });
  }

  deactivateUser(user: User): void {
    const config: ConfirmationConfig = {
      title: 'Deactivate Student',
      message: `Are you sure you want to deactivate ${user.firstName} ${user.lastName}?`,
      confirmText: 'Deactivate',
      cancelText: 'Cancel',
      type: 'warning',
      details: [
        'Student will lose access to the platform',
        'Progress data will be preserved',
        'Can be reactivated later'
      ]
    };

    this.confirmDialog.config = config;
    this.confirmDialog.show();
    
    const subscription = this.confirmDialog.confirmed.subscribe(() => {
      console.log('🔴 Deactivating user:', user.id, user.email);
      this.userService.deactivateUser(user.id).subscribe({
        next: (updatedUser) => {
          console.log('✅ User deactivated:', updatedUser);
          const index = this.users.findIndex(u => u.id === user.id);
          if (index !== -1) {
            this.users[index] = updatedUser;
          }
          this.applyFilters();
          this.toastService.success(`${user.firstName} ${user.lastName} has been deactivated.`);
        },
        error: (error) => {
          console.error('❌ Error deactivating student:', error);
          this.toastService.error('Failed to deactivate student. Please try again.');
        }
      });
      subscription.unsubscribe();
    });
  }

  deleteUser(user: User): void {
    const config: ConfirmationConfig = {
      title: '⚠️ Delete Student',
      message: `You are about to permanently delete ${user.firstName} ${user.lastName}. This action cannot be undone.`,
      confirmText: 'Delete Permanently',
      cancelText: 'Cancel',
      type: 'danger',
      requireTextConfirmation: true,
      confirmationText: user.lastName,
      details: [
        'All student records will be removed',
        'Enrollment history will be deleted',
        'Progress data will be lost',
        'This action is irreversible'
      ]
    };

    this.confirmDialog.config = config;
    this.confirmDialog.show();
    
    const subscription = this.confirmDialog.confirmed.subscribe(() => {
      this.userService.deleteUser(user.id).subscribe({
        next: () => {
          this.users = this.users.filter(u => u.id !== user.id);
          this.applyFilters();
          this.toastService.success(`${user.firstName} ${user.lastName} has been deleted.`);
        },
        error: (error) => {
          console.error('Error deleting student:', error);
          this.toastService.error('Failed to delete student. Please try again.');
        }
      });
      subscription.unsubscribe();
    });
  }

  // Helper methods
  getActiveCount(): number {
    return this.users.filter(u => u.isActive).length;
  }

  getInactiveCount(): number {
    return this.users.filter(u => !u.isActive).length;
  }

  getUserInitials(user: User): string {
    return `${user.firstName.charAt(0)}${user.lastName.charAt(0)}`.toUpperCase();
  }

  getUserAvatar(user: User): string {
    return user.profilePhoto || '';
  }

  getActivePercentage(): number {
    if (this.users.length === 0) return 0;
    return Math.round((this.getActiveCount() / this.users.length) * 100);
  }

  getFeesCollectedCount(): number {
    return this.users.filter(u => u.registrationFeePaid).length;
  }

  getFeesCollectedPercentage(): number {
    if (this.users.length === 0) return 0;
    return Math.round((this.getFeesCollectedCount() / this.users.length) * 100);
  }

  // New features
  itemsPerPageOptions = [12, 24, 48, 96];

  exportToCSV(): void {
    const headers = ['First Name', 'Last Name', 'Email', 'Phone', 'CIN', 'English Level', 'Status', 'Fee Paid'];
    const data = this.filteredUsers.map(u => [
      u.firstName,
      u.lastName,
      u.email,
      u.phone || '',
      u.cin || '',
      u.englishLevel || '',
      u.isActive ? 'Active' : 'Inactive',
      u.registrationFeePaid ? 'Paid' : 'Unpaid'
    ]);

    const csvContent = [
      headers.join(','),
      ...data.map(row => row.map(cell => `"${cell}"`).join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `students_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    this.toastService.success('Students exported successfully!');
  }

  get Math() {
    return Math;
  }

  // Bulk selection methods
  toggleSelectAll(): void {
    this.selectAll = !this.selectAll;
    if (this.selectAll) {
      this.paginatedUsers.forEach(user => this.selectedUserIds.add(user.id));
    } else {
      this.selectedUserIds.clear();
    }
  }

  toggleUserSelection(userId: number): void {
    if (this.selectedUserIds.has(userId)) {
      this.selectedUserIds.delete(userId);
    } else {
      this.selectedUserIds.add(userId);
    }
    this.selectAll = this.selectedUserIds.size === this.paginatedUsers.length;
  }

  isUserSelected(userId: number): boolean {
    return this.selectedUserIds.has(userId);
  }

  bulkActivate(): void {
    if (this.selectedUserIds.size === 0) {
      this.toastService.error('Please select at least one student.');
      return;
    }

    const count = this.selectedUserIds.size;
    if (confirm(`Activate ${count} selected student(s)?`)) {
      let completed = 0;
      this.selectedUserIds.forEach(userId => {
        const user = this.users.find(u => u.id === userId);
        if (user && !user.isActive) {
          this.userService.activateUser(userId).subscribe({
            next: (updatedUser) => {
              const index = this.users.findIndex(u => u.id === userId);
              if (index !== -1) {
                this.users[index] = updatedUser;
              }
              completed++;
              if (completed === this.selectedUserIds.size) {
                this.applyFilters();
                this.selectedUserIds.clear();
                this.selectAll = false;
                this.toastService.success(`${count} student(s) activated successfully!`);
              }
            },
            error: () => {
              completed++;
              if (completed === this.selectedUserIds.size) {
                this.applyFilters();
                this.toastService.error('Some students could not be activated.');
              }
            }
          });
        } else {
          completed++;
        }
      });
    }
  }

  bulkDeactivate(): void {
    if (this.selectedUserIds.size === 0) {
      this.toastService.error('Please select at least one student.');
      return;
    }

    const count = this.selectedUserIds.size;
    if (confirm(`Deactivate ${count} selected student(s)?`)) {
      let completed = 0;
      this.selectedUserIds.forEach(userId => {
        const user = this.users.find(u => u.id === userId);
        if (user && user.isActive) {
          this.userService.deactivateUser(userId).subscribe({
            next: (updatedUser) => {
              const index = this.users.findIndex(u => u.id === userId);
              if (index !== -1) {
                this.users[index] = updatedUser;
              }
              completed++;
              if (completed === this.selectedUserIds.size) {
                this.applyFilters();
                this.selectedUserIds.clear();
                this.selectAll = false;
                this.toastService.success(`${count} student(s) deactivated successfully!`);
              }
            },
            error: () => {
              completed++;
              if (completed === this.selectedUserIds.size) {
                this.applyFilters();
                this.toastService.error('Some students could not be deactivated.');
              }
            }
          });
        } else {
          completed++;
        }
      });
    }
  }

  exportSelected(): void {
    if (this.selectedUserIds.size === 0) {
      this.toastService.error('Please select at least one student to export.');
      return;
    }

    const selectedUsers = this.users.filter(u => this.selectedUserIds.has(u.id));
    const headers = ['First Name', 'Last Name', 'Email', 'Phone', 'CIN', 'English Level', 'Status', 'Fee Paid'];
    const data = selectedUsers.map(u => [
      u.firstName,
      u.lastName,
      u.email,
      u.phone || '',
      u.cin || '',
      u.englishLevel || '',
      u.isActive ? 'Active' : 'Inactive',
      u.registrationFeePaid ? 'Paid' : 'Unpaid'
    ]);

    const csvContent = [
      headers.join(','),
      ...data.map(row => row.map(cell => `"${cell}"`).join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `selected_students_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    this.toastService.success(`${selectedUsers.length} student(s) exported successfully!`);
  }

  // CSV Import functionality
  importFile: File | null = null;
  importPreview: any[] = [];
  importErrors: string[] = [];

  openImportModal(): void {
    this.showImportModal = true;
    this.importFile = null;
    this.importPreview = [];
    this.importErrors = [];
  }

  closeImportModal(): void {
    this.showImportModal = false;
    this.importFile = null;
    this.importPreview = [];
    this.importErrors = [];
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file && file.type === 'text/csv') {
      this.importFile = file;
      this.parseCSV(file);
    } else {
      this.toastService.error('Please select a valid CSV file.');
    }
  }

  parseCSV(file: File): void {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      const text = e.target.result;
      const lines = text.split('\n').filter((line: string) => line.trim());
      
      if (lines.length < 2) {
        this.toastService.error('CSV file is empty or invalid.');
        return;
      }

      const headers = lines[0].split(',').map((h: string) => h.trim().replace(/"/g, ''));
      this.importPreview = [];
      this.importErrors = [];

      for (let i = 1; i < lines.length; i++) {
        const values = lines[i].split(',').map((v: string) => v.trim().replace(/"/g, ''));
        const student: any = {};
        
        headers.forEach((header: string, index: number) => {
          student[header.toLowerCase().replace(/ /g, '')] = values[index] || '';
        });

        // Validation
        const errors: string[] = [];
        if (!student.firstname) errors.push('First name is required');
        if (!student.lastname) errors.push('Last name is required');
        if (!student.email) errors.push('Email is required');
        if (!student.password) errors.push('Password is required');
        
        if (student.email && !this.isValidEmail(student.email)) {
          errors.push('Invalid email format');
        }

        this.importPreview.push({
          data: student,
          errors: errors,
          valid: errors.length === 0
        });
      }

      this.toastService.success(`Parsed ${this.importPreview.length} rows. ${this.importPreview.filter(p => p.valid).length} valid.`);
    };
    reader.readAsText(file);
  }

  isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  importStudents(): void {
    const validStudents = this.importPreview.filter(p => p.valid);
    
    if (validStudents.length === 0) {
      this.toastService.error('No valid students to import.');
      return;
    }

    let imported = 0;
    let failed = 0;

    validStudents.forEach((preview, index) => {
      const student = preview.data;
      const userData: CreateUserRequest = {
        email: student.email,
        password: student.password,
        firstName: student.firstname,
        lastName: student.lastname,
        role: 'STUDENT'
      };

      if (student.phone) userData.phone = student.phone;
      if (student.cin) userData.cin = student.cin;
      if (student.dateofbirth) userData.dateOfBirth = student.dateofbirth;
      if (student.address) userData.address = student.address;
      if (student.city) userData.city = student.city;
      if (student.postalcode) userData.postalCode = student.postalcode;
      if (student.englishlevel) userData.englishLevel = student.englishlevel;

      this.userService.createUser(userData).subscribe({
        next: (newUser) => {
          imported++;
          this.users.push(newUser);
          
          if (imported + failed === validStudents.length) {
            this.applyFilters();
            this.closeImportModal();
            this.toastService.success(`Successfully imported ${imported} student(s). ${failed > 0 ? failed + ' failed.' : ''}`);
          }
        },
        error: (error) => {
          failed++;
          console.error('Error importing student:', error);
          
          if (imported + failed === validStudents.length) {
            this.applyFilters();
            this.closeImportModal();
            this.toastService.success(`Successfully imported ${imported} student(s). ${failed > 0 ? failed + ' failed.' : ''}`);
          }
        }
      });
    });
  }

  downloadTemplate(): void {
    const headers = ['First Name', 'Last Name', 'Email', 'Password', 'Phone', 'CIN', 'Date of Birth', 'Address', 'City', 'Postal Code', 'English Level'];
    const example = ['John', 'Doe', 'john.doe@example.com', 'password123', '+1234567890', 'AB123456', '1990-01-01', '123 Main St', 'New York', '10001', 'B1'];
    
    const csvContent = [
      headers.join(','),
      example.join(',')
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', 'students_import_template.csv');
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    this.toastService.success('Template downloaded successfully!');
  }

  getValidPreviewCount(): number {
    return this.importPreview.filter(p => p.valid).length;
  }

  // Preferences Management
  private loadPreferences(): void {
    const prefs = this.preferencesService.loadPreferences('students');
    if (prefs) {
      if (prefs.itemsPerPage) this.itemsPerPage = prefs.itemsPerPage;
      if (prefs.sortField) this.sortField = prefs.sortField as any;
      if (prefs.sortDirection) this.sortDirection = prefs.sortDirection;
      if (prefs.selectedStatus) this.selectedStatus = prefs.selectedStatus;
      if (prefs.selectedEnglishLevel) this.selectedEnglishLevel = prefs.selectedEnglishLevel;
      if (prefs.selectedPaymentStatus) this.selectedPaymentStatus = prefs.selectedPaymentStatus;
      // Load view mode
      const viewMode = localStorage.getItem('students_view_mode');
      if (viewMode) this.viewMode = viewMode as any;
    }
  }

  private savePreferences(): void {
    this.preferencesService.savePreferences('students', {
      itemsPerPage: this.itemsPerPage,
      sortField: this.sortField,
      sortDirection: this.sortDirection,
      selectedStatus: this.selectedStatus,
      selectedEnglishLevel: this.selectedEnglishLevel,
      selectedPaymentStatus: this.selectedPaymentStatus
    });
  }

  setViewMode(mode: 'comfortable' | 'compact' | 'spacious'): void {
    this.viewMode = mode;
    localStorage.setItem('students_view_mode', mode);
  }

  // Override methods to save preferences
  changeItemsPerPage(value: number): void {
    this.itemsPerPage = value;
    this.currentPage = 1;
    this.totalPages = Math.ceil(this.filteredUsers.length / this.itemsPerPage);
    this.savePreferences();
  }

  sortBy(field: 'firstName' | 'lastName' | 'email' | 'englishLevel' | 'registrationFeePaid'): void {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.applyFilters();
    this.savePreferences();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedStatus = 'ALL';
    this.selectedEnglishLevel = 'ALL';
    this.selectedPaymentStatus = 'ALL';
    this.applyFilters();
    this.savePreferences();
  }

  // Debounced search
  onSearchChange(searchTerm: string): void {
    if (this.searchDebounceTimer) {
      clearTimeout(this.searchDebounceTimer);
    }
    
    this.isSearching = true;
    
    this.searchDebounceTimer = setTimeout(() => {
      this.searchTerm = searchTerm;
      this.applyFilters();
      this.isSearching = false;
    }, 300); // 300ms debounce
  }
}
