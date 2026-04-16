import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { CourseService } from './course.service';
import { Course, CourseStatus, CreateCourseRequest } from '../models/course.model';
import { environment } from '../../../environments/environment';

describe('CourseService', () => {
  let service: CourseService;
  let httpMock: HttpTestingController;

  const mockCourse: Course = {
    id: 1,
    title: 'Advanced English Grammar',
    description: 'Master English grammar concepts',
    category: 'Grammar',
    level: 'B2',
    tutorId: 5,
    tutorName: 'John Smith',
    status: CourseStatus.PUBLISHED,
    thumbnailUrl: 'course-thumbnail.jpg',
    duration: 40,
    price: 299.99,
    createdAt: '2026-01-15T10:00:00',
    updatedAt: '2026-04-01T14:30:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CourseService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CourseService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a new course', (done) => {
    const newCourse: CreateCourseRequest = {
      title: 'Business English',
      description: 'English for professionals',
      category: 'Business',
      level: 'C1',
      tutorId: 5,
      price: 399.99
    };

    service.createCourse(newCourse).subscribe(course => {
      expect(course).toEqual(mockCourse);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/courses`);
    expect(req.request.method).toBe('POST');
    req.flush(mockCourse);
  });

  it('should get course by ID', (done) => {
    const courseId = 1;

    service.getCourseById(courseId).subscribe(course => {
      expect(course).toEqual(mockCourse);
      expect(course.id).toBe(courseId);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/courses/${courseId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCourse);
  });

  it('should get all courses', (done) => {
    const mockCourses: Course[] = [mockCourse];

    service.getAllCourses().subscribe(courses => {
      expect(courses.length).toBe(1);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/courses`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCourses);
  });

  it('should update a course', (done) => {
    const courseId = 1;
    const updateData: any = {
      title: 'Updated Course',
      description: 'Updated description',
      category: 'Grammar',
      level: 'B2',
      tutorId: 5
    };

    service.updateCourse(courseId, updateData).subscribe(course => {
      expect(course.title).toBe('Updated Course');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/courses/${courseId}`);
    expect(req.request.method).toBe('PUT');
    req.flush({ ...mockCourse, ...updateData });
  });

  it('should delete a course', (done) => {
    const courseId = 1;

    service.deleteCourse(courseId).subscribe(() => {
      expect(true).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/courses/${courseId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should get courses by status', (done) => {
    const publishedCourses: Course[] = [mockCourse];

    service.getCoursesByStatus(CourseStatus.PUBLISHED).subscribe(courses => {
      expect(courses.length).toBe(1);
      expect(courses[0].status).toBe(CourseStatus.PUBLISHED);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/courses/status/${CourseStatus.PUBLISHED}`);
    expect(req.request.method).toBe('GET');
    req.flush(publishedCourses);
  });

  it('should get courses by level', (done) => {
    const b2Courses: Course[] = [mockCourse];

    service.getCoursesByLevel('B2').subscribe(courses => {
      expect(courses.length).toBe(1);
      expect(courses[0].level).toBe('B2');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/courses/level/B2`);
    expect(req.request.method).toBe('GET');
    req.flush(b2Courses);
  });

  it('should get courses by tutor', (done) => {
    const tutorId = 5;
    const tutorCourses: Course[] = [mockCourse];

    service.getCoursesByTutor(tutorId).subscribe(courses => {
      expect(courses.length).toBe(1);
      expect(courses[0].tutorId).toBe(tutorId);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/courses/tutor/${tutorId}`);
    expect(req.request.method).toBe('GET');
    req.flush(tutorCourses);
  });

  it('should upload course thumbnail', (done) => {
    const courseId = 1;
    const file = new File(['thumbnail'], 'thumbnail.jpg', { type: 'image/jpeg' });
    const response = { url: 'http://example.com/thumbnail.jpg', message: 'Uploaded' };

    service.uploadThumbnail(courseId, file).subscribe(res => {
      expect(res.url).toBe('http://example.com/thumbnail.jpg');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/courses/${courseId}/upload-thumbnail`);
    expect(req.request.method).toBe('POST');
    req.flush(response);
  });

  it('should handle course not found error', (done) => {
    const courseId = 999;

    service.getCourseById(courseId).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(404);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/courses/${courseId}`);
    req.flush({ message: 'Course not found' }, { status: 404, statusText: 'Not Found' });
  });
});
