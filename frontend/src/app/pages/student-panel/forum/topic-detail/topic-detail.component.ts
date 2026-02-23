import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ForumService, Topic, Post, CreatePostRequest } from '../../../../services/forum.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ReactionBarComponent } from '../../../../components/reaction-bar/reaction-bar.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-topic-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactionBarComponent],
  providers: [ForumService],
  templateUrl: './topic-detail.component.html',
  styleUrl: './topic-detail.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
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

  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private forumService: ForumService
  ) {
    // Fix memory leak: use takeUntilDestroyed
    this.route.params
      .pipe(takeUntilDestroyed())
      .subscribe(params => {
        this.topicId = +params['topicId'];
        this.loadTopic();
        this.loadPosts();
      });
  }

  ngOnInit(): void {
    // Initialization moved to constructor with takeUntilDestroyed
  }

  loadTopic(): void {
    this.loading = true;
    this.error = null;
    
    this.forumService.getTopicById(this.topicId).subscribe({
      next: (data) => {
        this.topic = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading topic:', err);
        this.error = 'Error loading topic';
        this.loading = false;
        this.cdr.markForCheck();
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
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading posts:', err);
        this.loadingPosts = false;
        this.cdr.markForCheck();
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
      const currentUser = this.authService.currentUserValue;
      
      if (!currentUser) {
        Swal.fire({
          title: 'Authentication Required',
          text: 'You must be logged in to post a reply',
          icon: 'warning',
          confirmButtonColor: '#dc2626'
        });
        return;
      }

      const request: CreatePostRequest = {
        topicId: this.topicId,
        content: this.newPost.content,
        userId: currentUser.id,
        userName: currentUser.firstName + ' ' + currentUser.lastName
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
          this.loadPosts();
          this.loadTopic();
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Error creating post:', err);
          Swal.fire({
            title: 'Error',
            text: 'Error posting your reply. Please try again.',
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
          this.loadPosts();
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
            this.loadPosts();
            this.loadTopic();
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
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return false;
    
    return post.userId === currentUser.id || 
           currentUser.role === 'ADMIN' || 
           currentUser.role === 'MODERATOR';
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
