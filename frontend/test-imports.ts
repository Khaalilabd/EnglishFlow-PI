// Test file to verify all imports work correctly
import { StudentCasesComponent } from './src/app/pages/tutor-panel/student-cases/student-cases.component';
import { StudentCaseDetailComponent } from './src/app/pages/tutor-panel/student-case-detail/student-case-detail.component';
import { TeachingQualityDashboardComponent } from './src/app/pages/tutor-panel/teaching-quality-dashboard/teaching-quality-dashboard.component';
import { StudentCaseService } from './src/app/core/services/student-case.service';
import { 
  StudentCaseCard, 
  StudentCaseDetail, 
  AcademicRiskLevel,
  ComplaintStatus 
} from './src/app/core/models/student-case.model';

console.log('All imports successful!');
console.log('Components:', {
  StudentCasesComponent,
  StudentCaseDetailComponent,
  TeachingQualityDashboardComponent
});
console.log('Service:', StudentCaseService);
console.log('Models:', { AcademicRiskLevel, ComplaintStatus });
