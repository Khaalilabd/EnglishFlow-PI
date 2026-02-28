import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { ClubService } from '../../../core/services/club.service';
import { AuthService } from '../../../core/services/auth.service';
import { TaskService } from '../../../core/services/task.service';
import { MemberService } from '../../../core/services/member.service';
import { UserService } from '../../../core/services/user.service';
import { EventService, Event as ClubEvent } from '../../../core/services/event.service';
import { EventFeedbackService } from '../../../core/services/event-feedback.service';
import { ClubUpdateRequestService, ClubUpdateRequest } from '../../../core/services/club-update-request.service';
import { Club, ClubCategory, ClubStatus } from '../../../core/models/club.model';
import { Task, TaskStatus } from '../../../core/models/task.model';
import { filter, forkJoin, of } from 'rxjs';
import { Subscription } from 'rxjs';
import { CdkDragDrop, moveItemInArray, transferArrayItem, DragDropModule } from '@angular/cdk/drag-drop';

@Component({
  selector: 'app-student-clubs',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, DragDropModule],
  templateUrl: './clubs.component.html',
  styleUrls: ['./clubs.component.scss']
})
export class ClubsComponent implements OnInit, OnDestroy {
  allClubs: Club[] = [];
  myClubs: Club[] = [];
  filteredClubs: Club[] = [];
  loading = false;
  error: string | null = null;
  categories = Object.values(ClubCategory);
  selectedCategory: ClubCategory | null = null;
  currentUserId: number | null = null;
  
  // Store user roles for each club
  clubRoles: { [clubId: number]: 'PRESIDENT' | 'VICE_PRESIDENT' | 'SECRETARY' | 'TREASURER' | 'COMMUNICATION_MANAGER' | 'EVENT_MANAGER' | 'PARTNERSHIP_MANAGER' | 'MEMBER' } = {};
  
  private subscriptions = new Subscription();
  
  // Create club modal
  showCreateModal = false;
  clubForm: FormGroup;
  creating = false;
  createError: string | null = null;
  selectedImageFile: File | null = null;
  imagePreview: string | null = null;

  // Edit club modal
  showEditModal = false;
  editingClub: Club | null = null;
  editForm: FormGroup;
  updating = false;
  updateError: string | null = null;
  editImageFile: File | null = null;
  editImagePreview: string | null = null;

  // Details view (not modal)
  showDetailsView = false;
  selectedClub: Club | null = null;
  showDescription = true;  // Section description ouverte par défaut
  showObjective = true;    // Section objectif ouverte par défaut
  actualMemberCount = 0;   // Nombre réel de membres

  // Task management
  clubTasks: { [clubId: number]: Task[] } = {};
  newTaskText: string = '';
  TaskStatus = TaskStatus; // Expose enum to template
  ClubStatus = ClubStatus; // Expose enum to template
  ClubCategory = ClubCategory; // Expose enum to template
  loadingTasks = false;
  
  // Task editing
  editingTaskId: number | null = null;
  editingTaskText: string = '';
  
  // Category dropdown
  showCategoryDropdown = false;
  
  // Members management modal
  showMembersModal = false;
  clubMembers: any[] = [];
  
  // Pending update requests
  pendingRequests: ClubUpdateRequest[] = [];
  loadingRequests = false;
  loadingMembers = false;
  
  // Update requests modal
  showUpdateRequestsModal = false;
  
  // Club events
  clubEvents: ClubEvent[] = [];
  loadingEvents = false;
  
  // Event feedback stats
  eventFeedbackStats: { [eventId: number]: { averageRating: number; totalFeedbacks: number } } = {};

  // Helper method to filter pending requests by club ID
  getPendingRequestsForClub(clubId: number): ClubUpdateRequest[] {
    return this.pendingRequests.filter(r => r.clubId === clubId);
  }

  constructor(
    private clubService: ClubService,
    private authService: AuthService,
    private taskService: TaskService,
    private memberService: MemberService,
    private userService: UserService,
    private eventService: EventService,
    private eventFeedbackService: EventFeedbackService,
    private updateRequestService: ClubUpdateRequestService,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.clubForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      objective: [''],
      category: ['', Validators.required],
      maxMembers: [20, [Validators.required, Validators.min(5), Validators.max(100)]],
      image: ['']
    });

    this.editForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      objective: [''],
      category: ['', Validators.required],
      maxMembers: [20, [Validators.required, Validators.min(5), Validators.max(100)]],
      image: ['']
    });
  }

  ngOnInit() {
    this.getCurrentUser();
    this.checkRouteAndLoadClub();
    
    // Listen to route parameter changes
    const paramSub = this.route.paramMap.subscribe(params => {
      this.checkRouteAndLoadClub();
    });
    this.subscriptions.add(paramSub);

    // Also listen to navigation events
    const navSub = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.checkRouteAndLoadClub();
    });
    this.subscriptions.add(navSub);

    // Listen to user changes (login/logout)
    const authSub = this.authService.currentUser$.subscribe((user: any) => {
      const previousUserId = this.currentUserId;
      
      if (user && user.id) {
        this.currentUserId = user.id;
        
        // Si l'utilisateur a changé, réinitialiser les données
        if (previousUserId !== null && previousUserId !== this.currentUserId) {
          console.log('🔄 User changed from', previousUserId, 'to', this.currentUserId);
          // Réinitialiser les rôles des clubs
          this.clubRoles = {};
          // Recharger les clubs et les rôles
          this.loadClubs();
          this.loadPendingRequests();
        }
      } else {
        // Utilisateur déconnecté
        this.currentUserId = null;
        this.clubRoles = {};
        this.allClubs = [];
        this.myClubs = [];
        this.filteredClubs = [];
        this.pendingRequests = [];
      }
    });
    this.subscriptions.add(authSub);
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  private checkRouteAndLoadClub() {
    const clubId = this.route.snapshot.paramMap.get('id');
    
    if (clubId) {
      // Load and display specific club
      this.loadAndDisplayClub(Number(clubId));
    } else {
      // Load all clubs
      this.showDetailsView = false;
      this.selectedClub = null;
      this.loadClubs();
    }
  }

  loadAndDisplayClub(clubId: number) {
    this.loading = true;
    this.error = null;

    // Load club details and user roles in parallel
    forkJoin({
      club: this.clubService.getClubById(clubId),
      members: this.currentUserId ? this.memberService.getMembersByUser(this.currentUserId) : of([])
    }).subscribe({
      next: ({ club, members }) => {
        this.selectedClub = club;
        this.showDetailsView = true;
        
        // Process member roles
        if (Array.isArray(members)) {
          console.log('👥 Members loaded in clubs component:', members);
          members.forEach(member => {
            this.clubRoles[member.clubId] = member.rank;
            console.log(`  - Club ${member.clubId}: ${member.rank}`);
          });
          console.log('📊 Final clubRoles map:', this.clubRoles);
        }
        
        // Set myClubs to include this club for loadPendingRequests to work
        this.myClubs = [club];
        
        this.loading = false;
        
        // Load tasks, member count, events, and pending requests after displaying
        this.loadTasksForClub(club.id!);
        this.loadActualMemberCount(club.id!);
        this.loadClubEventsForClub(club.id!);
        this.loadPendingRequests();
      },
      error: (err) => {
        console.error('Error loading club:', err);
        this.error = 'Failed to load club. Please try again.';
        this.loading = false;
        // Fallback to loading all clubs
        this.loadClubs();
      }
    });
  }

  loadActualMemberCount(clubId: number) {
    this.memberService.getClubMemberCount(clubId).subscribe({
      next: (count) => {
        this.actualMemberCount = count;
        console.log(`✅ Member count for club ${clubId}: ${count}`);
      },
      error: (err) => {
        console.error('❌ Error loading member count:', err);
        this.actualMemberCount = 0;
      }
    });
  }

  getCurrentUser() {
    const user = this.authService.currentUserValue;
    if (user && user.id !== undefined && user.id !== null) {
      this.currentUserId = user.id;
    } else {
      console.error('No user found or user has no ID');
      this.error = 'User not authenticated. Please log in again.';
    }
  }

  loadClubs() {
    this.loading = true;
    this.error = null;

    // Load clubs and user roles in parallel
    forkJoin({
      clubs: this.clubService.getApprovedClubs(),
      members: this.currentUserId ? this.memberService.getMembersByUser(this.currentUserId) : of([])
    }).subscribe({
      next: ({ clubs, members }) => {
        // Ne pas filtrer les clubs suspendus, les afficher tous
        this.allClubs = clubs;
        
        // Process member roles
        if (Array.isArray(members)) {
          console.log('👥 Members loaded in clubs component:', members);
          members.forEach(member => {
            this.clubRoles[member.clubId] = member.rank;
            console.log(`  - Club ${member.clubId}: ${member.rank}`);
          });
          console.log('📊 Final clubRoles map:', this.clubRoles);
        }
        
        this.categorizeClubs();
        this.loading = false;
        
        // Load pending requests after clubs are loaded
        this.loadPendingRequests();
      },
      error: (err) => {
        console.error('Error loading clubs:', err);
        this.error = 'Failed to load clubs. Please try again.';
        this.loading = false;
      }
    });
  }

  loadUserRoles() {
    if (!this.currentUserId) return;

    console.log('🔍 Loading user roles for user:', this.currentUserId);

    // Get user's membership info for all clubs
    this.memberService.getMembersByUser(this.currentUserId).subscribe({
      next: (members) => {
        console.log('👥 Members loaded in clubs component:', members);
        members.forEach(member => {
          this.clubRoles[member.clubId] = member.rank;
          console.log(`  - Club ${member.clubId}: ${member.rank}`);
        });
        console.log('📊 Final clubRoles map:', this.clubRoles);
      },
      error: (err) => {
        console.error('❌ Error loading user roles:', err);
      }
    });
  }

  getUserRole(clubId: number): string {
    const role = this.clubRoles[clubId] || 'MEMBER';
    console.log(`🎭 getUserRole(${clubId}) = ${role}`);
    return role;
  }

  getRoleBadgeClass(role: string): string {
    const classes: { [key: string]: string } = {
      'PRESIDENT': 'bg-gradient-to-r from-[#F6BD60] to-[#e5ac4f] text-gray-900',
      'VICE_PRESIDENT': 'bg-gradient-to-r from-[#2D5757] to-[#3D3D60] text-white',
      'SECRETARY': 'bg-gradient-to-r from-blue-500 to-blue-600 text-white',
      'TREASURER': 'bg-gradient-to-r from-green-500 to-green-600 text-white',
      'COMMUNICATION_MANAGER': 'bg-gradient-to-r from-purple-500 to-purple-600 text-white',
      'EVENT_MANAGER': 'bg-gradient-to-r from-pink-500 to-pink-600 text-white',
      'PARTNERSHIP_MANAGER': 'bg-gradient-to-r from-indigo-500 to-indigo-600 text-white',
      'MEMBER': 'bg-gray-100 text-gray-700'
    };
    return classes[role] || classes['MEMBER'];
  }

  getRoleIcon(role: string): string {
    const icons: { [key: string]: string } = {
      'PRESIDENT': '👑',
      'VICE_PRESIDENT': '⭐',
      'SECRETARY': '📝',
      'TREASURER': '💰',
      'COMMUNICATION_MANAGER': '📢',
      'EVENT_MANAGER': '🎉',
      'PARTNERSHIP_MANAGER': '🤝',
      'MEMBER': '👤'
    };
    return icons[role] || icons['MEMBER'];
  }

  getRoleLabel(role: string): string {
    const labels: { [key: string]: string } = {
      'PRESIDENT': 'Président(e)',
      'VICE_PRESIDENT': 'Vice-président(e)',
      'SECRETARY': 'Secrétaire',
      'TREASURER': 'Trésorier(ère)',
      'COMMUNICATION_MANAGER': 'Responsable Communication',
      'EVENT_MANAGER': 'Responsable Événementiel',
      'PARTNERSHIP_MANAGER': 'Responsable Partenariats',
      'MEMBER': 'Membre'
    };
    return labels[role] || labels['MEMBER'];
  }

  categorizeClubs() {
    // Afficher tous les clubs approuvés
    this.myClubs = this.allClubs;
    this.applyFilter();
  }

  applyFilter() {
    if (this.selectedCategory === null) {
      this.filteredClubs = this.myClubs;
    } else {
      this.filteredClubs = this.myClubs.filter(club => club.category === this.selectedCategory);
    }
  }

  filterByCategory(category: ClubCategory | null) {
    this.selectedCategory = category;
    this.applyFilter();
    this.showCategoryDropdown = false; // Fermer le dropdown après sélection
  }

  toggleCategoryDropdown() {
    this.showCategoryDropdown = !this.showCategoryDropdown;
  }

  getSelectedCategoryDisplay(): string {
    if (this.selectedCategory === null) {
      return 'All Categories';
    }
    return this.getCategoryLabel(this.selectedCategory);
  }

  openCreateModal() {
    this.showCreateModal = true;
    this.clubForm.reset({ maxMembers: 20 });
    this.createError = null;
    this.selectedImageFile = null;
    this.imagePreview = null;
  }

  closeCreateModal() {
    this.showCreateModal = false;
    this.clubForm.reset();
    this.createError = null;
    this.selectedImageFile = null;
    this.imagePreview = null;
  }

  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      
      if (!file.type.startsWith('image/')) {
        alert('Please select an image file');
        return;
      }
      
      if (file.size > 5 * 1024 * 1024) {
        alert('Image size must be less than 5MB');
        return;
      }
      
      this.selectedImageFile = file;
      
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  onEditImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      
      if (!file.type.startsWith('image/')) {
        alert('Please select an image file');
        return;
      }
      
      if (file.size > 5 * 1024 * 1024) {
        alert('Image size must be less than 5MB');
        return;
      }
      
      this.editImageFile = file;
      
      const reader = new FileReader();
      reader.onload = (e) => {
        this.editImagePreview = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  private convertFileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        // Keep the full data URL format (data:image/png;base64,...)
        resolve(reader.result as string);
      };
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  }

  async createClub() {
    if (this.clubForm.invalid) {
      this.clubForm.markAllAsTouched();
      return;
    }

    this.creating = true;
    this.createError = null;

    try {
      const clubData: any = { ...this.clubForm.value };
      
      // Ajouter le createdBy (ID de l'utilisateur courant)
      if (this.currentUserId) {
        clubData.createdBy = this.currentUserId;
      }
      
      if (this.selectedImageFile) {
        clubData.image = await this.convertFileToBase64(this.selectedImageFile);
      }

      console.log('📤 Sending club data:', clubData);
      console.log('📤 Club data JSON:', JSON.stringify(clubData, null, 2));

      this.clubService.createClub(clubData).subscribe({
        next: (club) => {
          this.creating = false;
          this.closeCreateModal();
          this.loadClubs();
          alert('Club request submitted successfully! It will be reviewed by an Academic Affairs Officer.');
        },
        error: (err) => {
          console.error('❌ Error creating club:', err);
          console.error('❌ Error status:', err.status);
          console.error('❌ Error message:', err.error);
          
          // Afficher le message d'erreur du backend si disponible
          if (err.error && err.error.message) {
            this.createError = `Failed to create club: ${err.error.message}`;
          } else if (err.error && typeof err.error === 'string') {
            this.createError = `Failed to create club: ${err.error}`;
          } else {
            this.createError = 'Failed to create club. Please check all required fields.';
          }
          
          this.creating = false;
        }
      });
    } catch (error) {
      console.error('Error processing image:', error);
      this.createError = 'Failed to process image. Please try again.';
      this.creating = false;
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.clubForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  leaveClub(clubId: number) {
    if (!this.currentUserId) {
      alert('Please log in to leave a club.');
      return;
    }

    if (confirm('Are you sure you want to leave this club?')) {
      this.memberService.removeMemberFromClub(clubId, this.currentUserId).subscribe({
        next: () => {
          console.log('✅ Successfully left club');
          alert('You have left the club successfully!');
          // Remove from club roles map
          delete this.clubRoles[clubId];
          // Notify that club membership has changed (this will update the sidebar)
          this.clubService.notifyClubMembershipChanged();
          // Update member count if in details view
          if (this.showDetailsView && this.selectedClub?.id === clubId) {
            this.loadActualMemberCount(clubId);
          }
          // Reload clubs to update the UI
          this.loadClubs();
        },
        error: (err) => {
          console.error('❌ Error leaving club:', err);
          if (err.error && err.error.message) {
            alert(`Failed to leave club: ${err.error.message}`);
          } else if (err.error && typeof err.error === 'string') {
            alert(`Failed to leave club: ${err.error}`);
          } else {
            alert('Failed to leave club. Please try again.');
          }
        }
      });
    }
  }

  getCategoryBadgeClass(category: string): string {
    const classes: { [key: string]: string } = {
      'CONVERSATION': 'text-blue-800 bg-blue-100 dark:text-blue-200 dark:bg-blue-900',
      'BOOK': 'text-green-800 bg-green-100 dark:text-green-200 dark:bg-green-900',
      'DRAMA': 'text-orange-800 bg-orange-100 dark:text-orange-200 dark:bg-orange-900',
      'WRITING': 'text-purple-800 bg-purple-100 dark:text-purple-200 dark:bg-purple-900',
      'GRAMMAR': 'text-pink-800 bg-pink-100 dark:text-pink-200 dark:bg-pink-900',
      'VOCABULARY': 'text-indigo-800 bg-indigo-100 dark:text-indigo-200 dark:bg-indigo-900',
      'READING': 'text-teal-800 bg-teal-100 dark:text-teal-200 dark:bg-teal-900',
      'LISTENING': 'text-cyan-800 bg-cyan-100 dark:text-cyan-200 dark:bg-cyan-900',
      'SPEAKING': 'text-red-800 bg-red-100 dark:text-red-200 dark:bg-red-900',
      'PRONUNCIATION': 'text-amber-800 bg-amber-100 dark:text-amber-200 dark:bg-amber-900',
      'BUSINESS': 'text-slate-800 bg-slate-100 dark:text-slate-200 dark:bg-slate-900',
      'ACADEMIC': 'text-emerald-800 bg-emerald-100 dark:text-emerald-200 dark:bg-emerald-900'
    };
    return classes[category] || 'text-gray-800 bg-gray-100 dark:text-gray-200 dark:bg-gray-900';
  }

  getStatusBadgeClass(status?: string): string {
    const classes: { [key: string]: string } = {
      'PENDING': 'text-amber-800 bg-amber-100 dark:text-amber-200 dark:bg-amber-900',
      'APPROVED': 'text-green-800 bg-green-100 dark:text-green-200 dark:bg-green-900',
      'REJECTED': 'text-red-800 bg-red-100 dark:text-red-200 dark:bg-red-900',
      'SUSPENDED': 'text-red-800 bg-red-100 dark:text-red-200 dark:bg-red-900'
    };
    return classes[status || 'PENDING'] || 'text-gray-800 bg-gray-100';
  }

  getCategoryColorClass(category: string): string {
    const classes: { [key: string]: string } = {
      'CONVERSATION': 'bg-blue-100',
      'BOOK': 'bg-green-100',
      'DRAMA': 'bg-orange-100',
      'WRITING': 'bg-purple-100',
      'GRAMMAR': 'bg-pink-100',
      'VOCABULARY': 'bg-indigo-100',
      'READING': 'bg-teal-100',
      'LISTENING': 'bg-cyan-100',
      'SPEAKING': 'bg-red-100',
      'PRONUNCIATION': 'bg-amber-100',
      'BUSINESS': 'bg-slate-100',
      'ACADEMIC': 'bg-emerald-100'
    };
    return classes[category] || 'bg-gray-100';
  }

  getCategoryIcon(category: string): string {
    const icons: { [key: string]: string } = {
      'CONVERSATION': '💬',
      'BOOK': '📚',
      'DRAMA': '🎭',
      'WRITING': '✍️',
      'GRAMMAR': '📝',
      'VOCABULARY': '📖',
      'READING': '📰',
      'LISTENING': '🎧',
      'SPEAKING': '🗣️',
      'PRONUNCIATION': '🔊',
      'BUSINESS': '💼',
      'ACADEMIC': '🎓'
    };
    return icons[category] || '📖';
  }

  getCategoryLabel(category: string): string {
    const labels: { [key: string]: string } = {
      'CONVERSATION': 'Conversation',
      'BOOK': 'Book Club',
      'DRAMA': 'Drama',
      'WRITING': 'Writing',
      'GRAMMAR': 'Grammar',
      'VOCABULARY': 'Vocabulary',
      'READING': 'Reading',
      'LISTENING': 'Listening',
      'SPEAKING': 'Speaking',
      'PRONUNCIATION': 'Pronunciation',
      'BUSINESS': 'Business',
      'ACADEMIC': 'Academic'
    };
    return labels[category] || category;
  }

  getCategoryButtonColors(category: string): { active: string; inactive: string } {
    const colors: { [key: string]: { active: string; inactive: string } } = {
      'CONVERSATION': {
        active: 'bg-gradient-to-r from-blue-500 to-blue-600 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-blue-200 hover:border-blue-400'
      },
      'BOOK': {
        active: 'bg-gradient-to-r from-green-500 to-green-600 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-green-200 hover:border-green-400'
      },
      'DRAMA': {
        active: 'bg-gradient-to-r from-orange-500 to-orange-600 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-orange-200 hover:border-orange-400'
      },
      'WRITING': {
        active: 'bg-gradient-to-r from-purple-500 to-purple-600 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-purple-200 hover:border-purple-400'
      },
      'GRAMMAR': {
        active: 'bg-gradient-to-r from-pink-500 to-pink-600 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-pink-200 hover:border-pink-400'
      },
      'VOCABULARY': {
        active: 'bg-gradient-to-r from-indigo-500 to-indigo-600 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-indigo-200 hover:border-indigo-400'
      },
      'READING': {
        active: 'bg-gradient-to-r from-teal-500 to-teal-600 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-teal-200 hover:border-teal-400'
      },
      'LISTENING': {
        active: 'bg-gradient-to-r from-cyan-500 to-cyan-600 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-cyan-200 hover:border-cyan-400'
      },
      'SPEAKING': {
        active: 'bg-gradient-to-r from-red-500 to-red-600 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-red-200 hover:border-red-400'
      },
      'PRONUNCIATION': {
        active: 'bg-gradient-to-r from-amber-500 to-amber-600 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-amber-200 hover:border-amber-400'
      },
      'BUSINESS': {
        active: 'bg-gradient-to-r from-slate-600 to-slate-700 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-slate-200 hover:border-slate-400'
      },
      'ACADEMIC': {
        active: 'bg-gradient-to-r from-emerald-500 to-emerald-600 text-white shadow-xl',
        inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-emerald-200 hover:border-emerald-400'
      }
    };
    return colors[category] || {
      active: 'bg-gradient-to-r from-gray-500 to-gray-600 text-white shadow-xl',
      inactive: 'bg-white text-gray-700 shadow-md hover:shadow-lg border-2 border-gray-200 hover:border-gray-400'
    };
  }

  getClubCountByCategory(category: ClubCategory): number {
    return this.allClubs.filter(club => club.category === category).length;
  }

  // Members management methods
  openMembersModal(clubId: number) {
    if (this.getUserRole(clubId) !== 'PRESIDENT') {
      alert('Only the president can manage members.');
      return;
    }
    
    this.showMembersModal = true;
    this.loadClubMembers(clubId);
  }

  closeMembersModal() {
    this.showMembersModal = false;
    this.clubMembers = [];
  }

  loadClubMembers(clubId: number) {
    this.loadingMembers = true;
    this.memberService.getMembersByClub(clubId).subscribe({
      next: (members) => {
        console.log('📋 Raw members from API:', members);
        console.log('📋 Members data loaded:', members.length, 'members');
        
        // Log each member's rank
        members.forEach(m => {
          console.log(`  Member ID ${m.id}, User ID ${m.userId}: rank = "${m.rank}"`);
        });
        
        // Extract all unique user IDs
        const userIds = [...new Set(members.map(m => m.userId))];
        console.log('🔍 Fetching user details for IDs:', userIds);
        
        // Fetch user details for all members
        this.userService.getUsersByIds(userIds).subscribe({
          next: (users) => {
            console.log('👤 Users fetched from API:', users);
            
            // Create a map of userId -> user details
            const userMap = new Map(users.map(u => [u.id, u]));
            console.log('🗺️ User map created:', userMap);
            
            // Merge member data with user details
            this.clubMembers = members.map(member => {
              const user = userMap.get(member.userId);
              console.log(`  Looking up user ${member.userId}:`, user);
              
              const mergedMember = {
                ...member,
                firstName: user?.firstName || '',
                lastName: user?.lastName || '',
                image: user?.image || null,
                email: user?.email || ''
              };
              console.log(`  ✅ Merged member ${mergedMember.id}:`, {
                userId: member.userId,
                firstName: mergedMember.firstName,
                lastName: mergedMember.lastName,
                email: mergedMember.email,
                rank: mergedMember.rank
              });
              return mergedMember;
            });
            
            this.loadingMembers = false;
            console.log('📋 Final club members with user details:', this.clubMembers);
          },
          error: (err) => {
            console.error('❌ Error loading user details:', err);
            console.error('❌ Error status:', err.status);
            console.error('❌ Error message:', err.message);
            
            // Fallback: show members without full user details
            this.clubMembers = members.map(member => ({
              ...member,
              firstName: '',
              lastName: '',
              image: null,
              email: ''
            }));
            this.loadingMembers = false;
          }
        });
      },
      error: (err) => {
        console.error('❌ Error loading club members:', err);
        this.loadingMembers = false;
        alert('Failed to load club members.');
      }
    });
  }

  onRoleChange(event: Event, memberId: number, clubId: number) {
    const selectElement = event.target as HTMLSelectElement;
    const newRole = selectElement.value;
    const oldValue = selectElement.getAttribute('data-old-value') || selectElement.value;

    console.log('🔄 Role change requested:', { memberId, clubId, newRole, oldValue, currentUserId: this.currentUserId });

    if (!newRole) {
      console.warn('⚠️ No role selected');
      return;
    }

    if (!this.currentUserId) {
      console.error('❌ No current user ID available');
      alert('Error: User not authenticated. Please refresh the page.');
      return;
    }

    if (confirm(`Are you sure you want to change this member's role to ${this.getRoleLabel(newRole)}?`)) {
      console.log('✅ User confirmed role change');
      console.log('📤 Sending request: memberId=' + memberId + ', newRole=' + newRole + ', requesterId=' + this.currentUserId);
      
      this.memberService.updateMemberRole(memberId, newRole, this.currentUserId).subscribe({
        next: (updatedMember) => {
          console.log('✅ Role updated successfully:', updatedMember);
          alert(`Role successfully updated to ${this.getRoleLabel(newRole)}!`);
          // Reload members to get fresh data from server
          this.loadClubMembers(clubId);
          // Reload user roles
          this.loadUserRoles();
        },
        error: (err) => {
          console.error('❌ Error updating role:', err);
          console.error('❌ Error status:', err.status);
          console.error('❌ Error details:', err.error);
          console.error('❌ Full error object:', JSON.stringify(err, null, 2));
          
          let errorMessage = 'Failed to update role. Please try again.';
          
          if (err.error) {
            if (typeof err.error === 'string') {
              errorMessage = err.error;
            } else if (err.error.message) {
              errorMessage = err.error.message;
            } else if (err.error.error) {
              errorMessage = err.error.error;
            } else {
              errorMessage = JSON.stringify(err.error);
            }
          }
          
          alert(`Error ${err.status}: ${errorMessage}`);
          // Reload members to reset the dropdown
          this.loadClubMembers(clubId);
        }
      });
    } else {
      console.log('❌ User cancelled role change');
      // User cancelled, reload to reset the dropdown
      this.loadClubMembers(clubId);
    }
  }

  removeMember(memberId: number, clubId: number, userId: number) {
    if (confirm('Are you sure you want to remove this member from the club?')) {
      this.memberService.removeMemberFromClub(clubId, userId).subscribe({
        next: () => {
          console.log('✅ Member removed successfully');
          alert('Member removed successfully!');
          // Reload members
          this.loadClubMembers(clubId);
          // Update member count
          this.loadActualMemberCount(clubId);
        },
        error: (err) => {
          console.error('❌ Error removing member:', err);
          alert('Failed to remove member. Please try again.');
        }
      });
    }
  }

  getAvailableRoles(): string[] {
    return [
      'PRESIDENT',
      'VICE_PRESIDENT',
      'SECRETARY',
      'TREASURER',
      'COMMUNICATION_MANAGER',
      'EVENT_MANAGER',
      'PARTNERSHIP_MANAGER',
      'MEMBER'
    ];
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  }

  // Details view methods (not modal)
  openDetailsView(club: Club) {
    this.selectedClub = club;
    this.showDetailsView = true;
    this.showDescription = true;  // Réinitialiser à ouvert
    this.showObjective = true;    // Réinitialiser à ouvert
    this.loadTasksForClub(club.id!);
    this.loadClubEventsForClub(club.id!);
  }

  closeDetailsView() {
    this.showDetailsView = false;
    this.selectedClub = null;
    this.newTaskText = '';
    this.actualMemberCount = 0; // Reset member count
    // Reload clubs list
    this.loadClubs();
  }

  toggleDescription() {
    this.showDescription = !this.showDescription;
  }

  toggleObjective() {
    this.showObjective = !this.showObjective;
  }

  // Edit modal methods
  openEditModal(club: Club) {
    this.editingClub = club;
    this.editForm.patchValue({
      name: club.name,
      description: club.description,
      objective: club.objective || '',
      category: club.category,
      maxMembers: club.maxMembers,
      image: club.image || ''
    });
    this.editImagePreview = null;
    this.editImageFile = null;
    this.showEditModal = true;
    this.updateError = null;
  }

  closeEditModal() {
    this.showEditModal = false;
    this.editingClub = null;
    this.editForm.reset();
    this.updateError = null;
    this.editImagePreview = null;
    this.editImageFile = null;
  }

  async updateClub() {
    if (this.editForm.invalid || !this.editingClub?.id) {
      this.editForm.markAllAsTouched();
      return;
    }

    if (!this.currentUserId) {
      this.updateError = 'User not authenticated. Please log in again.';
      return;
    }

    this.updating = true;
    this.updateError = null;

    try {
      const clubData: any = { ...this.editForm.value };
      
      if (this.editImageFile) {
        clubData.image = await this.convertFileToBase64(this.editImageFile);
      }

      this.clubService.updateClub(this.editingClub.id, clubData, this.currentUserId).subscribe({
        next: (response) => {
          console.log('✅ Update request created:', response);
          
          this.updating = false;
          this.closeEditModal();
          
          // Recharger les demandes en attente
          this.loadPendingRequests();
          
          alert('Demande de modification créée avec succès ! Elle doit être approuvée par le vice-président et le secrétaire.');
        },
        error: (err) => {
          console.error('Error creating update request:', err);
          if (err.error && err.error.message) {
            this.updateError = err.error.message;
          } else if (err.error && typeof err.error === 'string') {
            this.updateError = err.error;
          } else {
            this.updateError = 'Failed to create update request. Please try again.';
          }
          this.updating = false;
        }
      });
    } catch (error) {
      console.error('Error processing image:', error);
      this.updateError = 'Failed to process image. Please try again.';
      this.updating = false;
    }
  }

  isEditFieldInvalid(fieldName: string): boolean {
    const field = this.editForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  // Delete club
  deleteClub(clubId: number) {
    if (confirm('Are you sure you want to delete this club? This action cannot be undone.')) {
      this.clubService.deleteClub(clubId).subscribe({
        next: () => {
          this.loadClubs();
          alert('Club deleted successfully!');
        },
        error: (err) => {
          console.error('Error deleting club:', err);
          alert('Failed to delete club. Please try again.');
        }
      });
    }
  }

  // Check if current user is the president of the club
  isClubPresident(club: Club): boolean {
    if (!club.id) return false;
    const role = this.clubRoles[club.id];
    console.log(`🎭 isClubPresident(${club.id}) = ${role === 'PRESIDENT'}, role = ${role}`);
    return role === 'PRESIDENT';
  }

  // Check if current user is the creator of the club
  isClubCreator(club: Club): boolean {
    return this.currentUserId !== null && club.createdBy === this.currentUserId;
  }

  // Check if current user is a member of the club
  isClubMember(clubId: number): boolean {
    return this.clubRoles.hasOwnProperty(clubId);
  }

  // Join a club
  joinClub(clubId: number) {
    if (!this.currentUserId) {
      alert('Please log in to join a club.');
      return;
    }

    if (confirm('Do you want to join this club?')) {
      this.memberService.addMemberToClub(clubId, this.currentUserId).subscribe({
        next: (member) => {
          console.log('✅ Successfully joined club:', member);
          alert('You have successfully joined the club!');
          // Update the club roles map
          this.clubRoles[clubId] = member.rank;
          // Notify that club membership has changed (this will update the sidebar)
          this.clubService.notifyClubMembershipChanged();
          // Update member count if in details view
          if (this.showDetailsView && this.selectedClub?.id === clubId) {
            this.loadActualMemberCount(clubId);
          }
          // Reload clubs to update the UI (this will also reload roles)
          this.loadClubs();
        },
        error: (err) => {
          console.error('❌ Error joining club:', err);
          if (err.error && err.error.message) {
            alert(`Failed to join club: ${err.error.message}`);
          } else if (err.error && typeof err.error === 'string') {
            alert(`Failed to join club: ${err.error}`);
          } else {
            alert('Failed to join club. The club might be full or you are already a member.');
          }
        }
      });
    }
  }

  // Task management methods
  loadTasksForClub(clubId: number) {
    this.loadingTasks = true;
    console.log('🔍 Loading tasks for club:', clubId, 'with userId:', this.currentUserId);
    this.taskService.getTasksByClubId(clubId, this.currentUserId || undefined).subscribe({
      next: (tasks) => {
        console.log('✅ Tasks loaded successfully:', tasks);
        this.clubTasks[clubId] = tasks;
        this.loadingTasks = false;
      },
      error: (err) => {
        console.error('❌ Error loading tasks:', err);
        console.error('❌ Error status:', err.status);
        console.error('❌ Error message:', err.error);
        // Silently handle error - just show empty task list
        this.clubTasks[clubId] = [];
        this.loadingTasks = false;
      }
    });
  }

  getTasksForClub(clubId: number): Task[] {
    return this.clubTasks[clubId] || [];
  }

  addTask(clubId: number) {
    if (!this.newTaskText.trim()) return;

    const newTask = {
      text: this.newTaskText.trim(),
      status: TaskStatus.TODO,
      clubId: clubId,
      createdBy: this.currentUserId || undefined
    };

    console.log('📤 Creating task:', newTask);

    this.taskService.createTask(newTask).subscribe({
      next: (task) => {
        console.log('✅ Task created successfully:', task);
        if (!this.clubTasks[clubId]) {
          this.clubTasks[clubId] = [];
        }
        this.clubTasks[clubId].push(task);
        this.newTaskText = '';
      },
      error: (err) => {
        console.error('❌ Error creating task:', err);
        console.error('❌ Error status:', err.status);
        console.error('❌ Error details:', err.error);
        alert('Failed to create task. Please try again.');
      }
    });
  }

  updateTaskStatus(clubId: number, taskId: number, newStatus: TaskStatus) {
    // Mettre à jour le statut normalement (ne plus supprimer automatiquement les tâches DONE)
    this.taskService.updateTask(taskId, { status: newStatus }, this.currentUserId || undefined).subscribe({
      next: (updatedTask) => {
        const tasks = this.clubTasks[clubId];
        const index = tasks.findIndex(t => t.id === taskId);
        if (index !== -1) {
          tasks[index] = updatedTask;
        }
      },
      error: (err) => {
        console.error('Error updating task:', err);
        alert('Failed to update task. Please try again.');
      }
    });
  }

  // Drag and Drop handler
  onTaskDrop(event: CdkDragDrop<Task[]>, clubId: number, newStatus: TaskStatus) {
    const task = event.item.data;
    
    if (event.previousContainer === event.container) {
      // Same column - just reorder
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      // Different column - transfer and update status
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
      
      // Update task status in backend
      if (task.id) {
        this.updateTaskStatus(clubId, task.id, newStatus);
      }
    }
  }

  deleteTask(clubId: number, taskId: number) {
    if (!confirm('Are you sure you want to delete this task?')) return;

    this.taskService.deleteTask(taskId, this.currentUserId || undefined).subscribe({
      next: () => {
        this.clubTasks[clubId] = this.clubTasks[clubId].filter(t => t.id !== taskId);
      },
      error: (err) => {
        console.error('Error deleting task:', err);
        alert('Failed to delete task. Please try again.');
      }
    });
  }

  // Start editing a task
  startEditingTask(task: Task) {
    this.editingTaskId = task.id!;
    this.editingTaskText = task.text;
  }

  // Cancel editing
  cancelEditingTask() {
    this.editingTaskId = null;
    this.editingTaskText = '';
  }

  // Save edited task
  saveEditedTask(clubId: number, taskId: number) {
    if (!this.editingTaskText.trim()) {
      alert('Task text cannot be empty');
      return;
    }

    this.taskService.updateTask(taskId, { text: this.editingTaskText.trim() }, this.currentUserId || undefined).subscribe({
      next: (updatedTask) => {
        const tasks = this.clubTasks[clubId];
        const index = tasks.findIndex(t => t.id === taskId);
        if (index !== -1) {
          tasks[index] = updatedTask;
        }
        this.cancelEditingTask();
      },
      error: (err) => {
        console.error('Error updating task:', err);
        alert('Failed to update task. Please try again.');
      }
    });
  }

  // Check if a task is being edited
  isEditingTask(taskId: number): boolean {
    return this.editingTaskId === taskId;
  }

  getTasksByStatus(clubId: number, status: TaskStatus): Task[] {
    return this.getTasksForClub(clubId).filter(t => t.status === status);
  }

  getTaskCountByStatus(clubId: number, status: TaskStatus): number {
    return this.getTasksByStatus(clubId, status).length;
  }

  getTotalTaskCount(clubId: number): number {
    return this.getTasksForClub(clubId).length;
  }

  getTaskStatusLabel(status: TaskStatus): string {
    const labels: { [key in TaskStatus]: string } = {
      [TaskStatus.TODO]: 'À faire',
      [TaskStatus.IN_PROGRESS]: 'En cours',
      [TaskStatus.DONE]: 'Terminé'
    };
    return labels[status];
  }

  getTaskStatusColor(status: TaskStatus): string {
    const colors: { [key in TaskStatus]: string } = {
      [TaskStatus.TODO]: 'bg-gray-100 text-gray-700',
      [TaskStatus.IN_PROGRESS]: 'bg-blue-100 text-blue-700',
      [TaskStatus.DONE]: 'bg-green-100 text-green-700'
    };
    return colors[status];
  }

  // Pending update requests methods
  loadPendingRequests() {
    if (!this.currentUserId) {
      console.log('❌ Cannot load pending requests: no current user');
      return;
    }
    
    console.log('🔍 Loading pending requests...');
    console.log('My clubs:', this.myClubs);
    console.log('Club roles:', this.clubRoles);
    
    this.loadingRequests = true;
    const requestsObservables = this.myClubs
      .filter(club => {
        const role = this.clubRoles[club.id!];
        console.log(`Club ${club.name} (${club.id}): role = ${role}`);
        return role === 'VICE_PRESIDENT' || role === 'SECRETARY';
      })
      .map(club => this.updateRequestService.getPendingRequestsForClub(club.id!));
    
    console.log('Clubs with VP/Secretary role:', requestsObservables.length);
    
    if (requestsObservables.length === 0) {
      this.loadingRequests = false;
      this.pendingRequests = [];
      console.log('⚠️ No clubs where user is VP or Secretary');
      return;
    }
    
    forkJoin(requestsObservables).subscribe({
      next: (results) => {
        this.pendingRequests = results.flat();
        console.log('📋 Pending requests loaded:', this.pendingRequests.length);
        console.log('Pending requests:', this.pendingRequests);
        this.loadingRequests = false;
      },
      error: (err) => {
        console.error('❌ Error loading pending requests:', err);
        this.loadingRequests = false;
      }
    });
  }

  approveRequest(requestId: number) {
    if (!this.currentUserId) {
      alert('Vous devez être connecté pour approuver');
      return;
    }

    this.updateRequestService.approveRequest(requestId, this.currentUserId).subscribe({
      next: (updatedRequest) => {
        if (updatedRequest.status === 'APPROVED') {
          alert('Demande approuvée et modifications appliquées !');
          this.loadClubs(); // Reload clubs to show updated info
          if (this.selectedClub) {
            this.loadAndDisplayClub(this.selectedClub.id!); // Reload the selected club
          }
        } else {
          alert('Votre approbation a été enregistrée. En attente de l\'autre approbation.');
        }
        this.loadPendingRequests();
        
        // Close modal if no more pending requests for this club
        if (this.selectedClub && this.getPendingRequestsForClub(this.selectedClub.id!).length === 0) {
          this.closeUpdateRequestsModal();
        }
      },
      error: (err) => {
        console.error('Error approving request:', err);
        alert(err.error?.message || 'Erreur lors de l\'approbation');
      }
    });
  }

  rejectRequest(requestId: number) {
    if (!this.currentUserId) {
      alert('Vous devez être connecté pour rejeter');
      return;
    }

    if (!confirm('Êtes-vous sûr de vouloir rejeter cette demande ?')) {
      return;
    }

    this.updateRequestService.rejectRequest(requestId, this.currentUserId).subscribe({
      next: () => {
        alert('Demande rejetée');
        this.loadPendingRequests();
        
        // Close modal if no more pending requests for this club
        if (this.selectedClub && this.getPendingRequestsForClub(this.selectedClub.id!).length === 0) {
          this.closeUpdateRequestsModal();
        }
      },
      error: (err) => {
        console.error('Error rejecting request:', err);
        alert(err.error?.message || 'Erreur lors du rejet');
      }
    });
  }

  getClubById(clubId: number): Club | undefined {
    return this.allClubs.find(c => c.id === clubId);
  }

  canApproveRequests(): boolean {
    return Object.values(this.clubRoles).some(role => 
      role === 'VICE_PRESIDENT' || role === 'SECRETARY'
    );
  }

  // Update requests modal methods
  openUpdateRequestsModal() {
    this.showUpdateRequestsModal = true;
  }

  closeUpdateRequestsModal() {
    this.showUpdateRequestsModal = false;
  }
  
  // Club events methods
  loadClubEventsForClub(clubId: number) {
    this.loadingEvents = true;
    // First, get the club president
    this.memberService.getMembersByClub(clubId).subscribe({
      next: (members) => {
        const president = members.find(m => m.rank === 'PRESIDENT');
        if (president) {
          // Load events created by the president
          this.eventService.getEventsByCreator(president.userId).subscribe({
            next: (events) => {
              this.clubEvents = events.filter(e => e.status === 'APPROVED').sort((a, b) => {
                const dateA = new Date(a.startDate || a.eventDate || '').getTime();
                const dateB = new Date(b.startDate || b.eventDate || '').getTime();
                return dateA - dateB;
              });
              this.loadingEvents = false;
              console.log('✅ Loaded', this.clubEvents.length, 'events for club president');
              
              // Load feedback stats for each event
              this.clubEvents.forEach(event => {
                if (event.id) {
                  this.loadEventFeedbackStats(event.id);
                }
              });
            },
            error: (error) => {
              console.error('Error loading club events:', error);
              this.clubEvents = [];
              this.loadingEvents = false;
            }
          });
        } else {
          console.log('⚠️ No president found for club');
          this.clubEvents = [];
          this.loadingEvents = false;
        }
      },
      error: (error) => {
        console.error('Error loading club members:', error);
        this.clubEvents = [];
        this.loadingEvents = false;
      }
    });
  }
  
  // Load feedback stats for an event
  loadEventFeedbackStats(eventId: number) {
    this.eventFeedbackService.getEventFeedbackStats(eventId).subscribe({
      next: (stats) => {
        this.eventFeedbackStats[eventId] = {
          averageRating: stats.averageRating,
          totalFeedbacks: stats.totalFeedbacks
        };
      },
      error: (err) => {
        console.error(`Error loading feedback stats for event ${eventId}:`, err);
        this.eventFeedbackStats[eventId] = {
          averageRating: 0,
          totalFeedbacks: 0
        };
      }
    });
  }
  
  // Check if event is finished
  isEventFinished(event: ClubEvent): boolean {
    const endDate = event.endDate || event.eventDate;
    if (!endDate) return false;
    return new Date(endDate) < new Date();
  }
  
  // Get feedback stats for an event
  getEventFeedbackStats(eventId: number): { averageRating: number; totalFeedbacks: number } {
    return this.eventFeedbackStats[eventId] || { averageRating: 0, totalFeedbacks: 0 };
  }
  
  loadClubEvents(creatorId: number) {
    this.loadingEvents = true;
    this.eventService.getEventsByCreator(creatorId).subscribe({
      next: (events) => {
        this.clubEvents = events.filter(e => e.status === 'APPROVED').sort((a, b) => {
          const dateA = new Date(a.startDate || a.eventDate || '').getTime();
          const dateB = new Date(b.startDate || b.eventDate || '').getTime();
          return dateA - dateB;
        });
        this.loadingEvents = false;
      },
      error: (error) => {
        console.error('Error loading club events:', error);
        this.clubEvents = [];
        this.loadingEvents = false;
      }
    });
  }
  
  formatEventDate(dateString: string): string {
    const date = new Date(dateString);
    const options: Intl.DateTimeFormatOptions = { 
      month: 'short', 
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    };
    return date.toLocaleDateString('en-US', options);
  }
  
  getEventStatusClass(status: string | undefined): string {
    switch (status) {
      case 'APPROVED':
        return 'bg-green-500 text-white';
      case 'PENDING':
        return 'bg-yellow-500 text-white';
      case 'REJECTED':
        return 'bg-red-500 text-white';
      default:
        return 'bg-gray-500 text-white';
    }
  }
  
  // Navigate to event details
  navigateToEventDetails(eventId: number) {
    this.router.navigate(['/user-panel/events', eventId]);
  }
}
