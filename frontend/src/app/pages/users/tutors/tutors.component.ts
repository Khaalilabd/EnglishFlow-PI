import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { UserService, User, UpdateUserRequest } from '../../../core/services/user.service';
import { RecruitmentService, ApplicationResponse } from '../../../core/services/recruitment.service';
import { ToastService } from '../../../core/services/toast.service';
import { ConfirmationDialogComponent, ConfirmationConfig } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { SkeletonLoaderComponent } from '../../../shared/components/skeleton-loader/skeleton-loader.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-tutors',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, ConfirmationDialogComponent, SkeletonLoaderComponent],
  templateUrl: './tutors.component.html',
  styleUrls: ['./tutors.component.scss']
})
export class TutorsComponent implements OnInit, OnDestroy {
  @ViewChild(ConfirmationDialogComponent) confirmDialog!: ConfirmationDialogComponent;
  
  users: User[] = [];
  filteredUsers: User[] = [];
  loading = false;
  searchTerm = '';
  selectedStatus = 'ALL';
  selectedExperienceRange = 'ALL';
  showAdvancedFilters = false;
  
  showEditModal = false;
  showViewModal = false;
  showDocumentModal = false;
  editForm!: FormGroup;
  selectedUser: User | null = null;
  selectedApplication: ApplicationResponse | null = null;
  selectedDocument: any = null;
  documentViewerUrl: string = '';
  loadingApplication = false;
  
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  // Sorting
  sortField: 'firstName' | 'lastName' | 'email' | 'yearsOfExperience' = 'firstName';
  sortDirection: 'asc' | 'desc' = 'asc';

  constructor(
    private userService: UserService,
    private recruitmentService: RecruitmentService,
    private fb: FormBuilder,
    private toastService: ToastService,
    private sanitizer: DomSanitizer
  ) {
    this.initForms();
  }

  ngOnInit(): void {
    this.loadTutors();
    this.setupKeyboardShortcuts();
  }

  ngOnDestroy(): void {
    document.removeEventListener('keydown', this.handleKeyboardShortcut);
  }

  private handleKeyboardShortcut = (event: KeyboardEvent): void => {
    if ((event.ctrlKey || event.metaKey) && event.key === 'f') {
      event.preventDefault();
      const searchInput = document.querySelector('.search-input') as HTMLInputElement;
      if (searchInput) searchInput.focus();
    }
    if (event.key === 'Escape') {
      if (this.showEditModal) this.closeEditModal();
      if (this.showViewModal) this.closeViewModal();
    }
    if ((event.ctrlKey || event.metaKey) && event.key === 'e') {
      event.preventDefault();
      this.exportToCSV();
    }
  };

  private setupKeyboardShortcuts(): void {
    document.addEventListener('keydown', this.handleKeyboardShortcut);
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedStatus = 'ALL';
    this.selectedExperienceRange = 'ALL';
    this.applyFilters();
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

    // Experience range filter
    if (this.selectedExperienceRange === '0-2') {
      filtered = filtered.filter(user => user.yearsOfExperience && user.yearsOfExperience <= 2);
    } else if (this.selectedExperienceRange === '3-5') {
      filtered = filtered.filter(user => user.yearsOfExperience && user.yearsOfExperience >= 3 && user.yearsOfExperience <= 5);
    } else if (this.selectedExperienceRange === '5+') {
      filtered = filtered.filter(user => user.yearsOfExperience && user.yearsOfExperience > 5);
    }

    // Apply sorting
    filtered.sort((a, b) => {
      let aValue: any = a[this.sortField];
      let bValue: any = b[this.sortField];
      
      if (aValue === null || aValue === undefined) aValue = '';
      if (bValue === null || bValue === undefined) bValue = '';
      
      if (typeof aValue === 'string') aValue = aValue.toLowerCase();
      if (typeof bValue === 'string') bValue = bValue.toLowerCase();
      
      if (aValue < bValue) return this.sortDirection === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });

    this.filteredUsers = filtered;
    this.totalPages = Math.ceil(this.filteredUsers.length / this.itemsPerPage);
    this.currentPage = 1;
  }

  sortBy(field: 'firstName' | 'lastName' | 'email' | 'yearsOfExperience'): void {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.applyFilters();
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
    const config: ConfirmationConfig = {
      title: 'Deactivate Tutor',
      message: `Are you sure you want to deactivate ${user.firstName} ${user.lastName}?`,
      confirmText: 'Deactivate',
      cancelText: 'Cancel',
      type: 'warning',
      details: [
        'Tutor will lose access to the platform',
        'Teaching history will be preserved',
        'Student assignments will be affected',
        'Can be reactivated later'
      ]
    };

    this.confirmDialog.config = config;
    this.confirmDialog.show();
    
    const subscription = this.confirmDialog.confirmed.subscribe(() => {
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
      subscription.unsubscribe();
    });
  }

  deleteUser(user: User): void {
    Swal.fire({
      title: '⚠️ Delete Tutor',
      html: `
        <p>You are about to permanently delete <strong>${user.firstName} ${user.lastName}</strong>.</p>
        <p class="text-danger">This action cannot be undone.</p>
        <ul class="text-left mt-3">
          <li>All tutor records will be removed</li>
          <li>Teaching history will be deleted</li>
          <li>Student assignments will be affected</li>
          <li>This action is irreversible</li>
        </ul>
        <p class="mt-3">Type <strong>${user.lastName}</strong> to confirm:</p>
      `,
      input: 'text',
      inputPlaceholder: `Type ${user.lastName} to confirm`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Delete Permanently',
      cancelButtonText: 'Cancel',
      preConfirm: (inputValue) => {
        if (inputValue !== user.lastName) {
          Swal.showValidationMessage(`Please type "${user.lastName}" to confirm`);
          return false;
        }
        return true;
      }
    }).then((result) => {
      if (result.isConfirmed) {
        this.userService.deleteUser(user.id).subscribe({
          next: () => {
            this.users = this.users.filter(u => u.id !== user.id);
            this.applyFilters();
            Swal.fire({
              icon: 'success',
              title: 'Deleted!',
              text: `${user.firstName} ${user.lastName} has been deleted.`,
              timer: 2000,
              showConfirmButton: false
            });
          },
          error: (error) => {
            console.error('Error deleting user:', error);
            Swal.fire({
              icon: 'error',
              title: 'Error',
              text: 'Failed to delete tutor. Please try again.'
            });
          }
        });
      }
    });
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
    this.selectedApplication = null;
    this.showViewModal = true;
    
    console.log('👤 Opening view modal for user:', user);
    console.log('📋 User applicationId:', user.applicationId);
    
    // Load recruitment application if exists
    if (user.applicationId) {
      console.log('✅ Loading recruitment application...');
      this.loadingApplication = true;
      this.recruitmentService.getApplicationByUserId(user.id).subscribe({
        next: (application) => {
          console.log('✅ Application loaded:', application);
          this.selectedApplication = application;
          this.loadingApplication = false;
        },
        error: (error) => {
          console.error('❌ Error loading application:', error);
          this.loadingApplication = false;
        }
      });
    } else {
      console.log('⚠️ No applicationId found for this user');
    }
  }

  closeViewModal(): void {
    this.showViewModal = false;
    this.selectedUser = null;
    this.selectedApplication = null;
  }

  // Document viewer methods
  openDocumentModal(document: any): void {
    this.selectedDocument = document;
    this.documentViewerUrl = this.getDocumentUrl(document);
    this.showDocumentModal = true;
  }

  closeDocumentModal(): void {
    this.showDocumentModal = false;
    this.selectedDocument = null;
    this.documentViewerUrl = '';
  }

  getDocumentUrl(document: any): string {
    return `http://localhost:8080/${document.filePath}`;
  }

  getSafeUrl(url: string): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }

  downloadDocument(document: any): void {
    const url = this.getDocumentUrl(document);
    window.open(url, '_blank');
  }

  getDocumentIcon(document: any): string {
    const type = document.type.toLowerCase();
    const fileType = document.fileType?.toLowerCase() || '';
    
    if (type === 'video_presentation' || fileType.includes('video')) {
      return '🎥';
    } else if (fileType.includes('pdf')) {
      return '📄';
    } else if (fileType.includes('image')) {
      return '🖼️';
    } else if (fileType.includes('word') || fileType.includes('doc')) {
      return '📝';
    }
    return '📎';
  }

  isVideoDocument(document: any): boolean {
    return document.type === 'VIDEO_PRESENTATION' || 
           document.fileType?.toLowerCase().includes('video');
  }

  isPdfDocument(document: any): boolean {
    return document.fileType?.toLowerCase().includes('pdf');
  }

  isImageDocument(document: any): boolean {
    return document.fileType?.toLowerCase().includes('image');
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  }

  getDocumentTypeName(type: string): string {
    const names: { [key: string]: string } = {
      'CV': 'Curriculum Vitae',
      'DEGREE': 'Degree Certificate',
      'CERTIFICATE': 'Teaching Certificate',
      'ID_CARD': 'ID Card',
      'VIDEO_PRESENTATION': 'Video Presentation',
      'OTHER': 'Other Document'
    };
    return names[type] || type;
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  }

  getScoreColor(score: number | undefined): string {
    if (!score) return '#999';
    if (score >= 80) return '#2D5757';
    if (score >= 60) return '#F6BD60';
    return '#C84630';
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

  // New features
  itemsPerPageOptions = [12, 24, 48, 96];

  changeItemsPerPage(value: number): void {
    this.itemsPerPage = value;
    this.currentPage = 1;
    this.totalPages = Math.ceil(this.filteredUsers.length / this.itemsPerPage);
  }

  exportToCSV(): void {
    const headers = ['First Name', 'Last Name', 'Email', 'Phone', 'CIN', 'Years of Experience', 'Status', 'Fee Paid'];
    const data = this.filteredUsers.map(u => [
      u.firstName,
      u.lastName,
      u.email,
      u.phone || '',
      u.cin || '',
      u.yearsOfExperience?.toString() || '',
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
    link.setAttribute('download', `tutors_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    this.toastService.success('Tutors exported successfully!');
  }

  get Math() {
    return Math;
  }
}
