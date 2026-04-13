import { Component, Input, OnChanges, SimpleChanges, ElementRef, ViewChild, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
  selector: 'app-highlighted-text',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div #textContainer 
         class="highlighted-text-container"
         [innerHTML]="processedHtml"></div>
  `,
  styles: [`
    .highlighted-text-container {
      position: relative;
    }

    .highlighted-text-container ::ng-deep .word-highlight {
      display: inline;
      transition: all 0.3s ease;
    }

    .highlighted-text-container ::ng-deep .word-highlight.active {
      background-color: #FDE047;
      font-weight: bold;
      padding: 2px 4px;
      border-radius: 4px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
  `]
})
export class HighlightedTextComponent implements OnChanges, AfterViewInit {
  @Input() htmlContent: string = '';
  @Input() isActive: boolean = false;
  
  private _highlightIndex: number = -1;
  @Input() 
  set highlightIndex(value: number) {
    console.log('Setter called - new highlightIndex:', value);
    this._highlightIndex = value;
    // Update highlight immediately when value changes
    if (this.wordElements.length > 0) {
      console.log('Calling updateHighlight from setter');
      this.updateHighlight();
      // Force change detection
      this.cdr.detectChanges();
    } else {
      console.log('No word elements yet, skipping update');
    }
  }
  get highlightIndex(): number {
    return this._highlightIndex;
  }
  
  @ViewChild('textContainer', { static: false }) textContainer?: ElementRef;
  
  processedHtml: SafeHtml = '';
  private words: string[] = [];
  private wordElements: HTMLElement[] = [];

  constructor(
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef
  ) {}

  ngAfterViewInit(): void {
    if (this.isActive) {
      setTimeout(() => {
        this.wrapWords();
      }, 300);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log('HighlightedTextComponent ngOnChanges called', {
      hasHtmlContent: !!changes['htmlContent'],
      hasIsActive: !!changes['isActive'],
      isActive: this.isActive,
      htmlContentLength: this.htmlContent?.length || 0
    });
    
    if (changes['htmlContent']) {
      console.log('HTML content changed, processing...');
      this.processContent();
    }

    if (changes['isActive'] && this.isActive) {
      console.log('Component activated, reprocessing content...');
      // When activated, reprocess content with wrapping
      setTimeout(() => {
        this.processContent();
      }, 100);
    }
  }

  private processContent(): void {
    if (!this.htmlContent) {
      this.processedHtml = '';
      return;
    }

    if (this.isActive) {
      console.log('Processing content for highlighting...');
      console.log('HTML content length:', this.htmlContent.length);
      
      // Extract text and wrap words
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = this.htmlContent;
      const textContent = tempDiv.textContent || tempDiv.innerText || '';
      
      this.words = textContent.split(/\s+/).filter(w => w.trim().length > 0);
      console.log(`Extracted ${this.words.length} words from content`);
      
      // Create HTML with wrapped words
      const wrappedHtml = this.words.map((word, index) => 
        `<span class="word-highlight" data-word-index="${index}">${this.escapeHtml(word)}</span>`
      ).join(' ');
      
      this.processedHtml = this.sanitizer.bypassSecurityTrustHtml(wrappedHtml);
      
      // Wait for DOM to update, then get word elements
      setTimeout(() => {
        this.wrapWords();
      }, 200);
    } else {
      // Not active, just show the HTML as-is
      this.processedHtml = this.sanitizer.bypassSecurityTrustHtml(this.htmlContent);
      this.wordElements = [];
    }
  }

  private wrapWords(): void {
    if (!this.textContainer) return;
    
    const container = this.textContainer.nativeElement;
    this.wordElements = Array.from(container.querySelectorAll('.word-highlight'));
    
    console.log(`Highlighted text component: wrapped ${this.wordElements.length} words`);
  }

  private updateHighlight(): void {
    console.log('updateHighlight called - index:', this._highlightIndex, 'total elements:', this.wordElements.length);
    
    // Remove all highlights
    this.wordElements.forEach(el => {
      el.classList.remove('active');
    });

    // Add highlight to current word
    if (this._highlightIndex >= 0 && this._highlightIndex < this.wordElements.length) {
      const element = this.wordElements[this._highlightIndex];
      element.classList.add('active');
      console.log('Highlighted word at index:', this._highlightIndex);
      
      // Scroll into view
      element.scrollIntoView({
        behavior: 'smooth',
        block: 'center',
        inline: 'nearest'
      });
    }
  }

  private escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }
}
