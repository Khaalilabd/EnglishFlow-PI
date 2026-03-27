import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-password-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './password-modal.component.html',
  styleUrls: ['./password-modal.component.scss']
})
export class PasswordModalComponent {
  @Input() password: string = '';
  @Input() teacherName: string = '';
  @Input() teacherEmail: string = '';
  @Output() closeModal = new EventEmitter<void>();

  showPassword = false;
  copied = false;

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  copyPassword(): void {
    navigator.clipboard.writeText(this.password).then(() => {
      this.copied = true;
      setTimeout(() => {
        this.copied = false;
      }, 2000);
    });
  }

  downloadCredentials(): void {
    const content = `Tutor Account Credentials
    
Name: ${this.teacherName}
Email: ${this.teacherEmail}
Temporary Password: ${this.password}

Important: Please change this password after your first login.

Platform: Jungle in English
Login URL: http://localhost:4200/login

Generated on: ${new Date().toLocaleString()}
`;

    const blob = new Blob([content], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `tutor-credentials-${this.teacherEmail}.txt`;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  close(): void {
    this.closeModal.emit();
  }
}
