import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PackEnrollmentService } from '../../../core/services/pack-enrollment.service';
import { PackService } from '../../../core/services/pack.service';
import { AuthService } from '../../../core/services/auth.service';
import { MlService, PredictionResponse, ClusteringResponse } from '../../../services/ml.service';
import { StudentAnalyticsService } from '../../../services/student-analytics.service';
import { PackEnrollment } from '../../../core/models/pack-enrollment.model';
import { Pack } from '../../../core/models/pack.model';

interface EnrollmentWithPrediction extends PackEnrollment {
  prediction?: PredictionResponse;
  clustering?: ClusteringResponse;
  loadingPrediction?: boolean;
  loadingClustering?: boolean;
  realAnalytics?: any; // Vraies données de la BD
}

@Component({
  selector: 'app-my-students',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './my-students.component.html',
  styleUrls: ['./my-students.component.scss']
})
export class MyStudentsComponent implements OnInit {
  enrollments: EnrollmentWithPrediction[] = [];
  packs: Pack[] = [];
  selectedPackId: number | null = null;
  filteredEnrollments: EnrollmentWithPrediction[] = [];
  selectedStudent: EnrollmentWithPrediction | null = null;
  showStudentModal = false;
  
  loading = true;
  
  // Stats
  totalStudents = 0;
  activeStudents = 0;
  completedStudents = 0;
  averageProgress = 0;
  atRiskStudents = 0;

  constructor(
    private packEnrollmentService: PackEnrollmentService,
    private packService: PackService,
    private authService: AuthService,
    private mlService: MlService,
    private studentAnalyticsService: StudentAnalyticsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadTutorData();
  }

  loadTutorData(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return;

    this.loading = true;

    // Load tutor's packs
    this.packService.getByTutorId(currentUser.id).subscribe({
      next: (packs) => {
        this.packs = packs;
        
        // Load enrollments for this tutor
        this.loadEnrollments(currentUser.id);
      },
      error: (error) => {
        console.error('Error loading packs:', error);
        this.loading = false;
      }
    });
  }

  loadEnrollments(tutorId: number): void {
    this.packEnrollmentService.getByTutorId(tutorId).subscribe({
      next: (enrollments) => {
        // Filter enrollments to only show those for packs that still exist
        const packIds = this.packs.map(p => p.id);
        this.enrollments = enrollments.filter(e => packIds.includes(e.packId));
        this.filteredEnrollments = this.enrollments;
        this.calculateStats();
        this.loadPredictions();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading enrollments:', error);
        this.loading = false;
      }
    });
  }

  loadPredictions(): void {
    // Charger les prédictions ML pour chaque étudiant avec VRAIES données
    this.enrollments.forEach(enrollment => {
      enrollment.loadingPrediction = true;
      enrollment.loadingClustering = true;
      
      // Récupérer les VRAIES analytics depuis la BD (via StudentAnalyticsService pour avoir le bon mapping)
      this.studentAnalyticsService.getStudentAnalytics(enrollment.studentId).subscribe({
        next: (analytics) => {
          console.log(`✅ Vraies analytics pour étudiant ${enrollment.studentId}:`, analytics);
          
          // STOCKER les vraies analytics dans l'enrollment (déjà converties avec sessions, assessments, etc.)
          enrollment.realAnalytics = analytics;
          
          // Utiliser les vraies données pour la prédiction
          const predictionData = this.mlService.calculatePredictionData(analytics);
          console.log(`📊 Données prédiction pour étudiant ${enrollment.studentId}:`, predictionData);
          
          this.loadPredictionAndClustering(enrollment, predictionData);
        },
        error: (err) => {
          console.error(`❌ Erreur récupération analytics pour étudiant ${enrollment.studentId}:`, err);
          console.warn(`⚠️ Utilisation de données par défaut pour étudiant ${enrollment.studentId}`);
          
          // Pas de vraies analytics disponibles
          enrollment.realAnalytics = null;
          
          // Fallback vers données minimales si erreur
          const fallbackData = this.mlService.calculatePredictionData({
            previousAttempts: 0,
            credits: 0,
            totalClicks: 0,
            sessions: 0,
            avgClicks: 0,
            maxClicks: 0,
            avgScore: 0,
            minScore: 0,
            maxScore: 0,
            assessments: 0,
            registrationDate: -30,
            isUnregistered: 0,
            dataQuality: 'low',
            lastUpdated: new Date()
          });
          
          this.loadPredictionAndClustering(enrollment, fallbackData);
        }
      });
    });
  }

  private loadPredictionAndClustering(enrollment: EnrollmentWithPrediction, predictionData: any): void {
      // Prédiction
      this.mlService.predictStudentSuccess(predictionData).subscribe({
        next: (prediction) => {
          enrollment.prediction = prediction;
          enrollment.loadingPrediction = false;
          this.calculateStats();
        },
        error: (err) => {
          console.error('Erreur prédiction:', err);
          enrollment.loadingPrediction = false;
        }
      });

      // Clustering - avec tous les champs requis
      const clusteringData = {
        num_of_prev_attempts: predictionData.num_of_prev_attempts,
        studied_credits: predictionData.studied_credits,
        total_clicks: predictionData.total_clicks,
        nb_sessions: predictionData.nb_sessions,
        avg_clicks: predictionData.avg_clicks,
        max_clicks: predictionData.max_clicks,
        avg_score: predictionData.avg_score,
        min_score: predictionData.min_score,
        max_score: predictionData.max_score,
        nb_assessments: predictionData.nb_assessments,
        nb_tma: Math.floor(predictionData.nb_assessments * 0.6), // TMA = 60% des assessments
        nb_cma: Math.floor(predictionData.nb_assessments * 0.3), // CMA = 30% des assessments
        nb_exams: Math.floor(predictionData.nb_assessments * 0.1), // Exams = 10% des assessments
        date_registration: predictionData.date_registration,
        is_unregistered: predictionData.is_unregistered,
        module_presentation_length: 180 // Durée standard d'un module (en jours)
      };

      this.mlService.identifyStudentCluster(clusteringData).subscribe({
        next: (clustering) => {
          enrollment.clustering = clustering;
          enrollment.loadingClustering = false;
        },
        error: (err) => {
          console.error('Erreur clustering:', err);
          enrollment.loadingClustering = false;
        }
      });
  }

  calculateStats(): void {
    this.totalStudents = this.enrollments.length;
    this.activeStudents = this.enrollments.filter(e => e.status === 'ACTIVE').length;
    this.completedStudents = this.enrollments.filter(e => e.status === 'COMPLETED').length;
    this.atRiskStudents = this.enrollments.filter(e => 
      e.prediction && e.prediction.probability.succes < 0.5
    ).length;
    
    if (this.enrollments.length > 0) {
      const totalProgress = this.enrollments.reduce((sum, e) => sum + (e.progressPercentage || 0), 0);
      this.averageProgress = Math.round(totalProgress / this.enrollments.length);
    }
  }

  filterByPack(packId: number | null): void {
    this.selectedPackId = packId;
    
    if (packId === null) {
      this.filteredEnrollments = this.enrollments;
    } else {
      this.filteredEnrollments = this.enrollments.filter(e => e.packId === packId);
    }
  }

  getPackEnrollmentCount(packId: number): number {
    return this.enrollments.filter(e => e.packId === packId).length;
  }

  getPackName(packId: number): string {
    const pack = this.packs.find(p => p.id === packId);
    return pack?.name || 'Unknown Pack';
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'status-active';
      case 'COMPLETED':
        return 'status-completed';
      case 'CANCELLED':
        return 'status-cancelled';
      default:
        return 'status-default';
    }
  }

  getProgressColor(progress: number): string {
    // Limiter à 100% au cas où
    const safeProgress = Math.min(progress, 100);
    
    if (safeProgress >= 80) return '#10b981';
    if (safeProgress >= 50) return '#3b82f6';
    if (safeProgress >= 25) return '#f59e0b';
    return '#6b7280';
  }
  
  getSafeProgress(progress: number | undefined): number {
    if (!progress) return 0;
    return Math.min(Math.max(progress, 0), 100); // Entre 0 et 100
  }

  viewStudentProgress(enrollment: PackEnrollment): void {
    // Ouvrir le modal avec les détails de l'étudiant
    this.selectedStudent = enrollment;
    this.showStudentModal = true;
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  }

  getRiskLevelClass(prediction?: PredictionResponse): string {
    if (!prediction) return 'risk-unknown';
    const successProb = prediction.probability.succes;
    if (successProb >= 0.7) return 'risk-low';
    if (successProb >= 0.5) return 'risk-medium';
    return 'risk-high';
  }

  getRiskLevelText(prediction?: PredictionResponse): string {
    if (!prediction) return 'Analyzing...';
    const successProb = prediction.probability.succes;
    const safeSuccessRate = this.getSafeProgress(successProb * 100);
    if (successProb >= 0.7) return `✅ Low Risk (${safeSuccessRate}%)`;
    if (successProb >= 0.5) return `⚠️ Medium (${safeSuccessRate}%)`;
    return `🚨 At Risk (${safeSuccessRate}%)`;
  }

  getClusterBadgeClass(clustering?: ClusteringResponse): string {
    if (!clustering) return 'cluster-unknown';
    switch (clustering.cluster) {
      case 0: return 'cluster-high';
      case 1: return 'cluster-medium';
      case 2: return 'cluster-low';
      default: return 'cluster-unknown';
    }
  }

  getClusterIcon(clustering?: ClusteringResponse): string {
    if (!clustering) return '❓';
    switch (clustering.cluster) {
      case 0: return '🌟';
      case 1: return '📚';
      case 2: return '🎯';
      default: return '❓';
    }
  }
}
