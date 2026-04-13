import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DictionaryService, DictionaryEntry, EnrichedDictionaryEntry } from '../../services/dictionary.service';
import { VocabularyService, SaveVocabularyRequest } from '../../services/vocabulary.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-dictionary-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="isOpen" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50" (click)="close()">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-3xl max-h-[85vh] overflow-hidden flex flex-col" (click)="$event.stopPropagation()">
        <!-- Header -->
        <div class="p-6 border-b border-gray-200 flex items-center justify-between bg-gradient-to-r from-blue-50 to-indigo-50">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center">
              <i class="fas fa-book text-white"></i>
            </div>
            <div>
              <h2 class="text-xl font-bold text-gray-900">Dictionary</h2>
              <p class="text-sm text-gray-600" *ngIf="word">Looking up: {{ word }}</p>
            </div>
          </div>
          <button (click)="close()" class="w-8 h-8 flex items-center justify-center rounded-lg hover:bg-gray-100 transition-colors">
            <i class="fas fa-times text-gray-500"></i>
          </button>
        </div>

        <!-- Content -->
        <div class="flex-1 overflow-y-auto p-6">
          <!-- Loading -->
          <div *ngIf="loading" class="flex items-center justify-center py-12">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          </div>

          <!-- Error -->
          <div *ngIf="error && !loading" class="text-center py-12">
            <i class="fas fa-exclamation-circle text-red-500 text-4xl mb-4"></i>
            <p class="text-gray-700 font-medium mb-2">{{ error }}</p>
            <p class="text-gray-500 text-sm">Word searched: <strong>{{ word }}</strong></p>
            <p class="text-gray-500 text-sm mt-2">Try searching for another word or the base form (e.g., "run" instead of "running")</p>
          </div>

          <!-- Results -->
          <div *ngIf="enrichedData && !loading" class="space-y-4">

            
            <!-- Context Section -->
            <div *ngIf="enrichedData.context" class="bg-yellow-50 border-l-4 border-yellow-400 p-4 rounded-r-lg">
              <p class="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
                <i class="fas fa-quote-left text-yellow-600"></i> 
                <span>Context from forum:</span>
              </p>
              <p class="text-gray-800 italic leading-relaxed">"{{ enrichedData.context }}"</p>
            </div>

            <!-- CEFR Level & Word Type -->
            <div class="flex flex-wrap gap-3">
              <div *ngIf="enrichedData.cefrLevel" class="flex items-center gap-2 px-4 py-2 bg-purple-100 rounded-lg border border-purple-200">
                <i class="fas fa-graduation-cap text-purple-600"></i>
                <div>
                  <p class="text-xs text-purple-600 font-medium">CEFR Level</p>
                  <p class="text-sm font-bold text-purple-700">{{ enrichedData.cefrLevel }}</p>
                </div>
              </div>
              <div *ngIf="enrichedData.wordType" class="flex items-center gap-2 px-4 py-2 bg-blue-100 rounded-lg border border-blue-200">
                <i class="fas fa-tag text-blue-600"></i>
                <div>
                  <p class="text-xs text-blue-600 font-medium">Type</p>
                  <p class="text-sm font-bold text-blue-700">{{ enrichedData.wordType }}</p>
                </div>
              </div>
            </div>

            <!-- Similar Words (Homophones) -->
            <div *ngIf="enrichedData.similarWords && enrichedData.similarWords.length > 0" class="p-4 bg-green-50 rounded-lg border border-green-200">
              <p class="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
                <i class="fas fa-link text-green-600"></i> 
                <span>Similar Sounding Words (Homophones):</span>
              </p>
              <div class="flex flex-wrap gap-2">
                <span *ngFor="let similar of enrichedData.similarWords" 
                      class="px-3 py-1.5 bg-green-200 text-green-800 rounded-full text-sm font-medium hover:bg-green-300 transition-colors cursor-pointer">
                  {{ similar }}
                </span>
              </div>
              <p class="text-xs text-gray-600 mt-2">
                <i class="fas fa-info-circle"></i> These words sound the same but have different meanings
              </p>
            </div>

            <!-- Common Confusions -->
            <div *ngIf="enrichedData.commonConfusions && enrichedData.commonConfusions.length > 0" class="p-4 bg-red-50 rounded-lg border border-red-200">
              <p class="text-sm font-semibold text-gray-700 mb-2 flex items-center gap-2">
                <i class="fas fa-exclamation-triangle text-red-600"></i> 
                <span>Often Confused With:</span>
              </p>
              <div class="flex flex-wrap gap-2">
                <span *ngFor="let confusion of enrichedData.commonConfusions" 
                      class="px-3 py-1.5 bg-red-200 text-red-800 rounded-full text-sm font-medium hover:bg-red-300 transition-colors cursor-pointer">
                      {{ confusion }}
                </span>
              </div>
              <p class="text-xs text-gray-600 mt-2">
                <i class="fas fa-lightbulb"></i> Be careful not to mix these words up!
              </p>
            </div>

            <!-- Image (if available) -->
            <div *ngIf="enrichedData.imageUrl" class="rounded-lg overflow-hidden shadow-md">
              <img [src]="enrichedData.imageUrl" 
                   [alt]="word" 
                   class="w-full h-48 object-cover">
            </div>

            <!-- Word and Phonetic (shown once) -->
            <div class="mb-6" *ngIf="enrichedData.basicData && enrichedData.basicData.length > 0">
              <h3 class="text-3xl font-bold text-gray-900">{{ enrichedData.basicData[0].word }}</h3>
              <div class="flex items-center gap-3 mt-2">
                <span *ngIf="enrichedData.basicData[0].phonetic" class="text-gray-600 italic text-lg">
                  {{ enrichedData.basicData[0].phonetic }}
                </span>
                <button *ngIf="getAudioUrl(enrichedData.basicData[0])" 
                        (click)="playAudio(getAudioUrl(enrichedData.basicData[0])!)"
                        class="flex items-center gap-2 px-3 py-1.5 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 transition-colors">
                  <i class="fas fa-volume-up"></i>
                  <span class="text-sm font-medium">Play</span>
                </button>
              </div>
            </div>

            <!-- All Meanings from all entries -->
            <div *ngFor="let entry of enrichedData.basicData; let i = index">
              <div *ngFor="let meaning of entry.meanings" class="mb-6">
                <div class="flex items-center gap-2 mb-3">
                  <span class="px-3 py-1 bg-indigo-100 text-indigo-700 rounded-full text-sm font-semibold">
                    {{ meaning.partOfSpeech }}
                  </span>
                </div>

                <!-- Definitions -->
                <ol class="space-y-3 ml-4">
                  <li *ngFor="let def of meaning.definitions; let j = index" class="relative pl-6">
                    <span class="absolute left-0 top-0 text-gray-400 font-medium">{{ j + 1 }}.</span>
                    <p class="text-gray-800 leading-relaxed">{{ def.definition }}</p>
                    <p *ngIf="def.example" class="text-gray-600 italic mt-1 text-sm bg-gray-50 p-2 rounded">
                      💬 "{{ def.example }}"
                    </p>
                  </li>
                </ol>

                <!-- Synonyms -->
                <div *ngIf="meaning.synonyms && meaning.synonyms.length > 0" class="mt-3">
                  <p class="text-sm font-semibold text-gray-700 mb-1">Synonyms:</p>
                  <div class="flex flex-wrap gap-2">
                    <span *ngFor="let syn of meaning.synonyms.slice(0, 5)" 
                          class="px-2 py-1 bg-green-50 text-green-700 rounded text-sm border border-green-200">
                      {{ syn }}
                    </span>
                  </div>
                </div>

                <!-- Antonyms -->
                <div *ngIf="meaning.antonyms && meaning.antonyms.length > 0" class="mt-3">
                  <p class="text-sm font-semibold text-gray-700 mb-1">Antonyms:</p>
                  <div class="flex flex-wrap gap-2">
                    <span *ngFor="let ant of meaning.antonyms.slice(0, 5)" 
                          class="px-2 py-1 bg-red-50 text-red-700 rounded text-sm border border-red-200">
                      {{ ant }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="p-4 border-t border-gray-200 bg-gray-50 flex justify-between items-center">
          <p class="text-xs text-gray-500">Powered by Free Dictionary API</p>
          <div class="flex gap-2">
            <button *ngIf="enrichedData && !isSaved" 
                    (click)="saveToVocabulary()" 
                    [disabled]="saving"
                    class="px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-600 text-white rounded-lg hover:from-green-600 hover:to-emerald-700 transition-all duration-300 flex items-center gap-2 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed">
              <i class="fas" [ngClass]="saving ? 'fa-spinner fa-spin' : 'fa-bookmark'"></i>
              <span>{{ saving ? 'Saving...' : 'Save to Vocabulary' }}</span>
            </button>
            
            <button *ngIf="isSaved" 
                    disabled
                    class="px-4 py-2 bg-gray-400 text-white rounded-lg flex items-center gap-2 cursor-not-allowed">
              <i class="fas fa-check-circle"></i>
              <span>Already Saved</span>
            </button>
            
            <button (click)="close()" 
                    class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
              Close
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class DictionaryModalComponent {
  @Input() isOpen = false;
  @Input() word: string = '';
  @Input() context: string = '';
  @Input() topicId: number | null = null;
  @Output() closeModal = new EventEmitter<void>();

  enrichedData: EnrichedDictionaryEntry | null = null;
  loading = false;
  error: string | null = null;
  private audio: HTMLAudioElement | null = null;
  saving = false;
  isSaved = false;

  constructor(
    private dictionaryService: DictionaryService,
    private vocabularyService: VocabularyService
  ) {}

  ngOnChanges(changes: any) {
    if (changes.isOpen && this.isOpen && this.word) {
      this.lookup();
      this.checkIfSaved();
    } else if (changes.word && this.word && this.isOpen) {
      this.lookup();
      this.checkIfSaved();
    } else if (changes.context && this.isOpen && this.word) {
      this.lookup();
    }
  }

  lookup() {
    this.loading = true;
    this.error = null;
    this.enrichedData = null;

    this.dictionaryService.lookupWordEnriched(this.word, this.context).subscribe({
      next: (data) => {
        this.enrichedData = data;
        this.loading = false;
        console.log('Enriched data received:', data);
      },
      error: (err) => {
        this.error = 'Word not found. Please try another word.';
        this.loading = false;
        console.error('Dictionary lookup error:', err);
      }
    });
  }

  checkIfSaved() {
    this.vocabularyService.isWordSaved(this.word).subscribe({
      next: (saved) => {
        this.isSaved = saved;
      },
      error: (err) => {
        console.error('Error checking if word is saved:', err);
      }
    });
  }

  saveToVocabulary() {
    if (!this.enrichedData || !this.enrichedData.basicData || this.enrichedData.basicData.length === 0) return;

    this.saving = true;
    const entry = this.enrichedData.basicData[0];
    const firstMeaning = entry.meanings[0];
    const firstDefinition = firstMeaning.definitions[0];

    const request: SaveVocabularyRequest = {
      word: entry.word,
      definition: firstDefinition.definition,
      phonetic: entry.phonetic || undefined,
      partOfSpeech: firstMeaning.partOfSpeech || undefined,
      example: firstDefinition.example || undefined,
      synonyms: firstMeaning.synonyms && firstMeaning.synonyms.length > 0 
        ? firstMeaning.synonyms.slice(0, 5).join(', ') 
        : undefined,
      antonyms: firstMeaning.antonyms && firstMeaning.antonyms.length > 0 
        ? firstMeaning.antonyms.slice(0, 5).join(', ') 
        : undefined,
      audioUrl: this.getAudioUrl(entry) || undefined,
      sourceTopicId: this.topicId || undefined
    };

    this.vocabularyService.saveWord(request).subscribe({
      next: (saved) => {
        this.saving = false;
        this.isSaved = true;
        Swal.fire({
          icon: 'success',
          title: 'Saved!',
          text: `"${entry.word}" has been added to your vocabulary`,
          timer: 2000,
          showConfirmButton: false
        });
      },
      error: (err) => {
        this.saving = false;
        console.error('Error saving word:', err);
        const errorMsg = err.error?.message || err.message || 'Failed to save word';
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: errorMsg
        });
      }
    });
  }

  getAudioUrl(entry: DictionaryEntry): string | null {
    if (entry.phonetics && entry.phonetics.length > 0) {
      const phoneticWithAudio = entry.phonetics.find(p => p.audio);
      return phoneticWithAudio?.audio || null;
    }
    return null;
  }

  playAudio(url: string) {
    if (this.audio) {
      this.audio.pause();
    }
    this.audio = new Audio(url);
    this.audio.play();
  }

  close() {
    this.closeModal.emit();
    if (this.audio) {
      this.audio.pause();
    }
  }
}
