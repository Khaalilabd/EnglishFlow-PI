import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarService } from '../../services/sidebar.service';

@Component({
  standalone: true,
  selector: 'app-student-header',
  imports: [CommonModule],
  template: `
    <header class="sticky top-0 z-40 w-full bg-white border-b border-gray-200">
      <div class="flex items-center justify-between px-6 py-4">
        <div class="flex items-center gap-4">
          <!-- Hamburger Toggle -->
          <button
            (click)="toggleSidebar()"
            class="block rounded-lg p-2 hover:bg-gray-100 xl:hidden"
          >
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path>
            </svg>
          </button>

          <!-- Search Bar -->
          <div class="hidden sm:block">
            <div class="relative">
              <input
                type="text"
                placeholder="Search courses, lessons..."
                class="w-80 rounded-lg border border-gray-300 bg-gray-50 py-2 pl-10 pr-4 text-sm focus:border-[#F6BD60] focus:outline-none focus:ring-2 focus:ring-[#F6BD60]/20"
              />
              <svg class="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
              </svg>
            </div>
          </div>
        </div>

        <div class="flex items-center gap-4">
          <!-- Notifications -->
          <button class="relative rounded-full p-2 hover:bg-gray-100 transition-colors">
            <svg class="h-6 w-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
            </svg>
            <span class="absolute right-1 top-1 h-2 w-2 rounded-full bg-red-500"></span>
          </button>

          <!-- Messages -->
          <button class="relative rounded-full p-2 hover:bg-gray-100 transition-colors">
            <svg class="h-6 w-6 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"></path>
            </svg>
            <span class="absolute right-1 top-1 h-2 w-2 rounded-full bg-[#F6BD60]"></span>
          </button>

          <!-- User Avatar -->
          <img
            src="https://i.pravatar.cc/150?img=12"
            alt="User"
            class="h-10 w-10 rounded-full border-2 border-[#F6BD60]"
          />
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
