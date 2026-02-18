import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Course, EnglishLevel, CourseStatus } from '../../../core/models/course.model';
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
  EnglishLevel = EnglishLevel;
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
    console.log('Loading published courses from:', `${environment.apiUrl}/courses/status/PUBLISHED`);
    this.courseService.getPublishedCourses().subscribe({
      next: (courses) => {
        console.log('Published courses loaded:', courses);
        this.courses = courses;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading courses:', error);
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          url: error.url
        });
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
  getLevelIcon(level: EnglishLevel): string {
    const icons = {
      [EnglishLevel.BEGINNER]: '📚',
      [EnglishLevel.ELEMENTARY]: '📖',
      [EnglishLevel.INTERMEDIATE]: '💬',
      [EnglishLevel.UPPER_INTERMEDIATE]: '💼',
      [EnglishLevel.ADVANCED]: '🎓'
    };
    return icons[level] || '📚';
  }

  getLevelColor(level: EnglishLevel): string {
    const colors = {
      [EnglishLevel.BEGINNER]: 'bg-green-100 text-green-800',
      [EnglishLevel.ELEMENTARY]: 'bg-blue-100 text-blue-800',
      [EnglishLevel.INTERMEDIATE]: 'bg-yellow-100 text-yellow-800',
      [EnglishLevel.UPPER_INTERMEDIATE]: 'bg-orange-100 text-orange-800',
      [EnglishLevel.ADVANCED]: 'bg-purple-100 text-purple-800'
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
