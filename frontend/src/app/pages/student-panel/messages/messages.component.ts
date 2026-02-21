import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessagingContainerComponent } from '../../../shared/components/messaging';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [CommonModule, MessagingContainerComponent],
  template: `
    <app-messaging-container></app-messaging-container>
  `
})
export class MessagesComponent implements OnInit {
  ngOnInit(): void {
    console.log('✅ MessagesComponent loaded with MessagingContainer');
  }
}
