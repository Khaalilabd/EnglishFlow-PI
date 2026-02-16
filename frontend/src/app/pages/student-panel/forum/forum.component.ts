import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ForumService, Category, CreateTopicRequest } from '../../../services/forum.service';

interface NewTopicForm {
  categoryId: number;
  subCategoryId: number;
  title: string;
  content: string;
}

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

  // Modal state
  showNewTopicModal = false;
  
  // Form data
  newTopic: NewTopicForm = {
    categoryId: 0,
    subCategoryId: 0,
    title: '',
    content: ''
  };

  // Available subcategories based on selected category
  availableSubCategories: any[] = [];

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

  openNewTopicModal(): void {
    this.showNewTopicModal = true;
    this.resetForm();
  }

  closeNewTopicModal(): void {
    this.showNewTopicModal = false;
    this.resetForm();
  }

  onCategoryChange(): void {
    console.log('Category changed to:', this.newTopic.categoryId);
    const selectedCategory = this.categories.find(c => c.id === this.newTopic.categoryId);
    console.log('Selected category:', selectedCategory);
    this.availableSubCategories = selectedCategory ? selectedCategory.subCategories : [];
    console.log('Available subcategories:', this.availableSubCategories);
    this.newTopic.subCategoryId = 0;
  }

  submitNewTopic(): void {
    if (this.isFormValid()) {
      const request: CreateTopicRequest = {
        subCategoryId: this.newTopic.subCategoryId,
        title: this.newTopic.title,
        content: this.newTopic.content,
        userId: 1, // TODO: Get from auth service
        userName: 'Utilisateur' // TODO: Get from auth service
      };

      this.forumService.createTopic(request).subscribe({
        next: (topic) => {
          console.log('Topic created:', topic);
          alert('✅ Sujet créé avec succès! Vous pouvez le voir dans la sous-catégorie correspondante.');
          this.closeNewTopicModal();
        },
        error: (err) => {
          console.error('Error creating topic:', err);
          alert('❌ Erreur lors de la création du sujet: ' + (err.error?.message || err.message));
        }
      });
    }
  }

  isFormValid(): boolean {
    return !!(
      this.newTopic.categoryId &&
      this.newTopic.subCategoryId &&
      this.newTopic.title.trim() &&
      this.newTopic.content.trim()
    );
  }

  resetForm(): void {
    this.newTopic = {
      categoryId: 0,
      subCategoryId: 0,
      title: '',
      content: ''
    };
    this.availableSubCategories = [];
  }
}
