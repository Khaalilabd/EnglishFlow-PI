import { Component, OnInit, OnDestroy, Pipe, PipeTransform } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { EbookService } from '../../../core/services/ebook.service';
import { AuthService } from '../../../core/services/auth.service';
import { ReviewService } from '../../../core/services/review.service';
import { ReadingProgressService } from '../../../core/services/reading-progress.service';
import { CollectionService } from '../../../core/services/collection.service';
import { Ebook, Review, ReadingProgress, Collection } from '../../../core/models/ebook.model';
import { ReviewModalComponent } from './components/review-modal.component';

// Pipe to sanitize URLs for iframe
@Pipe({
  name: 'sanitizeUrl',
  standalone: true
})
export class SanitizeUrlPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}
  
  transform(url: string): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }
}

interface Chapter {
  id: string;
  title: string;
  description?: string;
  file?: File;
  fileName?: string;
  pages?: number;
  quizId?: number;
  order: number;
  thumbnail?: string;
  expanded?: boolean;
}

interface EbookMetadata {
  chapters: Chapter[];
  totalPages?: number;
  estimatedReadTime?: number;
  coverImage?: string;
  keywords?: string[];
}

interface Playlist {
  id: string;
  name: string;
  ebookIds: number[];
  createdAt: number;
}

@Component({
  selector: 'app-ebooks',
  standalone: true,
  imports: [CommonModule, FormsModule, ReviewModalComponent, SanitizeUrlPipe],
  templateUrl: './ebooks.component.html',
  styleUrls: ['./ebooks.component.scss'],
  animations: [
    trigger('fadeIn', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms ease-out', style({ opacity: 1 }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ opacity: 0 }))
      ])
    ]),
    trigger('slideInRight', [
      transition(':enter', [
        style({ transform: 'translateX(100%)', opacity: 0 }),
        animate('400ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({ transform: 'translateX(0)', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms cubic-bezier(0.25, 0.8, 0.25, 1)', style({ transform: 'translateX(100%)', opacity: 0 }))
      ])
    ])
  ]
})
export class EbooksComponent implements OnInit, OnDestroy {
  ebooks: Ebook[] = [];
  filteredEbooks: Ebook[] = [];
  selectedLevel: string = 'all';
  isLoading = false;
  showUploadModal = false;
  selectedFile: File | null = null;
  
  // View modes
  displayMode: 'grid' | 'list' | 'carousel' | 'magazine' | 'shelf' = 'shelf';
  
  // Search and filters
  searchQuery: string = '';
  sortBy: 'newest' | 'popular' | 'rating' | 'favorited' | 'title' = 'newest';
  favorites: Set<number> = new Set();
  
  // Quick view
  showQuickView = false;
  quickViewEbook: Ebook | null = null;
  
  // Enhanced features
  wizardStep = 1;
  totalWizardSteps = 4;
  chapters: Chapter[] = [];
  draggedChapter: Chapter | null = null;
  dragOverChapter: Chapter | null = null;
  previewMode = false;
  pdfPreviewUrl: string | null = null;
  coverImageFile: File | null = null;
  coverImagePreview: string | null = null;
  
  // Publishing options
  pricingModel: 'free' | 'freemium' | 'premium' | 'token' = 'free';
  releaseSchedule: 'immediate' | 'scheduled' | 'drip' = 'immediate';
  scheduledDate: string = '';
  scheduledTime: string = '12:00';
  targetAudience: string[] = [];
  accessCode: string = '';
  priceAmount: number = 0;
  
  // View mode
  viewMode: 'published' | 'scheduled' | 'pending' | 'rejected' = 'published';
  showFilters: boolean = false;
  
  // Details modal
  showDetailsModal = false;
  selectedEbookForDetails: Ebook | null = null;
  detailsTab: 'details' | 'reviews' = 'details';
  
  // Confetti animation
  showConfetti = false;
  
  newEbook: Ebook = {
    title: '',
    description: '',
    fileUrl: '',
    level: 'A1',
    category: 'GENERAL',
    free: true
  };

  // New features
  ebookReviews: Map<number, Review[]> = new Map();
  userProgress: Map<number, ReadingProgress> = new Map();
  userCollections: Collection[] = [];
  showReviewModal = false;
  selectedEbookForReview: Ebook | null = null;
  newReview = { rating: 5, comment: '' };

  // Playlist features
  showPlaylistModal = false;
  playlistTab: 'my-playlist' | 'add-ebooks' = 'my-playlist';
  playlistContentTab: 'view' | 'add' = 'view';
  playlists: Playlist[] = [];
  selectedPlaylist: Playlist | null = null;
  showCreatePlaylistForm = false;
  newPlaylistName: string = '';
  playlistSearchQuery: string = '';
  filteredPlaylistEbooks: Ebook[] = [];

  constructor(
    private ebookService: EbookService, 
    public authService: AuthService,
    private reviewService: ReviewService,
    private progressService: ReadingProgressService,
    private collectionService: CollectionService,
    private sanitizer: DomSanitizer,
    private router: Router
  ) {}

  ngOnInit() {
    // Debug: Log current user role
    const currentUser = this.authService.currentUserValue;
    console.log('Current user role:', currentUser?.role);
    console.log('Is ACADEMIC_OFFICE_AFFAIR?', this.authService.hasRole(['ACADEMIC_OFFICE_AFFAIR']));
    
    // Set default display mode to grid for Academic Affairs
    if (this.authService.hasRole(['ACADEMIC_OFFICE_AFFAIR'])) {
      this.displayMode = 'grid';
      console.log('Display mode set to grid for Academic Affairs');
    }
    
    this.loadEbooks();
    this.loadUserProgress();
    this.loadUserCollections();
    this.loadFavorites();
    this.loadPlaylists();
    
    // Start carousel auto-play after a short delay (not for Academic Affairs)
    if (!this.authService.hasRole(['ACADEMIC_OFFICE_AFFAIR'])) {
      setTimeout(() => {
        this.startCarouselAutoPlay();
      }, 1000);
    }
  }

  // Check if current user is a tutor
  isTutor(): boolean {
    return this.authService.hasRole(['TUTOR', 'ADMIN']);
  }

  // Check if user should see playlists (not TUTOR or ACADEMIC_OFFICE_AFFAIR)
  canSeePlaylists(): boolean {
    return !this.authService.hasRole(['TUTOR', 'ACADEMIC_OFFICE_AFFAIR']);
  }

  // Get upload button text based on role
  getUploadButtonText(): string {
    return this.authService.hasRole(['TUTOR']) ? 'Request Upload' : 'Upload Ebook';
  }

  // Get status badge info for tutors
  getStatusBadge(ebook: Ebook): { text: string; color: string } | null {
    if (!this.isTutor() || !ebook.status) return null;
    
    switch (ebook.status) {
      case 'PENDING':
        return { text: 'Pending Approval', color: 'bg-yellow-500' };
      case 'PUBLISHED':
        return { text: 'Approved', color: 'bg-green-500' };
      case 'REJECTED':
        return { text: 'Rejected', color: 'bg-red-500' };
      default:
        return null;
    }
  }

  // Calculate slider position for segmented control
  getSliderPosition(): string {
    const isAcademicAffairs = this.authService.hasRole(['ACADEMIC_OFFICE_AFFAIR']);
    const hasPendingTab = this.isTutor() || isAcademicAffairs;
    
    if (!hasPendingTab) {
      // 2 tabs: Published, Scheduled (Students)
      return this.viewMode === 'published' ? '2px' : 'calc(50% + 1px)';
    } else {
      // 3 tabs
      if (this.viewMode === 'published') {
        return '2px';
      } else if (this.viewMode === 'pending') {
        return 'calc(33.333% + 1px)';
      } else {
        // scheduled or rejected (third tab)
        return 'calc(66.666% + 2px)';
      }
    }
  }

  // Calculate slider width for segmented control
  getSliderWidth(): string {
    const isAcademicAffairs = this.authService.hasRole(['ACADEMIC_OFFICE_AFFAIR']);
    const hasPendingTab = this.isTutor() || isAcademicAffairs;
    
    if (!hasPendingTab) {
      // 2 tabs
      return 'calc(50% - 4px)';
    } else {
      // 3 tabs
      return 'calc(33.333% - 4px)';
    }
  }

  loadEbooks() {
    this.isLoading = true;
    this.ebookService.getAllEbooks().subscribe({
      next: (data) => {
        // For students: only show PUBLISHED ebooks
        // For tutors: show all their ebooks (PENDING, PUBLISHED, REJECTED)
        // For ACADEMIC_OFFICE_AFFAIR: show all ebooks
        if (this.authService.hasRole(['STUDENT'])) {
          this.ebooks = data.filter(e => {
            const status = e.status as string;
            return status === 'PUBLISHED' || !e.status;
          });
        } else {
          this.ebooks = data;
        }
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading ebooks:', error);
        this.isLoading = false;
      }
    });
  }

  // View mode switching
  setDisplayMode(mode: 'grid' | 'list' | 'carousel' | 'magazine' | 'shelf') {
    this.displayMode = mode;
  }

  // Search functionality
  onSearch() {
    this.applyFilters();
  }

  // Sorting
  onSortChange() {
    this.applyFilters();
  }

  // Apply all filters
  applyFilters() {
    let filtered = this.ebooks;

    // Filter by view mode
    if (this.viewMode === 'published') {
      filtered = filtered.filter(e => this.isPublished(e));
    } else if (this.viewMode === 'scheduled') {
      filtered = filtered.filter(e => this.isScheduled(e));
    } else if (this.viewMode === 'pending') {
      filtered = filtered.filter(e => e.status === 'PENDING');
    } else if (this.viewMode === 'rejected') {
      filtered = filtered.filter(e => e.status === 'REJECTED');
    }

    // Filter by level
    if (this.selectedLevel !== 'all') {
      filtered = filtered.filter(e => e.level === this.selectedLevel);
    }

    // Search filter
    if (this.searchQuery.trim()) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(e => 
        e.title?.toLowerCase().includes(query) ||
        e.description?.toLowerCase().includes(query) ||
        e.category?.toLowerCase().includes(query)
      );
    }

    // Filter by favorited (only show favorited ebooks)
    if (this.sortBy === 'favorited') {
      filtered = filtered.filter(e => this.isFavorite(e.id!));
    }

    // Sort
    filtered = this.sortEbooks(filtered);

    this.filteredEbooks = filtered;
  }

  sortEbooks(ebooks: Ebook[]): Ebook[] {
    const sorted = [...ebooks];
    switch (this.sortBy) {
      case 'newest':
        return sorted.sort((a, b) => (b.id || 0) - (a.id || 0));
      case 'popular':
        return sorted.sort((a, b) => (b.downloadCount || 0) - (a.downloadCount || 0));
      case 'rating':
        return sorted.sort((a, b) => (this.getAverageRating(b) - this.getAverageRating(a)));
      case 'favorited':
        // When favorited is selected, ebooks are already filtered, just sort by newest
        return sorted.sort((a, b) => (b.id || 0) - (a.id || 0));
      case 'title':
        return sorted.sort((a, b) => (a.title || '').localeCompare(b.title || ''));
      default:
        return sorted;
    }
  }

  // Favorites
  loadFavorites() {
    try {
      const stored = localStorage.getItem('ebookFavorites');
      if (stored) {
        this.favorites = new Set(JSON.parse(stored));
      }
    } catch (error) {
      console.error('Error loading favorites:', error);
    }
  }

  saveFavorites() {
    try {
      localStorage.setItem('ebookFavorites', JSON.stringify(Array.from(this.favorites)));
    } catch (error) {
      console.error('Error saving favorites:', error);
    }
  }

  toggleFavorite(ebookId: number, event: Event) {
    event.stopPropagation();
    if (this.favorites.has(ebookId)) {
      this.favorites.delete(ebookId);
    } else {
      this.favorites.add(ebookId);
    }
    this.saveFavorites();
  }

  isFavorite(ebookId: number): boolean {
    return this.favorites.has(ebookId);
  }

  // Quick view modal
  openQuickView(ebook: Ebook, event: Event) {
    event.stopPropagation();
    this.quickViewEbook = ebook;
    this.showQuickView = true;
  }

  closeQuickView() {
    this.showQuickView = false;
    this.quickViewEbook = null;
  }

  // Helper methods for badges
  isNewEbook(ebook: Ebook): boolean {
    if (!ebook.id) return false;
    // Consider ebooks added in last 7 days as "new"
    // Since we don't have createdAt, use ID as proxy (higher ID = newer)
    const allIds = this.ebooks.map(e => e.id || 0);
    const maxId = Math.max(...allIds);
    return (ebook.id || 0) >= maxId - 5; // Last 5 ebooks
  }

  isPopular(ebook: Ebook): boolean {
    const avgDownloads = this.ebooks.reduce((sum, e) => sum + (e.downloadCount || 0), 0) / this.ebooks.length;
    return (ebook.downloadCount || 0) > avgDownloads * 1.5;
  }

  getEstimatedReadTime(ebook: Ebook): string {
    const metadata = this.getMetadata(ebook.description);
    if (metadata?.estimatedReadTime) {
      return `${metadata.estimatedReadTime} min`;
    }
    // Estimate based on file size (rough approximation)
    const pages = Math.floor((ebook.fileSize || 0) / 50000);
    const minutes = pages * 2;
    return minutes > 0 ? `${minutes} min` : '5 min';
  }

  // Star rating
  getAverageRating(ebook: Ebook): number {
    // Use the new averageRating field from backend
    return ebook.averageRating || 0;
  }

  getStarArray(rating: number): boolean[] {
    return Array(5).fill(false).map((_, i) => i < Math.round(rating));
  }

  // NEW: Load user progress
  loadUserProgress() {
    this.progressService.getUserProgress().subscribe({
      next: (progressList) => {
        progressList.forEach(progress => {
          this.userProgress.set(progress.ebookId, progress);
        });
      },
      error: (error) => console.error('Error loading progress:', error)
    });
  }

  // NEW: Load user collections
  loadUserCollections() {
    this.collectionService.getUserCollections().subscribe({
      next: (collections) => {
        this.userCollections = collections;
      },
      error: (error) => console.error('Error loading collections:', error)
    });
  }

  // NEW: Load reviews for an ebook
  loadEbookReviews(ebookId: number) {
    console.log(`Loading reviews for ebook ${ebookId}...`);
    this.reviewService.getEbookReviews(ebookId).subscribe({
      next: (reviews) => {
        console.log(`Loaded ${reviews.length} reviews for ebook ${ebookId}:`, reviews);
        // Verify all reviews belong to this ebook
        const validReviews = reviews.filter(r => r.ebookId === ebookId);
        if (validReviews.length !== reviews.length) {
          console.warn(`Warning: Filtered out ${reviews.length - validReviews.length} reviews that don't belong to ebook ${ebookId}`);
        }
        this.ebookReviews.set(ebookId, validReviews);
      },
      error: (error) => console.error(`Error loading reviews for ebook ${ebookId}:`, error)
    });
  }

  // NEW: Get progress for an ebook
  getProgress(ebookId: number): ReadingProgress | undefined {
    return this.userProgress.get(ebookId);
  }

  // NEW: Get progress percentage
  getProgressPercentage(ebookId: number): number {
    const progress = this.getProgress(ebookId);
    return progress?.progressPercentage || 0;
  }

  // NEW: Open review modal
  openReviewModal(ebook: Ebook, event: Event) {
    event.stopPropagation();
    this.selectedEbookForReview = ebook;
    
    // Clear previous reviews to avoid showing wrong data
    this.ebookReviews.delete(ebook.id!);
    
    // Initialize with default values first
    this.newReview = { rating: 5, comment: '' };
    
    // Load fresh reviews for this specific ebook
    this.reviewService.getEbookReviews(ebook.id!).subscribe({
      next: (reviews) => {
        console.log(`Loaded ${reviews.length} reviews for ebook ${ebook.id}:`, reviews);
        const validReviews = reviews.filter(r => r.ebookId === ebook.id);
        this.ebookReviews.set(ebook.id!, validReviews);
        
        // Check if user already has a review for this ebook
        const currentUserId = this.getCurrentUserId();
        const existingReview = validReviews.find(r => r.userId === currentUserId);
        
        if (existingReview) {
          // Pre-populate with existing review
          this.newReview = { 
            rating: existingReview.rating, 
            comment: existingReview.comment || '' 
          };
        }
      },
      error: (error) => console.error(`Error loading reviews for ebook ${ebook.id}:`, error)
    });
    
    this.showReviewModal = true;
  }

  // NEW: Close review modal
  closeReviewModal() {
    this.showReviewModal = false;
    this.selectedEbookForReview = null;
    this.newReview = { rating: 5, comment: '' };
  }

  // NEW: Submit review
  submitReview(reviewData: { rating: number; comment: string }) {
    if (!this.selectedEbookForReview?.id) return;

    const currentUserId = this.getCurrentUserId();
    const existingReview = this.getReviews(this.selectedEbookForReview.id).find(r => r.userId === currentUserId);

    if (existingReview && existingReview.id) {
      // Update existing review
      this.reviewService.updateReview(
        this.selectedEbookForReview.id,
        existingReview.id,
        {
          ebookId: this.selectedEbookForReview.id,
          rating: reviewData.rating,
          comment: reviewData.comment
        }
      ).subscribe({
        next: () => {
          // Clear cached reviews for this ebook
          this.ebookReviews.delete(this.selectedEbookForReview!.id!);
          
          // Reload reviews and ebook data
          this.loadEbookReviews(this.selectedEbookForReview!.id!);
          this.loadEbooks();
          this.closeReviewModal();
          alert('✅ Review updated successfully!');
        },
        error: (error) => {
          console.error('Error updating review:', error);
          alert('Failed to update review. Please try again.');
        }
      });
    } else {
      // Create new review
      this.reviewService.createReview({
        ebookId: this.selectedEbookForReview.id,
        rating: reviewData.rating,
        comment: reviewData.comment
      }).subscribe({
        next: () => {
          // Clear cached reviews for this ebook
          this.ebookReviews.delete(this.selectedEbookForReview!.id!);
          
          // Reload reviews and ebook data
          this.loadEbookReviews(this.selectedEbookForReview!.id!);
          this.loadEbooks();
          this.closeReviewModal();
          alert('✅ Review submitted successfully!');
        },
        error: (error) => {
          console.error('Error submitting review:', error);
          
          // Extract error message
          let errorMessage = 'Failed to submit review. Please try again.';
          
          if (error.error?.message) {
            errorMessage = error.error.message;
          } else if (error.message) {
            errorMessage = error.message;
          }
          
          // Check for duplicate review error
          if (errorMessage.includes('already reviewed') || 
              errorMessage.includes('unique_user_ebook') ||
              errorMessage.includes('only submit one review')) {
            errorMessage = '⚠️ You have already reviewed this ebook. You can only submit one review per ebook.';
          }
          
          alert(errorMessage);
        }
      });
    }
  }

  // NEW: Get reviews for display
  getReviews(ebookId: number): Review[] {
    return this.ebookReviews.get(ebookId) || [];
  }

  // NEW: Set rating (for star click)
  setRating(rating: number) {
    this.newReview.rating = rating;
  }

  // NEW: Get current user ID
  getCurrentUserId(): number {
    return parseInt(localStorage.getItem('userId') || '1');
  }

  // Related ebooks
  getRelatedEbooks(ebook: Ebook): Ebook[] {
    return this.ebooks
      .filter(e => e.id !== ebook.id && (e.level === ebook.level || e.category === ebook.category))
      .slice(0, 3);
  }

  // Recently added section
  getRecentlyAdded(): Ebook[] {
    return [...this.ebooks]
      .sort((a, b) => (b.id || 0) - (a.id || 0))
      .slice(0, 5);
  }

  // Confetti animation on download
  triggerConfetti() {
    this.showConfetti = true;
    setTimeout(() => {
      this.showConfetti = false;
    }, 3000);
  }

  filterByLevel(level: string) {
    this.selectedLevel = level;
    this.applyFilters();
  }

  setViewMode(mode: 'published' | 'scheduled' | 'pending' | 'rejected') {
    this.viewMode = mode;
    this.applyFilters();
  }

  getFilteredEbooks(): any[] {
    let filtered = this.filteredEbooks;
    
    // Filter by view mode
    if (this.viewMode === 'published') {
      filtered = filtered.filter(e => this.isPublished(e));
    } else if (this.viewMode === 'scheduled') {
      filtered = filtered.filter(e => this.isScheduled(e));
    } else if (this.viewMode === 'pending') {
      filtered = filtered.filter(e => e.status === 'PENDING');
    } else if (this.viewMode === 'rejected') {
      filtered = filtered.filter(e => e.status === 'REJECTED');
    }
    
    return filtered;
  }

  getEbookCountByMode(mode: string): number {
    if (mode === 'published') {
      return this.ebooks.filter(e => this.isPublished(e)).length;
    } else if (mode === 'scheduled') {
      return this.ebooks.filter(e => this.isScheduled(e)).length;
    } else if (mode === 'pending') {
      return this.ebooks.filter(e => e.status === 'PENDING').length;
    } else if (mode === 'rejected') {
      return this.ebooks.filter(e => e.status === 'REJECTED').length;
    }
    return this.ebooks.length;
  }

  isPublished(ebook: Ebook): boolean {
    // Check if status is explicitly PUBLISHED
    const status = ebook.status as string;
    if (status === 'PUBLISHED') {
      return true;
    }
    
    // For backward compatibility: check metadata for scheduled releases
    const metadata = this.getMetadata(ebook.description);
    if (metadata && metadata.release === 'scheduled' && metadata.scheduledDate) {
      const scheduledDateTime = new Date(metadata.scheduledDate);
      return scheduledDateTime <= new Date();
    }
    
    // If no status, consider it published (old ebooks)
    // But NOT if status is PENDING or REJECTED
    return !ebook.status;
  }

  isScheduled(ebook: Ebook): boolean {
    const metadata = this.getMetadata(ebook.description);
    if (metadata && metadata.release === 'scheduled' && metadata.scheduledDate) {
      const scheduledDateTime = new Date(metadata.scheduledDate);
      return scheduledDateTime > new Date();
    }
    return false;
  }

  getScheduledDate(ebook: Ebook): string {
    const metadata = this.getMetadata(ebook.description);
    if (metadata && metadata.scheduledDate) {
      return new Date(metadata.scheduledDate).toLocaleDateString();
    }
    return '';
  }

  toggleFilters() {
    this.showFilters = !this.showFilters;
  }

  hasActiveFilters(): boolean {
    return this.selectedLevel !== 'all' || this.searchQuery.trim() !== '' || this.sortBy !== 'newest';
  }

  clearFilters() {
    this.searchQuery = '';
    this.selectedLevel = 'all';
    this.sortBy = 'newest';
    this.applyFilters();
  }

  openDetailsModal(ebook: Ebook) {
    this.selectedEbookForDetails = ebook;
    this.detailsTab = 'details'; // Reset to details tab
    this.showDetailsModal = true;
    
    // Load reviews for this ebook
    if (ebook.id) {
      this.loadEbookReviews(ebook.id);
    }
  }

  closeDetailsModal() {
    this.showDetailsModal = false;
    this.selectedEbookForDetails = null;
    this.detailsTab = 'details';
  }

  editEbook(ebook: Ebook) {
    // Populate form with existing data
    this.newEbook = { ...ebook };
    const metadata = this.getMetadata(ebook.description);
    
    if (metadata) {
      this.chapters = metadata.chapters || [];
      this.pricingModel = metadata.pricing || 'free';
      this.priceAmount = metadata.price || 0;
      this.releaseSchedule = metadata.release || 'immediate';
      this.scheduledDate = metadata.scheduledDate ? metadata.scheduledDate.split('T')[0] : '';
      this.scheduledTime = metadata.scheduledDate ? metadata.scheduledDate.split('T')[1]?.substring(0, 5) || '12:00' : '12:00';
      this.targetAudience = metadata.audience || [];
    }
    
    // Load cover image from backend if it exists
    if (ebook.id && ebook.coverImageUrl) {
      this.coverImagePreview = this.getCoverImageUrl(ebook);
    } else {
      this.coverImagePreview = null;
    }
    
    // Clean description
    this.newEbook.description = this.getCleanDescription(ebook.description);
    
    // Mark that we're editing (file already exists)
    // We'll show the existing file name in the UI
    this.selectedFile = null; // Don't set a fake file
    this.coverImageFile = null; // Don't set a fake cover file
    
    // Open modal in edit mode
    this.showUploadModal = true;
    this.wizardStep = 1;
    this.closeDetailsModal();
  }

  getCoverImageUrl(ebook: Ebook): string | null {
    // First check if there's a cover image URL directly on the ebook (from backend)
    if (ebook.id && ebook.coverImageUrl) {
      // Use the correct API Gateway path
      return `http://localhost:8080/api/learning/ebooks/${ebook.id}/cover?t=${Date.now()}`;
    }
    
    // Check metadata for hasCoverImage flag
    const metadata = this.getMetadata(ebook.description);
    if (metadata && metadata.hasCoverImage && ebook.id) {
      // Try to load from backend with cache busting
      return `http://localhost:8080/api/learning/ebooks/${ebook.id}/cover?t=${Date.now()}`;
    }
    
    return null;
  }

  handleImageError(event: Event): void {
    const target = event.target as HTMLImageElement;
    console.error('Image failed to load:', target.src);
    if (target) {
      target.style.display = 'none';
      // Show the fallback emoji instead
      const parent = target.parentElement;
      if (parent) {
        const fallback = parent.querySelector('.text-6xl');
        if (fallback) {
          (fallback as HTMLElement).classList.remove('hidden');
        }
      }
    }
  }



  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file && file.type === 'application/pdf') {
      this.selectedFile = file;
      this.extractPdfMetadata(file);
      this.generatePdfPreview(file);
    } else if (file) {
      alert('Please select a valid PDF file');
    }
  }

  // Extract metadata from PDF
  extractPdfMetadata(file: File) {
    // Simulate PDF metadata extraction
    // In production, use a library like pdf.js
    const estimatedPages = Math.floor(file.size / 50000); // Rough estimate
    const estimatedReadTime = Math.ceil(estimatedPages * 2); // 2 min per page
    
    // Auto-suggest level based on file name or size
    if (file.name.toLowerCase().includes('beginner') || file.name.toLowerCase().includes('a1')) {
      this.newEbook.level = 'A1';
    } else if (file.name.toLowerCase().includes('intermediate') || file.name.toLowerCase().includes('b1')) {
      this.newEbook.level = 'B1';
    }
    
    // Store metadata in description as JSON
    const metadata: EbookMetadata = {
      chapters: this.chapters,
      totalPages: estimatedPages,
      estimatedReadTime: estimatedReadTime
    };
    
    console.log('Extracted metadata:', metadata);
  }

  // Generate PDF preview
  generatePdfPreview(file: File) {
    // Clean up previous blob URL if it exists
    if (this.pdfPreviewUrl && this.pdfPreviewUrl.startsWith('blob:')) {
      URL.revokeObjectURL(this.pdfPreviewUrl);
    }
    
    // Create a blob URL for the PDF file with explicit MIME type
    try {
      const blob = new Blob([file], { type: 'application/pdf' });
      this.pdfPreviewUrl = URL.createObjectURL(blob);
      console.log('PDF Preview URL created:', this.pdfPreviewUrl);
      console.log('Blob type:', blob.type);
      console.log('Blob size:', blob.size);
    } catch (error) {
      console.error('Error creating PDF preview:', error);
      this.pdfPreviewUrl = null;
    }
  }

  // Chapter management
  addChapter() {
    const newChapter: Chapter = {
      id: Date.now().toString(),
      title: `Chapter ${this.chapters.length + 1}`,
      description: '',
      order: this.chapters.length,
      expanded: false
    };
    this.chapters.push(newChapter);
  }

  removeChapter(index: number) {
    this.chapters.splice(index, 1);
    this.reorderChapters();
  }

  toggleChapterExpand(chapter: Chapter) {
    chapter.expanded = !chapter.expanded;
  }

  onChapterFileSelected(event: any, chapter: Chapter) {
    const file = event.target.files[0];
    if (file && file.type === 'application/pdf') {
      chapter.file = file;
      chapter.fileName = file.name;
      chapter.pages = Math.floor(file.size / 50000);
    }
  }

  onCoverImageSelected(event: any) {
    const file = event.target.files[0];
    if (file && file.type.startsWith('image/')) {
      this.coverImageFile = file;
      
      // Create a preview without compression for display
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const img = new Image();
        img.onload = () => {
          // Create canvas to resize image to reasonable dimensions
          const canvas = document.createElement('canvas');
          const ctx = canvas.getContext('2d');
          
          // Calculate dimensions to fit within 800x1200 while maintaining aspect ratio
          const maxWidth = 800;
          const maxHeight = 1200;
          let width = img.width;
          let height = img.height;
          
          // Calculate scaling factor
          const widthRatio = maxWidth / width;
          const heightRatio = maxHeight / height;
          const ratio = Math.min(widthRatio, heightRatio, 1); // Don't upscale
          
          width = Math.floor(width * ratio);
          height = Math.floor(height * ratio);
          
          canvas.width = width;
          canvas.height = height;
          
          // Draw resized image
          ctx?.drawImage(img, 0, 0, width, height);
          
          // Convert to base64 with good quality
          this.coverImagePreview = canvas.toDataURL('image/jpeg', 0.85);
        };
        img.src = e.target.result;
      };
      reader.readAsDataURL(file);
    } else if (file) {
      alert('Please select a valid image file (JPG or PNG)');
    }
  }

  getChapterProgress(chapter: Chapter): number {
    if (!chapter.file) return 0;
    if (chapter.fileName && chapter.pages) return 100;
    return 50;
  }

  previewChapterPdf(chapter: Chapter) {
    if (!chapter.file) {
      alert('No PDF file uploaded for this chapter');
      return;
    }
    
    // Create a blob URL from the file
    const fileUrl = URL.createObjectURL(chapter.file);
    
    // Open in a new window
    window.open(fileUrl, '_blank');
    
    // Clean up the URL after a delay
    setTimeout(() => URL.revokeObjectURL(fileUrl), 1000);
  }

  previewSelectedPdf() {
    if (!this.selectedFile) {
      alert('No PDF file selected');
      return;
    }
    
    // Create a blob URL from the file
    const fileUrl = URL.createObjectURL(this.selectedFile);
    
    // Open in a new window
    window.open(fileUrl, '_blank');
    
    // Clean up the URL after a delay
    setTimeout(() => URL.revokeObjectURL(fileUrl), 1000);
  }

  triggerFileUpload() {
    const fileInput = document.getElementById('pdfFile') as HTMLInputElement;
    if (fileInput) {
      fileInput.click();
    }
  }

  // Drag and drop for chapters
  onDragStart(event: DragEvent, chapter: Chapter) {
    this.draggedChapter = chapter;
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'move';
    }
  }

  onDragOver(event: DragEvent, chapter: Chapter) {
    event.preventDefault();
    this.dragOverChapter = chapter;
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = 'move';
    }
  }

  onDragLeave(chapter: Chapter) {
    if (this.dragOverChapter === chapter) {
      this.dragOverChapter = null;
    }
  }

  onDrop(event: DragEvent, targetChapter: Chapter) {
    event.preventDefault();
    this.dragOverChapter = null;
    
    if (this.draggedChapter && this.draggedChapter !== targetChapter) {
      const draggedIndex = this.chapters.indexOf(this.draggedChapter);
      const targetIndex = this.chapters.indexOf(targetChapter);
      
      this.chapters.splice(draggedIndex, 1);
      this.chapters.splice(targetIndex, 0, this.draggedChapter);
      
      this.reorderChapters();
    }
    this.draggedChapter = null;
  }

  onDragEnd() {
    this.draggedChapter = null;
    this.dragOverChapter = null;
  }

  isDragging(chapter: Chapter): boolean {
    return this.draggedChapter === chapter;
  }

  isDragOver(chapter: Chapter): boolean {
    return this.dragOverChapter === chapter;
  }

  reorderChapters() {
    this.chapters.forEach((chapter, index) => {
      chapter.order = index;
    });
  }

  // Wizard navigation
  nextWizardStep() {
    if (this.wizardStep === 1 && !this.validateBasicInfo()) {
      return;
    }
    if (this.wizardStep < this.totalWizardSteps) {
      this.wizardStep++;
    }
  }

  previousWizardStep() {
    if (this.wizardStep > 1) {
      this.wizardStep--;
    }
  }

  validateBasicInfo(): boolean {
    if (!this.newEbook.title?.trim()) {
      alert('Please enter an ebook title');
      return false;
    }
    // For create mode, require a file or chapters
    // For edit mode, file is optional (existing file will be kept)
    if (!this.newEbook.id && !this.selectedFile && this.chapters.length === 0) {
      alert('Please upload a PDF file or add chapters');
      return false;
    }
    return true;
  }

  togglePreview() {
    this.previewMode = !this.previewMode;
  }

  toggleAudience(audience: string) {
    const index = this.targetAudience.indexOf(audience);
    if (index > -1) {
      this.targetAudience.splice(index, 1);
    } else {
      this.targetAudience.push(audience);
    }
  }

  hasAudience(audience: string): boolean {
    return this.targetAudience.includes(audience);
  }

  getEstimatedReadTimeForUpload(): string {
    if (!this.selectedFile) return '0 min';
    const pages = Math.floor(this.selectedFile.size / 50000);
    const minutes = pages * 2;
    if (minutes < 60) return `${minutes} min`;
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}h ${mins}min`;
  }

  getEstimatedPages(): number {
    if (!this.selectedFile) return 0;
    return Math.floor(this.selectedFile.size / 50000);
  }

  openUploadModal() {
    this.showUploadModal = true;
    this.wizardStep = 1;
  }

  closeUploadModal() {
    if (this.wizardStep > 1 && !confirm('Are you sure? Your progress will be lost.')) {
      return;
    }
    this.showUploadModal = false;
    this.resetForm();
  }

  uploadEbook() {
    if (!this.newEbook.title) {
      alert('Please enter an ebook title');
      return;
    }
    
    // For create mode, file is required
    // For edit mode, file is optional (keep existing file)
    if (!this.newEbook.id && !this.selectedFile) {
      alert('Please select a PDF file');
      return;
    }

    // Validate scheduled release
    if (this.releaseSchedule === 'scheduled') {
      if (!this.scheduledDate) {
        alert('Please select a scheduled date');
        return;
      }
      if (!this.scheduledTime) {
        alert('Please select a scheduled time');
        return;
      }
    }

    // Combine scheduled date and time
    let scheduledDateTime = '';
    if (this.releaseSchedule === 'scheduled' && this.scheduledDate && this.scheduledTime) {
      scheduledDateTime = `${this.scheduledDate}T${this.scheduledTime}:00`;
    }

    // Create clean metadata object
    const metadata = {
      chapters: this.chapters.map(ch => ({
        id: ch.id,
        title: ch.title,
        description: ch.description,
        order: ch.order,
        pages: ch.pages,
        fileName: ch.fileName
      })),
      totalPages: this.selectedFile ? Math.floor(this.selectedFile.size / 50000) : 0,
      estimatedReadTime: this.selectedFile ? Math.ceil((this.selectedFile.size / 50000) * 2) : 0,
      pricing: this.pricingModel,
      price: this.priceAmount,
      release: this.releaseSchedule,
      scheduledDate: scheduledDateTime,
      audience: this.targetAudience,
      accessCode: this.accessCode,
      hasCoverImage: !!this.coverImageFile || !!this.coverImagePreview
    };
    
    // Store ONLY user description, keep metadata separate
    // Make sure we're working with clean description (no existing metadata)
    const originalDescription = this.getCleanDescription(this.newEbook.description) || '';
    
    // Create a copy of the ebook object for sending to backend
    const ebookToSend = { ...this.newEbook };
    
    // Set creator ID for new ebooks
    if (!ebookToSend.id) {
      ebookToSend.createdBy = this.getCurrentUserId();
    }
    
    // Temporarily store metadata in description for backend (limit total length)
    const metadataStr = JSON.stringify(metadata);
    const maxDescLength = 1500; // Leave room for metadata
    const truncatedDesc = originalDescription.substring(0, maxDescLength);
    ebookToSend.description = truncatedDesc + '\n\n__METADATA__\n' + metadataStr;
    ebookToSend.free = this.pricingModel === 'free';

    this.isLoading = true;
    
    // Check if editing or creating
    if (ebookToSend.id) {
      // Update existing ebook - file and cover image are optional
      this.ebookService.updateEbook(ebookToSend.id, ebookToSend, this.selectedFile || undefined, this.coverImageFile || undefined).subscribe({
        next: () => {
          this.loadEbooks();
          this.closeUploadModal();
          this.isLoading = false;
          alert('✅ Ebook updated successfully!');
        },
        error: (error) => {
          console.error('Error updating ebook:', error);
          alert('Error updating ebook: ' + (error.error?.message || error.message || 'Unknown error'));
          this.isLoading = false;
        }
      });
    } else {
      // Create new ebook - file is required, cover image is optional
      this.ebookService.createEbook(ebookToSend, this.selectedFile!, this.coverImageFile || undefined).subscribe({
        next: () => {
          this.loadEbooks();
          this.closeUploadModal();
          this.isLoading = false;
          
          // Show different message based on role
          if (this.authService.hasRole(['TUTOR'])) {
            alert('✅ Upload request submitted successfully! Your ebook is pending approval from Academic Office Affairs.');
          } else {
            alert('✅ Ebook uploaded successfully!');
          }
        },
        error: (error) => {
          console.error('Error uploading ebook:', error);
          alert('Error uploading ebook: ' + (error.error?.message || error.message || 'Unknown error'));
          this.isLoading = false;
        }
      });
    }
  }

  downloadEbook(ebook: Ebook) {
    if (ebook.id) {
      console.log('Starting download for ebook:', ebook.id, ebook.title);
      
      // Get the actual user ID from localStorage
      const userId = this.getCurrentUserId();
      
      // Track access with the actual user ID to increment download count
      this.ebookService.trackAccess(ebook.id, userId).subscribe({
        next: () => {
          console.log('Access tracked successfully for user:', userId);
          // Reload ebooks to update the download count in the UI
          this.loadEbooks();
        },
        error: (error) => {
          console.error('Error tracking access:', error);
        }
      });
      
      // Download the ebook
      this.ebookService.downloadEbook(ebook.id).subscribe({
        next: (blob) => {
          console.log('Download successful, blob size:', blob.size);
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `${ebook.title}.pdf`;
          link.click();
          window.URL.revokeObjectURL(url);
          
          // Trigger confetti animation
          this.triggerConfetti();
        },
        error: (error) => {
          console.error('Download error details:', error);
          console.error('Error status:', error.status);
          console.error('Error message:', error.message);
          console.error('Error body:', error.error);
          alert('Error downloading ebook. Please try again.');
        }
      });
    }
  }

  readOnline(ebook: Ebook) {
    if (ebook.id) {
      // Open reader in a new tab
      const readerUrl = `/user-panel/ebooks/read/${ebook.id}`;
      window.open(readerUrl, '_blank');
    }
  }
  
  // Extract clean description without metadata
  getCleanDescription(description: string): string {
    if (!description) return '';
    const metadataIndex = description.indexOf('__METADATA__');
    if (metadataIndex > -1) {
      return description.substring(0, metadataIndex).trim();
    }
    return description;
  }
  
  // Extract metadata from description
  getMetadata(description: string): any {
    if (!description) return null;
    const metadataIndex = description.indexOf('__METADATA__');
    if (metadataIndex > -1) {
      try {
        const jsonStr = description.substring(metadataIndex + 13); // Skip "__METADATA__\n"
        return JSON.parse(jsonStr);
      } catch (e) {
        return null;
      }
    }
    return null;
  }
  
  // Get pricing info from metadata
  getPricing(ebook: Ebook): string {
    const metadata = this.getMetadata(ebook.description);
    if (metadata && metadata.pricing) {
      if (metadata.pricing === 'premium' && metadata.price) {
        return `$${metadata.price}`;
      }
      return metadata.pricing === 'free' ? 'Free' : 'Premium';
    }
    return ebook.free ? 'Free' : 'Premium';
  }
  
  // Check if ebook is premium
  isPremium(ebook: Ebook): boolean {
    const metadata = this.getMetadata(ebook.description);
    if (metadata && metadata.pricing) {
      return metadata.pricing === 'premium';
    }
    return !ebook.free;
  }

  deleteEbook(id: number) {
    if (confirm('Are you sure you want to delete this ebook?')) {
      this.ebookService.deleteEbook(id).subscribe({
        next: () => {
          this.loadEbooks();
        },
        error: (error) => console.error('Error deleting ebook:', error)
      });
    }
  }

  resetForm() {
    // Clean up blob URL if it exists
    if (this.pdfPreviewUrl && this.pdfPreviewUrl.startsWith('blob:')) {
      URL.revokeObjectURL(this.pdfPreviewUrl);
    }
    
    this.newEbook = {
      title: '',
      description: '',
      fileUrl: '',
      level: 'A1',
      category: 'GENERAL',
      free: true
    };
    this.selectedFile = null;
    this.chapters = [];
    this.wizardStep = 1;
    this.previewMode = false;
    this.pdfPreviewUrl = null;
    this.coverImageFile = null;
    this.coverImagePreview = null;
    this.pricingModel = 'free';
    this.releaseSchedule = 'immediate';
    this.scheduledDate = '';
    this.scheduledTime = '12:00';
    this.targetAudience = [];
    this.accessCode = '';
    this.priceAmount = 0;
  }

  formatFileSize(bytes: number | undefined): string {
    if (!bytes) return 'Unknown';
    const mb = bytes / (1024 * 1024);
    return `${mb.toFixed(2)} MB`;
  }

  // Generate vibrant book spine colors
  getBookColor(index: number): string {
    const colors = [
      '#8B5CF6', // Purple
      '#EC4899', // Pink
      '#F59E0B', // Amber
      '#10B981', // Emerald
      '#3B82F6', // Blue
      '#EF4444', // Red
      '#14B8A6', // Teal
      '#F97316', // Orange
      '#6366F1', // Indigo
      '#84CC16', // Lime
    ];
    return colors[index % colors.length];
  }

  getDarkerBookColor(index: number): string {
    const darkerColors = [
      '#6D28D9', // Darker Purple
      '#BE185D', // Darker Pink
      '#D97706', // Darker Amber
      '#059669', // Darker Emerald
      '#2563EB', // Darker Blue
      '#DC2626', // Darker Red
      '#0F766E', // Darker Teal
      '#EA580C', // Darker Orange
      '#4F46E5', // Darker Indigo
      '#65A30D', // Darker Lime
    ];
    return darkerColors[index % darkerColors.length];
  }

  // Playlist Management
  openPlaylistModal() {
    this.showPlaylistModal = true;
    this.playlistContentTab = 'view';
    this.loadPlaylists();
    this.filteredPlaylistEbooks = [...this.ebooks];
    
    // Auto-select first playlist if available
    if (this.playlists.length > 0 && !this.selectedPlaylist) {
      this.selectedPlaylist = this.playlists[0];
    }
  }

  closePlaylistModal() {
    this.showPlaylistModal = false;
    this.playlistSearchQuery = '';
    this.filteredPlaylistEbooks = [];
    this.showCreatePlaylistForm = false;
    this.newPlaylistName = '';
  }

  loadPlaylists() {
    try {
      const stored = localStorage.getItem('ebookPlaylists');
      this.playlists = stored ? JSON.parse(stored) : [];
    } catch (error) {
      console.error('Error loading playlists:', error);
      this.playlists = [];
    }
  }

  savePlaylists() {
    try {
      localStorage.setItem('ebookPlaylists', JSON.stringify(this.playlists));
    } catch (error) {
      console.error('Error saving playlists:', error);
    }
  }

  createPlaylist() {
    if (!this.newPlaylistName.trim()) {
      alert('Please enter a playlist name');
      return;
    }

    const newPlaylist: Playlist = {
      id: Date.now().toString(),
      name: this.newPlaylistName.trim(),
      ebookIds: [],
      createdAt: Date.now()
    };

    this.playlists.push(newPlaylist);
    this.savePlaylists();
    this.selectedPlaylist = newPlaylist;
    this.showCreatePlaylistForm = false;
    this.newPlaylistName = '';
  }

  selectPlaylist(playlist: Playlist) {
    this.selectedPlaylist = playlist;
    this.playlistContentTab = 'view';
  }

  deletePlaylist(playlistId: string, event: Event) {
    event.stopPropagation();
    if (confirm('Are you sure you want to delete this playlist?')) {
      this.playlists = this.playlists.filter(p => p.id !== playlistId);
      this.savePlaylists();
      
      if (this.selectedPlaylist?.id === playlistId) {
        this.selectedPlaylist = this.playlists.length > 0 ? this.playlists[0] : null;
      }
    }
  }

  addToPlaylist(playlistId: string, ebook: Ebook) {
    if (!ebook.id) return;
    
    const playlist = this.playlists.find(p => p.id === playlistId);
    if (playlist && !playlist.ebookIds.includes(ebook.id)) {
      playlist.ebookIds.push(ebook.id);
      this.savePlaylists();
      alert(`✅ "${ebook.title}" added to "${playlist.name}"!`);
    }
  }

  removeFromPlaylist(playlistId: string, ebookId: number) {
    const playlist = this.playlists.find(p => p.id === playlistId);
    if (playlist) {
      playlist.ebookIds = playlist.ebookIds.filter(id => id !== ebookId);
      this.savePlaylists();
    }
  }

  isInPlaylist(playlistId: string, ebookId: number): boolean {
    const playlist = this.playlists.find(p => p.id === playlistId);
    return playlist ? playlist.ebookIds.includes(ebookId) : false;
  }

  getPlaylistEbooks(playlist: Playlist): Ebook[] {
    return this.ebooks.filter(e => playlist.ebookIds.includes(e.id!));
  }

  filterPlaylistSearch() {
    if (!this.playlistSearchQuery.trim()) {
      this.filteredPlaylistEbooks = [...this.ebooks];
      return;
    }

    const query = this.playlistSearchQuery.toLowerCase();
    this.filteredPlaylistEbooks = this.ebooks.filter(e => 
      e.title?.toLowerCase().includes(query) ||
      e.description?.toLowerCase().includes(query) ||
      e.category?.toLowerCase().includes(query) ||
      e.level?.toLowerCase().includes(query)
    );
  }

  // Carousel drag-to-scroll functionality
  private carouselDragging = false;
  private carouselStartX = 0;
  private carouselScrollLeft = 0;

  onCarouselDragStart(event: MouseEvent, element: HTMLElement) {
    this.carouselDragging = true;
    this.carouselStartX = event.pageX - element.offsetLeft;
    this.carouselScrollLeft = element.scrollLeft;
    element.style.cursor = 'grabbing';
    element.style.animationPlayState = 'paused';
  }

  onCarouselDragMove(event: MouseEvent, element: HTMLElement) {
    if (!this.carouselDragging) return;
    event.preventDefault();
    const x = event.pageX - element.offsetLeft;
    const walk = (x - this.carouselStartX) * 2; // Scroll speed multiplier
    element.scrollLeft = this.carouselScrollLeft - walk;
  }

  onCarouselDragEnd(element: HTMLElement) {
    this.carouselDragging = false;
    element.style.cursor = 'grab';
    element.style.animationPlayState = 'running';
  }

  // Carousel navigation with smooth transitions
  currentCarouselIndex = 0;
  carouselTransform = 0;
  private carouselInterval: any;

  ngOnDestroy() {
    if (this.carouselInterval) {
      clearInterval(this.carouselInterval);
    }
  }

  startCarouselAutoPlay() {
    // Auto-advance every 5 seconds
    this.carouselInterval = setInterval(() => {
      this.nextCarousel();
    }, 5000);
  }

  nextCarousel() {
    const totalBooks = this.getRecentlyAdded().length;
    if (totalBooks === 0) return;

    this.currentCarouselIndex = (this.currentCarouselIndex + 1) % totalBooks;
    this.updateCarouselTransform();
  }

  previousCarousel() {
    const totalBooks = this.getRecentlyAdded().length;
    if (totalBooks === 0) return;

    this.currentCarouselIndex = (this.currentCarouselIndex - 1 + totalBooks) % totalBooks;
    this.updateCarouselTransform();
  }

  goToCarouselSlide(index: number) {
    this.currentCarouselIndex = index;
    this.updateCarouselTransform();
  }

  updateCarouselTransform() {
    // Each book is 11rem (176px) + 1rem gap (16px) = 192px
    // Calculate percentage based on current index
    const bookWidth = 192; // 11rem + 1rem gap in pixels (approximate)
    const containerWidth = bookWidth * this.getRecentlyAdded().length * 2; // Total width of all books (2 sets)
    const offset = this.currentCarouselIndex * bookWidth;
    this.carouselTransform = -(offset / containerWidth) * 100;
  }

  // Approve ebook (Academic Affairs only)
  approveEbook(ebook: Ebook, event: Event) {
    event.stopPropagation();
    if (!confirm(`Approve "${ebook.title}"?`)) return;

    this.ebookService.approveEbook(ebook.id!).subscribe({
      next: () => {
        alert('✅ Ebook approved successfully!');
        this.loadEbooks();
        if (this.selectedEbookForDetails?.id === ebook.id) {
          this.closeDetailsModal();
        }
      },
      error: (error) => {
        console.error('Error approving ebook:', error);
        alert('Failed to approve ebook. Please try again.');
      }
    });
  }

  // Reject ebook (Academic Affairs only)
  rejectEbook(ebook: Ebook, event: Event) {
    event.stopPropagation();
    const reason = prompt(`Reject "${ebook.title}"?\n\nOptional: Enter rejection reason:`);
    if (reason === null) return; // User cancelled

    this.ebookService.rejectEbook(ebook.id!, reason || undefined).subscribe({
      next: () => {
        alert('❌ Ebook rejected.');
        this.loadEbooks();
        if (this.selectedEbookForDetails?.id === ebook.id) {
          this.closeDetailsModal();
        }
      },
      error: (error) => {
        console.error('Error rejecting ebook:', error);
        alert('Failed to reject ebook. Please try again.');
      }
    });
  }
}
