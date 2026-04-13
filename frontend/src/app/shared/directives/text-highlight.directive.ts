import { Directive, ElementRef, Input, OnChanges, SimpleChanges, Renderer2, AfterViewInit } from '@angular/core';

@Directive({
  selector: '[appTextHighlight]',
  standalone: true
})
export class TextHighlightDirective implements OnChanges, AfterViewInit {
  @Input() appTextHighlight: number = -1; // Word index to highlight
  @Input() highlightColor: string = '#FDE047'; // Yellow highlight
  @Input() triggerInit: boolean = false; // Trigger to reinitialize
  
  private words: HTMLElement[] = [];
  private originalContent: string = '';
  private isInitialized = false;

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  ngAfterViewInit(): void {
    // Wait a bit for content to be rendered
    setTimeout(() => {
      this.wrapWordsInSpans();
      this.isInitialized = true;
    }, 100);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['triggerInit'] && changes['triggerInit'].currentValue) {
      this.isInitialized = false;
      this.words = [];
      setTimeout(() => {
        this.wrapWordsInSpans();
        this.isInitialized = true;
      }, 200);
    }
    
    if (changes['appTextHighlight'] && this.isInitialized) {
      this.highlightWord(this.appTextHighlight);
    }
  }

  private wrapWordsInSpans(): void {
    const element = this.el.nativeElement;
    
    // Get all text nodes recursively
    const textContent = this.getTextContent(element);
    
    if (!textContent.trim()) {
      return;
    }
    
    this.originalContent = element.innerHTML;
    
    // Split into words
    const wordArray = textContent.split(/\s+/).filter((w: string) => w.trim().length > 0);
    
    if (wordArray.length === 0) {
      return;
    }
    
    // Clear element
    element.innerHTML = '';
    
    // Create span for each word
    wordArray.forEach((word: string, index: number) => {
      const span = this.renderer.createElement('span');
      this.renderer.setProperty(span, 'textContent', word);
      this.renderer.setAttribute(span, 'data-word-index', index.toString());
      this.renderer.setStyle(span, 'transition', 'all 0.3s ease');
      this.renderer.setStyle(span, 'display', 'inline');
      
      this.renderer.appendChild(element, span);
      this.words.push(span);
      
      // Add space after word (except last word)
      if (index < wordArray.length - 1) {
        const space = this.renderer.createText(' ');
        this.renderer.appendChild(element, space);
      }
    });
    
    console.log(`Wrapped ${this.words.length} words for highlighting`);
  }

  private getTextContent(element: HTMLElement): string {
    let text = '';
    
    const walk = (node: Node) => {
      if (node.nodeType === Node.TEXT_NODE) {
        text += node.textContent || '';
      } else if (node.nodeType === Node.ELEMENT_NODE) {
        const el = node as HTMLElement;
        // Skip script and style tags
        if (el.tagName !== 'SCRIPT' && el.tagName !== 'STYLE') {
          Array.from(node.childNodes).forEach(walk);
        }
      }
    };
    
    walk(element);
    return text;
  }

  private highlightWord(wordIndex: number): void {
    if (!this.isInitialized || this.words.length === 0) {
      return;
    }

    // Remove previous highlights
    this.words.forEach((span: HTMLElement) => {
      this.renderer.setStyle(span, 'backgroundColor', 'transparent');
      this.renderer.setStyle(span, 'fontWeight', 'normal');
      this.renderer.setStyle(span, 'padding', '0');
      this.renderer.setStyle(span, 'borderRadius', '0');
      this.renderer.setStyle(span, 'boxShadow', 'none');
    });

    // Highlight current word
    if (wordIndex >= 0 && wordIndex < this.words.length) {
      const span = this.words[wordIndex];
      this.renderer.setStyle(span, 'backgroundColor', this.highlightColor);
      this.renderer.setStyle(span, 'fontWeight', 'bold');
      this.renderer.setStyle(span, 'padding', '2px 4px');
      this.renderer.setStyle(span, 'borderRadius', '4px');
      this.renderer.setStyle(span, 'boxShadow', '0 2px 4px rgba(0,0,0,0.1)');
      
      // Scroll into view smoothly
      span.scrollIntoView({ 
        behavior: 'smooth', 
        block: 'center',
        inline: 'nearest'
      });
    }
  }

  public reset(): void {
    const element = this.el.nativeElement;
    element.innerHTML = this.originalContent;
    this.words = [];
    this.isInitialized = false;
  }
}
