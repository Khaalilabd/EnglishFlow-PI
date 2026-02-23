import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EbookService } from '../../../core/services/ebook.service';
import { AuthService } from '../../../core/services/auth.service';
import { ReviewService } from '../../../core/services/review.service';
import { ReadingProgressService } from '../../../core/services/reading-progress.service';
import { CollectionService } from '../../../core/services/collection.service';
import { Ebook, Review, ReadingProgress, Collection } from '../../../core/models/ebook.model';
import { ReviewModalComponent } from './components/review-modal.component';

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

@Component({
  selector: 'app-ebooks',
  standalone: true,
  imports: [CommonModule, FormsModule, ReviewModalComponent],
  templateUrl: './ebooks.component.html',
  styleUrls: ['./ebooks.component.scss']
})
export class EbooksComponent implements OnInit {
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
  sortBy: 'newest' | 'popular' | 'rating' | 'title' = 'newest';
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
  viewMode: 'published' | 'scheduled' = 'published';
  showFilters: boolean = false;
  
  // Details modal
  showDetailsModal = false;
  selectedEbookForDetails: Ebook | null = null;
  
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

  constructor(
    private ebookService: EbookService, 
    private authService: AuthService,
    private reviewService: ReviewService,
    private progressService: ReadingProgressService,
    private collectionService: CollectionService
  ) {}

  ngOnInit() {
    this.loadEbooks();
    this.loadUserProgress();
    this.loadUserCollections();
  }

  // Check if current user is a tutor
  isTutor(): boolean {
    return this.authService.hasRole(['TUTOR', 'ADMIN']);
  }

  loadEbooks() {
    this.isLoading = true;
    this.ebookService.getAllEbooks().subscribe({
      next: (data) => {
        // Show all ebooks for students (no draft/published concept for ebooks)
        this.ebooks = data;
        this.filteredEbooks = data;
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
      case 'title':
        return sorted.sort((a, b) => (a.title || '').localeCompare(b.title || ''));
      default:
        return sorted;
    }
  }

  // Favorites
  toggleFavorite(ebookId: number, event: Event) {
    event.stopPropagation();
    if (this.favorites.has(ebookId)) {
      this.favorites.delete(ebookId);
    } else {
      this.favorites.add(ebookId);
    }
    // TODO: Save to backend/localStorage
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
    this.newReview = { rating: 5, comment: '' };
    this.showReviewModal = true;
    
    // Clear previous reviews to avoid showing wrong data
    this.ebookReviews.delete(ebook.id!);
    
    // Load fresh reviews for this specific ebook
    this.loadEbookReviews(ebook.id!);
  }

  // NEW: Close review modal
  closeReviewModal() {
    this.showReviewModal = false;
    this.selectedEbookForReview = null;
    this.newReview = { rating: 5, comment: '' };
  }

  // NEW: Submit review
  submitReview() {
    if (!this.selectedEbookForReview?.id) return;

    this.reviewService.createReview({
      ebookId: this.selectedEbookForReview.id,
      rating: this.newReview.rating,
      comment: this.newReview.comment
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

  // NEW: Get reviews for display
  getReviews(ebookId: number): Review[] {
    return this.ebookReviews.get(ebookId) || [];
  }

  // NEW: Set rating (for star click)
  setRating(rating: number) {
    this.newReview.rating = rating;
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
      .slice(0, 6);
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

  setViewMode(mode: 'published' | 'scheduled') {
    this.viewMode = mode;
    this.applyFilters();
  }

  getFilteredEbooks(): any[] {
    let filtered = this.filteredEbooks;
    
    // Filter by view mode - only show published and scheduled to students
    if (this.viewMode === 'published') {
      filtered = filtered.filter(e => this.isPublished(e));
    } else if (this.viewMode === 'scheduled') {
      filtered = filtered.filter(e => this.isScheduled(e));
    }
    
    return filtered;
  }

  getEbookCountByMode(mode: string): number {
    if (mode === 'published') {
      return this.ebooks.filter(e => this.isPublished(e)).length;
    } else if (mode === 'scheduled') {
      return this.ebooks.filter(e => this.isScheduled(e)).length;
    }
    return this.ebooks.length;
  }

  isPublished(ebook: Ebook): boolean {
    const metadata = this.getMetadata(ebook.description);
    if (metadata && metadata.release === 'scheduled' && metadata.scheduledDate) {
      const scheduledDateTime = new Date(metadata.scheduledDate);
      return scheduledDateTime <= new Date();
    }
    // If no metadata or release is 'immediate', consider it published
    return !metadata || metadata.release === 'immediate' || !metadata.release;
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
    this.showDetailsModal = true;
  }

  closeDetailsModal() {
    this.showDetailsModal = false;
    this.selectedEbookForDetails = null;
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
      this.coverImagePreview = metadata.coverImageUrl || null;
    }
    
    // Clean description
    this.newEbook.description = this.getCleanDescription(ebook.description);
    
    // Mark that we're editing (file already exists)
    // We'll show the existing file name in the UI
    this.selectedFile = null; // Don't set a fake file
    
    // Open modal in edit mode
    this.showUploadModal = true;
    this.wizardStep = 1;
    this.closeDetailsModal();
  }

  getCoverImageUrl(ebook: Ebook): string | null {
    // Return the API endpoint for the cover image if it exists
    if (ebook.id && ebook.coverImageUrl) {
      return `http://localhost:8083/api/ebooks/${ebook.id}/cover`;
    }
    return null;
  }

  handleImageError(event: Event): void {
    const target = event.target as HTMLImageElement;
    if (target) {
      target.style.display = 'none';
    }
  }



  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file && file.type === 'application/pdf') {
      this.selectedFile = file;
      this.extractPdfMetadata(file);
      this.generatePreview(file);
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
  generatePreview(file: File) {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.pdfPreviewUrl = e.target.result;
    };
    reader.readAsDataURL(file);
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
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.coverImagePreview = e.target.result;
      };
      reader.readAsDataURL(file);
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
    if (!this.selectedFile && this.chapters.length === 0) {
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

    // Store cover image (limit to 500 chars to avoid DB issues)
    let coverImageUrl = '';
    if (this.coverImagePreview) {
      // Store a compressed version or just a flag
      coverImageUrl = this.coverImagePreview.substring(0, 500);
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
      hasCoverImage: !!coverImageUrl,
      coverImageUrl: coverImageUrl
    };
    
    // Store ONLY user description, keep metadata separate
    const originalDescription = this.newEbook.description || '';
    
    // Temporarily store metadata in description for backend (limit total length)
    const metadataStr = JSON.stringify(metadata);
    const maxDescLength = 1500; // Leave room for metadata
    const truncatedDesc = originalDescription.substring(0, maxDescLength);
    this.newEbook.description = truncatedDesc + '\n\n__METADATA__\n' + metadataStr;
    this.newEbook.free = this.pricingModel === 'free';

    this.isLoading = true;
    
    // Check if editing or creating
    if (this.newEbook.id) {
      // Update existing ebook - file and cover image are optional
      this.ebookService.updateEbook(this.newEbook.id, this.newEbook, this.selectedFile || undefined, this.coverImageFile || undefined).subscribe({
        next: () => {
          this.loadEbooks();
          this.closeUploadModal();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error updating ebook:', error);
          alert('Error updating ebook: ' + (error.error?.message || error.message || 'Unknown error'));
          this.isLoading = false;
        }
      });
    } else {
      // Create new ebook - file is required, cover image is optional
      this.ebookService.createEbook(this.newEbook, this.selectedFile!, this.coverImageFile || undefined).subscribe({
        next: () => {
          this.loadEbooks();
          this.closeUploadModal();
          this.isLoading = false;
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
      this.ebookService.trackAccess(ebook.id, 1).subscribe();
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
}
