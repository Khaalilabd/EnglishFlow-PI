import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { environment } from '../../../../environments/environment';

Chart.register(...registerables);

interface AnalyticsStats {
  totalStudents: number;
  newStudentsThisMonth: number;
  avgSuccessRate: number;
  successRateChange: number;
  atRiskStudents: number;
  atRiskPercentage: number;
  avgStudyTime: number;
}

interface AtRiskStudent {
  id: number;
  name: string;
  packName: string;
  successRate: number;
  lastActivity: string;
  cluster?: string;
}

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.scss']
})
export class AnalyticsComponent implements OnInit, AfterViewInit {
  @ViewChild('performanceChart') performanceChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('riskChart') riskChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('engagementChart') engagementChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('completionChart') completionChartRef!: ElementRef<HTMLCanvasElement>;

  loading = true;
  selectedTimeRange = 'month';
  
  stats: AnalyticsStats = {
    totalStudents: 0,
    newStudentsThisMonth: 0,
    avgSuccessRate: 0,
    successRateChange: 0,
    atRiskStudents: 0,
    atRiskPercentage: 0,
    avgStudyTime: 0
  };

  atRiskStudents: AtRiskStudent[] = [];

  private performanceChart?: Chart;
  private riskChart?: Chart;
  private engagementChart?: Chart;
  private completionChart?: Chart;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadAnalytics();
  }

  ngAfterViewInit(): void {
    // Charts will be created after data is loaded
  }

  loadAnalytics(): void {
    this.loading = true;
    const tutorId = this.authService.currentUserValue?.id;

    if (!tutorId) {
      this.loading = false;
      return;
    }

    // Load analytics data
    this.http.get<any>(`${environment.apiUrl}/tutors/${tutorId}/analytics?range=${this.selectedTimeRange}`)
      .subscribe({
        next: (data) => {
          this.stats = data.stats;
          this.atRiskStudents = data.atRiskStudents;
          this.loading = false;
          
          // Create charts after data is loaded
          setTimeout(() => {
            this.createPerformanceChart(data.performanceData);
            this.createRiskChart(data.riskData);
            this.createEngagementChart(data.engagementData);
            this.createCompletionChart(data.completionData);
          }, 100);
        },
        error: (error) => {
          console.error('Error loading analytics:', error);
          this.loading = false;
          // Load mock data for demo
          this.loadMockData();
        }
      });
  }

  loadMockData(): void {
    // Mock stats
    this.stats = {
      totalStudents: 24,
      newStudentsThisMonth: 5,
      avgSuccessRate: 72,
      successRateChange: 8,
      atRiskStudents: 3,
      atRiskPercentage: 12.5,
      avgStudyTime: 4.5
    };

    // Mock at-risk students
    this.atRiskStudents = [
      { id: 1, name: 'John Doe', packName: 'Beginner Pack', successRate: 35, lastActivity: '2026-05-10' },
      { id: 2, name: 'Jane Smith', packName: 'Intermediate Pack', successRate: 42, lastActivity: '2026-05-11' },
      { id: 3, name: 'Bob Johnson', packName: 'Beginner Pack', successRate: 48, lastActivity: '2026-05-12' }
    ];

    // Create charts with mock data
    setTimeout(() => {
      this.createPerformanceChart({
        labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
        data: [65, 68, 70, 72]
      });

      this.createRiskChart({
        labels: ['Low Risk', 'Medium Risk', 'High Risk'],
        data: [15, 6, 3]
      });

      this.createEngagementChart({
        labels: ['Week 1', 'Week 2', 'Week 3', 'Week 4'],
        sessions: [45, 52, 48, 55],
        clicks: [1200, 1350, 1280, 1420]
      });

      this.createCompletionChart({
        labels: ['Beginner Pack', 'Intermediate Pack', 'Advanced Pack'],
        data: [75, 60, 45]
      });
    }, 100);
  }

  createPerformanceChart(data: any): void {
    if (this.performanceChart) {
      this.performanceChart.destroy();
    }

    const ctx = this.performanceChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    this.performanceChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: data.labels,
        datasets: [{
          label: 'Average Score',
          data: data.data,
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          tension: 0.4,
          fill: true,
          pointRadius: 4,
          pointHoverRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            padding: 12,
            titleFont: { size: 14 },
            bodyFont: { size: 13 }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            max: 100,
            ticks: {
              callback: (value: number | string) => value + '%'
            }
          }
        }
      }
    });
  }

  createRiskChart(data: any): void {
    if (this.riskChart) {
      this.riskChart.destroy();
    }

    const ctx = this.riskChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    this.riskChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: data.labels,
        datasets: [{
          data: data.data,
          backgroundColor: [
            '#10b981', // Green for low risk
            '#f59e0b', // Orange for medium risk
            '#ef4444'  // Red for high risk
          ],
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              padding: 15,
              font: { size: 12 }
            }
          }
        }
      }
    });
  }

  createEngagementChart(data: any): void {
    if (this.engagementChart) {
      this.engagementChart.destroy();
    }

    const ctx = this.engagementChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    this.engagementChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: data.labels,
        datasets: [
          {
            label: 'Sessions',
            data: data.sessions,
            backgroundColor: '#8b5cf6',
            borderRadius: 6
          },
          {
            label: 'Clicks (÷10)',
            data: data.clicks.map((c: number) => c / 10),
            backgroundColor: '#ec4899',
            borderRadius: 6
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              padding: 15,
              font: { size: 12 }
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true
          }
        }
      }
    });
  }

  createCompletionChart(data: any): void {
    if (this.completionChart) {
      this.completionChart.destroy();
    }

    const ctx = this.completionChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    this.completionChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: data.labels,
        datasets: [{
          label: 'Completion Rate',
          data: data.data,
          backgroundColor: '#06b6d4',
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        indexAxis: 'y',
        plugins: {
          legend: {
            display: false
          }
        },
        scales: {
          x: {
            beginAtZero: true,
            max: 100,
            ticks: {
              callback: (value: number | string) => value + '%'
            }
          }
        }
      }
    });
  }

  getRiskClass(successRate: number): string {
    if (successRate >= 70) return 'risk-low';
    if (successRate >= 50) return 'risk-medium';
    return 'risk-high';
  }
  
  getRiskLabel(successRate: number): string {
    if (successRate >= 70) return 'Low Risk';
    if (successRate >= 50) return 'Medium Risk';
    return 'High Risk';
  }
  
  getClusterClass(cluster: string | undefined): string {
    if (!cluster) return 'cluster-unknown';
    // Cluster 0: High Performers (Étudiants Performants)
    if (cluster.includes('High Performers') || cluster.includes('Performants')) return 'cluster-high';
    // Cluster 2: At Risk (Étudiants À Risque)
    if (cluster.includes('At Risk') || cluster.includes('Risque')) return 'cluster-low';
    // Cluster 1: Average Students (Étudiants Moyens)
    return 'cluster-medium';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) return 'Today';
    if (diffDays === 1) return 'Yesterday';
    if (diffDays < 7) return `${diffDays} days ago`;
    return date.toLocaleDateString();
  }

  contactStudent(student: AtRiskStudent): void {
    // Navigate to messaging or open contact modal
    console.log('Contact student:', student);
    alert(`Contact feature coming soon for ${student.name}`);
  }
}
