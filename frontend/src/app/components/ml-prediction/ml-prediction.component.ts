import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MlService, PredictionResponse } from '../../services/ml.service';

interface AnalyzedFactor {
  icon: string;
  name: string;
  value: string;
  impact: string;
  impactIcon: string;
  impactClass: string;
}

@Component({
  selector: 'app-ml-prediction',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ml-prediction.component.html',
  styleUrls: ['./ml-prediction.component.scss']
})
export class MlPredictionComponent implements OnInit {
  @Input() studentId!: string;
  @Input() studentData: any;

  prediction: PredictionResponse | null = null;
  loading = false;
  error: string | null = null;
  analyzedFactors: AnalyzedFactor[] = [];

  constructor(private mlService: MlService) {}

  ngOnInit(): void {
    if (this.studentData) {
      this.loadPrediction();
    }
  }

  loadPrediction(): void {
    this.loading = true;
    this.error = null;

    const predictionData = this.mlService.calculatePredictionData(this.studentData);

    this.mlService.predictStudentSuccess(predictionData).subscribe({
      next: (response) => {
        this.prediction = response;
        this.calculateAnalyzedFactors(predictionData);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Unable to load prediction';
        this.loading = false;
        console.error('Prediction error:', err);
      }
    });
  }

  calculateAnalyzedFactors(data: any): void {
    this.analyzedFactors = [
      {
        icon: '📊',
        name: 'Average Score',
        value: `${data.avg_score.toFixed(1)}%`,
        impact: this.getScoreImpact(data.avg_score),
        impactIcon: this.getImpactIcon(data.avg_score, 70, 50),
        impactClass: this.getImpactClass(data.avg_score, 70, 50)
      },
      {
        icon: '🎯',
        name: 'Total Assessments',
        value: `${data.nb_assessments}`,
        impact: this.getAssessmentImpact(data.nb_assessments),
        impactIcon: this.getImpactIcon(data.nb_assessments, 10, 5),
        impactClass: this.getImpactClass(data.nb_assessments, 10, 5)
      },
      {
        icon: '👆',
        name: 'Platform Engagement',
        value: `${data.total_clicks} clicks`,
        impact: this.getEngagementImpact(data.total_clicks),
        impactIcon: this.getImpactIcon(data.total_clicks, 500, 200),
        impactClass: this.getImpactClass(data.total_clicks, 500, 200)
      },
      {
        icon: '📅',
        name: 'Study Sessions',
        value: `${data.nb_sessions}`,
        impact: this.getSessionImpact(data.nb_sessions),
        impactIcon: this.getImpactIcon(data.nb_sessions, 30, 15),
        impactClass: this.getImpactClass(data.nb_sessions, 30, 15)
      },
      {
        icon: '📚',
        name: 'Studied Credits',
        value: `${data.studied_credits}`,
        impact: this.getCreditsImpact(data.studied_credits),
        impactIcon: this.getImpactIcon(data.studied_credits, 40, 20),
        impactClass: this.getImpactClass(data.studied_credits, 40, 20)
      },
      {
        icon: '🔄',
        name: 'Previous Attempts',
        value: `${data.num_of_prev_attempts}`,
        impact: this.getAttemptsImpact(data.num_of_prev_attempts),
        impactIcon: data.num_of_prev_attempts === 0 ? '✓' : data.num_of_prev_attempts <= 2 ? '⚠' : '✗',
        impactClass: data.num_of_prev_attempts === 0 ? 'positive' : data.num_of_prev_attempts <= 2 ? 'neutral' : 'negative'
      }
    ];
  }

  getScoreImpact(score: number): string {
    if (score >= 70) return 'Strong positive impact';
    if (score >= 50) return 'Moderate impact';
    return 'Needs improvement';
  }

  getAssessmentImpact(count: number): string {
    if (count >= 10) return 'Excellent practice';
    if (count >= 5) return 'Good practice';
    return 'More practice needed';
  }

  getEngagementImpact(clicks: number): string {
    if (clicks >= 500) return 'Highly engaged';
    if (clicks >= 200) return 'Moderately engaged';
    return 'Low engagement';
  }

  getSessionImpact(sessions: number): string {
    if (sessions >= 30) return 'Consistent learner';
    if (sessions >= 15) return 'Regular learner';
    return 'Irregular learning';
  }

  getCreditsImpact(credits: number): string {
    if (credits >= 40) return 'Extensive experience';
    if (credits >= 20) return 'Good experience';
    return 'Limited experience';
  }

  getAttemptsImpact(attempts: number): string {
    if (attempts === 0) return 'First attempt';
    if (attempts <= 2) return 'Few retries';
    return 'Multiple retries';
  }

  getImpactIcon(value: number, goodThreshold: number, okThreshold: number): string {
    if (value >= goodThreshold) return '✓';
    if (value >= okThreshold) return '⚠';
    return '✗';
  }

  getImpactClass(value: number, goodThreshold: number, okThreshold: number): string {
    if (value >= goodThreshold) return 'positive';
    if (value >= okThreshold) return 'neutral';
    return 'negative';
  }

  getRiskColor(): string {
    if (!this.prediction) return 'gray';
    
    switch (this.prediction.risk_level) {
      case 'low': return '#10b981';
      case 'medium': return '#f59e0b';
      case 'high': return '#ef4444';
      default: return 'gray';
    }
  }

  getRiskIcon(): string {
    if (!this.prediction) return '❓';
    
    switch (this.prediction.risk_level) {
      case 'low': return '✅';
      case 'medium': return '⚠️';
      case 'high': return '🚨';
      default: return '❓';
    }
  }

  getSuccessPercentage(): number {
    return this.prediction ? Math.round(this.prediction.probability.succes * 100) : 0;
  }
}
