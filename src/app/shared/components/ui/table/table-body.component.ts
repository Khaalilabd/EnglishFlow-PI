import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  standalone: true,
  selector: 'app-table-body',
  imports: [CommonModule],
  template: `
    <tbody [ngClass]="className"><ng-content></ng-content></tbody>
  `,
})
export class TableBodyComponent {
  @Input() className = '';
}
