import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService, Toast } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed top-4 right-4 z-50 space-y-2">
      <div
        *ngFor="let toast of toasts$ | async"
        [class]="getToastClass(toast.type)"
        class="min-w-[300px] max-w-md rounded-lg shadow-lg p-4 flex items-start gap-3 animate-slide-in"
      >
        <div class="flex-shrink-0">
          <svg *ngIf="toast.type === 'success'" class="w-6 h-6 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <svg *ngIf="toast.type === 'error'" class="w-6 h-6 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <svg *ngIf="toast.type === 'warning'" class="w-6 h-6 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
          </svg>
          <svg *ngIf="toast.type === 'info'" class="w-6 h-6 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
        </div>
        <div class="flex-1">
          <p class="text-sm font-medium text-white">{{ toast.message }}</p>
        </div>
        <button
          (click)="toastService.remove(toast.id)"
          class="flex-shrink-0 text-white hover:text-gray-200 transition-colors"
        >
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </button>
      </div>
    </div>
  `,
  styles: [`
    @keyframes slide-in {
      from {
        transform: translateX(100%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    .animate-slide-in {
      animation: slide-in 0.3s ease-out;
    }
  `]
})
export class ToastComponent {
  toasts$;

  constructor(public toastService: ToastService) {
    this.toasts$ = this.toastService.toasts$;
  }

  getToastClass(type: Toast['type']): string {
    const baseClasses = 'backdrop-blur-sm';
    switch (type) {
      case 'success':
        return `${baseClasses} bg-green-500/90`;
      case 'error':
        return `${baseClasses} bg-red-500/90`;
      case 'warning':
        return `${baseClasses} bg-yellow-500/90`;
      case 'info':
        return `${baseClasses} bg-blue-500/90`;
      default:
        return `${baseClasses} bg-gray-500/90`;
    }
  }
}
