import { Component, Input, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TextToSpeechService, TTSVoice, TTSState, TTSBookmark } from '../../services/text-to-speech.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-tts-control',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="tts-control bg-white rounded-lg shadow-lg p-4 border border-gray-200">
      <!-- Header -->
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-2">
          <i class="fas fa-volume-up text-teal-600 text-xl"></i>
          <h3 class="font-bold text-gray-900">Text to Speech</h3>
        </div>
        <button (click)="onClose()" class="text-gray-400 hover:text-gray-600 transition-colors">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <!-- Voice Selection -->
      <div class="mb-4">
        <label class="block text-sm font-semibold text-gray-700 mb-2">
          <i class="fas fa-globe mr-1"></i> Accent
        </label>
        <div class="grid grid-cols-3 gap-2">
          <button *ngFor="let voice of ttsService.voices"
                  (click)="selectVoice(voice)"
                  [class]="voice === ttsService.selectedVoice ? 
                    'bg-teal-600 text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'"
                  class="px-3 py-2 rounded-lg transition-colors text-sm font-medium flex flex-col items-center gap-1">
            <span class="text-2xl">{{ voice.flag }}</span>
            <span class="text-xs">{{ voice.accent }}</span>
          </button>
        </div>
      </div>

      <!-- Speed Control -->
      <div class="mb-4">
        <label class="block text-sm font-semibold text-gray-700 mb-2">
          <i class="fas fa-tachometer-alt mr-1"></i> Speed: {{ ttsService.rate.toFixed(1) }}x
        </label>
        <input type="range" 
               [(ngModel)]="ttsService.rate"
               (change)="onRateChange()"
               min="0.5" 
               max="2.0" 
               step="0.1"
               class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-teal-600">
        <div class="flex justify-between text-xs text-gray-500 mt-1">
          <span>Slow</span>
          <span>Normal</span>
          <span>Fast</span>
        </div>
      </div>

      <!-- Progress Bar with Word Counter -->
      <div *ngIf="state.isPlaying || state.isPaused" class="mb-4">
        <div class="flex justify-between text-xs text-gray-600 mb-1">
          <span>Word {{ state.currentWordIndex + 1 }} / {{ state.totalWords }}</span>
          <span>{{ state.progress.toFixed(0) }}%</span>
        </div>
        <div class="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
          <div class="h-full bg-teal-600 rounded-full transition-all duration-300"
               [style.width.%]="state.progress"></div>
        </div>
      </div>

      <!-- Advanced Playback Controls -->
      <div class="mb-4">
        <div class="flex items-center justify-center gap-2">
          <!-- Skip Backward -->
          <button (click)="skipBackward()"
                  [disabled]="!state.isPlaying && !state.isPaused"
                  class="p-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  title="Rewind 10 seconds">
            <i class="fas fa-backward"></i>
            <span class="text-xs ml-1">10s</span>
          </button>

          <!-- Play/Pause/Resume/Stop -->
          <button *ngIf="!state.isPlaying && !state.isPaused"
                  (click)="play()"
                  [disabled]="!textToRead"
                  class="px-6 py-3 bg-teal-600 text-white rounded-lg hover:bg-teal-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2 font-medium">
            <i class="fas fa-play"></i>
            <span>Play</span>
          </button>

          <button *ngIf="state.isPlaying"
                  (click)="pause()"
                  class="px-6 py-3 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition-colors flex items-center gap-2 font-medium">
            <i class="fas fa-pause"></i>
            <span>Pause</span>
          </button>

          <button *ngIf="state.isPaused"
                  (click)="resume()"
                  class="px-6 py-3 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors flex items-center gap-2 font-medium">
            <i class="fas fa-play"></i>
            <span>Resume</span>
          </button>

          <button *ngIf="state.isPlaying || state.isPaused"
                  (click)="stop()"
                  class="px-4 py-3 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors flex items-center gap-2 font-medium">
            <i class="fas fa-stop"></i>
          </button>

          <!-- Skip Forward -->
          <button (click)="skipForward()"
                  [disabled]="!state.isPlaying && !state.isPaused"
                  class="p-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  title="Forward 10 seconds">
            <span class="text-xs mr-1">10s</span>
            <i class="fas fa-forward"></i>
          </button>
        </div>
      </div>

      <!-- Additional Controls -->
      <div class="flex items-center justify-between gap-2 mb-4">
        <!-- Add Bookmark -->
        <button (click)="addBookmark()"
                [disabled]="!state.isPlaying && !state.isPaused"
                class="flex-1 px-3 py-2 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 transition-colors text-sm font-medium disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2">
          <i class="fas fa-bookmark"></i>
          <span>Bookmark</span>
        </button>

        <!-- Repeat Mode -->
        <button (click)="toggleRepeat()"
                [class]="state.repeatMode ? 'bg-purple-600 text-white' : 'bg-purple-100 text-purple-700 hover:bg-purple-200'"
                class="flex-1 px-3 py-2 rounded-lg transition-colors text-sm font-medium flex items-center justify-center gap-2">
          <i class="fas fa-repeat"></i>
          <span>{{ state.repeatMode ? 'Repeating' : 'Repeat' }}</span>
        </button>
      </div>

      <!-- Repeat Mode Info -->
      <div *ngIf="state.repeatMode" class="mb-4 p-3 bg-purple-50 border border-purple-200 rounded-lg">
        <p class="text-sm text-purple-700 flex items-center gap-2">
          <i class="fas fa-info-circle"></i>
          <span>Repeat mode active - section will loop automatically</span>
        </p>
      </div>

      <!-- Bookmarks List -->
      <div *ngIf="state.bookmarks.length > 0" class="mb-4">
        <label class="block text-sm font-semibold text-gray-700 mb-2">
          <i class="fas fa-bookmark mr-1"></i> Bookmarks ({{ state.bookmarks.length }})
        </label>
        <div class="max-h-32 overflow-y-auto space-y-2">
          <div *ngFor="let bookmark of state.bookmarks" 
               class="flex items-center justify-between p-2 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors">
            <button (click)="jumpToBookmark(bookmark)"
                    class="flex-1 text-left text-sm text-gray-700 hover:text-blue-700 truncate">
              <i class="fas fa-bookmark text-blue-600 mr-2"></i>
              {{ bookmark.text }}
            </button>
            <button (click)="removeBookmark(bookmark.id)"
                    class="ml-2 text-red-500 hover:text-red-700 transition-colors">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Status Message -->
      <div *ngIf="!ttsService.isSupported()" class="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
        <p class="text-sm text-red-700">
          <i class="fas fa-exclamation-triangle mr-1"></i>
          Text-to-Speech is not supported in your browser.
        </p>
      </div>
    </div>
  `,
  styles: [`
    .tts-control {
      animation: slideUp 0.3s ease-out;
      max-height: 90vh;
      overflow-y: auto;
    }

    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    input[type="range"]::-webkit-slider-thumb {
      appearance: none;
      width: 16px;
      height: 16px;
      border-radius: 50%;
      background: #0d9488;
      cursor: pointer;
    }

    input[type="range"]::-moz-range-thumb {
      width: 16px;
      height: 16px;
      border-radius: 50%;
      background: #0d9488;
      cursor: pointer;
      border: none;
    }

    /* Custom scrollbar */
    .tts-control::-webkit-scrollbar {
      width: 6px;
    }

    .tts-control::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 10px;
    }

    .tts-control::-webkit-scrollbar-thumb {
      background: #0d9488;
      border-radius: 10px;
    }

    .tts-control::-webkit-scrollbar-thumb:hover {
      background: #0f766e;
    }
  `]
})
export class TtsControlComponent implements OnInit, OnDestroy {
  @Input() textToRead: string = '';
  @Input() onClose: () => void = () => {};

  state: TTSState = {
    isPlaying: false,
    isPaused: false,
    currentText: '',
    progress: 0,
    currentWordIndex: 0,
    totalWords: 0,
    bookmarks: [],
    repeatMode: false,
    repeatStart: 0,
    repeatEnd: 0
  };

  private subscription?: Subscription;

  constructor(
    public ttsService: TextToSpeechService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.subscription = this.ttsService.state$.subscribe(state => {
      console.log('TTS State updated:', state);
      this.state = state;
      // Force Angular to detect changes
      this.cdr.detectChanges();
    });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    this.ttsService.stop();
  }

  selectVoice(voice: TTSVoice): void {
    this.ttsService.setVoice(voice);
  }

  onRateChange(): void {
    this.ttsService.setRate(this.ttsService.rate);
  }

  play(): void {
    if (this.textToRead) {
      this.ttsService.speak(this.textToRead);
    }
  }

  pause(): void {
    this.ttsService.pause();
  }

  resume(): void {
    this.ttsService.resume();
  }

  stop(): void {
    this.ttsService.stop();
  }

  skipForward(): void {
    this.ttsService.skipForward();
  }

  skipBackward(): void {
    this.ttsService.skipBackward();
  }

  addBookmark(): void {
    const bookmark = this.ttsService.addBookmark();
    // Show success toast
    console.log('Bookmark added:', bookmark);
  }

  jumpToBookmark(bookmark: TTSBookmark): void {
    this.ttsService.jumpToBookmark(bookmark);
  }

  removeBookmark(bookmarkId: string): void {
    this.ttsService.removeBookmark(bookmarkId);
  }

  toggleRepeat(): void {
    this.ttsService.toggleRepeatMode();
    
    // Show notification
    if (this.state.repeatMode) {
      console.log('Repeat mode enabled - will repeat current section');
    } else {
      console.log('Repeat mode disabled');
    }
  }
}
