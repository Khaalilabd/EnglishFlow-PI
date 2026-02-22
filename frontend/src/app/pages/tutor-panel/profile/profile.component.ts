import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { AuthResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-tutor-profile',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="p-6 max-w-7xl mx-auto">
      <!-- Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">My Profile</h1>
        <p class="text-gray-600 mt-2">Manage your personal information and preferences</p>
      </div>

      <!-- Profile Card -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <!-- Cover Image -->
        <div class="h-32 bg-gradient-to-r from-teal-500 to-teal-600"></div>

        <!-- Profile Info -->
        <div class="px-8 pb-8">
          <!-- Avatar -->
          <div class="flex items-end -mt-16 mb-6">
            <img 
              [src]="getProfilePhotoUrl(currentUser?.profilePhoto)"
              [alt]="currentUser?.firstName + ' ' + currentUser?.lastName"
              class="w-32 h-32 rounded-full border-4 border-white shadow-lg object-cover">
            <div class="ml-6 mb-2">
              <h2 class="text-2xl font-bold text-gray-900">
                {{currentUser?.firstName}} {{currentUser?.lastName}}
              </h2>
              <p class="text-teal-600 font-medium">{{getRoleLabel(currentUser?.role)}}</p>
            </div>
          </div>

          <!-- Info Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Email -->
            <div class="flex items-start space-x-3">
              <div class="flex-shrink-0 w-10 h-10 bg-teal-100 rounded-lg flex items-center justify-center">
                <i class="fas fa-envelope text-teal-600"></i>
              </div>
              <div>
                <p class="text-sm text-gray-500">Email Address</p>
                <p class="text-gray-900 font-medium">{{currentUser?.email}}</p>
              </div>
            </div>

            <!-- Role -->
            <div class="flex items-start space-x-3">
              <div class="flex-shrink-0 w-10 h-10 bg-teal-100 rounded-lg flex items-center justify-center">
                <i class="fas fa-user-tag text-teal-600"></i>
              </div>
              <div>
                <p class="text-sm text-gray-500">Role</p>
                <p class="text-gray-900 font-medium">{{getRoleLabel(currentUser?.role)}}</p>
              </div>
            </div>

            <!-- User ID -->
            <div class="flex items-start space-x-3">
              <div class="flex-shrink-0 w-10 h-10 bg-teal-100 rounded-lg flex items-center justify-center">
                <i class="fas fa-id-card text-teal-600"></i>
              </div>
              <div>
                <p class="text-sm text-gray-500">User ID</p>
                <p class="text-gray-900 font-medium">#{{currentUser?.id}}</p>
              </div>
            </div>

            <!-- Status -->
            <div class="flex items-start space-x-3">
              <div class="flex-shrink-0 w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                <i class="fas fa-check-circle text-green-600"></i>
              </div>
              <div>
                <p class="text-sm text-gray-500">Account Status</p>
                <p class="text-green-600 font-medium">Active</p>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="mt-8 flex gap-4">
            <button 
              routerLink="/tutor-panel/settings"
              class="px-6 py-3 bg-teal-600 text-white rounded-lg hover:bg-teal-700 transition-colors font-medium">
              <i class="fas fa-cog mr-2"></i>
              Edit Profile
            </button>
            <button 
              class="px-6 py-3 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors font-medium">
              <i class="fas fa-key mr-2"></i>
              Change Password
            </button>
          </div>
        </div>
      </div>

      <!-- Quick Stats -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">Total Students</p>
              <p class="text-3xl font-bold text-gray-900 mt-2">0</p>
            </div>
            <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <i class="fas fa-users text-blue-600 text-xl"></i>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">Active Courses</p>
              <p class="text-3xl font-bold text-gray-900 mt-2">0</p>
            </div>
            <div class="w-12 h-12 bg-teal-100 rounded-lg flex items-center justify-center">
              <i class="fas fa-book text-teal-600 text-xl"></i>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-sm text-gray-500">Quizzes Created</p>
              <p class="text-3xl font-bold text-gray-900 mt-2">0</p>
            </div>
            <div class="w-12 h-12 bg-amber-100 rounded-lg flex items-center justify-center">
              <i class="fas fa-clipboard-list text-amber-600 text-xl"></i>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class TutorProfileComponent implements OnInit {
  currentUser: AuthResponse | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.currentUser = this.authService.currentUserValue;
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  getProfilePhotoUrl(photoUrl: string | null | undefined): string {
    if (!photoUrl) {
      const name = `${this.currentUser?.firstName || 'Tutor'}+${this.currentUser?.lastName || 'Name'}`;
      return `https://ui-avatars.com/api/?name=${name}&background=2D5757&color=fff&size=256`;
    }
    
    if (photoUrl.startsWith('http')) {
      return photoUrl;
    }
    
    return `http://localhost:8081${photoUrl}`;
  }

  getRoleLabel(role: string | undefined): string {
    switch(role) {
      case 'TUTOR': return 'Instructor';
      case 'TEACHER': return 'Teacher';
      case 'STUDENT': return 'Learner';
      case 'ADMIN': return 'Administrator';
      default: return role || 'User';
    }
  }
}
