import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { VocabularyService, VocabularyWord, VocabularyStats } from '../../../services/vocabulary.service';
import Swal from 'sweetalert2';
import jsPDF from 'jspdf';

@Component({
  selector: 'app-my-vocabulary',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './my-vocabulary.component.html',
  styleUrl: './my-vocabulary.component.scss'
})
export class MyVocabularyComponent implements OnInit {
  words: VocabularyWord[] = [];
  stats: VocabularyStats | null = null;
  loading = true;
  searchQuery = '';
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;
  sortBy = 'createdAt';
  
  // New features
  viewMode: 'grid' | 'flashcards' | 'practice' = 'grid';
  filterLevel: string = 'all';
  currentFlashcardIndex = 0;
  showFlashcardAnswer = false;
  practiceMode = false;
  practiceWords: VocabularyWord[] = [];
  currentPracticeIndex = 0;
  practiceScore = 0;
  userAnswer = '';
  showPracticeResult = false;
  practiceResults: { word: VocabularyWord; correct: boolean; userAnswer: string }[] = [];
  
  constructor(private vocabularyService: VocabularyService) {}

  ngOnInit(): void {
    this.loadVocabulary();
    this.loadStats();
  }

  loadVocabulary(): void {
    this.loading = true;
    
    if (this.searchQuery.trim()) {
      this.vocabularyService.searchVocabulary(this.searchQuery, this.currentPage, this.pageSize).subscribe({
        next: (data) => {
          this.words = data.content;
          this.totalPages = data.totalPages;
          this.totalElements = data.totalElements;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error loading vocabulary:', err);
          this.loading = false;
        }
      });
    } else {
      const level = this.filterLevel === 'all' ? undefined : this.filterLevel;
      this.vocabularyService.getUserVocabulary(this.currentPage, this.pageSize, this.sortBy, level).subscribe({
        next: (data) => {
          this.words = data.content;
          this.totalPages = data.totalPages;
          this.totalElements = data.totalElements;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error loading vocabulary:', err);
          this.loading = false;
        }
      });
    }
  }

  loadStats(): void {
    this.vocabularyService.getStats().subscribe({
      next: (stats) => {
        this.stats = stats;
      },
      error: (err) => {
        console.error('Error loading stats:', err);
      }
    });
  }

  onSearch(): void {
    this.currentPage = 0;
    this.loadVocabulary();
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.currentPage = 0;
    this.loadVocabulary();
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadVocabulary();
  }

  markAsReviewed(word: VocabularyWord, showToast: boolean = true): void {
    this.vocabularyService.markAsReviewed(word.id).subscribe({
      next: (updated) => {
        const index = this.words.findIndex(w => w.id === word.id);
        if (index !== -1) {
          this.words[index] = updated;
        }
        this.loadStats();
        
        if (showToast && updated.masteryLevel === 'LEARNING' && word.masteryLevel === 'NEW') {
          // Show notification when word moves from NEW to LEARNING
          Swal.fire({
            icon: 'success',
            title: 'Word Marked as Learning! 📚',
            text: `"${word.word}" is now in your learning list`,
            timer: 5000,
            showConfirmButton: false,
            toast: true,
            position: 'top-end',
            timerProgressBar: true
          });
        }
      },
      error: (err) => {
        console.error('Error marking as reviewed:', err);
      }
    });
  }

  deleteWord(word: VocabularyWord): void {
    Swal.fire({
      title: 'Delete Word?',
      text: `Are you sure you want to remove "${word.word}" from your vocabulary?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#EF4444',
      cancelButtonColor: '#6B7280',
      confirmButtonText: 'Yes, delete it',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.vocabularyService.deleteWord(word.id).subscribe({
          next: () => {
            this.words = this.words.filter(w => w.id !== word.id);
            this.loadStats();
            
            Swal.fire({
              icon: 'success',
              title: 'Deleted!',
              text: `"${word.word}" has been removed`,
              timer: 1500,
              showConfirmButton: false
            });
          },
          error: (err) => {
            console.error('Error deleting word:', err);
            Swal.fire({
              icon: 'error',
              title: 'Error',
              text: 'Failed to delete word'
            });
          }
        });
      }
    });
  }

  playAudio(audioUrl: string): void {
    const audio = new Audio(audioUrl);
    audio.play();
  }

  getMasteryColor(level: string): string {
    switch (level) {
      case 'NEW': return 'bg-gray-100 text-gray-700';
      case 'LEARNING': return 'bg-green-100 text-green-700';
      default: return 'bg-gray-100 text-gray-700';
    }
  }

  getMasteryIcon(level: string): string {
    switch (level) {
      case 'NEW': return 'fa-star';
      case 'LEARNING': return 'fa-book-reader';
      default: return 'fa-star';
    }
  }

  // New methods for enhanced features
  
  switchViewMode(mode: 'grid' | 'flashcards' | 'practice'): void {
    this.viewMode = mode;
    if (mode === 'flashcards') {
      this.currentFlashcardIndex = 0;
      this.showFlashcardAnswer = false;
    } else if (mode === 'practice') {
      this.startPractice();
    }
  }

  filterByLevel(level: string): void {
    this.filterLevel = level;
    this.currentPage = 0;
    this.loadVocabulary();
  }

  // Flashcards mode
  nextFlashcard(): void {
    if (this.currentFlashcardIndex < this.words.length - 1) {
      this.currentFlashcardIndex++;
      this.showFlashcardAnswer = false;
    }
  }

  previousFlashcard(): void {
    if (this.currentFlashcardIndex > 0) {
      this.currentFlashcardIndex--;
      this.showFlashcardAnswer = false;
    }
  }

  flipFlashcard(): void {
    this.showFlashcardAnswer = !this.showFlashcardAnswer;
  }

  // Practice mode
  startPractice(): void {
    if (this.words.length === 0) {
      Swal.fire({
        icon: 'info',
        title: 'No words to practice',
        text: 'Add some words to your vocabulary first!'
      });
      this.viewMode = 'grid';
      return;
    }

    // Select random words for practice (max 10)
    const shuffled = [...this.words].sort(() => 0.5 - Math.random());
    this.practiceWords = shuffled.slice(0, Math.min(10, this.words.length));
    this.currentPracticeIndex = 0;
    this.practiceScore = 0;
    this.userAnswer = '';
    this.showPracticeResult = false;
    this.practiceResults = [];
    this.practiceMode = true;
  }

  submitPracticeAnswer(): void {
    const currentWord = this.practiceWords[this.currentPracticeIndex];
    const correct = this.userAnswer.toLowerCase().trim() === currentWord.word.toLowerCase().trim();
    
    this.practiceResults.push({
      word: currentWord,
      correct: correct,
      userAnswer: this.userAnswer
    });

    if (correct) {
      this.practiceScore++;
      // Mark as reviewed to progress from NEW to LEARNING
      this.markAsReviewed(currentWord, false);
    }

    if (this.currentPracticeIndex < this.practiceWords.length - 1) {
      this.currentPracticeIndex++;
      this.userAnswer = '';
    } else {
      this.showPracticeResult = true;
      // Show notification about words marked as learning
      this.showLearningProgressNotification();
    }
  }

  showLearningProgressNotification(): void {
    const correctWords = this.practiceResults.filter(r => r.correct);
    const newWordsLearned = correctWords.filter(r => r.word.masteryLevel === 'NEW').length;
    
    if (newWordsLearned > 0) {
      setTimeout(() => {
        Swal.fire({
          icon: 'success',
          title: 'Great Progress! 🎉',
          text: `You've marked ${newWordsLearned} word${newWordsLearned > 1 ? 's' : ''} as Learning!`,
          timer: 5000,
          showConfirmButton: false,
          toast: true,
          position: 'top-end',
          timerProgressBar: true
        });
      }, 500);
    }
  }

  skipPracticeWord(): void {
    const currentWord = this.practiceWords[this.currentPracticeIndex];
    this.practiceResults.push({
      word: currentWord,
      correct: false,
      userAnswer: '(skipped)'
    });

    if (this.currentPracticeIndex < this.practiceWords.length - 1) {
      this.currentPracticeIndex++;
      this.userAnswer = '';
    } else {
      this.showPracticeResult = true;
    }
  }

  restartPractice(): void {
    this.startPractice();
  }

  exitPractice(): void {
    this.practiceMode = false;
    this.viewMode = 'grid';
    this.loadVocabulary();
    this.loadStats();
  }

  getProgressPercentage(): number {
    if (!this.stats || this.stats.totalWords === 0) return 0;
    // Only count LEARNING words in progress
    return Math.round((this.stats.learningWords / this.stats.totalWords) * 100);
  }

  getWordsToReview(): VocabularyWord[] {
    return this.words.filter(w => w.masteryLevel === 'NEW' || w.masteryLevel === 'LEARNING');
  }

  exportToPDF(): void {
    this.vocabularyService.exportVocabulary().subscribe({
      next: (allWords) => {
        const doc = new jsPDF();
        const pageWidth = doc.internal.pageSize.width;
        const pageHeight = doc.internal.pageSize.height;
        const margin = 20;
        let y = margin;

        // Header with gradient effect (simulated with colors)
        doc.setFillColor(26, 77, 77); // Teal color
        doc.rect(0, 0, pageWidth, 40, 'F');
        
        // Title
        doc.setTextColor(255, 255, 255);
        doc.setFontSize(24);
        doc.setFont('helvetica', 'bold');
        doc.text('My Vocabulary', pageWidth / 2, 20, { align: 'center' });
        
        // Subtitle with date
        doc.setFontSize(10);
        doc.setFont('helvetica', 'normal');
        const currentDate = new Date().toLocaleDateString('en-US', { 
          year: 'numeric', 
          month: 'long', 
          day: 'numeric' 
        });
        doc.text(`Generated on ${currentDate}`, pageWidth / 2, 30, { align: 'center' });
        
        y = 50;

        // Statistics box
        doc.setFillColor(240, 240, 240);
        doc.roundedRect(margin, y, pageWidth - 2 * margin, 20, 3, 3, 'F');
        
        doc.setTextColor(60, 60, 60);
        doc.setFontSize(10);
        doc.setFont('helvetica', 'bold');
        
        const newWords = allWords.filter(w => w.masteryLevel === 'NEW').length;
        const learningWords = allWords.filter(w => w.masteryLevel === 'LEARNING').length;
        
        doc.text(`Total: ${allWords.length} words`, margin + 5, y + 8);
        doc.text(`New: ${newWords}`, margin + 60, y + 8);
        doc.text(`Learning: ${learningWords}`, margin + 95, y + 8);
        
        doc.setTextColor(0, 0, 0);
        doc.text(`English Learning Platform`, pageWidth - margin - 5, y + 8, { align: 'right' });
        
        y += 35;

        // Words section
        allWords.forEach((word, index) => {
          // Check if we need a new page
          if (y > pageHeight - 60) {
            doc.addPage();
            y = margin;
          }

          // Calculate card height dynamically
          let cardHeight = 25; // Base height
          if (word.phonetic) cardHeight += 5;
          if (word.partOfSpeech) cardHeight += 5;
          cardHeight += doc.splitTextToSize(word.definition, pageWidth - 2 * margin - 10).length * 5;
          if (word.example) cardHeight += doc.splitTextToSize(`"${word.example}"`, pageWidth - 2 * margin - 16).length * 5 + 7;
          if (word.synonyms) cardHeight += doc.splitTextToSize(word.synonyms, pageWidth - 2 * margin - 10).length * 4 + 3;
          if (word.antonyms) cardHeight += doc.splitTextToSize(word.antonyms, pageWidth - 2 * margin - 10).length * 4 + 2;

          // Word card background
          doc.setFillColor(249, 250, 251);
          doc.roundedRect(margin, y, pageWidth - 2 * margin, cardHeight, 2, 2, 'F');
          
          // Word number and mastery badge
          doc.setFontSize(8);
          doc.setFont('helvetica', 'bold');
          doc.setTextColor(100, 100, 100);
          doc.text(`#${index + 1}`, margin + 3, y + 5);
          
          // Mastery level badge
          if (word.masteryLevel === 'NEW') {
            doc.setFillColor(229, 231, 235);
            doc.setTextColor(55, 65, 81);
          } else if (word.masteryLevel === 'LEARNING') {
            doc.setFillColor(209, 250, 229);
            doc.setTextColor(22, 101, 52);
          }
          
          const badgeText = word.masteryLevel;
          const badgeWidth = doc.getTextWidth(badgeText) + 6;
          doc.roundedRect(pageWidth - margin - badgeWidth - 3, y + 2, badgeWidth, 6, 1, 1, 'F');
          doc.text(badgeText, pageWidth - margin - badgeWidth / 2 - 3, y + 5.5, { align: 'center' });
          
          y += 10;

          // Word title
          doc.setFontSize(14);
          doc.setFont('helvetica', 'bold');
          doc.setTextColor(26, 77, 77); // Teal
          doc.text(word.word, margin + 3, y);
          
          // Phonetic
          if (word.phonetic) {
            doc.setFontSize(10);
            doc.setFont('helvetica', 'italic');
            doc.setTextColor(107, 114, 128);
            doc.text(word.phonetic, margin + 3 + doc.getTextWidth(word.word) + 3, y);
          }
          
          y += 7;

          // Part of speech
          if (word.partOfSpeech) {
            doc.setFontSize(8);
            doc.setFont('helvetica', 'bold');
            doc.setFillColor(224, 231, 255);
            doc.setTextColor(67, 56, 202);
            const posWidth = doc.getTextWidth(word.partOfSpeech) + 4;
            doc.roundedRect(margin + 3, y - 3, posWidth, 5, 1, 1, 'F');
            doc.text(word.partOfSpeech, margin + 5, y);
            y += 5;
          }

          // Definition
          doc.setFontSize(10);
          doc.setFont('helvetica', 'normal');
          doc.setTextColor(60, 60, 60);
          const splitDefinition = doc.splitTextToSize(word.definition, pageWidth - 2 * margin - 10);
          doc.text(splitDefinition, margin + 3, y);
          y += splitDefinition.length * 5;

          // Example
          if (word.example) {
            y += 3;
            doc.setFillColor(254, 252, 232);
            const exampleHeight = doc.splitTextToSize(`"${word.example}"`, pageWidth - 2 * margin - 16).length * 5 + 4;
            doc.roundedRect(margin + 3, y - 2, pageWidth - 2 * margin - 6, exampleHeight, 1, 1, 'F');
            
            doc.setFontSize(9);
            doc.setFont('helvetica', 'italic');
            doc.setTextColor(133, 77, 14);
            const splitExample = doc.splitTextToSize(`"${word.example}"`, pageWidth - 2 * margin - 16);
            doc.text(splitExample, margin + 6, y + 2);
            y += splitExample.length * 5 + 4;
          }

          // Synonyms
          if (word.synonyms) {
            y += 3;
            doc.setFontSize(8);
            doc.setFont('helvetica', 'bold');
            doc.setTextColor(22, 163, 74);
            doc.text('Synonyms: ', margin + 3, y);
            doc.setFont('helvetica', 'normal');
            doc.setTextColor(60, 60, 60);
            const synWidth = doc.getTextWidth('Synonyms: ');
            const splitSyn = doc.splitTextToSize(word.synonyms, pageWidth - 2 * margin - 10 - synWidth);
            doc.text(splitSyn, margin + 3 + synWidth, y);
            y += splitSyn.length * 4;
          }

          // Antonyms
          if (word.antonyms) {
            y += 2;
            doc.setFontSize(8);
            doc.setFont('helvetica', 'bold');
            doc.setTextColor(220, 38, 38);
            doc.text('Antonyms: ', margin + 3, y);
            doc.setFont('helvetica', 'normal');
            doc.setTextColor(60, 60, 60);
            const antWidth = doc.getTextWidth('Antonyms: ');
            const splitAnt = doc.splitTextToSize(word.antonyms, pageWidth - 2 * margin - 10 - antWidth);
            doc.text(splitAnt, margin + 3 + antWidth, y);
            y += splitAnt.length * 4;
          }

          y += 8; // Space between cards
        });

        // Footer on last page
        const totalPages = doc.getNumberOfPages();
        for (let i = 1; i <= totalPages; i++) {
          doc.setPage(i);
          doc.setFontSize(8);
          doc.setTextColor(150, 150, 150);
          doc.setFont('helvetica', 'normal');
          doc.text(
            `Page ${i} of ${totalPages}`,
            pageWidth / 2,
            pageHeight - 10,
            { align: 'center' }
          );
          doc.text(
            'English Learning Platform',
            pageWidth - margin,
            pageHeight - 10,
            { align: 'right' }
          );
        }

        doc.save('my-vocabulary.pdf');

        Swal.fire({
          icon: 'success',
          title: 'Exported!',
          text: 'Your vocabulary has been exported to PDF',
          timer: 2000,
          showConfirmButton: false,
          toast: true,
          position: 'top-end',
          timerProgressBar: true
        });
      },
      error: (err) => {
        console.error('Error exporting vocabulary:', err);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to export vocabulary'
        });
      }
    });
  }

  exportToCSV(): void {
    this.vocabularyService.exportVocabulary().subscribe({
      next: (allWords) => {
        const headers = ['Word', 'Definition', 'Phonetic', 'Part of Speech', 'Example', 'Synonyms', 'Antonyms', 'Mastery Level', 'Review Count'];
        const rows = allWords.map(word => [
          word.word,
          word.definition,
          word.phonetic || '',
          word.partOfSpeech || '',
          word.example || '',
          word.synonyms || '',
          word.antonyms || '',
          word.masteryLevel,
          word.reviewCount.toString()
        ]);

        const csvContent = [
          headers.join(','),
          ...rows.map(row => row.map(cell => `"${cell.replaceAll('"', '""')}"`).join(','))
        ].join('\n');

        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = 'my-vocabulary.csv';
        link.click();

        Swal.fire({
          icon: 'success',
          title: 'Exported!',
          text: 'Your vocabulary has been exported to CSV',
          timer: 2000,
          showConfirmButton: false
        });
      },
      error: (err) => {
        console.error('Error exporting vocabulary:', err);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Failed to export vocabulary'
        });
      }
    });
  }
}
