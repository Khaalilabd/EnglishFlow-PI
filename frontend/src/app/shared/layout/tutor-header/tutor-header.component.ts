import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SidebarService } from '../../services/sidebar.service';

@Component({
  selector: 'app-tutor-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <header 
      class="bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between shadow-sm transition-all duration-300"
      [class.ml-0]="true">
      
      <!-- Left: Search -->
      <div class="flex items-center flex-1 max-w-2xl">
        <div class="relative w-full">
          <i class="fas fa-search absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
          <input 
            type="text" 
            placeholder="Search courses, students, quizzes..."
            class="w-full pl-12 pr-4 py-2.5 bg-gray-50 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-teal-500 focus:border-transparent transition-all">
        </div>
      </div>

      <!-- Right: Actions -->
      <div class="flex items-center space-x-4 ml-6">
        <!-- Notifications -->
        <button class="relative p-2 hover:bg-gray-100 rounded-xl transition-colors">
          <i class="fas fa-bell text-gray-600 text-xl"></i>
          <span class="absolute top-1 right-1 w-2 h-2 bg-amber-500 rounded-full"></span>
        </button>

        <!-- Messages -->
        <button class="relative p-2 hover:bg-gray-100 rounded-xl transition-colors">
          <i class="fas fa-envelope text-gray-600 text-xl"></i>
          <span class="absolute top-0 right-0 bg-teal-600 text-white text-xs font-bold px-1.5 py-0.5 rounded-full">3</span>
        </button>

        <!-- User Menu -->
        <div class="flex items-center space-x-3 pl-4 border-l border-gray-200">
          <img 
            src="https://ui-avatars.com/api/?name=Tutor&background=2D5757&color=fff" 
            alt="Tutor"
            class="w-10 h-10 rounded-full border-2 border-teal-600">
          <div class="hidden md:block">
            <p class="text-sm font-semibold text-gray-800">Tutor Name</p>
            <p class="text-xs text-teal-600">Instructor</p>
          </div>
          <button class="p-2 hover:bg-gray-100 rounded-lg transition-colors">
            <i class="fas fa-chevron-down text-gray-600 text-sm"></i>
          </button>
        </div>
      </div>
    </header>
  `
})
export class TutorHeaderComponent implements OnInit {
  isCollapsed = false;

  constructor(private sidebarService: SidebarService) {}

  ngOnInit() {
    this.sidebarService.isExpanded$.subscribe((expanded: boolean) => {
      this.isCollapsed = !expanded;
    });
  }
}
