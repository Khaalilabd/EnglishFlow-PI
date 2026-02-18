# Exemples d'API - Club Service

## Afficher les clubs d'un étudiant avec ses rôles

### Endpoint
```
GET /api/clubs/user/{userId}/with-role
```

### Description
Récupère tous les clubs qu'un étudiant a rejoint avec son rôle dans chaque club.

### Exemple de requête
```bash
curl -X GET http://localhost:8084/api/clubs/user/123/with-role
```

### Exemple de réponse
```json
[
  {
    "id": 1,
    "name": "English Conversation Club",
    "description": "Practice English speaking skills through interactive conversations",
    "objective": "Improve fluency and confidence in English",
    "category": "LANGUAGE",
    "maxMembers": 20,
    "image": "data:image/png;base64,iVBORw0KG...",
    "status": "APPROVED",
    "createdBy": 123,
    "reviewedBy": 456,
    "reviewComment": "Great initiative!",
    "createdAt": "2024-01-15T10:00:00",
    "updatedAt": "2024-01-15T10:00:00",
    "userRole": "PRESIDENT",
    "joinedAt": "2024-01-15T10:00:00"
  },
  {
    "id": 2,
    "name": "Reading Club",
    "description": "Read and discuss English books together",
    "objective": "Improve reading comprehension and vocabulary",
    "category": "READING",
    "maxMembers": 15,
    "image": "data:image/png;base64,iVBORw0KG...",
    "status": "APPROVED",
    "createdBy": 789,
    "reviewedBy": 456,
    "reviewComment": "Approved",
    "createdAt": "2024-01-20T14:00:00",
    "updatedAt": "2024-01-20T14:00:00",
    "userRole": "MEMBER",
    "joinedAt": "2024-01-22T09:30:00"
  },
  {
    "id": 3,
    "name": "Writing Workshop",
    "description": "Improve writing skills through practice and feedback",
    "objective": "Master academic and creative writing",
    "category": "WRITING",
    "maxMembers": 12,
    "image": "data:image/png;base64,iVBORw0KG...",
    "status": "APPROVED",
    "createdBy": 555,
    "reviewedBy": 456,
    "reviewComment": "Excellent proposal",
    "createdAt": "2024-02-01T09:00:00",
    "updatedAt": "2024-02-01T09:00:00",
    "userRole": "SECRETARY",
    "joinedAt": "2024-02-03T11:15:00"
  }
]
```

### Utilisation dans Angular

#### Service TypeScript
```typescript
// club.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ClubWithRole {
  id: number;
  name: string;
  description: string;
  objective: string;
  category: string;
  maxMembers: number;
  image: string;
  status: string;
  createdBy: number;
  reviewedBy: number;
  reviewComment: string;
  createdAt: string;
  updatedAt: string;
  userRole: 'PRESIDENT' | 'SECRETARY' | 'MEMBER';
  joinedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClubService {
  private apiUrl = 'http://localhost:8080/api/clubs';

  constructor(private http: HttpClient) {}

  getMyClubsWithRole(userId: number): Observable<ClubWithRole[]> {
    return this.http.get<ClubWithRole[]>(`${this.apiUrl}/user/${userId}/with-role`);
  }
}
```

#### Component TypeScript
```typescript
// my-clubs.component.ts
import { Component, OnInit } from '@angular/core';
import { ClubService, ClubWithRole } from './club.service';

@Component({
  selector: 'app-my-clubs',
  templateUrl: './my-clubs.component.html'
})
export class MyClubsComponent implements OnInit {
  clubs: ClubWithRole[] = [];
  userId: number = 123; // Get from auth service

  constructor(private clubService: ClubService) {}

  ngOnInit(): void {
    this.loadMyClubs();
  }

  loadMyClubs(): void {
    this.clubService.getMyClubsWithRole(this.userId).subscribe({
      next: (clubs) => {
        this.clubs = clubs;
      },
      error: (error) => {
        console.error('Error loading clubs:', error);
      }
    });
  }

  getRoleBadgeClass(role: string): string {
    switch(role) {
      case 'PRESIDENT': return 'badge-president';
      case 'SECRETARY': return 'badge-secretary';
      case 'MEMBER': return 'badge-member';
      default: return 'badge-default';
    }
  }

  getRoleLabel(role: string): string {
    switch(role) {
      case 'PRESIDENT': return 'Président';
      case 'SECRETARY': return 'Secrétaire';
      case 'MEMBER': return 'Membre';
      default: return role;
    }
  }

  canManageClub(club: ClubWithRole): boolean {
    return club.userRole === 'PRESIDENT';
  }
}
```

#### Template HTML
```html
<!-- my-clubs.component.html -->
<div class="container">
  <h2>Mes Clubs</h2>
  
  <div class="clubs-grid">
    <div *ngFor="let club of clubs" class="club-card">
      <img [src]="club.image" [alt]="club.name" class="club-image">
      
      <div class="club-content">
        <div class="club-header">
          <h3>{{ club.name }}</h3>
          <span [class]="getRoleBadgeClass(club.userRole)" class="role-badge">
            {{ getRoleLabel(club.userRole) }}
          </span>
        </div>
        
        <p class="club-description">{{ club.description }}</p>
        
        <div class="club-meta">
          <span class="category">{{ club.category }}</span>
          <span class="joined-date">Rejoint le {{ club.joinedAt | date:'dd/MM/yyyy' }}</span>
        </div>
        
        <div class="club-actions">
          <button class="btn btn-primary" [routerLink]="['/clubs', club.id]">
            Voir le club
          </button>
          
          <button *ngIf="canManageClub(club)" 
                  class="btn btn-secondary" 
                  [routerLink]="['/clubs', club.id, 'manage']">
            Gérer
          </button>
        </div>
      </div>
    </div>
  </div>
  
  <div *ngIf="clubs.length === 0" class="no-clubs">
    <p>Vous n'avez rejoint aucun club pour le moment.</p>
    <button class="btn btn-primary" routerLink="/clubs/browse">
      Découvrir les clubs
    </button>
  </div>
</div>
```

#### Styles CSS
```css
/* my-clubs.component.css */
.clubs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.club-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s;
}

.club-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.club-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.club-content {
  padding: 15px;
}

.club-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.role-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.badge-president {
  background-color: #ffd700;
  color: #000;
}

.badge-secretary {
  background-color: #4169e1;
  color: #fff;
}

.badge-member {
  background-color: #90ee90;
  color: #000;
}

.club-meta {
  display: flex;
  justify-content: space-between;
  margin: 10px 0;
  font-size: 14px;
  color: #666;
}

.club-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.no-clubs {
  text-align: center;
  padding: 40px;
}
```

## Autres endpoints utiles

### Vérifier si un utilisateur est président
```
GET /api/members/club/{clubId}/user/{userId}/is-president
```

### Vérifier si un utilisateur est membre
```
GET /api/members/club/{clubId}/user/{userId}/is-member
```

### Obtenir tous les membres d'un club
```
GET /api/members/club/{clubId}
```
Retourne une liste avec les rôles de chaque membre.
