export type QuestionType = 'MCQ' | 'TRUE_FALSE' | 'OPEN';

export interface Quiz {
  id?: number;
  title: string;
  description: string;
  courseId?: number;
  durationMin?: number;
  maxScore: number;
  passingScore: number;
  published: boolean;
  publishAt?: string;
  shuffleQuestions?: boolean;
  shuffleOptions?: boolean;
  showAnswersTiming?: 'immediate' | 'end' | 'never' | 'after_deadline';
  category?: string;
  difficulty?: 'easy' | 'medium' | 'hard';
  tags?: string;
  dueDate?: string;
  createdAt?: string;
  updatedAt?: string;
  questions?: Question[];
}

export interface Question {
  id?: number;
  quizId?: number;
  content: string;
  type: QuestionType;
  options?: string;
  correctAnswer: string;
  points: number;
  orderIndex?: number;
  partialCreditEnabled?: boolean;
}

export interface QuizAttempt {
  id?: number;
  quizId: number;
  studentId: number;
  score?: number;
  startedAt?: string;
  submittedAt?: string;
  status: 'IN_PROGRESS' | 'COMPLETED';
}

export interface AttemptRequest {
  quizId: number;
  studentId: number;
  answers: { [questionId: number]: string };
}

export interface AttemptResult {
  attemptId: number;
  quizId: number;
  studentId: number;
  score: number;
  maxScore: number;
  passed: boolean;
  startedAt: string;
  submittedAt: string;
  status: string;
  answerDetails: {
    [questionId: number]: {
      studentAnswer: string;
      correctAnswer: string;
      isCorrect: boolean;
      pointsEarned: number;
    };
  };
}
