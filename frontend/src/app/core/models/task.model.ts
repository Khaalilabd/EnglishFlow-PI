export interface Task {
  id?: number;
  text: string;
  status: TaskStatus;
  clubId: number;
  createdBy?: number;
  createdAt?: string;
  updatedAt?: string;
}

export enum TaskStatus {
  TODO = 'TODO',
  IN_PROGRESS = 'IN_PROGRESS',
  DONE = 'DONE'
}

export interface CreateTaskRequest {
  text: string;
  status: TaskStatus;
  clubId: number;
  createdBy?: number;
}

export interface UpdateTaskRequest {
  text?: string;
  status?: TaskStatus;
}
