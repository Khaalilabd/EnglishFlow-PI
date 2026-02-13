import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AiSidebarHistoryComponent } from '../../components/ai/ai-sidebar-history/ai-sidebar-history.component';

@Component({
  standalone: true,
  selector: 'app-generator-layout',
  imports: [CommonModule, AiSidebarHistoryComponent],
  templateUrl: './generator-layout.component.html',
  styles: ``,
})
export class GeneratorLayoutComponent {
  sidebarOpen = true;

  closeSidebar = () => {
    this.sidebarOpen = false;
  };
}
