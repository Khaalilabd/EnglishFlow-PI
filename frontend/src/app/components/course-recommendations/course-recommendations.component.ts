import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { MlService, CourseRecommendation } from '../../services/ml.service';
import { CourseService } from '../../core/services/course.service';

export interface EnrichedRecommendation extends CourseRecommendation {
  course_id?: number;
  course_title?: string;
  course_description?: string;
  course_level?: string;
  course_credits?: number;
  course_thumbnail?: string;
}

@Component({
  selector: 'app-course-recommendations',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './course-recommendations.component.html',
  styleUrls: ['./course-recommendations.component.scss']
})
export class CourseRecommendationsComponent implements OnInit {
  @Input() studentId!: string;
  @Input() avgScore: number = 70;
  @Input() limit: number = 5;
  @Input() showTitle: boolean = true;

  recommendations: EnrichedRecommendation[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private mlService: MlService,
    private courseService: CourseService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadRecommendations();
  }

  loadRecommendations(): void {
    this.loading = true;
    this.error = null;

    const request$ = this.studentId
      ? this.mlService.recommendCourses(this.studentId, this.limit)
      : this.mlService.recommendCoursesForNewStudent(this.avgScore, this.limit);

    request$.subscribe({
      next: (response) => {
        // Enrichir les recommandations avec les détails des cours
        this.enrichRecommendations(response.recommendations);
      },
      error: (err) => {
        this.error = 'Impossible de charger les recommandations';
        this.loading = false;
        console.error('Erreur recommandations:', err);
      }
    });
  }

  /**
   * Enrichit les recommandations avec les détails complets des cours
   */
  private enrichRecommendations(recommendations: CourseRecommendation[]): void {
    if (recommendations.length === 0) {
      this.recommendations = [];
      this.loading = false;
      return;
    }

    // Récupérer tous les cours disponibles
    this.courseService.getAllCourses().pipe(
      catchError((err) => {
        console.error('Erreur getAllCourses:', err);
        return of([]);
      })
    ).subscribe({
      next: (response: any) => {
        console.log('Response type:', typeof response);
        console.log('Response is array:', Array.isArray(response));
        console.log('Response:', response);
        
        // S'assurer que courses est un tableau
        let courses: any[] = [];
        
        if (Array.isArray(response)) {
          courses = response;
        } else if (response && typeof response === 'object') {
          // Si c'est un objet, chercher le tableau dans les propriétés communes
          if ('data' in response && Array.isArray(response.data)) {
            courses = response.data;
          } else if ('courses' in response && Array.isArray(response.courses)) {
            courses = response.courses;
          } else if ('content' in response && Array.isArray(response.content)) {
            courses = response.content;
          } else {
            // Essayer de convertir l'objet en tableau
            const values = Object.values(response);
            courses = values.filter(v => v && typeof v === 'object');
          }
        }
        
        console.log('Courses extraits:', courses.length);
        
        // Afficher les premiers cours pour debug
        if (courses.length > 0) {
          console.log('Exemple de cours:', courses.slice(0, 3).map((c: any) => ({
            id: c.id,
            title: c.title,
            category: c.category,
            level: c.level
          })));
        }
        
        // Afficher les codes recherchés
        console.log('Codes ML recherchés:', recommendations.map(r => r.course_code));
        
        // Mapper les recommandations avec les détails des cours
        this.recommendations = recommendations.map((rec, index) => {
          // Chercher le cours par code (peut être le titre ou une partie du titre)
          let course = courses.find((c: any) => 
            c.title?.toLowerCase().includes(rec.course_code.toLowerCase()) ||
            c.id?.toString() === rec.course_code ||
            c.category?.toLowerCase() === rec.course_code.toLowerCase()
          );

          // Si aucun match trouvé et que les codes sont génériques (AAA, BBB, etc.)
          // Mapper vers un vrai cours de la base
          if (!course && /^[A-Z]{3}$/.test(rec.course_code) && courses.length > 0) {
            // Utiliser un index basé sur le score de recommandation pour avoir une distribution cohérente
            const courseIndex = Math.floor((rec.recommendation_score * courses.length)) % courses.length;
            course = courses[courseIndex];
            console.log(`🔄 Mapping générique ${rec.course_code} → ${course.title}`);
          }

          if (course) {
            console.log(`✅ Match trouvé pour ${rec.course_code}:`, course.title);
          } else {
            console.log(`❌ Aucun match pour ${rec.course_code}`);
          }

          return {
            ...rec,
            course_id: course?.id,
            course_name: rec.course_name || course?.title || rec.course_code,
            course_title: course?.title,
            course_description: course?.description,
            course_level: course?.level,
            course_credits: course?.duration,
            course_thumbnail: course?.thumbnailUrl
          };
        });
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors de l\'enrichissement:', err);
        // Utiliser les recommandations sans enrichissement
        this.recommendations = recommendations as EnrichedRecommendation[];
        this.loading = false;
      }
    });
  }

  getScoreColor(score: number): string {
    if (score >= 0.8) return '#48bb78';
    if (score >= 0.6) return '#ed8936';
    return '#f56565';
  }

  getSuccessRateClass(rate: number): string {
    if (rate >= 0.75) return 'high';
    if (rate >= 0.60) return 'medium';
    return 'low';
  }

  viewCourse(recommendation: EnrichedRecommendation): void {
    // Naviguer vers la page du cours avec l'ID réel
    if (recommendation.course_id) {
      // Utiliser la route existante dans user-panel
      this.router.navigate(['/user-panel/course', recommendation.course_id]);
    } else {
      console.error('Course ID not found for:', recommendation);
      alert('Course details not available');
    }
  }

  enrollCourse(recommendation: EnrichedRecommendation): void {
    // Logique d'inscription au cours
    if (recommendation.course_id) {
      console.log('Enrolling in course:', recommendation.course_id);
      // TODO: Implémenter l'inscription via CourseService
      alert('Enrollment feature coming soon!');
    } else {
      console.error('Course ID not found for enrollment');
      alert('Cannot enroll: Course not found');
    }
  }

  getCourseDisplayName(rec: EnrichedRecommendation): string {
    return rec.course_title || rec.course_name || rec.course_code;
  }
}
