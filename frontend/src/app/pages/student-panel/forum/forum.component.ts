import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ForumService, Category } from '../../../services/forum.service';

@Component({
  selector: 'app-forum',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  providers: [ForumService],
  templateUrl: './forum.component.html',
  styleUrl: './forum.component.scss'
})
export class ForumComponent implements OnInit {
  categories: Category[] = [];
  loading = true;
  error: string | null = null;

  constructor(private forumService: ForumService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.error = null;
    this.forumService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.loading = false;
        
        // Si aucune catégorie, initialiser
        if (!data || data.length === 0) {
          this.initializeCategories();
        }
      },
      error: (err) => {
        console.error('Error loading categories:', err);
        this.error = 'Erreur lors du chargement des catégories';
        this.loading = false;
      }
    });
  }

  initializeCategories(): void {
    this.forumService.initializeCategories().subscribe({
      next: () => {
        console.log('Categories initialized');
        this.loadCategories();
      },
      error: (err) => {
        console.error('Error initializing categories:', err);
      }
    });
  }
}
