import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  isActive: boolean;
  registrationFeePaid: boolean;
  createdAt: string;
  phone?: string;
  cin?: string;
  englishLevel?: string;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  loading = false;
  searchTerm = '';
  selectedRole = 'ALL';
  selectedStatus = 'ALL';
  
  // Pagination
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    // TODO: Replace with actual API call
    this.http.get<User[]>('http://localhost:8081/api/users').subscribe({
      next: (data) => {
        this.users = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading users:', error);
        // Mock data for development
        this.users = this.getMockUsers();
        this.applyFilters();
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

    // Role filter
    if (this.selectedRole !== 'ALL') {
      filtered = filtered.filter(user => user.role === this.selectedRole);
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

  toggleUserStatus(user: User): void {
    // TODO: API call to toggle status
    user.isActive = !user.isActive;
  }

  deleteUser(user: User): void {
    if (confirm(`Are you sure you want to delete ${user.firstName} ${user.lastName}?`)) {
      // TODO: API call to delete user
      this.users = this.users.filter(u => u.id !== user.id);
      this.applyFilters();
    }
  }

  editUser(user: User): void {
    // TODO: Navigate to edit page or open modal
    console.log('Edit user:', user);
  }

  viewUser(user: User): void {
    // TODO: Navigate to user details page
    console.log('View user:', user);
  }

  getStudentsCount(): number {
    return this.users.filter(u => u.role === 'STUDENT').length;
  }

  getTeachersCount(): number {
    return this.users.filter(u => u.role === 'TEACHER').length;
  }

  getActiveCount(): number {
    return this.users.filter(u => u.isActive).length;
  }

  get Math() {
    return Math;
  }

  getMockUsers(): User[] {
    return [
      {
        id: 1,
        email: 'john.doe@example.com',
        firstName: 'John',
        lastName: 'Doe',
        role: 'STUDENT',
        isActive: true,
        registrationFeePaid: true,
        createdAt: '2024-01-15T10:30:00',
        phone: '+216 20 123 456',
        cin: '12345678',
        englishLevel: 'Intermediate'
      },
      {
        id: 2,
        email: 'jane.smith@example.com',
        firstName: 'Jane',
        lastName: 'Smith',
        role: 'TEACHER',
        isActive: true,
        registrationFeePaid: true,
        createdAt: '2024-01-10T09:00:00',
        phone: '+216 21 234 567',
        cin: '23456789',
        englishLevel: 'Advanced'
      },
      {
        id: 3,
        email: 'bob.wilson@example.com',
        firstName: 'Bob',
        lastName: 'Wilson',
        role: 'STUDENT',
        isActive: false,
        registrationFeePaid: false,
        createdAt: '2024-02-01T14:20:00',
        phone: '+216 22 345 678',
        cin: '34567890',
        englishLevel: 'Beginner'
      },
      {
        id: 4,
        email: 'alice.brown@example.com',
        firstName: 'Alice',
        lastName: 'Brown',
        role: 'STUDENT',
        isActive: true,
        registrationFeePaid: true,
        createdAt: '2024-01-20T11:45:00',
        phone: '+216 23 456 789',
        cin: '45678901',
        englishLevel: 'Upper Intermediate'
      },
      {
        id: 5,
        email: 'admin@jungleinenglish.com',
        firstName: 'Admin',
        lastName: 'User',
        role: 'ADMIN',
        isActive: true,
        registrationFeePaid: true,
        createdAt: '2024-01-01T08:00:00',
        phone: '+216 24 567 890',
        cin: '56789012'
      }
    ];
  }
}
