import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CdkDragDrop, DragDropModule, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { TaskService } from '../../../core/services/task.service';
import { Task, TaskStatus, CreateTaskRequest, UpdateTaskRequest } from '../../../core/models/task.model';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-club-tasks',
  standalone: true,
  imports: [CommonModule, FormsModule, DragDropModule],
  templateUrl: './club-tasks.component.html',
  styleUrls: ['./club-tasks.component.scss']
})
export class ClubTasksComponent implements OnInit {
  @Input() clubId!: number;
  @Input() userId!: number;
  @Input() canManage: boolean = false; // Permission to manage tasks (add, edit, delete, drag)

  tasks: Task[] = [];
  tasksByStatus: { [status: string]: Task[] } = {
    [TaskStatus.TODO]: [],
    [TaskStatus.IN_PROGRESS]: [],
    [TaskStatus.DONE]: []
  };

  newTaskText = '';
  loading = false;
  editingTaskId: number | null = null;
  editingTaskText = '';

  TaskStatus = TaskStatus;

  constructor(
    private taskService: TaskService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.loadTasks();
  }

  loadTasks() {
    this.loading = true;
    this.taskService.getTasksByClubId(this.clubId, this.userId).subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.organizeTasks();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading tasks:', err);
        this.loading = false;
      }
    });
  }

  organizeTasks() {
    // Clear arrays but keep references
    this.tasksByStatus[TaskStatus.TODO].length = 0;
    this.tasksByStatus[TaskStatus.IN_PROGRESS].length = 0;
    this.tasksByStatus[TaskStatus.DONE].length = 0;

    // Populate arrays
    this.tasks.forEach(task => {
      this.tasksByStatus[task.status].push(task);
    });
  }

  getTasksByStatus(status: TaskStatus): Task[] {
    return this.tasksByStatus[status] || [];
  }

  addTask() {
    if (!this.newTaskText.trim()) return;

    const newTask: CreateTaskRequest = {
      text: this.newTaskText.trim(),
      status: TaskStatus.TODO,
      clubId: this.clubId,
      createdBy: this.userId
    };

    this.taskService.createTask(newTask).subscribe({
      next: (task) => {
        this.tasks.push(task);
        this.organizeTasks();
        this.newTaskText = '';
        this.notificationService.success('Success', 'Task added successfully');
      },
      error: (err) => {
        console.error('Error adding task:', err);
        this.notificationService.error('Error', 'Failed to add task');
      }
    });
  }

  onTaskDrop(event: CdkDragDrop<Task[]>, newStatus: TaskStatus) {
    const task = event.item.data;

    if (event.previousContainer === event.container) {
      // Same column - reorder
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      // Different column - transfer and update status
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );

      // Update task status
      const oldStatus = task.status;
      task.status = newStatus;
      const taskIndex = this.tasks.findIndex(t => t.id === task.id);
      if (taskIndex !== -1) {
        this.tasks[taskIndex].status = newStatus;
      }

      // Update in backend
      if (task.id) {
        this.updateTask(task.id, { status: newStatus }, oldStatus);
      }
    }
  }

  updateTask(taskId: number, updates: UpdateTaskRequest, oldStatus?: TaskStatus) {
    this.taskService.updateTask(taskId, updates, this.userId).subscribe({
      next: () => {
        console.log('Task updated successfully');
      },
      error: (err: any) => {
        console.error('Error updating task:', err);
        this.notificationService.error('Error', 'Failed to update task');
        // Revert on error
        if (oldStatus) {
          const taskIndex = this.tasks.findIndex(t => t.id === taskId);
          if (taskIndex !== -1) {
            this.tasks[taskIndex].status = oldStatus;
          }
          this.organizeTasks();
        }
      }
    });
  }

  startEditingTask(task: Task) {
    this.editingTaskId = task.id!;
    this.editingTaskText = task.text;
  }

  saveEditedTask(taskId: number) {
    if (!this.editingTaskText.trim()) {
      this.cancelEditingTask();
      return;
    }

    this.taskService.updateTask(taskId, { text: this.editingTaskText.trim() }, this.userId).subscribe({
      next: (updatedTask) => {
        const taskIndex = this.tasks.findIndex(t => t.id === taskId);
        if (taskIndex !== -1) {
          this.tasks[taskIndex] = updatedTask;
        }
        this.organizeTasks();
        this.cancelEditingTask();
        this.notificationService.success('Success', 'Task updated successfully');
      },
      error: (err) => {
        console.error('Error updating task:', err);
        this.notificationService.error('Error', 'Failed to update task');
      }
    });
  }

  cancelEditingTask() {
    this.editingTaskId = null;
    this.editingTaskText = '';
  }

  isEditingTask(taskId: number): boolean {
    return this.editingTaskId === taskId;
  }

  deleteTask(taskId: number) {
    if (!confirm('Are you sure you want to delete this task?')) return;

    this.taskService.deleteTask(taskId, this.userId).subscribe({
      next: () => {
        this.tasks = this.tasks.filter(t => t.id !== taskId);
        this.organizeTasks();
        this.notificationService.success('Success', 'Task deleted successfully');
      },
      error: (err) => {
        console.error('Error deleting task:', err);
        this.notificationService.error('Error', 'Failed to delete task');
      }
    });
  }

  trackByTaskId(index: number, task: Task): number {
    return task.id!;
  }
}
