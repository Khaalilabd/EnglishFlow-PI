import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface ConfirmationConfig {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  type?: 'danger' | 'warning' | 'info';
  requireTextConfirmation?: boolean;
  confirmationText?: string;
  icon?: string;
  details?: string[];
}

@Component({
  selector: 'app-confirmation-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.scss']
})
export class ConfirmationDialogComponent {
  @Input() config: ConfirmationConfig = {
    title: 'Confirm Action',
    message: 'Are you sure?',
    confirmText: 'Confirm',
    cancelText: 'Cancel',
    type: 'warning'
  };
  
  @Output() confirmed = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  userInput = '';
  isVisible = false;

  show(): void {
    this.isVisible = true;
    this.userInput = '';
  }

  hide(): void {
    this.isVisible = false;
    this.userInput = '';
  }

  onConfirm(): void {
    if (this.config.requireTextConfirmation) {
      if (this.userInput === this.config.confirmationText) {
        this.confirmed.emit();
        this.hide();
      }
    } else {
      this.confirmed.emit();
      this.hide();
    }
  }

  onCancel(): void {
    this.cancelled.emit();
    this.hide();
  }

  get canConfirm(): boolean {
    if (this.config.requireTextConfirmation) {
      return this.userInput === this.config.confirmationText;
    }
    return true;
  }

  get iconSvg(): string {
    switch (this.config.type) {
      case 'danger':
        return 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z';
      case 'warning':
        return 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z';
      case 'info':
        return 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z';
      default:
        return 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z';
    }
  }
}
