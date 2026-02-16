import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ForumService, Category, Topic, Post } from '../../../services/forum.service';
import Swal from 'sweetalert2';

interface TabType {
  id: 'categories' | 'topics' | 'posts';
  label: string;
  icon: string;
}

interface NewTopicForm {
  categoryId: number;
  subCategoryId: number;
  title: string;
  content: string;
}

interface NewCategoryForm {
  name: string;
  description: string;
  icon: string;
  color: string;
}

interface NewSubCategoryForm {
  categoryId: number;
  name: string;
  description: string;
}

@Component({
  selector: 'app-forum-management',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './forum-management.component.html',
  styleUrl: './forum-management.component.scss'
})
export class ForumManagementComponent implements OnInit {
  activeTab: 'categories' | 'topics' | 'posts' = 'categories';
  
  tabs: TabType[] = [
    { id: 'categories', label: 'Categories', icon: 'fa-folder-tree' },
    { id: 'topics', label: 'Topics', icon: 'fa-comments' },
    { id: 'posts', label: 'Posts', icon: 'fa-comment-dots' }
  ];

  // Categories
  categories: Category[] = [];
  loadingCategories = true;

  // Topics
  allTopics: Topic[] = [];
  loadingTopics = true;
  selectedCategoryForTopics = 0;

  // Posts
  allPosts: Post[] = [];
  loadingPosts = true;
  selectedTopicForPosts = 0;

  // Pagination
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;

  // Modals
  showNewTopicModal = false;
  showNewCategoryModal = false;
  showNewSubCategoryModal = false;

  // Forms
  newTopic: NewTopicForm = {
    categoryId: 0,
    subCategoryId: 0,
    title: '',
    content: ''
  };

  newCategory: NewCategoryForm = {
    name: '',
    description: '',
    icon: 'fa-folder',
    color: 'primary'
  };

  newSubCategory: NewSubCategoryForm = {
    categoryId: 0,
    name: '',
    description: ''
  };

  availableSubCategories: any[] = [];

  constructor(private forumService: ForumService) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  switchTab(tab: 'categories' | 'topics' | 'posts'): void {
    this.activeTab = tab;
    
    if (tab === 'topics' && this.allTopics.length === 0) {
      this.loadAllTopics();
    } else if (tab === 'posts' && this.allPosts.length === 0) {
      this.loadAllPosts();
    }
  }

  // Categories Management
  loadCategories(): void {
    this.loadingCategories = true;
    this.forumService.getAllCategories().subscribe({
      next: (data) => {
        this.categories = data;
        this.loadingCategories = false;
      },
      error: (err) => {
        console.error('Error loading categories:', err);
        this.loadingCategories = false;
        Swal.fire({
          title: 'Error',
          text: 'Failed to load categories',
          icon: 'error',
          confirmButtonColor: '#dc2626'
        });
      }
    });
  }

  // Topics Management
  loadAllTopics(): void {
    this.loadingTopics = true;
    
    // Charger les topics de toutes les sous-catégories
    if (this.categories.length > 0) {
      const allSubCategories = this.categories.flatMap(cat => cat.subCategories);
      
      if (allSubCategories.length > 0) {
        // Pour simplifier, on charge les topics de la première sous-catégorie
        // Dans une vraie app, il faudrait une API pour récupérer tous les topics
        this.forumService.getTopicsBySubCategory(allSubCategories[0].id, 0, 100).subscribe({
          next: (response) => {
            this.allTopics = response.content;
            this.loadingTopics = false;
          },
          error: (err) => {
            console.error('Error loading topics:', err);
            this.loadingTopics = false;
          }
        });
      } else {
        this.loadingTopics = false;
      }
    }
  }

  deleteTopic(topicId: number): void {
    Swal.fire({
      title: 'Delete Topic',
      text: 'Are you sure you want to delete this topic? All associated posts will also be deleted.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.forumService.deleteTopic(topicId).subscribe({
          next: () => {
            Swal.fire({
              title: 'Deleted!',
              text: 'The topic has been successfully deleted.',
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
            this.loadAllTopics();
          },
          error: (err) => {
            console.error('Error deleting topic:', err);
            Swal.fire({
              title: 'Error',
              text: 'Failed to delete topic',
              icon: 'error',
              confirmButtonColor: '#dc2626'
            });
          }
        });
      }
    });
  }

  pinTopic(topic: Topic): void {
    const action = topic.isPinned ? 'unpin' : 'pin';
    const actionText = topic.isPinned ? 'Unpin' : 'Pin';
    
    Swal.fire({
      title: `${actionText} Topic`,
      text: `Are you sure you want to ${action} this topic?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#f59e0b',
      cancelButtonColor: '#6b7280',
      confirmButtonText: actionText,
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        const apiCall = topic.isPinned 
          ? this.forumService.unpinTopic(topic.id)
          : this.forumService.pinTopic(topic.id);
        
        apiCall.subscribe({
          next: (updatedTopic) => {
            Swal.fire({
              title: 'Success!',
              text: `Topic has been ${action}ned successfully.`,
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
            this.loadAllTopics();
          },
          error: (err) => {
            console.error(`Error ${action}ning topic:`, err);
            Swal.fire({
              title: 'Error',
              text: `Failed to ${action} topic`,
              icon: 'error',
              confirmButtonColor: '#dc2626'
            });
          }
        });
      }
    });
  }

  lockTopic(topic: Topic): void {
    const action = topic.isLocked ? 'unlock' : 'lock';
    const actionText = topic.isLocked ? 'Unlock' : 'Lock';
    
    Swal.fire({
      title: `${actionText} Topic`,
      text: `Are you sure you want to ${action} this topic?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: topic.isLocked ? '#10b981' : '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: actionText,
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        const apiCall = topic.isLocked 
          ? this.forumService.unlockTopic(topic.id)
          : this.forumService.lockTopic(topic.id);
        
        apiCall.subscribe({
          next: (updatedTopic) => {
            Swal.fire({
              title: 'Success!',
              text: `Topic has been ${action}ed successfully.`,
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
            this.loadAllTopics();
          },
          error: (err) => {
            console.error(`Error ${action}ing topic:`, err);
            Swal.fire({
              title: 'Error',
              text: `Failed to ${action} topic`,
              icon: 'error',
              confirmButtonColor: '#dc2626'
            });
          }
        });
      }
    });
  }

  // Posts Management
  loadAllPosts(): void {
    this.loadingPosts = true;
    
    // Pour simplifier, on charge les posts du premier topic disponible
    if (this.allTopics.length > 0) {
      this.forumService.getPostsByTopic(this.allTopics[0].id, 0, 100).subscribe({
        next: (response) => {
          this.allPosts = response.content;
          this.loadingPosts = false;
        },
        error: (err) => {
          console.error('Error loading posts:', err);
          this.loadingPosts = false;
        }
      });
    } else {
      this.loadingPosts = false;
    }
  }

  deletePost(postId: number): void {
    Swal.fire({
      title: 'Delete Post',
      text: 'Are you sure you want to delete this post?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.forumService.deletePost(postId).subscribe({
          next: () => {
            Swal.fire({
              title: 'Deleted!',
              text: 'The post has been successfully deleted.',
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
            this.loadAllPosts();
          },
          error: (err) => {
            console.error('Error deleting post:', err);
            Swal.fire({
              title: 'Error',
              text: 'Failed to delete post',
              icon: 'error',
              confirmButtonColor: '#dc2626'
            });
          }
        });
      }
    });
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

  getTruncatedContent(content: string, maxLength: number = 100): string {
    if (content.length <= maxLength) return content;
    return content.substring(0, maxLength) + '...';
  }

  // Modal Management
  openNewTopicModal(): void {
    this.showNewTopicModal = true;
    this.resetTopicForm();
  }

  closeNewTopicModal(): void {
    this.showNewTopicModal = false;
    this.resetTopicForm();
  }

  openNewCategoryModal(): void {
    this.showNewCategoryModal = true;
    this.resetCategoryForm();
  }

  closeNewCategoryModal(): void {
    this.showNewCategoryModal = false;
    this.resetCategoryForm();
  }

  openNewSubCategoryModal(): void {
    this.showNewSubCategoryModal = true;
    this.resetSubCategoryForm();
  }

  closeNewSubCategoryModal(): void {
    this.showNewSubCategoryModal = false;
    this.resetSubCategoryForm();
  }

  // Topic Creation
  onCategoryChange(): void {
    const selectedCategory = this.categories.find(c => c.id === this.newTopic.categoryId);
    this.availableSubCategories = selectedCategory ? selectedCategory.subCategories : [];
    this.newTopic.subCategoryId = 0;
  }

  submitNewTopic(): void {
    if (this.isTopicFormValid()) {
      const request = {
        subCategoryId: this.newTopic.subCategoryId,
        title: this.newTopic.title,
        content: this.newTopic.content,
        userId: 1, // TODO: Get from auth service
        userName: 'Admin' // TODO: Get from auth service
      };

      this.forumService.createTopic(request).subscribe({
        next: (topic) => {
          Swal.fire({
            title: 'Success!',
            text: 'Topic created successfully.',
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
          });
          this.closeNewTopicModal();
          this.loadAllTopics();
        },
        error: (err) => {
          console.error('Error creating topic:', err);
          Swal.fire({
            title: 'Error',
            text: 'Failed to create topic',
            icon: 'error',
            confirmButtonColor: '#dc2626'
          });
        }
      });
    }
  }

  isTopicFormValid(): boolean {
    return !!(
      this.newTopic.categoryId &&
      this.newTopic.subCategoryId &&
      this.newTopic.title.trim() &&
      this.newTopic.content.trim()
    );
  }

  resetTopicForm(): void {
    this.newTopic = {
      categoryId: 0,
      subCategoryId: 0,
      title: '',
      content: ''
    };
    this.availableSubCategories = [];
  }

  // Category Management (TODO: Implement API)
  submitNewCategory(): void {
    if (this.isCategoryFormValid()) {
      const request = {
        name: this.newCategory.name,
        description: this.newCategory.description,
        icon: this.newCategory.icon,
        color: this.newCategory.color
      };

      this.forumService.createCategory(request).subscribe({
        next: (category) => {
          Swal.fire({
            title: 'Success!',
            text: 'Category created successfully.',
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
          });
          this.closeNewCategoryModal();
          this.loadCategories();
        },
        error: (err) => {
          console.error('Error creating category:', err);
          Swal.fire({
            title: 'Error',
            text: 'Failed to create category',
            icon: 'error',
            confirmButtonColor: '#dc2626'
          });
        }
      });
    }
  }

  isCategoryFormValid(): boolean {
    return !!(this.newCategory.name.trim() && this.newCategory.description.trim());
  }

  resetCategoryForm(): void {
    this.newCategory = {
      name: '',
      description: '',
      icon: 'fa-folder',
      color: 'primary'
    };
  }

  // SubCategory Management (TODO: Implement API)
  submitNewSubCategory(): void {
    if (this.isSubCategoryFormValid()) {
      const request = {
        categoryId: this.newSubCategory.categoryId,
        name: this.newSubCategory.name,
        description: this.newSubCategory.description
      };

      this.forumService.createSubCategory(request).subscribe({
        next: (subCategory) => {
          Swal.fire({
            title: 'Success!',
            text: 'Subcategory created successfully.',
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
          });
          this.closeNewSubCategoryModal();
          this.loadCategories();
        },
        error: (err) => {
          console.error('Error creating subcategory:', err);
          Swal.fire({
            title: 'Error',
            text: 'Failed to create subcategory',
            icon: 'error',
            confirmButtonColor: '#dc2626'
          });
        }
      });
    }
  }

  isSubCategoryFormValid(): boolean {
    return !!(
      this.newSubCategory.categoryId &&
      this.newSubCategory.name.trim() &&
      this.newSubCategory.description.trim()
    );
  }

  resetSubCategoryForm(): void {
    this.newSubCategory = {
      categoryId: 0,
      name: '',
      description: ''
    };
  }

  deleteSubCategory(subCategoryId: number, subCategoryName: string): void {
    Swal.fire({
      title: 'Delete Subcategory',
      text: `Are you sure you want to delete "${subCategoryName}"? All topics in this subcategory will also be deleted.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.forumService.deleteSubCategory(subCategoryId).subscribe({
          next: () => {
            Swal.fire({
              title: 'Deleted!',
              text: 'The subcategory has been successfully deleted.',
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
            this.loadCategories();
          },
          error: (err) => {
            console.error('Error deleting subcategory:', err);
            Swal.fire({
              title: 'Error',
              text: 'Failed to delete subcategory',
              icon: 'error',
              confirmButtonColor: '#dc2626'
            });
          }
        });
      }
    });
  }

  deleteCategory(categoryId: number, categoryName: string): void {
    Swal.fire({
      title: 'Delete Category',
      text: `Are you sure you want to delete "${categoryName}"? All subcategories and topics will also be deleted.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#6b7280',
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
      reverseButtons: true
    }).then((result) => {
      if (result.isConfirmed) {
        this.forumService.deleteCategory(categoryId).subscribe({
          next: () => {
            Swal.fire({
              title: 'Deleted!',
              text: 'The category has been successfully deleted.',
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
            this.loadCategories();
          },
          error: (err) => {
            console.error('Error deleting category:', err);
            Swal.fire({
              title: 'Error',
              text: 'Failed to delete category',
              icon: 'error',
              confirmButtonColor: '#dc2626'
            });
          }
        });
      }
    });
  }
}
