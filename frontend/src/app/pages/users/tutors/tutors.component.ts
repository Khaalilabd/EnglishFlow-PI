import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UserService, User } from '../../../core/services/user.service';

@Component({
  selector: 'app-tutors',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './tutors.component.html',
  styleUrls: ['./tutors.component.scss']
})
export class TutorsComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  loading = false;
  searchTerm = '';
  selectedStatus = 'ALL';
  
  // Pagination
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadTutors();
  }

  loadTutors(): void {
    this.loading = true;
    this.userService.getUsersByRole('TUTOR').subscribe({
      next: (data) => {
        this.users = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading tutors:', error);
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
        user.isActive = updatedUser.isActive;
        alert('Tutor activated successfully!');
      },
      error: (error: any) => {
        console.error('Error activating tutor:', error);
        alert('Failed to activate tutor.');
      }
    });
  }

  deactivateUser(user: User): void {
    if (confirm(`Are you sure you want to deactivate ${user.firstName} ${user.lastName}?`)) {
      this.userService.deactivateUser(user.id).subscribe({
        next: (updatedUser: User) => {
          user.isActive = updatedUser.isActive;
          alert('Tutor deactivated successfully!');
        },
        error: (error: any) => {
          console.error('Error deactivating tutor:', error);
          alert('Failed to deactivate tutor.');
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
        },
        error: (error) => {
          console.error('Error deleting user:', error);
        }
      });
    }
  }

  editUser(user: User): void {
    console.log('Edit user:', user);
  }

  viewUser(user: User): void {
    console.log('View user:', user);
  }

  getActiveCount(): number {
    return this.users.filter(u => u.isActive).length;
  }

  get Math() {
    return Math;
  }
}
