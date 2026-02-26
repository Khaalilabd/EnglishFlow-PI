import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Pack, PackStatus } from '../../../core/models/pack.model';
import { PackService } from '../../../core/services/pack.service';

@Component({
  selector: 'app-pack-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pack-management.component.html',
  styleUrls: ['./pack-management.component.scss']
})
export class PackManagementComponent implements OnInit {
  packs: Pack[] = [];
  filteredPacks: Pack[] = [];
  
  searchTerm = '';
  filterStatus: PackStatus | 'ALL' = 'ALL';
  
  PackStatus = PackStatus;

  constructor(
    private packService: PackService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPacks();
  }

  loadPacks(): void {
    this.packService.getAllPacks().subscribe({
      next: (packs) => {
        this.packs = packs;
        this.applyFilters();
      },
      error: (error) => console.error('Error loading packs:', error)
    });
  }

  createNewPack(): void {
    this.router.navigate(['/dashboard/packs/create']);
  }

  viewPackDetails(id: number): void {
    this.router.navigate(['/dashboard/packs', id]);
  }

  editPack(id: number): void {
    this.router.navigate(['/dashboard/packs/edit', id]);
  }

  deletePack(id: number): void {
    if (confirm('Are you sure you want to delete this pack?')) {
      this.packService.deletePack(id).subscribe({
        next: () => this.loadPacks(),
        error: (error) => console.error('Error deleting pack:', error)
      });
    }
  }

  applyFilters(): void {
    this.filteredPacks = this.packs.filter(pack => {
      const matchesSearch = !this.searchTerm || 
        pack.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        pack.tutorName.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesStatus = this.filterStatus === 'ALL' || pack.status === this.filterStatus;
      
      return matchesSearch && matchesStatus;
    });
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onFilterChange(): void {
    this.applyFilters();
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
}
