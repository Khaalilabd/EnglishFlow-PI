import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarService } from '../../services/sidebar.service';
import { SponsorSidebarComponent } from './sponsor-sidebar.component';
import { BackdropComponent } from '../backdrop/backdrop.component';
import { StudentHeaderComponent } from '../student-header/student-header.component';
import { ToastComponent } from '../../components/toast/toast.component';

@Component({
  standalone: true,
  selector: 'app-sponsor-layout',
  imports: [
    CommonModule,
    RouterModule,
    StudentHeaderComponent,
    SponsorSidebarComponent,
    BackdropComponent,
    ToastComponent
  ],
  template: `
    <div class="flex h-screen bg-gradient-to-br from-[#F7EDE2] via-white to-[#F7EDE2] overflow-hidden">
      <app-sponsor-sidebar></app-sponsor-sidebar>
      <app-backdrop></app-backdrop>

      <div
        class="flex-1 flex flex-col overflow-hidden transition-all duration-300"
        [ngClass]="{
          'xl:ml-[280px]': (isExpanded$ | async) || (isHovered$ | async),
          'xl:ml-[80px]': !(isExpanded$ | async) && !(isHovered$ | async)
        }"
      >
        <app-student-header/>
        <main class="flex-1 overflow-y-auto bg-gradient-to-br from-[#F7EDE2]/30 to-white/50">
          <router-outlet></router-outlet>
        </main>
      </div>

      <app-toast></app-toast>
    </div>
  `
})
export class SponsorLayoutComponent {
  readonly isExpanded$;
  readonly isHovered$;
  readonly isMobileOpen$;

  constructor(public sidebarService: SidebarService) {
    this.isExpanded$ = this.sidebarService.isExpanded$;
    this.isHovered$ = this.sidebarService.isHovered$;
    this.isMobileOpen$ = this.sidebarService.isMobileOpen$;
  }
}
