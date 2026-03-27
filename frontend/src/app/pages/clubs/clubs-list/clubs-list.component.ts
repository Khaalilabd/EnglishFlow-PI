import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ClubService } from '../../../core/services/club.service';
import { Club } from '../../../core/models/club.model';

@Component({
  selector: 'app-clubs-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './clubs-list.component.html',
  styleUrls: ['./clubs-list.component.scss']
})
export class ClubsListComponent implements OnInit {
  clubs: Club[] = [];
  loading = false;
  error: string | null = null;

  constructor(private clubService: ClubService) {}

  ngOnInit() {
    this.loadClubs();
  }

  loadClubs() {
    this.loading = true;
    this.error = null;
    
    this.clubService.getAllClubs().subscribe({
      next: (clubs) => {
        this.clubs = clubs;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading clubs:', err);
        this.error = 'Failed to load clubs. Please try again.';
        this.loading = false;
      }
    });
  }

  deleteClub(id: number) {
    if (confirm('Are you sure you want to delete this club?')) {
      this.clubService.deleteClub(id).subscribe({
        next: () => {
          this.loadClubs();
        },
        error: (err) => {
          console.error('Error deleting club:', err);
          alert('Failed to delete club. Please try again.');
        }
      });
    }
  }

  getCategoryBadgeClass(category: string): string {
    const classes: { [key: string]: string } = {
      'CONVERSATION': 'text-blue-800 bg-blue-100',
      'BOOK': 'text-green-800 bg-green-100',
      'DRAMA': 'text-orange-800 bg-orange-100',
      'WRITING': 'text-purple-800 bg-purple-100'
    };
    return classes[category] || 'text-gray-800 bg-gray-100';
  }

  getClubsByCategory(category: string) {
    return this.clubs.filter(club => club.category === category);
  }
}
