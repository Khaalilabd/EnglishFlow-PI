import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  standalone: true,
  selector: 'app-table-cell',
  imports: [CommonModule],
  template: `
    @if (isHeader) {
    <ng-container>
      <th [ngClass]="className"><ng-content></ng-content></th>
    </ng-container>
    } @else {
    <td [ngClass]="className"><ng-content></ng-content></td>
    }
  `,
  styles: ``
})
export class TableCellComponent {
  @Input() isHeader = false;
  @Input() className = '';
}
