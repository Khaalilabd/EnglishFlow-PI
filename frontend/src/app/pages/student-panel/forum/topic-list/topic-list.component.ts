import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ForumService, Topic, CreateTopicRequest } from '../../../../services/forum.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-topic-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  providers: [ForumService],
  templateUrl: './topic-list.component.html',
  styleUrl: './topic-list.component.scss'
})
export class TopicListComponent implements OnInit {
  topics: Topic[] = [];
  subCategoryId!: number;
  subCategoryName = '';
  loading = true;
  error: string | null = null;
  
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;

  // Modal state
  showNewTopicModal = false;
  showEditTopicModal = false;
  showDeleteConfirmModal = false;
  
  newTopic = {
    title: '',
    content: ''
  };

  editingTopic: Topic | null = null;
  deletingTopicId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private forumService: ForumService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.subCategoryId = +params['subCategoryId'];
      this.subCategoryName = params['subCategoryName'] || 'Subcategory';
      this.loadTopics();
    });
  }

  loadTopics(): void {
    this.loading = true;
    this.error = null;
    
    this.forumService.getTopicsBySubCategory(this.subCategoryId, this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.topics = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading topics:', err);
        this.error = 'Error loading topics';
        this.loading = false;
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

  openEditTopicModal(event: Event, topic: Topic): void {
    event.stopPropagation(); // Empêcher la navigation vers le topic
    this.editingTopic = topic;
    this.newTopic = {
      title: topic.title,
      content: topic.content
    };
    this.showEditTopicModal = true;
  }

  closeEditTopicModal(): void {
    this.showEditTopicModal = false;
    this.editingTopic = null;
    this.resetForm();
  }

  openDeleteConfirmModal(event: Event, topicId: number): void {
    event.stopPropagation(); // Empêcher la navigation vers le topic
    
    Swal.fire({
      title: 'Delete Topic',
      text: 'Are you sure you want to delete this topic? All associated replies will also be deleted.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.deleteTopic(topicId);
      }
    });
  }

  closeDeleteConfirmModal(): void {
    this.showDeleteConfirmModal = false;
    this.deletingTopicId = null;
  }

  submitNewTopic(): void {
    if (this.isFormValid()) {
      const request: CreateTopicRequest = {
        subCategoryId: this.subCategoryId,
        title: this.newTopic.title,
        content: this.newTopic.content,
        userId: 1, // TODO: Get from auth service
        userName: 'User' // TODO: Get from auth service
      };

      this.forumService.createTopic(request).subscribe({
        next: (topic) => {
          console.log('Topic created:', topic);
          this.closeNewTopicModal();
          this.loadTopics(); // Recharger la liste
        },
        error: (err) => {
          console.error('Error creating topic:', err);
          alert('❌ Error creating topic');
        }
      });
    }
  }

  submitEditTopic(): void {
    if (this.isFormValid() && this.editingTopic) {
      const request: CreateTopicRequest = {
        subCategoryId: this.subCategoryId,
        title: this.newTopic.title,
        content: this.newTopic.content,
        userId: this.editingTopic.userId,
        userName: this.editingTopic.userName
      };

      this.forumService.updateTopic(this.editingTopic.id, request).subscribe({
        next: (topic) => {
          console.log('Topic updated:', topic);
          this.closeEditTopicModal();
          this.loadTopics(); // Recharger la liste
        },
        error: (err) => {
          console.error('Error updating topic:', err);
          alert('❌ Error updating topic');
        }
      });
    }
  }

  confirmDeleteTopic(): void {
    if (this.deletingTopicId) {
      this.forumService.deleteTopic(this.deletingTopicId).subscribe({
        next: () => {
          console.log('Topic deleted');
          this.closeDeleteConfirmModal();
          this.loadTopics(); // Recharger la liste
        },
        error: (err) => {
          console.error('Error deleting topic:', err);
          alert('❌ Error deleting topic');
        }
      });
    }
  }

  deleteTopic(topicId: number): void {
    this.forumService.deleteTopic(topicId).subscribe({
      next: () => {
        Swal.fire({
          title: 'Deleted!',
          text: 'The topic has been successfully deleted.',
          icon: 'success',
          timer: 2000,
          showConfirmButton: false
        });
        this.loadTopics(); // Recharger la liste
      },
      error: (err) => {
        console.error('Error deleting topic:', err);
        Swal.fire({
          title: 'Error',
          text: 'Error deleting topic',
          icon: 'error',
          confirmButtonColor: '#dc2626'
        });
      }
    });
  }

  canEditOrDelete(topic: Topic): boolean {
    // TODO: Vérifier si l'utilisateur connecté est l'auteur du topic
    // Pour l'instant, on permet à tout le monde (à des fins de test)
    return true;
  }

  isFormValid(): boolean {
    return !!(this.newTopic.title.trim() && this.newTopic.content.trim());
  }

  resetForm(): void {
    this.newTopic = {
      title: '',
      content: ''
    };
  }

  goToTopic(topicId: number): void {
    // Détecter si on est dans le dashboard ou le student panel
    const currentUrl = this.router.url;
    if (currentUrl.includes('/dashboard/')) {
      this.router.navigate(['/dashboard/forum/topic', topicId]);
    } else {
      this.router.navigate(['/user-panel/forum/topic', topicId]);
    }
  }

  goBack(): void {
    // Détecter si on vient du dashboard ou du student panel
    const currentUrl = this.router.url;
    if (currentUrl.includes('/dashboard/')) {
      this.router.navigate(['/dashboard/forum']);
    } else {
      this.router.navigate(['/user-panel/forum']);
    }
  }

  getTimeAgo(date: string): string {
    const now = new Date();
    const created = new Date(date);
    const diffMs = now.getTime() - created.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} min ago`;
    if (diffHours < 24) return `${diffHours}h ago`;
    if (diffDays < 7) return `${diffDays}d ago`;
    return created.toLocaleDateString('en-US');
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadTopics();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTopics();
    }
  }
}
