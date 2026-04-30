import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Club, ClubStatus, MembershipRequest } from '../../../core/models/club.model';
import { ClubExpensesComponent } from '../../clubs/club-expenses/club-expenses.component';
import { ClubTasksComponent } from '../../clubs/club-tasks/club-tasks.component';
import { ClubMembershipRequestsComponent } from '../../clubs/club-membership-requests/club-membership-requests.component';
import { Event as ClubEvent } from '../../../core/services/event.service';
import { ClubHistoryService, ClubHistory } from '../../../core/services/club-history.service';
import { MemberService } from '../../../core/services/member.service';
import { NotificationService } from '../../../core/services/notification.service';

type TabType = 'overview' | 'members' | 'join-requests' | 'tasks' | 'expenses' | 'history';

@Component({
  selector: 'app-clubs-details',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, ClubExpensesComponent, ClubTasksComponent, ClubMembershipRequestsComponent],
  templateUrl: './clubs-details.component.html',
  styleUrls: ['./clubs-details.component.scss']
})
export class ClubsDetailsComponent implements OnChanges {
  @Input() selectedClub!: Club;
  activeTab: TabType = 'overview';
  @Input() set initialTab(tab: TabType) { if (tab) this.activeTab = tab; }
  @Input() clubMembers: any[] = [];
  @Input() paymentPendingRequests: any[] = [];
  @Input() actualMemberCount = 0;
  @Input() loadingMembers = false;
  @Input() clubEvents: ClubEvent[] = [];
  @Input() loadingEvents = false;
  @Input() clubMembershipRequestsCount: { [clubId: number]: number } = {};
  @Input() currentUserId: number | null = null;
  @Input() userPaymentPendingRequest: MembershipRequest | null = null;
  @Input() isAcademicManager = false;

  // local state
  memberSearchQuery = '';
  memberFilter: 'all' | 'active' | 'pending' = 'all';
  clubHistory: ClubHistory[] = [];
  loadingHistory = false;

  // Manage member modal
  showManageMemberModal = false;
  selectedMember: any = null;
  selectedNewRole = '';
  availableRoles = ['VICE_PRESIDENT','SECRETARY','TREASURER','COMMUNICATION_MANAGER','EVENT_MANAGER','PARTNERSHIP_MANAGER','MEMBER'];

  // Transfer presidency modal (shown when president tries to leave)
  showTransferPresidencyModal = false;
  selectedSuccessorId: number | null = null;

  @Output() tabChange = new EventEmitter<TabType>();
  @Output() back = new EventEmitter<void>();
  @Output() joinClub = new EventEmitter<number>();
  @Output() leaveClub = new EventEmitter<number>();
  @Output() leftClubAsPresident = new EventEmitter<number>(); // fired after transfer, no need to call remove again
  @Output() membersReload = new EventEmitter<void>();
  ClubStatus = ClubStatus;

  @Input() getUserRole!: (clubId: number) => string;
  @Input() getRoleIcon!: (role: string) => string;
  @Input() getRoleLabel!: (role: string) => string;
  @Input() getRoleBadgeClass!: (role: string) => string;
  @Input() getCategoryIcon!: (category: string) => string;
  @Input() getCategoryLabel!: (category: string) => string;
  @Input() isClubPresident!: (club: Club) => boolean;
  @Input() isClubMember!: (clubId: number) => boolean;
  @Input() canViewHistory!: (clubId: number) => boolean;
  @Input() hasPendingRequest!: (clubId: number) => boolean;
  @Input() getMembershipRequestsCount!: (clubId: number) => number;
  @Input() getMemberRoleSections!: (searchQuery: string, filter: 'all' | 'active' | 'pending') => string[];
  @Input() getMembersByRole!: (role: string, searchQuery: string) => any[];
  @Input() getPendingByRole!: (role: string, searchQuery: string) => any[];

  constructor(
    private clubHistoryService: ClubHistoryService,
    private memberService: MemberService,
    private notificationService: NotificationService
  ) {}

  ngOnChanges(changes: SimpleChanges) {
    if (changes['selectedClub'] && !changes['selectedClub'].firstChange) {
      this.clubHistory = [];
      this.activeTab = 'overview';
    }
  }

  get currentRole(): string {
    return this.selectedClub?.id ? (this.getUserRole?.(this.selectedClub.id) ?? 'MEMBER') : 'MEMBER';
  }

  get isPaymentPending(): boolean {
    return !!this.userPaymentPendingRequest;
  }

  get isManagementRole(): boolean {
    return this.currentRole === 'PRESIDENT' || this.currentRole === 'VICE_PRESIDENT' || this.currentRole === 'SECRETARY';
  }

  onHistoryTabClick() {
    this.activeTab = 'history';
    this.fetchHistory();
  }

  setTab(tab: TabType) {
    this.activeTab = tab;
    this.tabChange.emit(tab);
  }

  // ===== MANAGE MEMBER MODAL =====
  openManageMember(member: any) {
    this.selectedMember = member;
    this.selectedNewRole = member.rank || 'MEMBER';
    this.showManageMemberModal = true;
  }

  closeManageMemberModal() {
    this.showManageMemberModal = false;
    this.selectedMember = null;
    this.selectedNewRole = '';
  }

  confirmRoleChange() {
    if (!this.selectedMember || !this.currentUserId || !this.selectedClub) return;
    if (this.selectedNewRole === this.selectedMember.rank) {
      this.notificationService.warning('No Change', 'The selected role is the same as the current role.');
      return;
    }
    this.memberService.updateMemberRole(this.selectedMember.id, this.selectedNewRole, this.currentUserId).subscribe({
      next: () => {
        this.notificationService.success('Role Updated', `Role updated to ${this.getRoleLabel(this.selectedNewRole)}!`);
        this.closeManageMemberModal();
        this.membersReload.emit();
      },
      error: (err) => {
        const msg = err.error?.message || err.error || 'Failed to update role.';
        this.notificationService.error('Error', msg);
      }
    });
  }

  confirmRemoveMember() {
    if (!this.selectedMember || !this.selectedClub) return;
    if (!confirm('Are you sure you want to remove this member?')) return;
    this.memberService.removeMemberFromClub(this.selectedClub.id!, this.selectedMember.userId).subscribe({
      next: () => {
        this.notificationService.success('Member Removed', 'Member removed successfully!');
        this.closeManageMemberModal();
        this.membersReload.emit();
      },
      error: () => {
        this.notificationService.error('Error', 'Failed to remove member.');
      }
    });
  }

  // ===== TRANSFER PRESIDENCY (when president leaves) =====
  get nonPresidentMembers(): any[] {
    return this.clubMembers.filter(m => m.rank !== 'PRESIDENT');
  }

  onLeaveClub() {
    if (!this.selectedClub?.id) return;
    if (this.currentRole === 'PRESIDENT') {
      if (this.nonPresidentMembers.length === 0) {
        this.notificationService.warning('Cannot Leave', 'You are the only member. Delete the club instead.');
        return;
      }
      this.selectedSuccessorId = null;
      this.showTransferPresidencyModal = true;
    } else {
      this.leaveClub.emit(this.selectedClub.id);
    }
  }

  closeTransferModal() {
    this.showTransferPresidencyModal = false;
    this.selectedSuccessorId = null;
  }

  confirmTransferAndLeave() {
    if (!this.selectedSuccessorId || !this.currentUserId || !this.selectedClub?.id) return;
    this.memberService.transferPresidencyAndLeave(this.selectedClub.id, this.currentUserId, this.selectedSuccessorId).subscribe({
      next: () => {
        this.notificationService.success('Presidency Transferred', 'You have left the club and transferred presidency.');
        this.closeTransferModal();
        this.leftClubAsPresident.emit(this.selectedClub.id!); // UI refresh only, no extra DELETE call
      },
      error: (err) => {
        const msg = err.error?.message || err.error || 'Failed to transfer presidency.';
        this.notificationService.error('Error', msg);
      }
    });
  }

  // ===== HISTORY =====
  fetchHistory() {
    if (!this.selectedClub?.id) return;
    this.loadingHistory = true;
    this.clubHistory = [];
    this.clubHistoryService.getClubHistory(this.selectedClub.id).subscribe({
      next: (history) => { this.clubHistory = history; this.loadingHistory = false; },
      error: () => { this.loadingHistory = false; }
    });
  }

  getHistoryIcon(type: string): string {
    const icons: { [key: string]: string } = {
      'MEMBER_JOINED': '👋', 'MEMBER_LEFT': '🚪', 'MEMBER_REMOVED': '🚫',
      'RANK_CHANGED': '⭐', 'CLUB_CREATED': '🎉', 'CLUB_UPDATED': '✏️',
      'CLUB_STATUS_CHANGED': '🔄', 'EVENT_CREATED': '📅', 'EVENT_PARTICIPATED': '🎯',
      'ACHIEVEMENT_EARNED': '🏆', 'CONTRIBUTION': '💡',
      'EXPENSE_ADDED': '💸', 'EXPENSE_UPDATED': '✏️', 'EXPENSE_DELETED': '🗑️',
      'PAYMENT_CONFIRMED': '💳', 'TASK_CREATED': '✅', 'TASK_UPDATED': '🔄',
      'TASK_DELETED': '🗑️', 'OTHER': '📝'
    };
    return icons[type] || '📝';
  }

  getHistoryTypeBadgeClass(type: string): string {
    const classes: { [key: string]: string } = {
      'MEMBER_JOINED': 'bg-green-100 text-green-700', 'MEMBER_LEFT': 'bg-gray-100 text-gray-700',
      'MEMBER_REMOVED': 'bg-red-100 text-red-700', 'RANK_CHANGED': 'bg-blue-100 text-blue-700',
      'CLUB_CREATED': 'bg-purple-100 text-purple-700', 'CLUB_UPDATED': 'bg-yellow-100 text-yellow-700',
      'CLUB_STATUS_CHANGED': 'bg-orange-100 text-orange-700', 'EVENT_CREATED': 'bg-indigo-100 text-indigo-700',
      'EXPENSE_ADDED': 'bg-emerald-100 text-emerald-700', 'EXPENSE_UPDATED': 'bg-yellow-100 text-yellow-700',
      'EXPENSE_DELETED': 'bg-red-100 text-red-700', 'PAYMENT_CONFIRMED': 'bg-green-100 text-green-700',
      'TASK_CREATED': 'bg-emerald-100 text-emerald-700', 'TASK_UPDATED': 'bg-sky-100 text-sky-700',
      'TASK_DELETED': 'bg-rose-100 text-rose-700',
    };
    return classes[type] || 'bg-gray-100 text-gray-700';
  }

  formatHistoryDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    if (Number.isNaN(date.getTime())) return dateString;
    const diffDays = Math.floor((Date.now() - date.getTime()) / 86400000);
    if (diffDays === 0) return 'Today';
    if (diffDays === 1) return 'Yesterday';
    if (diffDays < 7) return `${diffDays} days ago`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)}w ago`;
    if (diffDays < 365) return `${Math.floor(diffDays / 30)}mo ago`;
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }

  readonly sumFn = (acc: number, d: { count: number }) => acc + d.count;

  // Returns last 7 days join counts — includes payment pending
  getMemberJoinTrend(): { label: string; count: number }[] {
    const days = 7;
    const result: { label: string; count: number }[] = [];
    const now = new Date();
    const allMembers = [
      ...this.clubMembers,
      ...this.paymentPendingRequests.map(r => ({ joinedAt: r.requestedAt || r.createdAt }))
    ];

    for (let i = days - 1; i >= 0; i--) {
      const day = new Date(now);
      day.setDate(now.getDate() - i);
      const label = day.toLocaleDateString('en-US', { weekday: 'short' });
      const count = allMembers.filter(m => {
        if (!m.joinedAt) return false;
        const joined = new Date(m.joinedAt);
        return joined.getFullYear() === day.getFullYear() &&
               joined.getMonth() === day.getMonth() &&
               joined.getDate() === day.getDate();
      }).length;
      result.push({ label, count });
    }
    return result;
  }

  get maxJoinCount(): number {
    return Math.max(1, ...this.getMemberJoinTrend().map(d => d.count));
  }

  getPaymentDaysRemaining(): number {
    if (!this.userPaymentPendingRequest?.paymentDeadline) return 0;
    const deadline = new Date(this.userPaymentPendingRequest.paymentDeadline);
    const diff = deadline.getTime() - Date.now();
    return Math.max(0, Math.ceil(diff / 86400000));
  }

  sortedMembersByRole(role: string, searchQuery: string): any[] {
    return [...this.getMembersByRole(role, searchQuery)].sort((a, b) =>
      `${a.firstName} ${a.lastName}`.localeCompare(`${b.firstName} ${b.lastName}`)
    );
  }

  sortedPendingByRole(role: string, searchQuery: string): any[] {
    return [...this.getPendingByRole(role, searchQuery)].sort((a, b) =>
      `${a.user?.firstName} ${a.user?.lastName}`.localeCompare(`${b.user?.firstName} ${b.user?.lastName}`)
    );
  }
}
