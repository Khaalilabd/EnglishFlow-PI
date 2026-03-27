import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PackService } from '../../../core/services/pack.service';
import { TutorAvailabilityService } from '../../../core/services/tutor-availability.service';
import { CourseService } from '../../../core/services/course.service';
import { CourseCategoryService } from '../../../core/services/course-category.service';
import { AuthService } from '../../../core/services/auth.service';
import { Pack, PackStatus } from '../../../core/models/pack.model';
import { TutorAvailability } from '../../../core/models/tutor-availability.model';
import { Course } from '../../../core/models/course.model';
import { CourseCategory } from '../../../core/models/course-category.model';

@Component({
  selector: 'app-pack-create',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pack-create.component.html',
  styleUrls: ['./pack-create.component.scss']
})
export class PackCreateComponent implements OnInit {
  pack: Pack = {
    name: '',
    category: '',
    level: 'A1',
    tutorId: 0,
    tutorName: '',
    tutorRating: 0,
    courseIds: [],
    price: 0,
    estimatedDuration: 0,
    maxStudents: 30,
    description: '',
    status: PackStatus.DRAFT,
    createdBy: 0
  };

  tutors: TutorAvailability[] = [];
  availableCourses: Course[] = [];
  categories: CourseCategory[] = [];
  selectedTutor: TutorAvailability | null = null;
  
  loading = false;
  saving = false;
  isEditMode = false;
  packId: number | null = null;
  
  message = '';
  messageType: 'success' | 'error' = 'success';

  // Enums
  allCategories: CourseCategory[] = [];
  allLevels = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];
  allStatuses = Object.values(PackStatus);

  constructor(
    private packService: PackService,
    private tutorAvailabilityService: TutorAvailabilityService,
    private courseService: CourseService,
    private categoryService: CourseCategoryService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadTutors();
    
    // Check if edit mode
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.packId = +params['id'];
        this.loadPack(this.packId);
      }
    });

    // Set createdBy
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.pack.createdBy = currentUser.id;
    }
  }

  loadCategories(): void {
    this.categoryService.getActiveCategories().subscribe({
      next: (categories) => {
        this.allCategories = categories;
        if (categories.length > 0 && !this.pack.category) {
          this.pack.category = categories[0].name;
        }
      },
      error: (error) => {
        console.error('Error loading categories:', error);
        this.showMessage('Failed to load categories', 'error');
      }
    });
  }

  loadTutors(): void {
    this.tutorAvailabilityService.getTutorsWithCapacity().subscribe({
      next: (tutors) => {
        this.tutors = tutors;
      },
      error: (error) => {
        console.error('Error loading tutors:', error);
        this.showMessage('Failed to load tutors', 'error');
      }
    });
  }

  loadPack(id: number): void {
    this.loading = true;
    this.packService.getById(id).subscribe({
      next: (pack) => {
        this.pack = pack;
        this.onTutorChange();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading pack:', error);
        this.showMessage('Failed to load pack', 'error');
        this.loading = false;
      }
    });
  }

  onTutorChange(): void {
    if (this.pack.tutorId) {
      // Find selected tutor
      this.selectedTutor = this.tutors.find(t => t.tutorId === this.pack.tutorId) || null;
      
      if (this.selectedTutor) {
        this.pack.tutorName = this.selectedTutor.tutorName;
        this.pack.maxStudents = Math.min(this.pack.maxStudents, this.selectedTutor.availableCapacity || 30);
        
        // Load tutor's courses
        this.loadTutorCourses(this.pack.tutorId);
      }
    } else {
      this.selectedTutor = null;
      this.availableCourses = [];
      this.pack.courseIds = [];
    }
  }

  loadTutorCourses(tutorId: number): void {
    this.courseService.getCoursesByTutor(tutorId).subscribe({
      next: (courses) => {
        this.availableCourses = courses;
        this.calculateEstimatedDuration();
      },
      error: (error) => {
        console.error('Error loading courses:', error);
        this.showMessage('Failed to load tutor courses', 'error');
      }
    });
  }

  toggleCourse(courseId: number): void {
    const index = this.pack.courseIds.indexOf(courseId);
    if (index > -1) {
      this.pack.courseIds.splice(index, 1);
    } else {
      this.pack.courseIds.push(courseId);
    }
    this.calculateEstimatedDuration();
    this.generatePackName();
  }

  isCourseSelected(courseId: number): boolean {
    return this.pack.courseIds.includes(courseId);
  }

  calculateEstimatedDuration(): void {
    const selectedCourses = this.availableCourses.filter(c => 
      c.id && this.pack.courseIds.includes(c.id)
    );
    this.pack.estimatedDuration = selectedCourses.reduce((sum, c) => 
      sum + (c.duration || 0), 0
    ) / 60; // Convert minutes to hours
  }

  generatePackName(): void {
    if (!this.isEditMode && this.pack.category && this.pack.level && this.selectedTutor) {
      const categoryLabel = this.getCategoryLabel(this.pack.category);
      this.pack.name = `${categoryLabel} ${this.pack.level} - ${this.selectedTutor.tutorName}`;
    }
  }

  onCategoryChange(): void {
    this.generatePackName();
  }

  onLevelChange(): void {
    this.generatePackName();
  }

  savePack(): void {
    if (!this.validateForm()) {
      this.showMessage('Please fill all required fields', 'error');
      return;
    }

    this.saving = true;
    
    const saveObservable = this.isEditMode && this.packId
      ? this.packService.updatePack(this.packId, this.pack)
      : this.packService.createPack(this.pack);

    saveObservable.subscribe({
      next: (pack) => {
        this.showMessage(`Pack ${this.isEditMode ? 'updated' : 'created'} successfully!`, 'success');
        this.saving = false;
        setTimeout(() => {
          this.router.navigate(['/dashboard/packs']);
        }, 1500);
      },
      error: (error) => {
        console.error('Error saving pack:', error);
        this.showMessage('Failed to save pack', 'error');
        this.saving = false;
      }
    });
  }

  validateForm(): boolean {
    return this.pack.name.trim() !== '' &&
           this.pack.tutorId > 0 &&
           this.pack.courseIds.length > 0 &&
           this.pack.price > 0 &&
           this.pack.maxStudents > 0;
  }

  cancel(): void {
    this.router.navigate(['/dashboard/packs']);
  }

  showMessage(text: string, type: 'success' | 'error'): void {
    this.message = text;
    this.messageType = type;
    setTimeout(() => {
      this.message = '';
    }, 5000);
  }

  getCategoryLabel(categoryName: string): string {
    const category = this.allCategories.find(c => c.name === categoryName);
    return category ? category.name : categoryName;
  }

  getTutorStatusColor(tutor: TutorAvailability): string {
    const percentage = tutor.capacityPercentage || 0;
    if (percentage >= 80) return 'text-red-600';
    if (percentage >= 50) return 'text-orange-600';
    return 'text-green-600';
  }

  getCourseDuration(course: Course): string {
    if (!course.duration) return '0h';
    const hours = Math.floor(course.duration / 60);
    const mins = course.duration % 60;
    if (hours > 0 && mins > 0) {
      return `${hours}h ${mins}m`;
    } else if (hours > 0) {
      return `${hours}h`;
    }
    return `${mins}m`;
  }
}
