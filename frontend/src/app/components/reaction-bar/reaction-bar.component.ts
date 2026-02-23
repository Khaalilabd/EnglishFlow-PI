import { Component, Input, OnInit, ChangeDetectionStrategy, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactionService, ReactionType, ReactionCount } from '../../services/reaction.service';
import { AuthService } from '../../core/services/auth.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-reaction-bar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reaction-bar.component.html',
  styleUrl: './reaction-bar.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReactionBarComponent implements OnInit {
  @Input() targetId!: number;
  @Input() targetType: 'topic' | 'post' = 'topic';
  @Input() size: 'small' | 'medium' | 'large' = 'medium';

  reactions: ReactionCount[] = [];
  userReaction: ReactionType | null = null;
  loading = false;

  private reactionService = inject(ReactionService);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  // Available reaction types
  reactionTypes = [
    { type: ReactionType.LIKE, icon: '👍', label: 'Like' },
    { type: ReactionType.HELPFUL, icon: '💡', label: 'Helpful' },
    { type: ReactionType.INSIGHTFUL, icon: '🎯', label: 'Insightful' }
  ];

  ngOnInit(): void {
    this.loadReactions();
  }

  loadReactions(): void {
    const loadMethod = this.targetType === 'topic' 
      ? this.reactionService.getTopicReactions(this.targetId)
      : this.reactionService.getPostReactions(this.targetId);

    loadMethod.pipe(takeUntilDestroyed()).subscribe({
      next: (data) => {
        this.reactions = data;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error loading reactions:', err);
      }
    });
  }

  toggleReaction(type: ReactionType): void {
    if (this.loading) return;

    const currentUser = this.authService.currentUserValue;
    if (!currentUser) {
      console.error('User not authenticated');
      return;
    }

    this.loading = true;

    // If user already reacted with this type, remove it
    if (this.userReaction === type) {
      this.removeReaction();
    } else {
      this.addReaction(type);
    }
  }

  private addReaction(type: ReactionType): void {
    const addMethod = this.targetType === 'topic'
      ? this.reactionService.addReactionToTopic(this.targetId, type)
      : this.reactionService.addReactionToPost(this.targetId, type);

    addMethod.pipe(takeUntilDestroyed()).subscribe({
      next: () => {
        this.userReaction = type;
        this.updateLocalReactionCount(type, 1);
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error adding reaction:', err);
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  private removeReaction(): void {
    const removeMethod = this.targetType === 'topic'
      ? this.reactionService.removeReactionFromTopic(this.targetId)
      : this.reactionService.removeReactionFromPost(this.targetId);

    const previousReaction = this.userReaction;
    
    removeMethod.pipe(takeUntilDestroyed()).subscribe({
      next: () => {
        if (previousReaction) {
          this.updateLocalReactionCount(previousReaction, -1);
        }
        this.userReaction = null;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error removing reaction:', err);
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  private updateLocalReactionCount(type: ReactionType, delta: number): void {
    const existingReaction = this.reactions.find(r => r.type === type);
    
    if (existingReaction) {
      existingReaction.count += delta;
      if (existingReaction.count <= 0) {
        this.reactions = this.reactions.filter(r => r.type !== type);
      }
    } else if (delta > 0) {
      this.reactions.push({ type, count: delta });
    }
  }

  getReactionCount(type: ReactionType): number {
    const reaction = this.reactions.find(r => r.type === type);
    return reaction ? reaction.count : 0;
  }

  hasReacted(type: ReactionType): boolean {
    return this.userReaction === type;
  }

  getTotalReactions(): number {
    return this.reactions.reduce((sum, r) => sum + r.count, 0);
  }
}
