export enum QuestionType {
  MULTIPLE_CHOICE = 'MULTIPLE_CHOICE',
  TRUE_FALSE = 'TRUE_FALSE',
  SHORT_ANSWER = 'SHORT_ANSWER'
}

export interface Question {
  id?: number;
  quizId: number;
  questionText: string;
  questionType: QuestionType;
  optionA?: string;
  optionB?: string;
  optionC?: string;
  optionD?: string;
  correctAnswer: string;
  points?: number;
  orderIndex?: number;
  explanation?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateQuestionRequest {
  quizId: number;
  questionText: string;
  questionType: QuestionType;
  optionA?: string;
  optionB?: string;
  optionC?: string;
  optionD?: string;
  correctAnswer: string;
  points?: number;
  orderIndex?: number;
  explanation?: string;
}

export interface UpdateQuestionRequest {
  questionText: string;
  questionType: QuestionType;
  optionA?: string;
  optionB?: string;
  optionC?: string;
  optionD?: string;
  correctAnswer: string;
  points?: number;
  orderIndex?: number;
  explanation?: string;
}
