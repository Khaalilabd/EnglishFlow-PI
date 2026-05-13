import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { environment } from '../../../environments/environment';
import { AtRiskStudentsComponent } from '../../components/at-risk-students/at-risk-students.component';

interface OnlineLessonSchedule {
  lessonId: number;
  lessonTitle: string;
  courseTitle: string;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  canStart: boolean;
}

@Component({
  selector: 'app-tutor-panel',
  standalone: true,
  imports: [CommonModule, RouterModule, AtRiskStudentsComponent],
  templateUrl: './tutor-panel.component.html',
  styleUrls: ['./tutor-panel.component.scss']
})
export class TutorPanelComponent implements OnInit {
  tutorId: number = 0;
  onlineLessons: OnlineLessonSchedule[] = [];
  loadingLessons = false;
  myStudents: any[] = []; // Liste des étudiants du tuteur
  
  // Real stats (will be loaded from API)
  stats = [
    { icon: 'fas fa-users', label: 'Total Students', value: '0', change: '+0%', color: 'purple' },
    { icon: 'fas fa-book', label: 'Active Courses', value: '0', change: '+0', color: 'blue' },
    { icon: 'fas fa-clipboard-list', label: 'Quizzes Created', value: '0', change: '+0', color: 'orange' },
    { icon: 'fas fa-chart-line', label: 'Avg. Score', value: '0%', change: '+0%', color: 'green' }
  ];

  recentQuizzes: any[] = [];
  upcomingClasses: any[] = [];
  recentActivity: any[] = [];
  
  loadingStats = true;

  constructor(
    private readonly router: Router,
    private readonly http: HttpClient,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    const currentUser = this.authService.currentUserValue;
    if (currentUser?.id) {
      this.tutorId = currentUser.id;
      this.loadOnlineLessons();
      this.loadMyStudents();
      this.loadDashboardStats();
      this.loadRecentQuizzes();
      this.loadRecentActivity();
    }
  }

  loadMyStudents(): void {
    // Charger les étudiants du tuteur depuis l'API
    this.http.get<any[]>(`${environment.apiUrl}/tutors/${this.tutorId}/students`)
      .subscribe({
        next: (students) => {
          this.myStudents = students;
        },
        error: (err) => {
          console.warn('API /tutors/students not available, using demo data:', err.status);
          // Données de démo pour tester le composant ML
          this.myStudents = [
            { 
              id: '1', 
              firstName: 'Ahmed', 
              lastName: 'Ben Ali', 
              email: 'ahmed@example.com', 
              credits: 45, 
              avgScore: 42,
              totalClicks: 800,
              sessions: 50,
              assessments: 3
            },
            { 
              id: '2', 
              firstName: 'Sara', 
              lastName: 'Mansouri', 
              email: 'sara@example.com', 
              credits: 30, 
              avgScore: 38,
              totalClicks: 600,
              sessions: 40,
              assessments: 2
            },
            { 
              id: '3', 
              firstName: 'Mohamed', 
              lastName: 'Trabelsi', 
              email: 'mohamed@example.com', 
              credits: 60, 
              avgScore: 48,
              totalClicks: 1200,
              sessions: 80,
              assessments: 5
            },
            { 
              id: '4', 
              firstName: 'Fatima', 
              lastName: 'Khalil', 
              email: 'fatima@example.com', 
              credits: 25, 
              avgScore: 35,
              totalClicks: 500,
              sessions: 30,
              assessments: 2
            },
            { 
              id: '5', 
              firstName: 'Youssef', 
              lastName: 'Amri', 
              email: 'youssef@example.com', 
              credits: 50, 
              avgScore: 45,
              totalClicks: 900,
              sessions: 60,
              assessments: 4
            }
          ];
        }
      });
  }

  loadOnlineLessons(): void {
    this.loadingLessons = true;
    this.http.get<any[]>(`${environment.apiUrl}/online-lessons/tutor/${this.tutorId}/scheduled`)
      .subscribe({
        next: (lessons) => {
          this.onlineLessons = lessons.map(lesson => ({
            ...lesson,
            canStart: this.canStartLesson(lesson)
          }));
          this.loadingLessons = false;
        },
        error: (err) => {
          console.error('Failed to load online lessons:', err);
          this.loadingLessons = false;
        }
      });
  }

  canStartLesson(lesson: any): boolean {
    const now = new Date();
    const dayMap: { [key: string]: number } = {
      MONDAY: 1, TUESDAY: 2, WEDNESDAY: 3, THURSDAY: 4,
      FRIDAY: 5, SATURDAY: 6, SUNDAY: 0
    };
    const lessonDay = dayMap[lesson.dayOfWeek];
    if (now.getDay() !== lessonDay) return false;

    const [startH, startM] = lesson.startTime.split(':').map(Number);
    const [endH, endM] = lesson.endTime.split(':').map(Number);
    const startMinutes = startH * 60 + startM;
    const endMinutes = endH * 60 + endM;
    const nowMinutes = now.getHours() * 60 + now.getMinutes();

    // Enable button 15 minutes before start time until end time
    return nowMinutes >= startMinutes - 15 && nowMinutes <= endMinutes;
  }

  getDayName(dayOfWeek: string): string {
    const days: { [key: string]: string } = {
      'MONDAY': 'Monday',
      'TUESDAY': 'Tuesday',
      'WEDNESDAY': 'Wednesday',
      'THURSDAY': 'Thursday',
      'FRIDAY': 'Friday',
      'SATURDAY': 'Saturday',
      'SUNDAY': 'Sunday'
    };
    return days[dayOfWeek] || dayOfWeek;
  }

  startMeeting(lesson: OnlineLessonSchedule): void {
    const roomId = `lesson-${lesson.lessonId}`;
    this.router.navigate(['/meeting', roomId], {
      queryParams: { lessonId: lesson.lessonId }
    });
  }

  loadDashboardStats(): void {
    this.loadingStats = true;
    
    // Load tutor's courses
    this.http.get<any[]>(`${environment.apiUrl}/courses/tutor/${this.tutorId}`).subscribe({
      next: (courses) => {
        const activeCourses = courses.filter(c => c.isPublished);
        this.stats[1].value = activeCourses.length.toString();
        
        // Count total students enrolled in tutor's courses
        let totalStudents = 0;
        let courseCount = 0;
        
        activeCourses.forEach(course => {
          this.http.get<any[]>(`${environment.apiUrl}/courses/enrollments/course/${course.id}`).subscribe({
            next: (enrollments) => {
              totalStudents += enrollments.length;
              courseCount++;
              
              if (courseCount === activeCourses.length) {
                this.stats[0].value = totalStudents.toString();
                this.loadQuizStats(activeCourses);
              }
            },
            error: () => {
              courseCount++;
              if (courseCount === activeCourses.length) {
                this.stats[0].value = totalStudents.toString();
                this.loadQuizStats(activeCourses);
              }
            }
          });
        });
        
        if (activeCourses.length === 0) {
          this.loadingStats = false;
        }
      },
      error: (err) => {
        console.error('Error loading courses:', err);
        this.loadingStats = false;
      }
    });
  }

  loadQuizStats(courses: any[]): void {
    const courseIds = courses.map(c => c.id);
    
    // Load all quizzes
    this.http.get<any[]>(`${environment.apiUrl}/learning/quizzes`).subscribe({
      next: (allQuizzes) => {
        const tutorQuizzes = allQuizzes.filter(q => courseIds.includes(q.courseId));
        this.stats[2].value = tutorQuizzes.length.toString();
        
        // Calculate average score from quiz attempts
        this.calculateAverageScore(tutorQuizzes);
      },
      error: (err) => {
        console.error('Error loading quizzes:', err);
        this.loadingStats = false;
      }
    });
  }

  calculateAverageScore(quizzes: any[]): void {
    if (quizzes.length === 0) {
      this.loadingStats = false;
      return;
    }
    
    let totalScore = 0;
    let totalAttempts = 0;
    let processedQuizzes = 0;
    
    quizzes.forEach(quiz => {
      this.http.get<any[]>(`${environment.apiUrl}/learning/attempts/quiz/${quiz.id}`).subscribe({
        next: (attempts) => {
          const completedAttempts = attempts.filter(a => a.status === 'COMPLETED');
          completedAttempts.forEach(attempt => {
            if (attempt.score && quiz.maxScore) {
              totalScore += (attempt.score / quiz.maxScore) * 100;
              totalAttempts++;
            }
          });
          
          processedQuizzes++;
          if (processedQuizzes === quizzes.length) {
            const avgScore = totalAttempts > 0 ? Math.round(totalScore / totalAttempts) : 0;
            this.stats[3].value = `${avgScore}%`;
            this.loadingStats = false;
          }
        },
        error: () => {
          processedQuizzes++;
          if (processedQuizzes === quizzes.length) {
            const avgScore = totalAttempts > 0 ? Math.round(totalScore / totalAttempts) : 0;
            this.stats[3].value = `${avgScore}%`;
            this.loadingStats = false;
          }
        }
      });
    });
  }

  loadRecentQuizzes(): void {
    this.http.get<any[]>(`${environment.apiUrl}/courses/tutor/${this.tutorId}`).subscribe({
      next: (courses) => {
        const courseIds = courses.map(c => c.id);
        
        this.http.get<any[]>(`${environment.apiUrl}/learning/quizzes`).subscribe({
          next: (allQuizzes) => {
            const tutorQuizzes = allQuizzes
              .filter(q => courseIds.includes(q.courseId))
              .sort((a, b) => new Date(b.createdAt || 0).getTime() - new Date(a.createdAt || 0).getTime())
              .slice(0, 3);
            
            this.recentQuizzes = tutorQuizzes.map(quiz => ({
              title: quiz.title,
              students: 0,
              avgScore: 0,
              status: quiz.published ? 'active' : 'draft',
              dueDate: quiz.publishAt || new Date().toISOString()
            }));
            
            // Load attempts for each quiz to get student count and avg score
            this.recentQuizzes.forEach((quiz, index) => {
              const quizId = tutorQuizzes[index].id;
              this.http.get<any[]>(`${environment.apiUrl}/learning/attempts/quiz/${quizId}`).subscribe({
                next: (attempts) => {
                  const uniqueStudents = new Set(attempts.map(a => a.studentId));
                  quiz.students = uniqueStudents.size;
                  
                  const completedAttempts = attempts.filter(a => a.status === 'COMPLETED');
                  if (completedAttempts.length > 0) {
                    const totalScore = completedAttempts.reduce((sum, a) => {
                      const maxScore = tutorQuizzes[index].maxScore || 100;
                      return sum + ((a.score || 0) / maxScore) * 100;
                    }, 0);
                    quiz.avgScore = Math.round(totalScore / completedAttempts.length);
                  }
                },
                error: (err) => console.error('Error loading quiz attempts:', err)
              });
            });
          },
          error: (err) => console.error('Error loading quizzes:', err)
        });
      },
      error: (err) => console.error('Error loading courses:', err)
    });
  }

  loadRecentActivity(): void {
    this.http.get<any[]>(`${environment.apiUrl}/courses/tutor/${this.tutorId}`).subscribe({
      next: (courses) => {
        const courseIds = courses.map(c => c.id);
        
        this.http.get<any[]>(`${environment.apiUrl}/learning/quizzes`).subscribe({
          next: (allQuizzes) => {
            const tutorQuizzes = allQuizzes.filter(q => courseIds.includes(q.courseId));
            const quizMap = new Map(tutorQuizzes.map(q => [q.id, q.title]));
            
            let allAttempts: any[] = [];
            let processedQuizzes = 0;
            
            if (tutorQuizzes.length === 0) {
              this.recentActivity = [];
              return;
            }
            
            tutorQuizzes.forEach(quiz => {
              this.http.get<any[]>(`${environment.apiUrl}/learning/attempts/quiz/${quiz.id}`).subscribe({
                next: (attempts) => {
                  allAttempts = allAttempts.concat(
                    attempts
                      .filter(a => a.status === 'COMPLETED' && a.submittedAt)
                      .map(a => ({
                        ...a,
                        quizTitle: quizMap.get(quiz.id) || 'Unknown Quiz'
                      }))
                  );
                  
                  processedQuizzes++;
                  if (processedQuizzes === tutorQuizzes.length) {
                    // Sort by submission date and take top 3
                    this.recentActivity = allAttempts
                      .sort((a, b) => new Date(b.submittedAt).getTime() - new Date(a.submittedAt).getTime())
                      .slice(0, 3)
                      .map(attempt => ({
                        student: `Student #${attempt.studentId}`,
                        action: 'completed quiz',
                        quiz: attempt.quizTitle,
                        score: Math.round((attempt.score / (tutorQuizzes.find(q => q.id === attempt.quizId)?.maxScore || 100)) * 100),
                        time: this.getTimeAgo(attempt.submittedAt)
                      }));
                  }
                },
                error: () => {
                  processedQuizzes++;
                  if (processedQuizzes === tutorQuizzes.length && allAttempts.length > 0) {
                    this.recentActivity = allAttempts
                      .sort((a, b) => new Date(b.submittedAt).getTime() - new Date(a.submittedAt).getTime())
                      .slice(0, 3)
                      .map(attempt => ({
                        student: `Student #${attempt.studentId}`,
                        action: 'completed quiz',
                        quiz: attempt.quizTitle,
                        score: Math.round((attempt.score / 100) * 100),
                        time: this.getTimeAgo(attempt.submittedAt)
                      }));
                  }
                }
              });
            });
          },
          error: (err) => console.error('Error loading quizzes:', err)
        });
      },
      error: (err) => console.error('Error loading courses:', err)
    });
  }

  getTimeAgo(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    
    if (diffMins < 60) return `${diffMins} minutes ago`;
    const diffHours = Math.floor(diffMins / 60);
    if (diffHours < 24) return `${diffHours} hours ago`;
    const diffDays = Math.floor(diffHours / 24);
    return `${diffDays} days ago`;
  }
}
