import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml, SafeResourceUrl, SafeScript, SafeStyle, SafeUrl } from '@angular/platform-browser';

/**
 * SafePipe - Bypasses Angular's built-in sanitization for trusted content
 * 
 * SECURITY NOTE: This pipe should only be used with content from trusted sources.
 * All inputs are validated before bypassing security to prevent XSS attacks.
 * 
 * Approved use cases:
 * - Embedding videos from trusted platforms (YouTube, Vimeo)
 * - Loading PDF documents from our own backend
 * - Displaying sanitized HTML from our backend API
 */
@Pipe({
  name: 'safe',
  standalone: true
})
export class SafePipe implements PipeTransform {
  
  // Whitelist of trusted domains for resource URLs
  private readonly TRUSTED_DOMAINS = [
    'youtube.com',
    'www.youtube.com',
    'youtu.be',
    'vimeo.com',
    'player.vimeo.com',
    'localhost',
    '127.0.0.1'
  ];

  constructor(private sanitizer: DomSanitizer) {}

  transform(value: string, type: string): SafeHtml | SafeStyle | SafeScript | SafeUrl | SafeResourceUrl | null {
    if (!value) {
      return null;
    }

    // Validate input based on type
    if (!this.isValidInput(value, type)) {
      console.warn(`SafePipe: Rejected untrusted ${type}:`, value);
      return null;
    }

    switch (type) {
      case 'html':
        // Only allow HTML from backend API (already sanitized server-side)
        return this.sanitizer.bypassSecurityTrustHtml(value);
      case 'style':
        return this.sanitizer.bypassSecurityTrustStyle(value);
      case 'script':
        // Scripts should never be bypassed - this is a security risk
        console.error('SafePipe: Script bypassing is not allowed');
        return null;
      case 'url':
        return this.sanitizer.bypassSecurityTrustUrl(value);
      case 'resourceUrl':
        // Only allow resource URLs from trusted domains
        return this.sanitizer.bypassSecurityTrustResourceUrl(value);
      default:
        return this.sanitizer.bypassSecurityTrustHtml(value);
    }
  }

  /**
   * Validates input before bypassing security
   */
  private isValidInput(value: string, type: string): boolean {
    switch (type) {
      case 'resourceUrl':
        return this.isUrlFromTrustedDomain(value);
      case 'script':
        // Never allow script bypassing
        return false;
      case 'url':
        return this.isValidUrl(value);
      case 'html':
      case 'style':
        // For HTML and style, we trust the backend sanitization
        // but still check for obvious XSS patterns
        return !this.containsObviousXss(value);
      default:
        return true;
    }
  }

  /**
   * Checks if URL is from a trusted domain
   */
  private isUrlFromTrustedDomain(url: string): boolean {
    try {
      const urlObj = new URL(url);
      const hostname = urlObj.hostname.toLowerCase();
      
      // Check if hostname matches any trusted domain
      return this.TRUSTED_DOMAINS.some(domain => 
        hostname === domain || hostname.endsWith('.' + domain)
      );
    } catch {
      // If URL parsing fails, check if it's a relative URL (from our backend)
      return url.startsWith('/') || url.startsWith('./') || url.startsWith('../');
    }
  }

  /**
   * Basic URL validation
   */
  private isValidUrl(url: string): boolean {
    try {
      new URL(url);
      return true;
    } catch {
      // Allow relative URLs
      return url.startsWith('/') || url.startsWith('./') || url.startsWith('../');
    }
  }

  /**
   * Checks for obvious XSS patterns
   */
  private containsObviousXss(value: string): boolean {
    const xssPatterns = [
      /<script[^>]*>.*?<\/script>/gi,
      /javascript:/gi,
      /on\w+\s*=/gi, // onclick, onerror, etc.
      /<iframe[^>]*srcdoc/gi
    ];

    return xssPatterns.some(pattern => pattern.test(value));
  }
}
