import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LessonService } from '../../../core/services/lesson.service';
import { Lesson } from '../../../core/models/lesson.model';

@Component({
  selector: 'app-lessons-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './lessons-list.component.html'
})
export class LessonsListComponent implements OnInit {
  lessons: Lesson[] = [];
  loading = false;
  error: string | null = null;

  constructor(private lessonService: LessonService) {}

  ngOnInit(): void {
    this.loadLessons();
  }

  loadLessons(): void {
    this.loading = true;
    this.error = null;
    this.lessonService.getAllLessons().subscribe({
      next: (data) => {
        this.lessons = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load lessons';
        this.loading = false;
        console.error('Error loading lessons:', err);
      }
    });
  }

  deleteLesson(id: number): void {
    if (confirm('Are you sure you want to delete this lesson?')) {
      this.lessonService.deleteLesson(id).subscribe({
        next: () => {
          this.loadLessons();
        },
        error: (err) => {
          console.error('Error deleting lesson:', err);
          alert('Failed to delete lesson');
        }
      });
    }
  }
}
