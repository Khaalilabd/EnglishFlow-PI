import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DictionaryEntry {
  word: string;
  phonetic?: string;
  phonetics: Phonetic[];
  meanings: Meaning[];
  sourceUrl?: string;
}

export interface Phonetic {
  text?: string;
  audio?: string;
}

export interface Meaning {
  partOfSpeech: string;
  definitions: Definition[];
  synonyms?: string[];
  antonyms?: string[];
}

export interface Definition {
  definition: string;
  example?: string;
  synonyms?: string[];
  antonyms?: string[];
}

export interface EnrichedDictionaryEntry {
  basicData: DictionaryEntry[];
  context?: string;
  cefrLevel?: string;
  wordType?: string;
  similarWords?: string[];
  commonConfusions?: string[];
  imageUrl?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DictionaryService {
  private apiUrl = `${environment.apiUrl}/community/dictionary`;

  constructor(private http: HttpClient) {}

  lookupWord(word: string): Observable<DictionaryEntry[]> {
    return this.http.get<DictionaryEntry[]>(`${this.apiUrl}/${word}`);
  }

  lookupWordEnriched(word: string, context?: string): Observable<EnrichedDictionaryEntry> {
    let params = new HttpParams();
    if (context) {
      params = params.set('context', context);
    }
    return this.http.get<EnrichedDictionaryEntry>(`${this.apiUrl}/enriched/${word}`, { params });
  }
}
