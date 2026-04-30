import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface TTSVoice {
  name: string;
  lang: string;
  accent: string;
  flag: string;
}

export interface TTSBookmark {
  id: string;
  position: number;
  text: string;
  timestamp: Date;
}

export interface TTSState {
  isPlaying: boolean;
  isPaused: boolean;
  currentText: string;
  progress: number;
  currentWordIndex: number;
  totalWords: number;
  bookmarks: TTSBookmark[];
  repeatMode: boolean;
  repeatStart: number;
  repeatEnd: number;
}

@Injectable({
  providedIn: 'root'
})
export class TextToSpeechService {
  private synth: SpeechSynthesis;
  private utterance: SpeechSynthesisUtterance | null = null;
  private availableVoices: SpeechSynthesisVoice[] = [];
  private words: string[] = [];
  private currentWordIndex = 0;
  private highlightCallback?: (wordIndex: number) => void;
  private wordTrackingInterval: any = null;
  private startTime = 0;
  private pausedTime = 0;
  private totalPausedDuration = 0;
  
  private stateSubject = new BehaviorSubject<TTSState>({
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
  });
  
  public state$: Observable<TTSState> = this.stateSubject.asObservable();
  
  // Predefined voices with accents
  public voices: TTSVoice[] = [
    { name: 'US English', lang: 'en-US', accent: 'American', flag: '🇺🇸' },
    { name: 'UK English', lang: 'en-GB', accent: 'British', flag: '🇬🇧' },
    { name: 'Australian English', lang: 'en-AU', accent: 'Australian', flag: '🇦🇺' }
  ];
  
  public selectedVoice: TTSVoice = this.voices[0];
  public rate: number = 1.0; // Speed: 0.5 to 2.0
  public pitch: number = 1.0; // Pitch: 0 to 2

  constructor() {
    this.synth = window.speechSynthesis;
    this.loadVoices();
    
    // Load voices when they become available
    if (speechSynthesis.onvoiceschanged !== undefined) {
      speechSynthesis.onvoiceschanged = () => this.loadVoices();
    }
  }

  private loadVoices(): void {
    this.availableVoices = this.synth.getVoices();
    console.log('Available voices:', this.availableVoices);
  }

  private findBestVoice(lang: string): SpeechSynthesisVoice | null {
    // Try to find exact match
    let voice = this.availableVoices.find(v => v.lang === lang);
    
    // Try to find language match (e.g., en-US matches en)
    if (!voice) {
      const langPrefix = lang.split('-')[0];
      voice = this.availableVoices.find(v => v.lang.startsWith(langPrefix));
    }
    
    // Fallback to first English voice
    if (!voice) {
      voice = this.availableVoices.find(v => v.lang.startsWith('en'));
    }
    
    return voice || null;
  }

  setHighlightCallback(callback: (wordIndex: number) => void): void {
    this.highlightCallback = callback;
  }

  speak(text: string): void {
    // Stop any ongoing speech
    this.stop();
    
    // Clean text (remove HTML tags if any)
    const cleanText = text.replaceAll(/<[^>]*>/g, '').trim();
    
    if (!cleanText) {
      console.warn('No text to speak');
      return;
    }

    // Split text into words for highlighting
    this.words = cleanText.split(/\s+/);
    this.currentWordIndex = 0;

    this.speakFromWord(0);
  }

  private speakFromWord(startIndex: number): void {
    if (startIndex >= this.words.length) {
      this.updateState({ isPlaying: false, isPaused: false, currentText: '', progress: 100 });
      return;
    }

    this.currentWordIndex = startIndex;
    const textToSpeak = this.words.slice(startIndex).join(' ');

    this.utterance = new SpeechSynthesisUtterance(textToSpeak);
    
    // Find and set the best voice
    const voice = this.findBestVoice(this.selectedVoice.lang);
    if (voice) {
      this.utterance.voice = voice;
    }
    
    this.utterance.lang = this.selectedVoice.lang;
    this.utterance.rate = this.rate;
    this.utterance.pitch = this.pitch;
    
    // Calculate word duration based on rate - used as fallback
    const baseWordsPerMinute = 120;
    const adjustedWordsPerMinute = baseWordsPerMinute * this.rate;
    const msPerWord = (60 * 1000) / adjustedWordsPerMinute;
    
    let lastBoundaryTime = 0;
    let useBoundary = false; // Will be set to true if onboundary works
    
    this.totalPausedDuration = 0;
    
    // Event listeners
    this.utterance.onstart = () => {
      console.log('TTS Started - setting up word tracking');
      this.startTime = Date.now();
      lastBoundaryTime = this.startTime;
      
      this.updateState({ 
        isPlaying: true, 
        isPaused: false, 
        currentText: this.words.join(' '), 
        progress: 0,
        currentWordIndex: startIndex,
        totalWords: this.words.length
      });
      
      // Highlight first word immediately
      if (this.highlightCallback) {
        this.highlightCallback(startIndex);
      }
      
      // Fallback tracking - only used if onboundary doesn't work
      this.wordTrackingInterval = setInterval(() => {
        // Only use fallback if onboundary hasn't fired recently (>500ms)
        const timeSinceLastBoundary = Date.now() - lastBoundaryTime;
        
        if (!useBoundary || timeSinceLastBoundary > 500) {
          if (this.synth.speaking && !this.synth.paused) {
            const elapsed = Date.now() - this.startTime - this.totalPausedDuration;
            const expectedWordIndex = startIndex + Math.floor(elapsed / msPerWord);
            
            if (expectedWordIndex < this.words.length && expectedWordIndex !== this.currentWordIndex) {
              this.currentWordIndex = expectedWordIndex;
              
              const progress = (expectedWordIndex / this.words.length) * 100;
              this.updateState({ 
                progress,
                currentWordIndex: expectedWordIndex
              });
              
              if (this.highlightCallback) {
                console.log('[Fallback] Highlighting word:', expectedWordIndex);
                this.highlightCallback(expectedWordIndex);
              }
            }
          }
        }
      }, 150);
    };
    
    this.utterance.onend = () => {
      console.log('TTS Ended');
      if (this.wordTrackingInterval) {
        clearInterval(this.wordTrackingInterval);
        this.wordTrackingInterval = null;
      }
      
      const state = this.stateSubject.value;
      if (state.repeatMode && state.repeatStart !== undefined && state.repeatEnd !== undefined) {
        this.speakFromWord(state.repeatStart);
      } else {
        this.updateState({ 
          isPlaying: false, 
          isPaused: false, 
          currentText: '', 
          progress: 100,
          currentWordIndex: this.words.length
        });
      }
    };
    
    this.utterance.onerror = (event) => {
      console.error('Speech synthesis error:', event);
      if (this.wordTrackingInterval) {
        clearInterval(this.wordTrackingInterval);
        this.wordTrackingInterval = null;
      }
      this.updateState({ isPlaying: false, isPaused: false, currentText: '', progress: 0 });
    };
    
    // Use onboundary for precise tracking when available
    this.utterance.onboundary = (event) => {
      if (event.name === 'word') {
        useBoundary = true; // Mark that onboundary is working
        lastBoundaryTime = Date.now();
        
        // Calculate word index based on character position
        const spokenText = textToSpeak.substring(0, event.charIndex);
        const wordsSpoken = spokenText.split(/\s+/).filter(w => w.length > 0).length;
        const absoluteWordIndex = startIndex + wordsSpoken;
        
        if (absoluteWordIndex >= startIndex && absoluteWordIndex < this.words.length) {
          this.currentWordIndex = absoluteWordIndex;
          
          const progress = (absoluteWordIndex / this.words.length) * 100;
          this.updateState({ 
            progress,
            currentWordIndex: absoluteWordIndex
          });
          
          if (this.highlightCallback) {
            console.log('[Boundary] Highlighting word:', absoluteWordIndex);
            this.highlightCallback(absoluteWordIndex);
          }
        }
      }
    };
    
    console.log('Starting speech synthesis...');
    this.synth.speak(this.utterance);
  }

  pause(): void {
    if (this.synth.speaking && !this.synth.paused) {
      this.pausedTime = Date.now();
      this.synth.pause();
      this.updateState({ isPaused: true, isPlaying: false });
    }
  }

  resume(): void {
    if (this.synth.paused) {
      // Add the paused duration to total
      if (this.pausedTime > 0) {
        this.totalPausedDuration += Date.now() - this.pausedTime;
        this.pausedTime = 0;
      }
      this.synth.resume();
      this.updateState({ isPaused: false, isPlaying: true });
    }
  }

  stop(): void {
    if (this.synth.speaking || this.synth.paused) {
      this.synth.cancel();
      this.currentWordIndex = 0;
      
      // Clear interval
      if (this.wordTrackingInterval) {
        clearInterval(this.wordTrackingInterval);
        this.wordTrackingInterval = null;
      }
      
      this.updateState({ 
        isPlaying: false, 
        isPaused: false, 
        currentText: '', 
        progress: 0,
        currentWordIndex: 0,
        totalWords: 0
      });
    }
  }

  // Skip forward by 10 seconds (approximately 25 words at normal speed)
  skipForward(): void {
    const wordsToSkip = Math.ceil(25 * this.rate);
    const newIndex = Math.min(this.currentWordIndex + wordsToSkip, this.words.length - 1);
    
    const wasPlaying = this.synth.speaking && !this.synth.paused;
    
    this.synth.cancel();
    
    setTimeout(() => {
      if (wasPlaying) {
        this.speakFromWord(newIndex);
      } else {
        this.currentWordIndex = newIndex;
        this.updateState({ 
          currentWordIndex: newIndex,
          progress: (newIndex / this.words.length) * 100
        });
      }
    }, 100);
  }

  // Skip backward by 10 seconds
  skipBackward(): void {
    const wordsToSkip = Math.ceil(25 * this.rate);
    const newIndex = Math.max(this.currentWordIndex - wordsToSkip, 0);
    
    const wasPlaying = this.synth.speaking && !this.synth.paused;
    
    this.synth.cancel();
    
    setTimeout(() => {
      if (wasPlaying) {
        this.speakFromWord(newIndex);
      } else {
        this.currentWordIndex = newIndex;
        this.updateState({ 
          currentWordIndex: newIndex,
          progress: (newIndex / this.words.length) * 100
        });
      }
    }, 100);
  }

  // Add bookmark at current position
  addBookmark(): TTSBookmark {
    const bookmark: TTSBookmark = {
      id: Date.now().toString(),
      position: this.currentWordIndex,
      text: this.words.slice(this.currentWordIndex, this.currentWordIndex + 5).join(' ') + '...',
      timestamp: new Date()
    };
    
    const currentState = this.stateSubject.value;
    const bookmarks = [...currentState.bookmarks, bookmark];
    this.updateState({ bookmarks });
    
    return bookmark;
  }

  // Jump to bookmark
  jumpToBookmark(bookmark: TTSBookmark): void {
    if (this.synth.speaking || this.synth.paused) {
      this.synth.cancel();
      setTimeout(() => {
        this.speakFromWord(bookmark.position);
      }, 100);
    } else {
      this.speakFromWord(bookmark.position);
    }
  }

  // Remove bookmark
  removeBookmark(bookmarkId: string): void {
    const currentState = this.stateSubject.value;
    const bookmarks = currentState.bookmarks.filter(b => b.id !== bookmarkId);
    this.updateState({ bookmarks });
  }

  // Set repeat section
  setRepeatSection(start: number, end: number): void {
    this.updateState({ 
      repeatMode: true,
      repeatStart: start,
      repeatEnd: end
    });
  }

  // Clear repeat section
  clearRepeatSection(): void {
    this.updateState({ 
      repeatMode: false,
      repeatStart: 0,
      repeatEnd: 0
    });
  }

  // Toggle repeat mode
  toggleRepeatMode(): void {
    const currentState = this.stateSubject.value;
    if (currentState.repeatMode) {
      this.clearRepeatSection();
    } else {
      // Repeat from the very beginning of the text
      this.setRepeatSection(0, this.words.length - 1);
      console.log(`Repeat mode enabled: will restart from beginning`);

      // If currently paused, restart immediately from the beginning
      if (currentState.isPaused) {
        this.synth.cancel();
        if (this.wordTrackingInterval) {
          clearInterval(this.wordTrackingInterval);
          this.wordTrackingInterval = null;
        }
        setTimeout(() => this.speakFromWord(0), 100);
      }
    }
  }

  setVoice(voice: TTSVoice): void {
    this.selectedVoice = voice;
  }

  setRate(rate: number): void {
    this.rate = Math.max(0.5, Math.min(2.0, rate));
    
    // If currently speaking, restart with new rate
    if (this.utterance && this.synth.speaking) {
      const currentIndex = this.currentWordIndex;
      this.stop();
      setTimeout(() => this.speakFromWord(currentIndex), 100);
    }
  }

  setPitch(pitch: number): void {
    this.pitch = Math.max(0, Math.min(2, pitch));
  }

  private updateState(partial: Partial<TTSState>): void {
    const currentState = this.stateSubject.value;
    this.stateSubject.next({ ...currentState, ...partial });
  }

  isSupported(): boolean {
    return 'speechSynthesis' in window;
  }

  getCurrentWords(): string[] {
    return this.words;
  }
}
