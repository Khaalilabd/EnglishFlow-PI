import { Component, OnInit, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { EbookService } from '../../../core/services/ebook.service';
import { Ebook } from '../../../core/models/ebook.model';

declare var pdfjsLib: any;

@Component({
  selector: 'app-ebook-reader',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ebook-reader.component.html',
  styleUrls: ['./ebook-reader.component.scss']
})
export class EbookReaderComponent implements OnInit, AfterViewInit {
  @ViewChild('flipbook', { static: false }) flipbookElement!: ElementRef;
  
  ebook: Ebook | null = null;
  pdfUrl: string | null = null;
  pdfBlob: Blob | null = null;
  isLoading = true;
  currentPage = 1;
  totalPages = 0;
  pages: string[] = [];
  isDragging = false;
  isFlipping = false;
  flipDirection: 'forward' | 'backward' = 'forward';
  flipProgress = 0;
  sliceCount = 25; // Number of vertical slices for curve effect
  mouseX = 0;
  mouseY = 0;
  isHovering = false;
  showBookmarksPanel = false;
  bookmarks: number[] = [];
  
  // Zoom and view controls
  zoomLevel = 1;
  fitMode: 'width' | 'page' | 'auto' = 'auto';
  isAutoPlay = false;
  autoPlayInterval: any = null;

  constructor(
    private route: ActivatedRoute,
    private ebookService: EbookService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    const ebookId = this.route.snapshot.paramMap.get('id');
    if (ebookId) {
      this.ebook = { id: parseInt(ebookId) } as Ebook; // Set ebook ID early for localStorage
      this.loadBookmarks(); // Load bookmarks before loading ebook
      this.loadEbook(parseInt(ebookId));
    }
  }

  ngAfterViewInit() {
    // Keyboard navigation
    document.addEventListener('keydown', (e) => {
      if (e.key === 'ArrowRight') this.nextPage();
      if (e.key === 'ArrowLeft') this.previousPage();
      if (e.key === 'Home') this.goToFirstPage();
      if (e.key === 'End') this.goToLastPage();
    });
  }

  loadEbook(id: number) {
    this.isLoading = true;
    this.ebookService.getEbookById(id).subscribe({
      next: (ebook) => {
        this.ebook = ebook;
        this.loadPdf(id);
      },
      error: (error) => {
        console.error('Error loading ebook:', error);
        this.isLoading = false;
        alert('Failed to load ebook');
        window.close();
      }
    });
  }

  loadPdf(id: number) {
    this.ebookService.downloadEbook(id).subscribe({
      next: (blob) => {
        this.pdfBlob = blob;
        const url = URL.createObjectURL(blob);
        this.pdfUrl = url;
        this.renderPdfPages(url);
      },
      error: (error) => {
        console.error('Error loading PDF:', error);
        this.isLoading = false;
        alert('Failed to load PDF');
        window.close();
      }
    });
  }

  async renderPdfPages(url: string) {
    try {
      const script = document.createElement('script');
      script.src = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.min.js';
      document.head.appendChild(script);

      script.onload = async () => {
        pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.worker.min.js';
        
        const loadingTask = pdfjsLib.getDocument(url);
        const pdf = await loadingTask.promise;
        this.totalPages = pdf.numPages;

        for (let pageNum = 1; pageNum <= pdf.numPages; pageNum++) {
          const page = await pdf.getPage(pageNum);
          const scale = 2.5;
          const viewport = page.getViewport({ scale });

          const canvas = document.createElement('canvas');
          const context = canvas.getContext('2d');
          canvas.height = viewport.height;
          canvas.width = viewport.width;

          await page.render({
            canvasContext: context,
            viewport: viewport
          }).promise;

          this.pages.push(canvas.toDataURL());
        }

        this.isLoading = false;
      };
    } catch (error) {
      console.error('Error rendering PDF:', error);
      this.isLoading = false;
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages && !this.isFlipping) {
      this.flipDirection = 'forward';
      this.isFlipping = true;
      this.animateFlip();
    }
  }

  previousPage() {
    if (this.currentPage > 1 && !this.isFlipping) {
      this.flipDirection = 'backward';
      this.isFlipping = true;
      this.animateFlip();
    }
  }

  animateFlip() {
    const duration = 800;
    const startTime = Date.now();
    
    const animate = () => {
      const elapsed = Date.now() - startTime;
      this.flipProgress = Math.min(elapsed / duration, 1);
      
      if (this.flipProgress < 1) {
        requestAnimationFrame(animate);
      } else {
        // Animation complete
        if (this.flipDirection === 'forward') {
          this.currentPage += 2;
        } else {
          this.currentPage -= 2;
          if (this.currentPage < 1) this.currentPage = 1;
        }
        this.isFlipping = false;
        this.flipProgress = 0;
      }
    };
    
    requestAnimationFrame(animate);
  }

  getSliceTransform(sliceIndex: number): string {
    if (!this.isFlipping) return 'translateZ(0px) rotateY(0deg)';
    
    const progress = this.flipProgress;
    const maxRotation = 180;
    
    // Easing function for smooth animation
    const easeInOutCubic = (t: number) => t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
    const easedProgress = easeInOutCubic(progress);
    
    // Each slice rotates progressively more from spine to edge
    const sliceProgress = (sliceIndex / this.sliceCount);
    const rotation = easedProgress * maxRotation * (0.3 + sliceProgress * 0.7);
    
    // Calculate elevation (Z-axis) - creates cylindrical arc
    // Elevation peaks at 90 degrees rotation
    const angleInRadians = (rotation * Math.PI) / 180;
    const maxElevation = 80; // Maximum height the page lifts
    const elevation = Math.sin(angleInRadians) * maxElevation * sliceProgress;
    
    const direction = this.flipDirection === 'forward' ? -1 : 1;
    
    return `translateZ(${elevation}px) rotateY(${direction * rotation}deg)`;
  }

  getSliceBrightness(sliceIndex: number): string {
    if (!this.isFlipping) return 'brightness(100%)';
    
    const progress = this.flipProgress;
    const sliceProgress = sliceIndex / this.sliceCount;
    
    // Darken slices based on their angle during flip
    const angle = progress * 180 * (0.3 + sliceProgress * 0.7);
    const brightness = 100 - (Math.abs(Math.sin(angle * Math.PI / 180)) * 30);
    
    return `brightness(${brightness}%)`;
  }

  getSliceArray(): number[] {
    return Array.from({ length: this.sliceCount }, (_, i) => i);
  }

  goToFirstPage() {
    if (!this.isFlipping) {
      this.currentPage = 1;
    }
  }

  goToLastPage() {
    if (!this.isFlipping) {
      this.currentPage = this.totalPages % 2 === 0 ? this.totalPages - 1 : this.totalPages;
    }
  }

  onMouseDown(event: MouseEvent) {
    this.isDragging = true;
  }

  onMouseMove(event: MouseEvent) {
    const bookElement = document.querySelector('.book-pages');
    if (bookElement) {
      const rect = bookElement.getBoundingClientRect();
      this.mouseX = event.clientX - rect.left;
      this.mouseY = event.clientY - rect.top;
      this.isHovering = true;
    }
  }

  onMouseLeave() {
    this.isHovering = false;
  }

  onMouseUp(event: MouseEvent) {
    if (this.isDragging && !this.isFlipping) {
      const bookElement = document.querySelector('.book-pages');
      if (bookElement) {
        const rect = bookElement.getBoundingClientRect();
        const x = event.clientX - rect.left;
        const width = rect.width;

        // Determine flip direction based on click position
        if (x > width / 2) {
          this.nextPage();
        } else {
          this.previousPage();
        }
      }
    }
    this.isDragging = false;
  }

  goToPage() {
    if (this.currentPage < 1) {
      this.currentPage = 1;
    } else if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }
    if (this.currentPage % 2 === 0 && this.currentPage > 1) {
      this.currentPage--;
    }
  }

  // Zoom controls
  zoomIn() {
    if (this.zoomLevel < 3) {
      this.zoomLevel += 0.25;
      this.fitMode = 'auto';
    }
  }

  zoomOut() {
    if (this.zoomLevel > 0.5) {
      this.zoomLevel -= 0.25;
      this.fitMode = 'auto';
    }
  }

  fitWidth() {
    this.fitMode = 'width';
    this.zoomLevel = 1;
  }

  fitPage() {
    this.fitMode = 'page';
    this.zoomLevel = 1;
  }

  autoFit() {
    this.fitMode = 'auto';
    this.zoomLevel = 1;
  }

  // Auto play
  toggleAutoPlay() {
    this.isAutoPlay = !this.isAutoPlay;
    if (this.isAutoPlay) {
      this.autoPlayInterval = setInterval(() => {
        if (this.currentPage < this.totalPages) {
          this.nextPage();
        } else {
          this.stopAutoPlay();
        }
      }, 3000);
    } else {
      this.stopAutoPlay();
    }
  }

  stopAutoPlay() {
    this.isAutoPlay = false;
    if (this.autoPlayInterval) {
      clearInterval(this.autoPlayInterval);
      this.autoPlayInterval = null;
    }
  }

  // Download
  downloadPdf() {
    if (this.pdfBlob && this.ebook) {
      const url = window.URL.createObjectURL(this.pdfBlob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${this.ebook.title}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    }
  }

  // Print
  printPdf() {
    if (this.pdfUrl) {
      const printWindow = window.open(this.pdfUrl, '_blank');
      if (printWindow) {
        printWindow.onload = () => {
          printWindow.print();
        };
      }
    }
  }

  // Bookmark functions
  bookmarkPage() {
    if (!this.bookmarks.includes(this.currentPage)) {
      this.bookmarks.push(this.currentPage);
      this.bookmarks.sort((a, b) => a - b);
      this.saveBookmarks();
      alert(`Bookmarked page ${this.currentPage}`);
    } else {
      alert(`Page ${this.currentPage} is already bookmarked`);
    }
  }

  removeBookmark(page: number) {
    this.bookmarks = this.bookmarks.filter(p => p !== page);
    this.saveBookmarks();
  }

  goToBookmark(page: number) {
    this.currentPage = page % 2 === 0 ? page - 1 : page;
    this.showBookmarksPanel = false;
  }

  toggleBookmarksPanel() {
    this.showBookmarksPanel = !this.showBookmarksPanel;
  }

  loadBookmarks() {
    if (this.ebook?.id) {
      const saved = localStorage.getItem(`bookmarks_${this.ebook.id}`);
      if (saved) {
        try {
          this.bookmarks = JSON.parse(saved);
          console.log('Loaded bookmarks:', this.bookmarks);
        } catch (e) {
          console.error('Error loading bookmarks:', e);
          this.bookmarks = [];
        }
      }
    }
  }

  saveBookmarks() {
    if (this.ebook?.id) {
      localStorage.setItem(`bookmarks_${this.ebook.id}`, JSON.stringify(this.bookmarks));
      console.log('Saved bookmarks:', this.bookmarks);
    }
  }

  loadBookmark() {
    const bookmark = localStorage.getItem(`bookmark_${this.ebook?.id}`);
    if (bookmark) {
      this.currentPage = parseInt(bookmark);
      this.goToPage();
    }
  }

  ngOnDestroy() {
    this.stopAutoPlay();
    if (this.pdfUrl) {
      URL.revokeObjectURL(this.pdfUrl);
    }
  }
}
