import { Component, OnInit, NO_ERRORS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-student-settings',
  standalone: true,
  imports: [CommonModule],
  template: `<div class="p-6"><h1>Student Settings</h1></div>`,
  schemas: [NO_ERRORS_SCHEMA]
})
export class StudentSettingsComponent implements OnInit {
  constructor() {}

  ngOnInit() {}
}
