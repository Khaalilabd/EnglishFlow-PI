import { LessonType } from './lesson.model';

export interface LessonMedia {
  id?: number;
  url: string;
  mediaType: LessonType;
  position: number;
  title?: string;
  description?: string;
  lessonId: number;
}
