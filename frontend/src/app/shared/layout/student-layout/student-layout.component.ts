import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SidebarService } from '../../services/sidebar.service';
import { StudentSidebarComponent } from '../student-sidebar/student-sidebar.component';
import { BackdropComponent } from '../backdrop/backdrop.component';
import { StudentHeaderComponent } from '../student-header/student-header.component';

@Component({
  standalone: true,
  selector: 'app-student-layout',
  imports: [
    CommonModule,
    RouterModule,
    StudentHeaderComponent,
    StudentSidebarComponent,
    BackdropComponent
  ],
  templateUrl: './student-layout.component.html',
})
export class StudentLayoutComponent {
  readonly isExpanded$;
  readonly isHovered$;
  readonly isMobileOpen$;

  constructor(public sidebarService: SidebarService) {
    this.isExpanded$ = this.sidebarService.isExpanded$;
    this.isHovered$ = this.sidebarService.isHovered$;
    this.isMobileOpen$ = this.sidebarService.isMobileOpen$;
  }
}
