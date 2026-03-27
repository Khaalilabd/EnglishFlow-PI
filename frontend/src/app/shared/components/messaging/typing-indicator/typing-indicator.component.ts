import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-typing-indicator',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="flex items-end gap-1 animate-fade-in">
      <div class="bg-[#202c33] rounded-lg rounded-bl-none px-4 py-3 flex items-center gap-3 shadow-md">
        <span class="text-sm text-white">{{ userName }} écrit</span>
        <div class="flex gap-1">
          <span class="w-2 h-2 bg-[#00a884] rounded-full animate-bounce" style="animation-delay: 0ms"></span>
          <span class="w-2 h-2 bg-[#00a884] rounded-full animate-bounce" style="animation-delay: 150ms"></span>
          <span class="w-2 h-2 bg-[#00a884] rounded-full animate-bounce" style="animation-delay: 300ms"></span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    @keyframes bounce {
      0%, 60%, 100% {
        transform: translateY(0);
      }
      30% {
        transform: translateY(-6px);
      }
    }
    
    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: translateY(10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }
    
    .animate-bounce {
      animation: bounce 1.4s infinite ease-in-out;
    }
    
    .animate-fade-in {
      animation: fadeIn 0.3s ease-out;
    }
  `]
})
export class TypingIndicatorComponent {
  @Input() userName: string = 'Quelqu\'un';
}
