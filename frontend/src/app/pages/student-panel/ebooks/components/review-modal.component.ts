import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Ebook, Review } from '../../../../core/models/ebook.model';

@Component({
  selector: 'app-review-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div *ngIf="isOpen" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4 modal-backdrop" (click)="close()">
      <div class="bg-white rounded-2xl shadow-2xl max-w-2xl w-full review-modal" (click)="$event.stopPropagation()">
        <!-- Header -->
        <div class="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between rounded-t-2xl">
          <h3 class="text-2xl font-bold text-gray-800">
            Review: {{ ebook?.title }}
          </h3>
          <button (click)="close()" class="text-gray-400 hover:text-gray-600 transition-colors">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <!-- Content -->
        <div class="p-6 modal-content">
          <!-- Rating Selection -->
          <div class="mb-6">
            <label class="block text-sm font-semibold text-gray-700 mb-3">Your Rating</label>
            <div class="flex gap-2 star-rating">
              <button *ngFor="let star of [1,2,3,4,5]"
                      (click)="setRating(star)"
                      type="button"
                      class="star transition-transform hover:scale-110">
                <svg class="w-10 h-10"
                     [class.filled]="star <= rating"
                     [class.empty]="star > rating"
                     fill="currentColor" viewBox="0 0 20 20">
                  <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"></path>
                </svg>
              </button>
            </div>
          </div>

          <!-- Comment -->
          <div class="mb-6">
            <label class="block text-sm font-semibold text-gray-700 mb-2">Your Review</label>
            <textarea 
              [(ngModel)]="comment"
              rows="4"
              placeholder="Share your thoughts about this ebook..."
              class="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-[#2D5F5D] resize-none transition-colors">
            </textarea>
          </div>

          <!-- Submit Button -->
          <button 
            (click)="submit()"
            class="w-full bg-gradient-to-r from-[#2D5F5D] to-[#1e4442] hover:from-[#234948] hover:to-[#152e2d] text-white px-6 py-3 rounded-lg font-semibold transition-all shadow-lg hover:shadow-xl">
            Submit Review
          </button>

          <!-- Existing Reviews -->
          <div *ngIf="reviews && reviews.length > 0" class="mt-8">
            <h4 class="text-lg font-bold text-gray-800 mb-4">Recent Reviews ({{ reviews.length }})</h4>
            <div class="space-y-4 max-h-96 overflow-y-auto">
              <div *ngFor="let review of reviews" 
                   class="bg-gray-50 rounded-lg p-4 hover:bg-gray-100 transition-colors relative">
                <div class="flex items-center justify-between mb-2">
                  <div class="flex items-center gap-2">
                    <div class="w-10 h-10 bg-[#2D5F5D] rounded-full flex items-center justify-center text-white font-bold">
                      {{ review.userName?.charAt(0) || 'U' }}
                    </div>
                    <div>
                      <p class="font-semibold text-gray-800">{{ review.userName || 'Anonymous' }}</p>
                      <p class="text-xs text-gray-500">{{ review.createdAt | date:'short' }}</p>
                    </div>
                  </div>
                  <div class="flex items-center gap-2">
                    <div class="flex gap-0.5">
                      <svg *ngFor="let star of [1,2,3,4,5]"
                           class="w-4 h-4"
                           [class.text-yellow-400]="star <= review.rating"
                           [class.text-gray-300]="star > review.rating"
                           fill="currentColor" viewBox="0 0 20 20">
                        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"></path>
                      </svg>
                    </div>
                    <button *ngIf="canDeleteReview(review)"
                            (click)="onDeleteReview(review.id!, $event)"
                            class="ml-2 text-red-500 hover:text-red-700 transition-colors p-1 rounded hover:bg-red-50"
                            title="Delete review">
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                      </svg>
                    </button>
                  </div>
                </div>
                <p class="text-gray-700">{{ review.comment }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .modal-backdrop {
      animation: fadeIn 0.2s ease-out;
    }

    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }

    .review-modal {
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

    .modal-content {
      max-height: calc(90vh - 120px);
      overflow-y: auto;
    }

    .star-rating {
      display: flex;
      gap: 0.5rem;
    }

    .star {
      cursor: pointer;
      transition: all 0.2s ease;
    }

    .star:hover {
      transform: scale(1.1);
    }

    .star svg.filled {
      color: #fbbf24;
    }

    .star svg.empty {
      color: #d1d5db;
    }
  `]
})
export class ReviewModalComponent {
  @Input() isOpen = false;
  @Input() ebook: Ebook | null = null;
  @Input() reviews: Review[] = [];
  @Input() currentUserId: number = 0;
  @Output() closeModal = new EventEmitter<void>();
  @Output() submitReview = new EventEmitter<{ rating: number; comment: string }>();
  @Output() deleteReview = new EventEmitter<number>();

  rating = 5;
  comment = '';

  canDeleteReview(review: Review): boolean {
    return review.userId === this.currentUserId;
  }

  onDeleteReview(reviewId: number, event: Event) {
    event.stopPropagation();
    this.deleteReview.emit(reviewId);
  }

  setRating(star: number) {
    this.rating = star;
  }

  submit() {
    this.submitReview.emit({ rating: this.rating, comment: this.comment });
  }

  close() {
    this.closeModal.emit();
  }
}
