import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClubService } from '../../../core/services/club.service';
import { Club, Member } from '../../../core/models/club.model';

@Component({
  selector: 'app-club-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './club-detail.component.html',
  styleUrls: ['./club-detail.component.scss']
})
export class ClubDetailComponent implements OnInit {
  club: Club | null = null;
  members: Member[] = [];
  loading = false;
  loadingMembers = false;
  error: string | null = null;
  clubId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clubService: ClubService
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.clubId = +params['id'];
      this.loadClub();
      this.loadMembers();
    });
  }

  loadClub() {
    this.loading = true;
    this.error = null;

    this.clubService.getClubById(this.clubId).subscribe({
      next: (club) => {
        this.club = club;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading club:', err);
        this.error = 'Failed to load club details.';
        this.loading = false;
      }
    });
  }

  loadMembers() {
    this.loadingMembers = true;

    this.clubService.getClubMembers(this.clubId).subscribe({
      next: (members) => {
        this.members = members;
        this.loadingMembers = false;
      },
      error: (err) => {
        console.error('Error loading members:', err);
        this.loadingMembers = false;
      }
    });
  }

  deleteClub() {
    if (confirm('Are you sure you want to delete this club? This action cannot be undone.')) {
      this.clubService.deleteClub(this.clubId).subscribe({
        next: () => {
          this.router.navigate(['/dashboard/clubs']);
        },
        error: (err) => {
          console.error('Error deleting club:', err);
          alert('Failed to delete club. Please try again.');
        }
      });
    }
  }

  removeMember(userId: number) {
    if (confirm('Are you sure you want to remove this member?')) {
      this.clubService.leaveClub(this.clubId, userId).subscribe({
        next: () => {
          this.loadMembers();
          this.loadClub();
        },
        error: (err) => {
          console.error('Error removing member:', err);
          alert('Failed to remove member. Please try again.');
        }
      });
    }
  }

  showAddMemberModal() {
    const userId = prompt('Enter user ID to add:');
    if (userId) {
      this.clubService.joinClub(this.clubId, { userId: +userId }).subscribe({
        next: () => {
          this.loadMembers();
          this.loadClub();
        },
        error: (err) => {
          console.error('Error adding member:', err);
          alert('Failed to add member. Please try again.');
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
}
