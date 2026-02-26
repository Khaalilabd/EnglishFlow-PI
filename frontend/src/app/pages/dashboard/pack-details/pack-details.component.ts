import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Pack, PackStatus } from '../../../core/models/pack.model';
import { PackService } from '../../../core/services/pack.service';
import { CourseService } from '../../../core/services/course.service';
import { Course } from '../../../core/models/course.model';

@Component({
  selector: 'app-pack-details',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './pack-details.component.html',
  styleUrls: ['./pack-details.component.scss']
})
export class PackDetailsComponent implements OnInit {
  pack: Pack | null = null;
  courses: Course[] = [];
  loading = true;
  PackStatus = PackStatus;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private packService: PackService,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    const packId = this.route.snapshot.paramMap.get('id');
    if (packId) {
      this.loadPackDetails(+packId);
    }
  }

  loadPackDetails(packId: number): void {
    this.packService.getById(packId).subscribe({
      next: (pack) => {
        this.pack = pack;
        this.loadCourses();
      },
      error: (error) => {
        console.error('Error loading pack:', error);
        this.loading = false;
      }
    });
  }

  loadCourses(): void {
    if (!this.pack || !this.pack.courseIds.length) {
      this.loading = false;
      return;
    }

    // Load all courses and filter by pack's courseIds
    this.courseService.getAllCourses().subscribe({
      next: (allCourses: Course[]) => {
        this.courses = allCourses.filter(c => 
          this.pack!.courseIds.includes(c.id!)
        );
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading courses:', error);
        this.loading = false;
      }
    });
  }

  editPack(): void {
    if (this.pack) {
      this.router.navigate(['/dashboard/packs'], { 
        queryParams: { edit: this.pack.id } 
      });
    }
  }

  deletePack(): void {
    if (!this.pack) return;
    
    if (confirm(`Are you sure you want to delete the pack "${this.pack.name}"?`)) {
      this.packService.deletePack(this.pack.id!).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/packs']);
        },
        error: (error) => console.error('Error deleting pack:', error)
      });
    }
  }

  getStatusBadgeClass(status: PackStatus): string {
    const classes: Record<string, string> = {
      [PackStatus.DRAFT]: 'bg-gray-100 text-gray-800',
      [PackStatus.ACTIVE]: 'bg-green-100 text-green-800',
      [PackStatus.INACTIVE]: 'bg-yellow-100 text-yellow-800',
      [PackStatus.ARCHIVED]: 'bg-red-100 text-red-800'
    };
    return classes[status] || 'bg-gray-100 text-gray-800';
  }

  formatDate(date: string | undefined): string {
    if (!date) return 'Not set';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard/packs']);
  }
}
