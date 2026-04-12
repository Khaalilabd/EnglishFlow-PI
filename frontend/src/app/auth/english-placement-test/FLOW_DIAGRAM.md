# English Placement Test - Flow Diagram

## User Journey

```
┌─────────────────────────────────────────────────────────────────┐
│                     NEW STUDENT REGISTRATION                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │  Fill Form       │
                    │  - Basic Info    │
                    │  - Personal      │
                    │  - Profile       │
                    └──────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │  Submit & Wait   │
                    │  for Email       │
                    └──────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │  Click Email     │
                    │  Activation Link │
                    └──────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │  Account Active  │
                    │  Login Page      │
                    └──────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │  Enter Login     │
                    │  Credentials     │
                    └──────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PLACEMENT TEST TRIGGER                        │
│  Check: role === 'STUDENT' && englishLevel === null             │
└─────────────────────────────────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    │                   │
                    ▼                   ▼
            ┌──────────────┐    ┌──────────────┐
            │ Show Test    │    │ Skip Test    │
            │ (Overlay)    │    │ Go Dashboard │
            └──────────────┘    └──────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SECTION 1: GRAMMAR                          │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  Question 1/10 - Level: A1                             │    │
│  │  "I ___ a student."                                    │    │
│  │  [am] [is] [are] [be]                                  │    │
│  │                                                         │    │
│  │  ✓ Correct! → Level stays A1                          │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  Question 2/10 - Level: A1                             │    │
│  │  "She ___ to school every day."                        │    │
│  │  [go] [goes] [going] [went]                            │    │
│  │                                                         │    │
│  │  ✓ Correct! → 2 in a row → Level UP to A2            │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ... continues for 10 questions with adaptive difficulty        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SECTION 2: LISTENING                          │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  Question 1/5                                          │    │
│  │  ┌──────────────────────────────────────────────┐     │    │
│  │  │  [▶ Play Audio]  🔊 ▂▃▅▆▅▃▂  Plays left: 1  │     │    │
│  │  └──────────────────────────────────────────────┘     │    │
│  │                                                         │    │
│  │  "What is the main topic?"                             │    │
│  │  ○ Travel plans                                        │    │
│  │  ○ Work schedule                                       │    │
│  │  ● Weekend activities  ✓                               │    │
│  │  ○ Family gathering                                    │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ... continues for 5 questions                                  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     SECTION 3: READING                           │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  ┌──────────────────────────────────────────────┐     │    │
│  │  │ Climate change is one of the most pressing   │     │    │
│  │  │ issues of our time. Rising temperatures are  │     │    │
│  │  │ causing glaciers to melt and sea levels to   │     │    │
│  │  │ rise. Scientists warn that immediate action  │     │    │
│  │  │ is needed to prevent catastrophic...         │     │    │
│  │  └──────────────────────────────────────────────┘     │    │
│  │                                                         │    │
│  │  "What is the main concern mentioned?"                 │    │
│  │  ○ Economic growth                                     │    │
│  │  ● Climate change  ✓                                   │    │
│  │  ○ Population increase                                 │    │
│  │  ○ Technology advancement                              │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ... continues for 5 questions                                  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SECTION 4: SPEAKING                           │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  "Describe what you did yesterday in 2-3 sentences"   │    │
│  │                                                         │    │
│  │         ┌─────────────────────────────┐               │    │
│  │         │         🎤                   │               │    │
│  │         │    [Start Recording]        │               │    │
│  │         └─────────────────────────────┘               │    │
│  │                                                         │    │
│  │  Transcript:                                           │    │
│  │  ┌──────────────────────────────────────────────┐     │    │
│  │  │ Yesterday I went to the park with my         │     │    │
│  │  │ friends. We played football and had a        │     │    │
│  │  │ picnic. It was a beautiful day.              │     │    │
│  │  └──────────────────────────────────────────────┘     │    │
│  │                                                         │    │
│  │  ✓ Past tense detected! +5 bonus points               │    │
│  │                                                         │    │
│  │  [Skip this section]  [Continue]                       │    │
│  └────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    CALCULATING RESULTS...                        │
│                                                                  │
│  Grammar Score:   28 pts (adaptive, reached B1 level)          │
│  Listening Score: 8 pts  (4/5 correct × 2 pts)                 │
│  Reading Score:   10 pts (5/5 correct × 2 pts)                 │
│  Speaking Score:  5 pts  (keywords detected)                    │
│  ─────────────────────────────────────────────────────          │
│  Total Score:     51 pts                                        │
│                                                                  │
│  51 pts → B2 Level (Upper-Intermediate)                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      SECTION 5: RESULTS                          │
│                                                                  │
│                    🎉 Congratulations! 🎉                        │
│                                                                  │
│                    ┌─────────────────┐                          │
│                    │                 │                          │
│                    │       B2        │                          │
│                    │ Upper-Inter.    │                          │
│                    │                 │                          │
│                    └─────────────────┘                          │
│                                                                  │
│  Correct answers: 17/20                                         │
│  Your score is 68%.                                             │
│                                                                  │
│  You are a B2 (Upper-Intermediate) English user.               │
│                                                                  │
│  Skill Breakdown:                                               │
│  Grammar:   ████████████████░░░░  80%                          │
│  Listening: ████████████░░░░░░░░  60%                          │
│  Reading:   ████████████████████  100%                         │
│  Speaking:  ████████████████████  100%                         │
│                                                                  │
│  💡 Your reading is strong, but your listening needs more       │
│     practice.                                                   │
│                                                                  │
│  Based on your score, you should start practicing B2            │
│  grammar topics.                                                │
│                                                                  │
│            ┌──────────────────────────────┐                    │
│            │  Enter my dashboard →        │                    │
│            └──────────────────────────────┘                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │  Update Database │
                    │  PUT /api/users  │
                    │  englishLevel:B2 │
                    └──────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │  Navigate to     │
                    │  Student Panel   │
                    │  /user-panel     │
                    └──────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      STUDENT DASHBOARD                           │
│  Welcome back, John! Your English level: B2                     │
│                                                                  │
│  Recommended courses for B2 level:                              │
│  - Advanced Grammar                                             │
│  - Business English                                             │
│  - IELTS Preparation                                            │
└─────────────────────────────────────────────────────────────────┘
```

## Technical Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    COMPONENT LIFECYCLE                           │
└─────────────────────────────────────────────────────────────────┘

AppComponent.ngOnInit()
    │
    ├─→ Subscribe to router events
    │   └─→ On NavigationEnd
    │       └─→ checkPlacementTest()
    │           │
    │           ├─→ Get currentUser from AuthService
    │           ├─→ Check if route is /user-panel/*
    │           └─→ Call PlacementTestService.shouldShowTest()
    │               │
    │               └─→ If true: triggerTest()
    │                   └─→ showPlacementTest = true
    │
    └─→ Subscribe to PlacementTestService.showTest$
        └─→ Update showPlacementTest flag

EnglishPlacementTestComponent.ngOnInit()
    │
    ├─→ startTimer()
    │   └─→ setInterval(1000ms)
    │       └─→ Decrement timeRemaining
    │           └─→ If 0: finishTest()
    │
    └─→ initSpeechRecognition()
        └─→ Check browser support
            └─→ Create SpeechRecognition instance

User Interaction Flow:
    │
    ├─→ Section 1: Grammar
    │   ├─→ selectAnswer(index)
    │   ├─→ submitAnswer()
    │   │   ├─→ Check correctness
    │   │   ├─→ Update score
    │   │   ├─→ Adjust difficulty
    │   │   └─→ setTimeout(800ms) → nextQuestion()
    │   └─→ After 10 questions → moveToNextSection()
    │
    ├─→ Section 2: Listening
    │   ├─→ playAudio()
    │   │   └─→ new Audio(src).play()
    │   ├─→ selectAnswer(index)
    │   ├─→ submitAnswer()
    │   └─→ After 5 questions → moveToNextSection()
    │
    ├─→ Section 3: Reading
    │   ├─→ selectAnswer(index)
    │   ├─→ submitAnswer()
    │   └─→ After 5 questions → moveToNextSection()
    │
    ├─→ Section 4: Speaking
    │   ├─→ startRecording()
    │   │   └─→ recognition.start()
    │   ├─→ stopRecording()
    │   │   ├─→ recognition.stop()
    │   │   └─→ Check for keywords
    │   └─→ finishTest() or skipSpeaking()
    │
    └─→ Section 5: Results
        ├─→ Calculate total score
        ├─→ Determine CEFR level
        ├─→ Generate insights
        ├─→ updateEnglishLevel()
        │   └─→ HTTP PUT /api/users/{id}
        │       └─→ Update AuthService.currentUser
        └─→ goToDashboard()
            └─→ router.navigate(['/user-panel/dashboard'])
```

## State Management

```
PlacementTestService
    │
    ├─→ showTestSubject: BehaviorSubject<boolean>
    │   └─→ Emits: true/false
    │
    ├─→ shouldShowTest(user): boolean
    │   └─→ Returns: user.role === 'STUDENT' && !user.englishLevel
    │
    ├─→ triggerTest(): void
    │   └─→ showTestSubject.next(true)
    │
    └─→ hideTest(): void
        └─→ showTestSubject.next(false)

Component State
    │
    ├─→ currentSection: 'grammar' | 'listening' | 'reading' | 'speaking' | 'results'
    ├─→ currentQuestionIndex: number
    ├─→ selectedAnswer: number | null
    ├─→ showFeedback: boolean
    ├─→ isCorrect: boolean
    │
    ├─→ currentLevel: 'A1' | 'A2' | 'B1' | 'B2' | 'C1'
    ├─→ consecutiveCorrect: number
    ├─→ consecutiveWrong: number
    │
    ├─→ grammarScore: number
    ├─→ listeningScore: number
    ├─→ readingScore: number
    ├─→ speakingScore: number
    ├─→ totalScore: number
    ├─→ finalLevel: string
    │
    └─→ timeRemaining: number (seconds)
```

## Animation Timeline

```
Section Transition (350ms)
    0ms:   opacity: 0, translateY(20px)
    350ms: opacity: 1, translateY(0)

Correct Answer Feedback (800ms)
    0ms:   Border turns green
    100ms: Checkmark appears
    800ms: Auto-advance to next question

Wrong Answer Feedback (800ms)
    0ms:   Card shakes (500ms animation)
    100ms: Correct answer revealed in green
    800ms: Auto-advance to next question

Results Screen
    0ms:   Fade in
    300ms: Level badge scales in
    500ms: Confetti animation starts
    800ms: Skill bars start filling (1200ms duration)
```

## Data Flow

```
Question Bank (Hardcoded)
    │
    ├─→ grammarQuestions: { A1: [], A2: [], B1: [], B2: [], C1: [] }
    ├─→ listeningQuestions: []
    └─→ readingQuestions: { A1: [], A2: [], B1: [], B2: [], C1: [] }
    
    ↓
    
Current Question Selection
    │
    ├─→ Grammar: grammarQuestions[currentLevel][index]
    ├─→ Listening: listeningQuestions[currentQuestionIndex]
    └─→ Reading: readingQuestions[currentLevel][0]
    
    ↓
    
User Answer
    │
    └─→ submitAnswer()
        │
        ├─→ Compare with correctAnswer
        ├─→ Update score
        ├─→ Adjust difficulty (grammar only)
        └─→ Show feedback
    
    ↓
    
Score Calculation
    │
    ├─→ Grammar: Sum of weighted points
    ├─→ Listening: Correct × 2
    ├─→ Reading: Correct × 2
    └─→ Speaking: 5 if keywords detected
    
    ↓
    
CEFR Level Determination
    │
    └─→ calculateLevel(totalScore)
        └─→ Returns: 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2'
    
    ↓
    
Database Update
    │
    └─→ PUT /api/users/{id}
        └─→ Body: { englishLevel: finalLevel }
    
    ↓
    
Navigation
    │
    └─→ router.navigate(['/user-panel/dashboard'])
```
