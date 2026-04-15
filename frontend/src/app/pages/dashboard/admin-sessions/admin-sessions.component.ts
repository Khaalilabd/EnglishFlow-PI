import { Component, OnInit, NO_ERRORS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-sessions',
  standalone: true,
  imports: [CommonModule],
  template: `<div class="p-6"><h1>Session Management</h1></div>`,
  schemas: [NO_ERRORS_SCHEMA]
})
export class AdminSessionsComponent implements OnInit {
  constructor() {}

  ngOnInit() {}
}
