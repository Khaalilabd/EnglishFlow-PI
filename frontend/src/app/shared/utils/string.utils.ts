/**
 * String utility functions to avoid code duplication
 */

/**
 * Safely parse integer with radix
 */
export function parseIntSafe(value: string | null | undefined, defaultValue: number = 0): number {
  if (!value) return defaultValue;
  const parsed = Number.parseInt(value, 10);
  return Number.isNaN(parsed) ? defaultValue : parsed;
}

/**
 * Safely parse float
 */
export function parseFloatSafe(value: string | null | undefined, defaultValue: number = 0): number {
  if (!value) return defaultValue;
  const parsed = Number.parseFloat(value);
  return Number.isNaN(parsed) ? defaultValue : parsed;
}

/**
 * Replace all occurrences using regex
 */
export function replaceAllRegex(str: string, pattern: RegExp, replacement: string): string {
  return str.replaceAll(pattern, replacement);
}

/**
 * Remove non-numeric characters
 */
export function removeNonNumeric(str: string): string {
  return str.replaceAll(/\D/g, '');
}

/**
 * Clean phone number (remove spaces, dashes, parentheses)
 */
export function cleanPhoneNumber(phone: string): string {
  return phone.replaceAll(/[\s\-()]/g, '');
}

/**
 * Get character code point at index
 */
export function getCodePoint(str: string, index: number): number {
  return str.codePointAt(index) ?? 0;
}

/**
 * Generate hash from string for color generation
 */
export function stringToHash(str: string): number {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = getCodePoint(str, i) + ((hash << 5) - hash);
  }
  return Math.abs(hash);
}
