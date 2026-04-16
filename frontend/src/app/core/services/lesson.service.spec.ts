import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { LessonService } from './lesson.service';
import { Lesson, LessonType, CreateLessonRequest } from '../models/lesson.model';
import { environment } from '../../../environments/environment';

describe('LessonService', () => {
  let service: LessonService;
  let httpMock: HttpTestingController;

  const mockLesson: Lesson = {
    id: 1,
    title: 'Introduction to Grammar',
    description: 'Learn basic grammar rules',
    lessonType: LessonType.VIDEO,
    contentUrl: 'https://example.com/video.mp4',
    duration: 30,
    orderIndex: 1,
    chapterId: 1,
    isPublished: true,
    isPreview: false,
    createdAt: '2026-01-15T10:00:00',
    updatedAt: '2026-04-01T14:00:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        LessonService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(LessonService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a new lesson', (done) => {
    const newLesson: CreateLessonRequest = {
      title: 'New Lesson',
      description: 'New lesson description',
      lessonType: LessonType.VIDEO,
      chapterId: 1,
      orderIndex: 2,
      isPublished: false,
      isPreview: false
    };

    service.createLesson(newLesson).subscribe(lesson => {
      expect(lesson).toEqual(mockLesson);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons`);
    expect(req.request.method).toBe('POST');
    req.flush(mockLesson);
  });

  it('should get lesson by ID', (done) => {
    const lessonId = 1;

    service.getLessonById(lessonId).subscribe(lesson => {
      expect(lesson).toEqual(mockLesson);
      expect(lesson.id).toBe(lessonId);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons/${lessonId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockLesson);
  });

  it('should update a lesson', (done) => {
    const lessonId = 1;
    const updateData: any = {
      title: 'Updated Lesson',
      description: 'Updated description',
      lessonType: LessonType.VIDEO,
      orderIndex: 1,
      isPublished: true,
      isPreview: false,
      chapterId: 1
    };

    service.updateLesson(lessonId, updateData).subscribe(lesson => {
      expect(lesson.title).toBe('Updated Lesson');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons/${lessonId}`);
    expect(req.request.method).toBe('PUT');
    req.flush({ ...mockLesson, ...updateData });
  });

  it('should delete a lesson', (done) => {
    const lessonId = 1;

    service.deleteLesson(lessonId).subscribe(() => {
      expect(true).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons/${lessonId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should get lessons by chapter', (done) => {
    const chapterId = 1;
    const chapterLessons: Lesson[] = [mockLesson];

    service.getLessonsByChapter(chapterId).subscribe(lessons => {
      expect(lessons.length).toBe(1);
      expect(lessons[0].chapterId).toBe(chapterId);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons/chapter/${chapterId}`);
    expect(req.request.method).toBe('GET');
    req.flush(chapterLessons);
  });

  it('should get published lessons by chapter', (done) => {
    const chapterId = 1;
    const publishedLessons: Lesson[] = [mockLesson];

    service.getPublishedLessonsByChapter(chapterId).subscribe(lessons => {
      expect(lessons.length).toBe(1);
      expect(lessons[0].isPublished).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons/chapter/${chapterId}/published`);
    req.flush(publishedLessons);
  });

  it('should get lessons by course', (done) => {
    const courseId = 1;
    const courseLessons: Lesson[] = [mockLesson];

    service.getLessonsByCourse(courseId).subscribe(lessons => {
      expect(lessons.length).toBe(1);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons/course/${courseId}`);
    req.flush(courseLessons);
  });

  it('should get preview lessons by course', (done) => {
    const courseId = 1;
    const previewLessons: Lesson[] = [
      { ...mockLesson, isPreview: true }
    ];

    service.getPreviewLessonsByCourse(courseId).subscribe(lessons => {
      expect(lessons.length).toBe(1);
      expect(lessons[0].isPreview).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons/course/${courseId}/preview`);
    req.flush(previewLessons);
  });

  it('should upload video', (done) => {
    const lessonId = 1;
    const file = new File(['video'], 'lesson.mp4', { type: 'video/mp4' });
    const response = { url: 'https://example.com/video.mp4', message: 'Video uploaded' };

    service.uploadVideo(lessonId, file).subscribe(res => {
      expect(res.url).toBe('https://example.com/video.mp4');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons/${lessonId}/upload-video`);
    expect(req.request.method).toBe('POST');
    req.flush(response);
  });

  it('should upload document', (done) => {
    const lessonId = 1;
    const file = new File(['document'], 'lesson.pdf', { type: 'application/pdf' });
    const response = { url: 'https://example.com/document.pdf', message: 'Document uploaded' };

    service.uploadDocument(lessonId, file).subscribe(res => {
      expect(res.url).toBe('https://example.com/document.pdf');
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons/${lessonId}/upload-document`);
    expect(req.request.method).toBe('POST');
    req.flush(response);
  });

  it('should publish all lessons in a course', (done) => {
    const courseId = 1;
    const publishedLessons: Lesson[] = [
      { ...mockLesson, isPublished: true }
    ];

    service.publishAllLessonsByCourse(courseId).subscribe(lessons => {
      expect(lessons.length).toBe(1);
      expect(lessons[0].isPublished).toBe(true);
      done();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons/course/${courseId}/publish-all`);
    expect(req.request.method).toBe('PUT');
    req.flush(publishedLessons);
  });

  it('should handle lesson not found error', (done) => {
    const lessonId = 999;

    service.getLessonById(lessonId).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(404);
        done();
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lessons/${lessonId}`);
    req.flush({ message: 'Lesson not found' }, { status: 404, statusText: 'Not Found' });
  });
});
