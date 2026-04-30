import { Component, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { trigger, transition, style, animate, query, group, stagger } from '@angular/animations';
import { AuthService } from '../../core/services/auth.service';
import { PlacementTestService } from '../../core/services/placement-test.service';

// Type definitions matching JSON structure
interface Question {
  id: string;
  level: 'A1' | 'A2' | 'B1' | 'B2' | 'C1';
  type: 'fill-in-the-blank' | 'multiple-choice';
  sentence?: string;
  question?: string;
  options: string[];
  answer: string;
  topic: string;
}

interface ListeningQuestion {
  id: string;
  level: 'A1' | 'A2' | 'B1' | 'B2' | 'C1';
  audioSrc: string;
  audioTranscript: string;
  question: string;
  options: string[];
  answer: string;
  topic: string;
}

interface ReadingQuestion {
  id: string;
  level: 'A1' | 'A2' | 'B1' | 'B2' | 'C1';
  passage: string;
  question: string;
  options: string[];
  answer: string;
  topic: string;
}

interface PictureDescriptionQuestion {
  id: string;
  level: 'B2' | 'C1' | 'C2';
  imageSrc: string;
  question: string;
  options: string[];
  answer: string;
}

interface SpeakingQuestion {
  id: string;
  level: 'A1' | 'A2' | 'B1' | 'B2' | 'C1';
  prompt: string;
  exampleResponse: string;
  keywordsToDetect: string[];
  topic: string;
}

interface QuestionBank {
  meta: {
    version: string;
    totalQuestions: number;
    levels: string[];
    sections: string[];
  };
  grammar: Question[];
  listening: ListeningQuestion[];
  reading: ReadingQuestion[];
  pictureDescription: PictureDescriptionQuestion[];
  speaking: SpeakingQuestion[];
  levelMap: any;
  scoringRules: any;
}

type Section = 'grammar' | 'listening' | 'reading' | 'pictureDescription' | 'speaking' | 'results';

@Component({
  selector: 'app-english-placement-test',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './english-placement-test.component.html',
  styleUrls: ['./english-placement-test.component.scss'],
  animations: [
    trigger('slideIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(20px)' }),
        animate('350ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ])
    ]),
    trigger('questionSlide', [
      transition(':increment', [
        group([
          query(':leave', [
            style({ opacity: 1, transform: 'translateY(0)' }),
            animate('350ms ease-in-out', style({ opacity: 0, transform: 'translateY(-60px)' }))
          ], { optional: true }),
          query(':enter', [
            style({ opacity: 0, transform: 'translateY(60px)' }),
            animate('350ms 150ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ], { optional: true })
        ])
      ]),
      transition(':decrement', [
        group([
          query(':leave', [
            style({ opacity: 1, transform: 'translateY(0)' }),
            animate('350ms ease-in-out', style({ opacity: 0, transform: 'translateY(60px)' }))
          ], { optional: true }),
          query(':enter', [
            style({ opacity: 0, transform: 'translateY(-60px)' }),
            animate('350ms 150ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
          ], { optional: true })
        ])
      ])
    ]),
    trigger('buttonFade', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(10px)' }),
        animate('250ms 200ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ opacity: 0, transform: 'translateY(-10px)' }))
      ])
    ])
  ]
})
export class EnglishPlacementTestComponent implements OnInit, OnDestroy {
  @Output() testCompleted = new EventEmitter<string>();

  // Test state
  testStarted = false; // NEW: Track if test has started
  currentSection: Section = 'grammar';
  currentQuestionIndex = 0;
  selectedAnswer: number | null = null;
  showFeedback = false;
  isCorrect = false;
  
  // Adaptive difficulty
  currentLevel: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' = 'A1';
  consecutiveCorrect = 0;
  consecutiveWrong = 0;
  
  // Scores
  grammarScore = 0;
  listeningScore = 0;
  readingScore = 0;
  pictureDescriptionScore = 0;
  speakingScore = 0;
  totalScore = 0;
  finalLevel = '';
  
  // Timer
  timeRemaining = 15 * 60; // 15 minutes in seconds
  timerInterval: any;
  
  // Listening
  audioPlaysLeft = 2;
  isAudioPlaying = false;
  audioElement: HTMLAudioElement | null = null;
  
  // Speaking
  isRecording = false;
  transcript = '';
  recognition: any = null;
  speechSupported = false;
  
  // Results
  skillBreakdown = {
    grammar: 0,
    listening: 0,
    reading: 0,
    pictureDescription: 0,
    speaking: 0
  };
  personalizedInsight = '';
  showConfetti = false;

  // Question banks - loaded from JSON
  questionBank: QuestionBank | null = null;
  grammarQuestions: { [key: string]: Question[] } = {};
  listeningQuestions: ListeningQuestion[] = [];
  readingQuestions: { [key: string]: ReadingQuestion[] } = {};
  pictureDescriptionQuestions: PictureDescriptionQuestion[] = [];
  speakingQuestions: SpeakingQuestion[] = [];
  
  // Track used question IDs to avoid repeats
  usedGrammarQuestionIds: Set<string> = new Set();
  usedReadingQuestionIds: Set<string> = new Set();

  constructor(
    private router: Router,
    private http: HttpClient,
    private authService: AuthService,
    private placementTestService: PlacementTestService
  ) {}

  ngOnInit(): void {
    this.loadQuestions();
    // Don't start timer or check speech until test is started
  }
  
  startTest(): void {
    this.testStarted = true;
    this.startTimer();
    this.checkSpeechSupport();
  }

  private loadQuestions(): void {
    this.http.get<QuestionBank>('assets/data/placement-test-questions.json').subscribe({
      next: (data) => {
        this.questionBank = data;
        
        // Organize grammar questions by level and shuffle them
        this.grammarQuestions = {
          A1: this.shuffleArray([...data.grammar.filter(q => q.level === 'A1')]),
          A2: this.shuffleArray([...data.grammar.filter(q => q.level === 'A2')]),
          B1: this.shuffleArray([...data.grammar.filter(q => q.level === 'B1')]),
          B2: this.shuffleArray([...data.grammar.filter(q => q.level === 'B2')]),
          C1: this.shuffleArray([...data.grammar.filter(q => q.level === 'C1')])
        };
        
        // Shuffle listening questions
        this.listeningQuestions = this.shuffleArray([...data.listening]);
        
        // Organize reading questions by level and shuffle them
        this.readingQuestions = {
          A1: this.shuffleArray([...data.reading.filter(q => q.level === 'A1')]),
          A2: this.shuffleArray([...data.reading.filter(q => q.level === 'A2')]),
          B1: this.shuffleArray([...data.reading.filter(q => q.level === 'B1')]),
          B2: this.shuffleArray([...data.reading.filter(q => q.level === 'B2')]),
          C1: this.shuffleArray([...data.reading.filter(q => q.level === 'C1')])
        };
        
        // Shuffle picture description questions
        this.pictureDescriptionQuestions = this.shuffleArray([...data.pictureDescription]);
        
        // Shuffle speaking questions
        this.speakingQuestions = this.shuffleArray([...data.speaking]);
        
        console.log('✅ Questions loaded and shuffled successfully');
      },
      error: (error) => {
        console.error('❌ Failed to load questions:', error);
        this.grammarQuestions = { A1: [], A2: [], B1: [], B2: [], C1: [] };
        this.listeningQuestions = [];
        this.readingQuestions = { A1: [], A2: [], B1: [], B2: [], C1: [] };
        this.pictureDescriptionQuestions = [];
        this.speakingQuestions = [];
      }
    });
  }
  
  /**
   * Fisher-Yates shuffle algorithm to randomize array order
   */
  private shuffleArray<T>(array: T[]): T[] {
    const shuffled = [...array];
    for (let i = shuffled.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }
    return shuffled;
  }

  private checkSpeechSupport(): void {
    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
    this.speechSupported = !!SpeechRecognition;
    
    if (this.speechSupported) {
      this.recognition = new SpeechRecognition();
      this.recognition.continuous = false;
      this.recognition.interimResults = false;
      this.recognition.lang = 'en-US';
      
      this.recognition.onresult = (event: any) => {
        this.transcript = event.results[0][0].transcript;
      };
      
      this.recognition.onend = () => {
        this.isRecording = false;
      };
    }
  }

  ngOnDestroy(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
    if (this.recognition) {
      this.recognition.stop();
    }
    // Clean up audio element
    if (this.audioElement) {
      this.audioElement.pause();
      this.audioElement = null;
    }
  }

  startTimer(): void {
    this.timerInterval = setInterval(() => {
      this.timeRemaining--;
      if (this.timeRemaining <= 0) {
        this.finishTest();
      }
    }, 1000);
  }

  get timerDisplay(): string {
    const minutes = Math.floor(this.timeRemaining / 60);
    const seconds = this.timeRemaining % 60;
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  }

  get timerClass(): string {
    if (this.timeRemaining <= 120) return 'timer-critical';
    if (this.timeRemaining <= 300) return 'timer-warning';
    return '';
  }

  get currentGrammarQuestion(): Question | null {
    const questions = this.grammarQuestions[this.currentLevel];
    if (!questions || this.currentQuestionIndex >= 7) return null; // Changed from 10 to 7
    
    // Find an unused question at this level
    const availableQuestions = questions.filter(q => !this.usedGrammarQuestionIds.has(q.id));
    
    if (availableQuestions.length === 0) {
      // If all questions used, reset and use any question
      const questionIndex = this.currentQuestionIndex % questions.length;
      const question = questions[questionIndex];
      return question || null;
    }
    
    // Use the first available unused question
    const question = availableQuestions[0];
    this.usedGrammarQuestionIds.add(question.id);
    return question;
  }
  
  /**
   * Shuffle answer options and track the correct answer position
   * DISABLED - Keep options in original order for consistency
   */
  private shuffleQuestionOptions<T extends Question | ListeningQuestion | ReadingQuestion>(question: T): T {
    return question;
  }

  get currentListeningQuestion(): ListeningQuestion | null {
    if (this.currentQuestionIndex >= this.listeningQuestions.length) return null;
    const question = this.listeningQuestions[this.currentQuestionIndex];
    return question || null;
  }

  get currentReadingQuestion(): ReadingQuestion | null {
    const questions = this.readingQuestions[this.currentLevel];
    if (!questions || this.currentQuestionIndex >= 3) return null;
    
    // Find an unused question at this level
    const availableQuestions = questions.filter(q => !this.usedReadingQuestionIds.has(q.id));
    
    if (availableQuestions.length === 0) {
      // If all questions used, reset and use any question
      const questionIndex = this.currentQuestionIndex % questions.length;
      const question = questions[questionIndex];
      return question || null;
    }
    
    // Use the first available unused question
    const question = availableQuestions[0];
    this.usedReadingQuestionIds.add(question.id);
    return question;
  }

  get currentPictureDescriptionQuestion(): PictureDescriptionQuestion | null {
    if (this.currentQuestionIndex >= this.pictureDescriptionQuestions.length) return null;
    return this.pictureDescriptionQuestions[this.currentQuestionIndex];
  }

  get currentSpeakingQuestion(): SpeakingQuestion | null {
    if (this.currentQuestionIndex >= this.speakingQuestions.length) return null;
    return this.speakingQuestions[this.currentQuestionIndex];
  }

  selectAnswer(index: number): void {
    if (this.showFeedback) return;
    this.selectedAnswer = index;
  }

  submitAnswer(): void {
    if (this.selectedAnswer === null) return;

    let selectedAnswerText = '';
    let correctAnswerText = '';
    
    if (this.currentSection === 'grammar' && this.currentGrammarQuestion) {
      selectedAnswerText = this.currentGrammarQuestion.options[this.selectedAnswer];
      correctAnswerText = this.currentGrammarQuestion.answer;
    } else if (this.currentSection === 'listening' && this.currentListeningQuestion) {
      selectedAnswerText = this.currentListeningQuestion.options[this.selectedAnswer];
      correctAnswerText = this.currentListeningQuestion.answer;
    } else if (this.currentSection === 'reading' && this.currentReadingQuestion) {
      selectedAnswerText = this.currentReadingQuestion.options[this.selectedAnswer];
      correctAnswerText = this.currentReadingQuestion.answer;
    } else if (this.currentSection === 'pictureDescription' && this.currentPictureDescriptionQuestion) {
      selectedAnswerText = this.currentPictureDescriptionQuestion.options[this.selectedAnswer];
      correctAnswerText = this.currentPictureDescriptionQuestion.answer;
    }

    this.isCorrect = selectedAnswerText === correctAnswerText;
    this.showFeedback = true;

    if (this.currentSection === 'grammar') {
      if (this.isCorrect) {
        this.consecutiveCorrect++;
        this.consecutiveWrong = 0;
        this.grammarScore += this.getLevelPoints(this.currentLevel);
        
        if (this.consecutiveCorrect >= 2) {
          this.increaseDifficulty();
          this.consecutiveCorrect = 0;
        }
      } else {
        this.consecutiveWrong++;
        this.consecutiveCorrect = 0;
        
        if (this.consecutiveWrong >= 2) {
          this.decreaseDifficulty();
          this.consecutiveWrong = 0;
        }
      }
    } else if (this.currentSection === 'listening') {
      if (this.isCorrect) this.listeningScore += 2;
    } else if (this.currentSection === 'reading') {
      if (this.isCorrect) this.readingScore += 2;
    } else if (this.currentSection === 'pictureDescription') {
      if (this.isCorrect) this.pictureDescriptionScore += 2;
    }

    // Auto-advance to next question after 800ms
    setTimeout(() => {
      this.nextQuestion();
    }, 800);
  }

  getLevelPoints(level: string): number {
    const points: { [key: string]: number } = { A1: 1, A2: 2, B1: 3, B2: 4, C1: 5 };
    return points[level] || 1;
  }

  increaseDifficulty(): void {
    const levels: ('A1' | 'A2' | 'B1' | 'B2' | 'C1')[] = ['A1', 'A2', 'B1', 'B2', 'C1'];
    const currentIndex = levels.indexOf(this.currentLevel);
    if (currentIndex < levels.length - 1) {
      this.currentLevel = levels[currentIndex + 1];
    }
  }

  decreaseDifficulty(): void {
    const levels: ('A1' | 'A2' | 'B1' | 'B2' | 'C1')[] = ['A1', 'A2', 'B1', 'B2', 'C1'];
    const currentIndex = levels.indexOf(this.currentLevel);
    if (currentIndex > 0) {
      this.currentLevel = levels[currentIndex - 1];
    }
  }

  nextQuestion(): void {
    this.showFeedback = false;
    this.selectedAnswer = null;
    
    // Stop any playing audio and clean up
    if (this.audioElement) {
      this.audioElement.pause();
      this.audioElement.currentTime = 0;
      this.audioElement = null;
    }
    this.isAudioPlaying = false;
    
    this.currentQuestionIndex++;

    // Reset audio plays for next listening question
    if (this.currentSection === 'listening') {
      this.audioPlaysLeft = 2;
    }

    if (this.currentSection === 'grammar' && this.currentQuestionIndex >= 7) {
      this.moveToNextSection();
    } else if (this.currentSection === 'listening' && this.currentQuestionIndex >= this.listeningQuestions.length) {
      this.moveToNextSection();
    } else if (this.currentSection === 'reading' && this.currentQuestionIndex >= 3) {
      this.moveToNextSection();
    } else if (this.currentSection === 'pictureDescription' && this.currentQuestionIndex >= this.pictureDescriptionQuestions.length) {
      this.moveToNextSection();
    }
  }

  moveToNextSection(): void {
    this.currentQuestionIndex = 0;
    this.audioPlaysLeft = 2;
    
    if (this.currentSection === 'grammar') {
      this.currentSection = 'listening';
    } else if (this.currentSection === 'listening') {
      this.currentSection = 'reading';
      this.usedReadingQuestionIds.clear();
    } else if (this.currentSection === 'reading') {
      this.currentSection = 'pictureDescription';
    } else if (this.currentSection === 'pictureDescription') {
      this.currentSection = 'speaking';
    } else if (this.currentSection === 'speaking') {
      this.finishTest();
    }
  }

  playAudio(): void {
    if (this.audioPlaysLeft <= 0 || !this.currentListeningQuestion) return;
    
    // Stop and clean up any existing audio
    if (this.audioElement) {
      this.audioElement.pause();
      this.audioElement.currentTime = 0;
      this.audioElement = null;
    }
    
    this.isAudioPlaying = true;
    this.audioElement = new Audio(this.currentListeningQuestion.audioSrc);
    
    this.audioElement.addEventListener('ended', () => {
      this.isAudioPlaying = false;
      this.audioPlaysLeft--;
    });
    
    this.audioElement.addEventListener('error', (err) => {
      console.error('Audio playback failed:', err);
      this.isAudioPlaying = false;
    });
    
    this.audioElement.play().catch(err => {
      console.error('Audio playback failed:', err);
      this.isAudioPlaying = false;
    });
  }

  startRecording(): void {
    if (!this.recognition) return;
    
    this.isRecording = true;
    this.transcript = '';
    this.recognition.start();
  }

  stopRecording(): void {
    if (!this.recognition) return;
    
    this.recognition.stop();
    this.isRecording = false;
    
    const keywords = this.currentSpeakingQuestion?.keywordsToDetect || [];
    const transcriptLower = this.transcript.toLowerCase();
    const foundKeywords = keywords.filter(kw => transcriptLower.includes(kw.toLowerCase()));
    
    if (foundKeywords.length > 0) {
      this.speakingScore += 5;
    }
  }

  skipSpeaking(): void {
    this.moveToNextSection();
  }

  finishTest(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }

    this.totalScore = this.grammarScore + this.listeningScore + this.readingScore + this.pictureDescriptionScore + this.speakingScore;
    
    this.skillBreakdown = {
      grammar: Math.round((this.grammarScore / 35) * 100),
      listening: Math.round((this.listeningScore / 10) * 100),
      reading: Math.round((this.readingScore / 6) * 100),
      pictureDescription: Math.round((this.pictureDescriptionScore / 10) * 100),
      speaking: Math.round((this.speakingScore / 5) * 100)
    };

    this.finalLevel = this.calculateLevel(this.totalScore);
    this.personalizedInsight = this.generateInsight();
    this.currentSection = 'results';
    this.showConfetti = true;

    this.updateEnglishLevel();
  }

  calculateLevel(score: number): string {
    // Max score is now 66 (35 grammar + 10 listening + 6 reading + 10 picture + 5 speaking)
    if (score >= 59) return 'C2'; // ~89%
    if (score >= 50) return 'C1'; // ~76%
    if (score >= 37) return 'B2'; // ~56%
    if (score >= 24) return 'B1'; // ~36%
    if (score >= 14) return 'A2'; // ~21%
    return 'A1';
  }

  generateInsight(): string {
    const scores = [
      { name: 'grammar', score: this.skillBreakdown.grammar },
      { name: 'listening', score: this.skillBreakdown.listening },
      { name: 'reading', score: this.skillBreakdown.reading },
      { name: 'picture description', score: this.skillBreakdown.pictureDescription },
      { name: 'speaking', score: this.skillBreakdown.speaking }
    ];
    
    const strongest = scores.reduce((a, b) => a.score > b.score ? a : b, scores[0]);
    const weakest = scores.reduce((a, b) => a.score < b.score ? a : b, scores[0]);
    
    return `Your ${strongest.name} is strong, but your ${weakest.name} could use more practice.`;
  }

  updateEnglishLevel(): void {
    const currentUser = this.authService.currentUserValue;
    if (!currentUser) return;

    console.log('📝 Updating English level to:', this.finalLevel);

    this.http.put(`http://localhost:8080/api/users/${currentUser.id}`, {
      englishLevel: this.finalLevel
    }).subscribe({
      next: () => {
        console.log('✅ English level updated successfully in database');
        
        // Immediately update localStorage to reflect the change
        const updatedUser = { ...currentUser, englishLevel: this.finalLevel };
        this.authService.updateCurrentUser(updatedUser);
        
        console.log('💾 Updated user in localStorage with englishLevel:', this.finalLevel);
        
        // Also load fresh data from backend to ensure consistency
        setTimeout(() => {
          this.authService.loadFreshUserData(currentUser.id);
        }, 500);
      },
      error: (error) => {
        console.error('❌ Failed to update English level:', error);
      }
    });
  }

  goToDashboard(): void {
    this.testCompleted.emit(this.finalLevel);
    this.placementTestService.hideTest();
    
    setTimeout(() => {
      this.router.navigate(['/user-panel/dashboard']);
    }, 300);
  }

  get progressPercentage(): number {
    let total = 0;
    let current = 0;

    if (this.currentSection === 'grammar') {
      total = 7;
      current = this.currentQuestionIndex;
    } else if (this.currentSection === 'listening') {
      total = 7 + 5;
      current = 7 + this.currentQuestionIndex;
    } else if (this.currentSection === 'reading') {
      total = 7 + 5 + 3;
      current = 7 + 5 + this.currentQuestionIndex;
    } else if (this.currentSection === 'pictureDescription') {
      total = 7 + 5 + 3 + 5;
      current = 7 + 5 + 3 + this.currentQuestionIndex;
    } else if (this.currentSection === 'speaking') {
      total = 7 + 5 + 3 + 5 + 1;
      current = 7 + 5 + 3 + 5;
    } else {
      return 100;
    }

    return Math.round((current / total) * 100);
  }

  get sectionTitle(): string {
    const titles: { [key in Section]: string } = {
      grammar: 'Grammar & Vocabulary',
      listening: 'Listening Comprehension',
      reading: 'Reading Comprehension',
      pictureDescription: 'Picture Description',
      speaking: 'Speaking Task',
      results: 'Your Results'
    };
    return titles[this.currentSection];
  }

  getCorrectAnswersCount(): number {
    const grammarCorrect = Math.round(this.grammarScore / 3);
    const listeningCorrect = this.listeningScore / 2;
    const readingCorrect = this.readingScore / 2;
    const pictureCorrect = this.pictureDescriptionScore / 2;
    return grammarCorrect + listeningCorrect + readingCorrect + pictureCorrect;
  }

  getScorePercentage(): number {
    // Max score: 35 (grammar) + 10 (listening) + 6 (reading) + 10 (picture) + 5 (speaking) = 66
    return Math.round((this.totalScore / 66) * 100);
  }

  getOptionLetter(index: number): string {
    return String.fromCodePoint(65 + index);
  }

  getLevelName(): string {
    const names: { [key: string]: string } = {
      A1: 'Elementary',
      A2: 'Pre-Intermediate',
      B1: 'Intermediate',
      B2: 'Upper-Intermediate',
      C1: 'Advanced',
      C2: 'Proficient'
    };
    return names[this.finalLevel] || '';
  }

  getLevelArticle(): string {
    return ['A1', 'A2'].includes(this.finalLevel) ? 'an' : 'a';
  }

  // Helper methods to safely access options
  isCorrectGrammarAnswer(index: number): boolean {
    return this.currentGrammarQuestion?.options[index] === this.currentGrammarQuestion?.answer;
  }

  isCorrectListeningAnswer(index: number): boolean {
    return this.currentListeningQuestion?.options[index] === this.currentListeningQuestion?.answer;
  }

  isCorrectReadingAnswer(index: number): boolean {
    return this.currentReadingQuestion?.options[index] === this.currentReadingQuestion?.answer;
  }

  isCorrectPictureDescriptionAnswer(index: number): boolean {
    return this.currentPictureDescriptionQuestion?.options[index] === this.currentPictureDescriptionQuestion?.answer;
  }
}
