import { Directive, HostListener, Output, EventEmitter } from '@angular/core';

export interface WordSelection {
  word: string;
  context: string;
}

@Directive({
  selector: '[appWordLookup]',
  standalone: true
})
export class WordLookupDirective {
  @Output() wordSelected = new EventEmitter<WordSelection>();

  @HostListener('dblclick', ['$event'])
  onDoubleClick(event: MouseEvent) {
    const selection = window.getSelection();
    if (selection && selection.toString().trim()) {
      let word = selection.toString().trim();
      
      // Get the context (sentence containing the word)
      const context = this.extractContext(selection);
      
      // Remove all punctuation, special characters, and markdown symbols
      word = word.replaceAll(/[\*\_\~\`\[\]\(\)\{\}\#\+\-\=\|\\\:\;\"\'\<\>\,\.\?\/\!\@\$\%\^\&]/g, '');
      
      // Remove numbers
      word = word.replaceAll(/[0-9]/g, '');
      
      // Get only the first word if multiple words selected
      word = word.split(/\s+/)[0];
      
      // Convert to lowercase for API
      word = word.toLowerCase();
      
      // Only emit if we have a valid word (at least 2 characters)
      if (word && word.length >= 2) {
        this.wordSelected.emit({ word, context });
      }
    }
  }

  private extractContext(selection: Selection): string {
    try {
      const range = selection.getRangeAt(0);
      const container = range.commonAncestorContainer;
      
      // Get the text content of the parent element
      let fullText = '';
      if (container.nodeType === Node.TEXT_NODE && container.parentElement) {
        fullText = container.parentElement.textContent || '';
      } else if (container.nodeType === Node.ELEMENT_NODE) {
        fullText = (container as Element).textContent || '';
      }
      
      // Find the sentence containing the selected word
      const selectedText = selection.toString().trim();
      const sentences = fullText.split(/[.!?]+/);
      
      for (const sentence of sentences) {
        if (sentence.includes(selectedText)) {
          return sentence.trim();
        }
      }
      
      // Fallback: return a portion of text around the selection
      const selectedIndex = fullText.indexOf(selectedText);
      if (selectedIndex !== -1) {
        const start = Math.max(0, selectedIndex - 50);
        const end = Math.min(fullText.length, selectedIndex + selectedText.length + 50);
        return '...' + fullText.substring(start, end).trim() + '...';
      }
      
      return '';
    } catch (error) {
      console.error('Error extracting context:', error);
      return '';
    }
  }
}
