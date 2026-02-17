import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService, Toast } from '../../../core/services/toast.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed bottom-6 right-6 z-[9999] space-y-3 max-w-sm">
      <div
        *ngFor="let toast of toasts"
        [ngClass]="{
          'bg-white border-l-4 border-green-500': toast.type === 'success',
          'bg-white border-l-4 border-red-500': toast.type === 'error',
          'bg-white border-l-4 border-[#F6BD60]': toast.type === 'warning',
          'bg-white border-l-4 border-[#2D5757]': toast.type === 'info'
        }"
        class="px-5 py-4 rounded-lg shadow-2xl flex items-start gap-3 animate-slide-in">
        <div class="flex-shrink-0 mt-0.5">
          <i *ngIf="toast.type === 'success'" class="fas fa-check-circle text-xl text-green-500"></i>
          <i *ngIf="toast.type === 'error'" class="fas fa-times-circle text-xl text-red-500"></i>
          <i *ngIf="toast.type === 'warning'" class="fas fa-exclamation-triangle text-xl text-[#F6BD60]"></i>
          <i *ngIf="toast.type === 'info'" class="fas fa-info-circle text-xl text-[#2D5757]"></i>
        </div>
        <div class="flex-1 min-w-0">
          <p class="font-semibold text-sm text-gray-900 mb-0.5" *ngIf="toast.type === 'success'">Success</p>
          <p class="font-semibold text-sm text-gray-900 mb-0.5" *ngIf="toast.type === 'error'">Error</p>
          <p class="font-semibold text-sm text-gray-900 mb-0.5" *ngIf="toast.type === 'warning'">Warning</p>
          <p class="font-semibold text-sm text-gray-900 mb-0.5" *ngIf="toast.type === 'info'">Info</p>
          <p class="text-sm text-gray-600 leading-relaxed break-words">{{ toast.message }}</p>
        </div>
        <button
          (click)="removeToast(toast.id)"
          class="flex-shrink-0 hover:bg-gray-100 rounded-lg p-1.5 transition-all text-gray-400 hover:text-gray-600">
          <i class="fas fa-times text-sm"></i>
        </button>
      </div>
    </div>
  `,
  styles: [`
    @keyframes slideIn {
      from {
        transform: translateX(120%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    @keyframes slideOut {
      from {
        transform: translateX(0);
        opacity: 1;
      }
      to {
        transform: translateX(120%);
        opacity: 0;
      }
    }

    .animate-slide-in {
      animation: slideIn 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
    }

    .animate-slide-out {
      animation: slideOut 0.3s ease-in;
    }
  `]
})
export class ToastComponent implements OnInit, OnDestroy {
  toasts: Toast[] = [];
  private subscription?: Subscription;

  constructor(private toastService: ToastService) {}

  ngOnInit() {
    this.subscription = this.toastService.toast$.subscribe(toast => {
      this.toasts.push(toast);
      
      if (toast.duration) {
        setTimeout(() => {
          this.removeToast(toast.id);
        }, toast.duration);
      }
    });
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  removeToast(id: number) {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }
}
