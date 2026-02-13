import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-logo',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex items-center justify-center" [class]="containerClass">
      <!-- Using the actual logo image with fallback -->
      <div class="relative flex items-center justify-center">
        <img 
          [src]="logoSrc" 
          [alt]="'Jungle in English Logo'" 
          [style.max-width.px]="width"
          [style.max-height.px]="height"
          class="object-contain w-auto h-auto"
          (error)="onImageError()"
          [class.hidden]="imageError"
        />
        
        <!-- Fallback: Styled text version if image fails to load -->
        <div *ngIf="imageError" class="flex flex-col items-center justify-center p-4 bg-primary rounded-2xl shadow-lg" [style.width.px]="width" [style.height.px]="height">
          <div class="text-center">
            <h1 class="text-2xl md:text-3xl font-bold text-secondary mb-1 leading-tight">
              Jungle in<br/>English
            </h1>
            <p class="text-xs md:text-sm text-secondary/80 mt-1">جنقل بال ANGLAIS</p>
          </div>
        </div>
      </div>
    </div>
  `
})
export class LogoComponent {
  @Input() width: number = 320;
  @Input() height: number = 130;
  @Input() containerClass: string = '';
  
  imageError: boolean = false;
  
  // Path to the logo - update this to match your actual logo location
  logoSrc: string = '/images/logo/jungle-in-english.png';
  
  onImageError(): void {
    this.imageError = true;
  }
}
