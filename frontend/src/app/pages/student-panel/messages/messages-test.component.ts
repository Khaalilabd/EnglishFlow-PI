import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-messages-test',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-8 bg-white">
      <h1 class="text-3xl font-bold text-green-600 mb-4">✅ TEST RÉUSSI !</h1>
      <p class="text-lg text-gray-700">Si vous voyez ce message, le composant se charge correctement.</p>
      <div class="mt-4 p-4 bg-blue-50 rounded">
        <p class="text-blue-800">Le problème est donc dans le composant MessagingContainer.</p>
      </div>
    </div>
  `
})
export class MessagesTestComponent {}
