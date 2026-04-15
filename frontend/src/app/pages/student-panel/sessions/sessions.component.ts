import { Component, OnInit, NO_ERRORS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sessions',
  standalone: true,
  imports: [CommonModule],
  template: `<div class="p-6"><h1>My Sessions</h1></div>`,
  schemas: [NO_ERRORS_SCHEMA]
})
export class SessionsComponent implements OnInit {
  constructor() {}

  ngOnInit() {}
}
