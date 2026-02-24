import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Course, CourseStatus, CEFR_LEVELS } from '../../../core/models/course.model';
import { CourseService } from '../../../core/services/course.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-course-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './course-catalog.component.html',
  styleUrls: ['./course-catalog.component.scss']
})
export class CourseCatalogComponent implements OnInit {
  courses: Course[] = [];
  filteredCourses: Course[] = [];
  searchTerm = '';
  selectedLevel = 'ALL';
  sortBy = 'newest';
  loading = true;

  // Expose enums to template
  levels = CEFR_LEVELS;
  CourseStatus = CourseStatus;

  constructor(
    private router: Router,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.loading = true;
    this.courseService.getPublishedCourses().subscribe({
      next: (courses) => {
        this.courses = courses;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading courses:', error);
        this.loading = false;
        this.courses = [];
        this.applyFilters();
      }
    });
  }

  applyFilters(): void {
    let filtered = [...this.courses];

    // Search filter
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(course =>
        course.title.toLowerCase().includes(term) ||
        course.description.toLowerCase().includes(term)
      );
    }

    // Level filter
    if (this.selectedLevel !== 'ALL') {
      filtered = filtered.filter(course => course.level === this.selectedLevel);
    }

    // Sort
    switch (this.sortBy) {
      case 'newest':
        filtered.sort((a, b) => {
          const dateA = new Date(a.createdAt || 0).getTime();
          const dateB = new Date(b.createdAt || 0).getTime();
          return dateB - dateA;
        });
        break;
      case 'title':
        filtered.sort((a, b) => a.title.localeCompare(b.title));
        break;
      case 'level':
        filtered.sort((a, b) => a.level.localeCompare(b.level));
        break;
    }

    this.filteredCourses = filtered;
  }

  viewCourse(courseId: number | undefined): void {
    if (courseId) {
      this.router.navigate(['/user-panel/course', courseId]);
    }
  }

  // Helper methods
  getLevelIcon(level: string): string {
    const icons: Record<string, string> = {
      'A1': '📚',
      'A2': '📖',
      'B1': '💬',
      'B2': '💼',
      'C1': '🎓',
      'C2': '🏆'
    };
    return icons[level] || '📚';
  }

  getLevelColor(level: string): string {
    const colors: Record<string, string> = {
      'A1': 'bg-green-100 text-green-800',
      'A2': 'bg-blue-100 text-blue-800',
      'B1': 'bg-yellow-100 text-yellow-800',
      'B2': 'bg-orange-100 text-orange-800',
      'C1': 'bg-purple-100 text-purple-800',
      'C2': 'bg-red-100 text-red-800'
    };
    return colors[level] || 'bg-gray-100 text-gray-800';
  }

  formatDuration(minutes: number | undefined): string {
    if (!minutes) return '0h';
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours > 0 && mins > 0) {
      return `${hours}h ${mins}m`;
    } else if (hours > 0) {
      return `${hours}h`;
    }
    return `${mins}m`;
  }
}
