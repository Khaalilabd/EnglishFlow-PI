import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ChapterService } from '../../../core/services/chapter.service';
import { Chapter } from '../../../core/models/chapter.model';

@Component({
  selector: 'app-chapters-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './chapters-list.component.html'
})
export class ChaptersListComponent implements OnInit {
  chapters: Chapter[] = [];
  loading = false;
  error: string | null = null;

  constructor(private chapterService: ChapterService) {}

  ngOnInit(): void {
    this.loadChapters();
  }

  loadChapters(): void {
    this.loading = true;
    this.error = null;
    this.chapterService.getAllChapters().subscribe({
      next: (data) => {
        this.chapters = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load chapters';
        this.loading = false;
        console.error('Error loading chapters:', err);
      }
    });
  }

  deleteChapter(id: number): void {
    if (confirm('Are you sure you want to delete this chapter?')) {
      this.chapterService.deleteChapter(id).subscribe({
        next: () => {
          this.loadChapters();
        },
        error: (err) => {
          console.error('Error deleting chapter:', err);
          alert('Failed to delete chapter');
        }
      });
    }
  }
}
