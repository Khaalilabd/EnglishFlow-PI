import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MlService, PredictionResponse } from '../../services/ml.service';

interface StudentWithPrediction {
  id: string;
  name: string;
  email: string;
  prediction: PredictionResponse;
  loading?: boolean;
}

@Component({
  selector: 'app-at-risk-students',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './at-risk-students.component.html',
  styleUrls: ['./at-risk-students.component.scss']
})
export class AtRiskStudentsComponent implements OnInit {
  @Input() students: any[] = [];
  @Input() maxDisplay: number = 5;

  atRiskStudents: StudentWithPrediction[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private mlService: MlService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.students && this.students.length > 0) {
      this.analyzeStudents();
    }
  }

  analyzeStudents(): void {
    this.loading = true;
    this.error = null;

    // Analyser chaque étudiant
    const predictions = this.students.map(student => {
      const predictionData = this.mlService.calculatePredictionData(student);
      
      return this.mlService.predictStudentSuccess(predictionData).toPromise()
        .then(prediction => ({
          id: student.id,
          name: student.name || `${student.firstName} ${student.lastName}`,
          email: student.email,
          prediction
        }))
        .catch(() => null);
    });

    Promise.all(predictions).then(results => {
      // Filtrer les étudiants à risque (probabilité de succès < 50%)
      this.atRiskStudents = results
        .filter((r): r is StudentWithPrediction => 
          r !== null && r.prediction !== undefined && r.prediction.probability.succes < 0.5
        )
        .sort((a, b) => a.prediction.probability.succes - b.prediction.probability.succes)
        .slice(0, this.maxDisplay);

      this.loading = false;
    }).catch(err => {
      this.error = 'Erreur lors de l\'analyse des étudiants';
      this.loading = false;
      console.error('Erreur analyse:', err);
    });
  }

  getRiskLevel(successProb: number): string {
    if (successProb < 0.3) return 'Critique';
    if (successProb < 0.5) return 'Élevé';
    return 'Moyen';
  }

  getRiskColor(successProb: number): string {
    if (successProb < 0.3) return '#e53e3e';
    if (successProb < 0.5) return '#ed8936';
    return '#f6ad55';
  }

  viewStudent(studentId: string): void {
    this.router.navigate(['/students', studentId]);
  }

  contactStudent(student: StudentWithPrediction): void {
    // Logique pour contacter l'étudiant
    console.log('Contacter:', student.email);
    // TODO: Ouvrir modal de message ou rediriger vers messagerie
  }
}
