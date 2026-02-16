import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ForumService, Topic, Post, CreatePostRequest } from '../../../../services/forum.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-topic-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  providers: [ForumService],
  templateUrl: './topic-detail.component.html',
  styleUrl: './topic-detail.component.scss'
})
export class TopicDetailComponent implements OnInit {
  topic: Topic | null = null;
  posts: Post[] = [];
  topicId!: number;
  loading = true;
  loadingPosts = true;
  error: string | null = null;
  
  currentPage = 0;
  pageSize = 20;
  totalPages = 0;
  totalElements = 0;

  // Modal state
  showReplyModal = false;
  showEditPostModal = false;
  
  // Form data
  newPost = {
    content: ''
  };

  editingPost: Post | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private forumService: ForumService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.topicId = +params['topicId'];
      this.loadTopic();
      this.loadPosts();
    });
  }

  loadTopic(): void {
    this.loading = true;
    this.error = null;
    
    this.forumService.getTopicById(this.topicId).subscribe({
      next: (data) => {
        this.topic = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading topic:', err);
        this.error = 'Error loading topic';
        this.loading = false;
      }
    });
  }

  loadPosts(): void {
    this.loadingPosts = true;
    
    this.forumService.getPostsByTopic(this.topicId, this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.posts = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.loadingPosts = false;
      },
      error: (err) => {
        console.error('Error loading posts:', err);
        this.loadingPosts = false;
      }
    });
  }

  openReplyModal(): void {
    if (this.topic?.isLocked) {
      Swal.fire({
        title: 'Topic Locked',
        text: 'This topic is locked and no longer accepts new replies.',
        icon: 'warning',
        confirmButtonColor: '#dc2626'
      });
      return;
    }
    this.showReplyModal = true;
    this.resetForm();
  }

  closeReplyModal(): void {
    this.showReplyModal = false;
    this.resetForm();
  }

  openEditPostModal(event: Event, post: Post): void {
    event.stopPropagation();
    this.editingPost = post;
    this.newPost = {
      content: post.content
    };
    this.showEditPostModal = true;
  }

  closeEditPostModal(): void {
    this.showEditPostModal = false;
    this.editingPost = null;
    this.resetForm();
  }

  submitReply(): void {
    if (this.isFormValid()) {
      const request: CreatePostRequest = {
        topicId: this.topicId,
        content: this.newPost.content,
        userId: 1, // TODO: Get from auth service
        userName: 'User' // TODO: Get from auth service
      };

      this.forumService.createPost(request).subscribe({
        next: (post) => {
          console.log('Post created:', post);
          Swal.fire({
            title: 'Success!',
            text: 'Your reply has been posted successfully.',
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
          });
          this.closeReplyModal();
          this.loadPosts(); // Recharger les posts
          this.loadTopic(); // Recharger le topic pour mettre à jour le compteur
        },
        error: (err) => {
          console.error('Error creating post:', err);
          Swal.fire({
            title: 'Error',
            text: 'Error posting your reply',
            icon: 'error',
            confirmButtonColor: '#dc2626'
          });
        }
      });
    }
  }

  submitEditPost(): void {
    if (this.isFormValid() && this.editingPost) {
      const request: CreatePostRequest = {
        topicId: this.topicId,
        content: this.newPost.content,
        userId: this.editingPost.userId,
        userName: this.editingPost.userName
      };

      this.forumService.updatePost(this.editingPost.id, request).subscribe({
        next: (post) => {
          console.log('Post updated:', post);
          Swal.fire({
            title: 'Updated!',
            text: 'Your reply has been updated successfully.',
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
          });
          this.closeEditPostModal();
          this.loadPosts(); // Recharger les posts
        },
        error: (err) => {
          console.error('Error updating post:', err);
          Swal.fire({
            title: 'Error',
            text: 'Error updating your reply',
            icon: 'error',
            confirmButtonColor: '#dc2626'
          });
        }
      });
    }
  }

  deletePost(event: Event, postId: number): void {
    event.stopPropagation();
    
    Swal.fire({
      title: 'Delete Reply',
      text: 'Are you sure you want to delete this reply?',
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
              text: 'The reply has been successfully deleted.',
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
            this.loadPosts(); // Recharger les posts
            this.loadTopic(); // Recharger le topic pour mettre à jour le compteur
          },
          error: (err) => {
            console.error('Error deleting post:', err);
            Swal.fire({
              title: 'Error',
              text: 'Error deleting reply',
              icon: 'error',
              confirmButtonColor: '#dc2626'
            });
          }
        });
      }
    });
  }

  canEditOrDelete(post: Post): boolean {
    // TODO: Vérifier si l'utilisateur connecté est l'auteur du post
    // Pour l'instant, on permet à tout le monde (à des fins de test)
    return true;
  }

  isFormValid(): boolean {
    return !!(this.newPost.content.trim());
  }

  resetForm(): void {
    this.newPost = {
      content: ''
    };
  }

  goBack(): void {
    // Détecter si on vient du dashboard ou du student panel
    const currentUrl = this.router.url;
    if (currentUrl.includes('/dashboard/')) {
      if (this.topic) {
        this.router.navigate(['/dashboard/forum/topics', this.topic.subCategoryId, 'Topics']);
      } else {
        this.router.navigate(['/dashboard/forum']);
      }
    } else {
      if (this.topic) {
        this.router.navigate(['/user-panel/forum/topics', this.topic.subCategoryId, 'Topics']);
      } else {
        this.router.navigate(['/user-panel/forum']);
      }
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
      this.loadPosts();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadPosts();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }
}
