import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  standalone: true,
  selector: 'app-table',
  imports:[CommonModule],
  template: `<table [ngClass]="'min-w-full ' + className"><ng-content></ng-content></table>`,
})
export class TableComponent {
  @Input() className = '';
}