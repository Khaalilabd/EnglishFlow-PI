import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { ClubService } from '../../../core/services/club.service';
import { AuthService } from '../../../core/services/auth.service';
import { TaskService } from '../../../core/services/task.service';
import { Club, ClubCategory, ClubStatus } from '../../../core/models/club.model';
import { Task, TaskStatus } from '../../../core/models/task.model';

@Component({
  selector: 'app-student-clubs',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './clubs.component.html',
  styleUrls: ['./clubs.component.scss']
})
export class ClubsComponent implements OnInit {
  allClubs: Club[] = [];
  myClubs: Club[] = [];
  filteredClubs: Club[] = [];
  loading = false;
  error: string | null = null;
  categories = Object.values(ClubCategory);
  selectedCategory: ClubCategory | null = null;
  currentUserId: number | null = null;
  
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

  // Details modal
  showDetailsModal = false;
  selectedClub: Club | null = null;
  showDescription = true;  // Section description ouverte par défaut
  showObjective = true;    // Section objectif ouverte par défaut

  // Task management
  clubTasks: { [clubId: number]: Task[] } = {};
  newTaskText: string = '';
  TaskStatus = TaskStatus; // Expose enum to template
  ClubStatus = ClubStatus; // Expose enum to template
  loadingTasks = false;

  constructor(
    private clubService: ClubService,
    private authService: AuthService,
    private taskService: TaskService,
    private fb: FormBuilder
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
    this.loadClubs();
  }

  getCurrentUser() {
    const user = this.authService.currentUserValue;
    console.log('Current user:', user); // Debug
    if (user && user.id !== undefined && user.id !== null) {
      this.currentUserId = user.id;
      console.log('Current user ID:', this.currentUserId); // Debug
    } else {
      console.error('No user found or user has no ID');
      this.error = 'User not authenticated. Please log in again.';
    }
  }

  loadClubs() {
    this.loading = true;
    this.error = null;

    // Charger les clubs créés par l'utilisateur courant
    if (this.currentUserId) {
      this.clubService.getClubsByUser(this.currentUserId).subscribe({
        next: (clubs) => {
          this.allClubs = clubs;
          this.categorizeClubs();
          this.loading = false;
        },
        error: (err) => {
          console.error('Error loading clubs:', err);
          this.error = 'Failed to load clubs. Please try again.';
          this.loading = false;
        }
      });
    } else {
      // Si pas d'utilisateur connecté, afficher un message
      this.error = 'User not authenticated. Please log in again.';
      this.loading = false;
    }
  }

  categorizeClubs() {
    if (!this.currentUserId) {
      this.myClubs = this.allClubs;
      this.filteredClubs = this.allClubs;
      return;
    }

    // Afficher tous les clubs créés par l'utilisateur (peu importe le statut)
    // Cela inclut les demandes PENDING, APPROVED, et REJECTED
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

  getCategoryBadgeClass(category: string): string {
    const classes: { [key: string]: string } = {
      'CONVERSATION': 'text-blue-800 bg-blue-100 dark:text-blue-200 dark:bg-blue-900',
      'BOOK': 'text-green-800 bg-green-100 dark:text-green-200 dark:bg-green-900',
      'DRAMA': 'text-orange-800 bg-orange-100 dark:text-orange-200 dark:bg-orange-900',
      'WRITING': 'text-purple-800 bg-purple-100 dark:text-purple-200 dark:bg-purple-900'
    };
    return classes[category] || 'text-gray-800 bg-gray-100 dark:text-gray-200 dark:bg-gray-900';
  }

  getStatusBadgeClass(status?: string): string {
    const classes: { [key: string]: string } = {
      'PENDING': 'text-amber-800 bg-amber-100 dark:text-amber-200 dark:bg-amber-900',
      'APPROVED': 'text-green-800 bg-green-100 dark:text-green-200 dark:bg-green-900',
      'REJECTED': 'text-red-800 bg-red-100 dark:text-red-200 dark:bg-red-900'
    };
    return classes[status || 'PENDING'] || 'text-gray-800 bg-gray-100';
  }

  getCategoryColorClass(category: string): string {
    const classes: { [key: string]: string } = {
      'CONVERSATION': 'bg-blue-100',
      'BOOK': 'bg-green-100',
      'DRAMA': 'bg-orange-100',
      'WRITING': 'bg-purple-100'
    };
    return classes[category] || 'bg-gray-100';
  }

  getCategoryIcon(category: string): string {
    const icons: { [key: string]: string } = {
      'CONVERSATION': '💬',
      'BOOK': '📚',
      'DRAMA': '🎭',
      'WRITING': '✍️'
    };
    return icons[category] || '📖';
  }

  // Details modal methods
  openDetailsModal(club: Club) {
    this.selectedClub = club;
    this.showDetailsModal = true;
    this.showDescription = true;  // Réinitialiser à ouvert
    this.showObjective = true;    // Réinitialiser à ouvert
    this.loadTasksForClub(club.id!);
  }

  closeDetailsModal() {
    this.showDetailsModal = false;
    this.selectedClub = null;
    this.newTaskText = '';
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

    this.updating = true;
    this.updateError = null;

    try {
      const clubData: any = { ...this.editForm.value };
      
      if (this.editImageFile) {
        clubData.image = await this.convertFileToBase64(this.editImageFile);
      }

      this.clubService.updateClub(this.editingClub.id, clubData).subscribe({
        next: () => {
          this.updating = false;
          this.closeEditModal();
          this.loadClubs();
          alert('Club updated successfully!');
        },
        error: (err) => {
          console.error('Error updating club:', err);
          this.updateError = 'Failed to update club. Please try again.';
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

  // Check if current user is the creator of the club
  isClubCreator(club: Club): boolean {
    return this.currentUserId !== null && club.createdBy === this.currentUserId;
  }

  // Task management methods
  loadTasksForClub(clubId: number) {
    this.loadingTasks = true;
    this.taskService.getTasksByClubId(clubId).subscribe({
      next: (tasks) => {
        this.clubTasks[clubId] = tasks;
        this.loadingTasks = false;
      },
      error: (err) => {
        console.error('Error loading tasks:', err);
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

    this.taskService.createTask(newTask).subscribe({
      next: (task) => {
        if (!this.clubTasks[clubId]) {
          this.clubTasks[clubId] = [];
        }
        this.clubTasks[clubId].push(task);
        this.newTaskText = '';
      },
      error: (err) => {
        console.error('Error creating task:', err);
        alert('Failed to create task. Please try again.');
      }
    });
  }

  updateTaskStatus(clubId: number, taskId: number, newStatus: TaskStatus) {
    // Si le nouveau statut est DONE, supprimer la tâche automatiquement
    if (newStatus === TaskStatus.DONE) {
      this.taskService.deleteTask(taskId).subscribe({
        next: () => {
          // Retirer la tâche de la liste
          this.clubTasks[clubId] = this.clubTasks[clubId].filter(t => t.id !== taskId);
        },
        error: (err) => {
          console.error('Error deleting completed task:', err);
          alert('Failed to delete completed task. Please try again.');
        }
      });
    } else {
      // Sinon, mettre à jour le statut normalement
      this.taskService.updateTask(taskId, { status: newStatus }).subscribe({
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
  }

  deleteTask(clubId: number, taskId: number) {
    if (!confirm('Are you sure you want to delete this task?')) return;

    this.taskService.deleteTask(taskId).subscribe({
      next: () => {
        this.clubTasks[clubId] = this.clubTasks[clubId].filter(t => t.id !== taskId);
      },
      error: (err) => {
        console.error('Error deleting task:', err);
        alert('Failed to delete task. Please try again.');
      }
    });
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
}
