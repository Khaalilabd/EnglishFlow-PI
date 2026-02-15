import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { TutorSidebarComponent } from '../tutor-sidebar/tutor-sidebar.component';
import { TutorHeaderComponent } from '../tutor-header/tutor-header.component';
import { SidebarService } from '../../services/sidebar.service';

@Component({
  selector: 'app-tutor-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, TutorSidebarComponent, TutorHeaderComponent],
  template: `
    <div class="flex h-screen bg-gray-50 overflow-hidden">
      <app-tutor-sidebar></app-tutor-sidebar>
      <div class="flex-1 flex flex-col overflow-hidden" 
           [class.ml-72]="isExpanded"
           [class.ml-20]="!isExpanded"
           style="transition: margin-left 300ms ease;">
        <app-tutor-header></app-tutor-header>
        <main class="flex-1 overflow-y-auto">
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `
})
export class TutorLayoutComponent {
  isExpanded = true;

  constructor(private sidebarService: SidebarService) {
    this.sidebarService.isExpanded$.subscribe(expanded => {
      this.isExpanded = expanded;
    });
  }
}
