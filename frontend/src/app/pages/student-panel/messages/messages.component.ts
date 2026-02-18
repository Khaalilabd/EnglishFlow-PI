import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessagingContainerComponent } from '../../../shared/components/messaging/messaging-container/messaging-container.component';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [CommonModule, MessagingContainerComponent],
  template: `
    <div class="min-h-screen bg-gray-50 p-6">
      <div class="max-w-7xl mx-auto">
        <!-- En-tête -->
        <div class="mb-6">
          <h1 class="text-3xl font-bold text-gray-900">💬 Messages</h1>
          <p class="text-gray-600 mt-2">Communiquez avec vos contacts</p>
        </div>

        <!-- Conteneur de messagerie -->
        <app-messaging-container></app-messaging-container>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      width: 100%;
      min-height: 100vh;
    }
  `]
})
export class MessagesComponent implements OnInit {
  ngOnInit(): void {
    console.log('✅ MessagesComponent loaded with MessagingContainer');
  }
}
