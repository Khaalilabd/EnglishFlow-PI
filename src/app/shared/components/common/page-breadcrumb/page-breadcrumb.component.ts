import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-page-breadcrumb',
  imports: [
    RouterModule,
  ],
  templateUrl: './page-breadcrumb.component.html',
  styles: ``
})
export class PageBreadcrumbComponent {
  @Input() pageTitle = '';
}
