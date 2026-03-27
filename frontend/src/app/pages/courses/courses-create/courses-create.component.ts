import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CreateCourseRequest, CourseStatus, CEFR_LEVELS } from '../../../core/models/course.model';
import { CourseService } from '../../../core/services/course.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-courses-create',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './courses-create.component.html',
  styleUrls: ['./courses-create.component.scss']
})
export class CoursesCreateComponent implements OnInit {
  course: CreateCourseRequest = {
    title: '',
    description: '',
    category: 'General English',
    level: 'A1',
    maxStudents: 30,
    duration: 60,
    tutorId: 1,
    fileUrl: '',
    status: CourseStatus.DRAFT
  };

  levels = CEFR_LEVELS;
  statuses = Object.values(CourseStatus);

  constructor(
    private router: Router,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    console.log('Create course component initialized');
    console.log('API URL:', environment.apiUrl);
  }

  onSubmit(): void {
    console.log('Submitting course:', this.course);
    console.log('API endpoint:', `${environment.apiUrl}/courses`);
    
    // Validate required fields
    if (!this.course.title || !this.course.description) {
      alert('Please fill in all required fields (Title and Description)');
      return;
    }
    
    this.courseService.createCourse(this.course).subscribe({
      next: (createdCourse) => {
        console.log('Course created successfully:', createdCourse);
        alert(`Course "${createdCourse.title}" created successfully!`);
        this.router.navigate(['/dashboard/courses']);
      },
      error: (error) => {
        console.error('Error creating course:', error);
        console.error('Error details:', {
          status: error.status,
          statusText: error.statusText,
          message: error.message,
          error: error.error
        });
        
        let errorMessage = 'Error creating course. ';
        if (error.status === 0) {
          errorMessage += 'Cannot connect to server. Please ensure the backend is running on port 8088.';
        } else if (error.status === 404) {
          errorMessage += 'API endpoint not found. Check the API Gateway configuration.';
        } else if (error.error && error.error.message) {
          errorMessage += error.error.message;
        } else {
          errorMessage += 'Please check the console for details.';
        }
        
        alert(errorMessage);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard/courses']);
  }
}
