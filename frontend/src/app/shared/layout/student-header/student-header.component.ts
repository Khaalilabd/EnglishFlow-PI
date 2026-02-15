import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarService } from '../../services/sidebar.service';

@Component({
  standalone: true,
  selector: 'app-student-header',
  imports: [CommonModule],
  template: `
    <header 
      class="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between shadow-sm transition-all duration-300">
      
      <!-- Left: Mobile Toggle + Search -->
      <div class="flex items-center flex-1 max-w-2xl gap-4">
        <!-- Hamburger Toggle for Mobile -->
        <button
          (click)="toggleSidebar()"
          class="block rounded-xl p-2 hover:bg-gray-100 xl:hidden transition-colors"
        >
          <svg class="h-6 w-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
          </svg>
        </button>
        
        <!-- Search Bar -->
        <div class="relative flex-1">
          <i class="fas fa-search absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
          <input 
            type="text" 
            placeholder="Search courses, lessons, assignments..."
            class="w-full pl-12 pr-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#2D5757] focus:border-transparent transition-all">
        </div>
      </div>

      <!-- Right: Actions -->
      <div class="flex items-center space-x-4 ml-6">
        <!-- Notifications -->
        <button class="relative p-2 hover:bg-gray-100 rounded-xl transition-colors">
          <i class="fas fa-bell text-gray-600 text-xl"></i>
          <span class="absolute top-1 right-1 w-2 h-2 bg-[#F6BD60] rounded-full animate-pulse"></span>
        </button>

        <!-- Messages -->
        <button class="relative p-2 hover:bg-gray-100 rounded-xl transition-colors">
          <i class="fas fa-envelope text-gray-600 text-xl"></i>
          <span class="absolute top-0 right-0 bg-[#2D5757] text-white text-xs font-bold px-1.5 py-0.5 rounded-full">5</span>
        </button>

        <!-- User Menu -->
        <div class="flex items-center space-x-3 pl-4 border-l border-gray-200">
          <img 
            src="https://i.pravatar.cc/150?img=12" 
            alt="Student"
            class="w-10 h-10 rounded-full border-2 border-[#2D5757] shadow-sm">
          <div class="hidden md:block">
            <p class="text-sm font-semibold text-gray-800">Student Name</p>
            <p class="text-xs text-[#2D5757]">Learner</p>
          </div>
          <button class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
            <i class="fas fa-chevron-down text-gray-600 text-sm"></i>
          </button>
        </div>
      </div>
    </header>
  `
})
export class StudentHeaderComponent {
  constructor(private sidebarService: SidebarService) {}

  toggleSidebar() {
    this.sidebarService.toggleMobileOpen();
  }
}
