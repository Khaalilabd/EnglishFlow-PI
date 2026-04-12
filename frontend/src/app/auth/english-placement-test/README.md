# English Level Placement Test

An interactive, adaptive English proficiency test that appears automatically for new students after registration and activation.

## Features

### 5-Section Test Structure

1. **Grammar & Vocabulary** (10 questions, adaptive)
   - Alternates between fill-in-the-blank and sentence correction
   - Starts at A1 level
   - Difficulty adapts based on performance:
     - 2 consecutive correct → level up
     - 2 consecutive wrong → level down
   - Weighted scoring by difficulty level

2. **Listening Comprehension** (5 questions)
   - Audio playback with waveform animation
   - One replay allowed per question
   - Multiple choice answers revealed after audio

3. **Reading Comprehension** (5 questions)
   - Short passages (3-5 sentences)
   - Complexity matches grammar section level
   - Single question per passage

4. **Speaking Task** (1 task, optional)
   - Uses browser SpeechRecognition API
   - Real-time transcript display
   - Basic keyword detection for past tense
   - Graceful fallback if not supported

5. **Results Screen**
   - Animated CEFR level badge (A1-C2)
   - Skill breakdown with animated bars
   - Personalized insights
   - Direct link to student dashboard

## Scoring System

### Points by Section
- **Grammar**: 1-5 points per question (based on difficulty level)
  - A1 = 1pt, A2 = 2pt, B1 = 3pt, B2 = 4pt, C1 = 5pt
- **Listening**: 2 points per question
- **Reading**: 2 points per question
- **Speaking**: 5 bonus points if keywords detected

### CEFR Level Mapping
- 0-15 points = A1 (Elementary)
- 16-25 points = A2 (Pre-Intermediate)
- 26-40 points = B1 (Intermediate)
- 41-55 points = B2 (Upper-Intermediate)
- 56-65 points = C1 (Advanced)
- 66+ points = C2 (Proficient)

## UI/UX Features

### Animations
- Smooth section transitions (fade + translateY)
- Correct answer: green flash + checkmark
- Wrong answer: red shake animation
- Progress bar smooth animation
- Confetti burst on results screen
- Skill bars fill animation (1.2s)

### Timer
- 15-minute countdown
- Color changes:
  - Blue: > 5 minutes
  - Amber: 5-2 minutes
  - Red: < 2 minutes (pulsing)

### Responsive Design
- Mobile-first approach
- Minimum 48px tap targets
- Optimized for all screen sizes

## Technical Implementation

### Component Structure
```
english-placement-test/
├── english-placement-test.component.ts    # Main logic
├── english-placement-test.component.html  # Template
├── english-placement-test.component.scss  # Styles & animations
└── README.md                              # This file
```

### Key Technologies
- Angular 17+ standalone component
- Browser SpeechRecognition API
- HTML5 Audio API
- CSS animations (no external libraries)
- HttpClient for API calls

### State Management
- Local component state (no NgRx needed)
- PlacementTestService for visibility control
- AuthService integration for user updates

## Integration

### Automatic Display
The test appears automatically when:
1. User is a STUDENT
2. User has no `englishLevel` set
3. User navigates to `/user-panel/*` routes

### Backend Integration
- Updates `users.english_level` field via PUT `/api/users/{id}`
- No new database tables required
- Uses existing UpdateUserRequest DTO

## Extending the Question Bank

### Adding Grammar Questions
```typescript
grammarQuestions: { [key: string]: Question[] } = {
  A1: [
    {
      id: 1,
      text: 'Your question here ___.',
      type: 'fill-blank', // or 'sentence-correction'
      options: ['option1', 'option2', 'option3', 'option4'],
      correctAnswer: 0, // index of correct option
      level: 'A1'
    }
  ]
}
```

### Adding Reading Passages
```typescript
readingQuestions: { [key: string]: ReadingQuestion[] } = {
  B1: [
    {
      id: 1,
      passage: 'Your passage text here...',
      question: 'What is the main idea?',
      options: ['A', 'B', 'C', 'D'],
      correctAnswer: 2,
      level: 'B1'
    }
  ]
}
```

### Adding Audio Files
1. Place MP3 files in `/assets/audio/`
2. Update `listeningQuestions` array:
```typescript
{
  id: 1,
  audioSrc: '/assets/audio/your-file.mp3',
  question: 'What did you hear?',
  options: ['A', 'B', 'C', 'D'],
  correctAnswer: 1
}
```

## Browser Compatibility

### Required Features
- ES6+ JavaScript
- CSS Grid & Flexbox
- CSS Animations
- HTML5 Audio

### Optional Features
- SpeechRecognition API (Chrome, Edge, Safari)
  - Graceful fallback if not supported
  - Speaking section becomes skippable

## Customization

### Colors
Primary colors defined in SCSS:
- Primary: `#2563EB` (calm blue)
- Secondary: `#0D9488` (teal)
- Success: `#10B981` (green)
- Error: `#EF4444` (red)

### Timer Duration
Change in component:
```typescript
timeRemaining = 15 * 60; // 15 minutes in seconds
```

### Question Count
Modify in component:
```typescript
// Grammar: 10 questions
// Listening: 5 questions
// Reading: 5 questions
// Speaking: 1 task
```

## Testing

### Manual Testing Checklist
- [ ] Test appears after student login
- [ ] All 5 sections display correctly
- [ ] Adaptive difficulty works
- [ ] Timer counts down properly
- [ ] Audio playback works
- [ ] Speech recognition works (Chrome)
- [ ] Results calculate correctly
- [ ] Level updates in database
- [ ] Navigation to dashboard works
- [ ] Mobile responsive layout
- [ ] All animations smooth

### Test Data
Use these credentials to test:
- Register a new student account
- Activate via email link
- Login and test should appear automatically

## Troubleshooting

### Test doesn't appear
- Check user role is STUDENT
- Verify `englishLevel` is null/empty
- Check PlacementTestService state
- Verify route is `/user-panel/*`

### Audio not playing
- Check audio files exist in `/assets/audio/`
- Verify file paths are correct
- Check browser console for errors
- Test with different audio formats

### Speech recognition not working
- Only works in Chrome, Edge, Safari
- Requires HTTPS in production
- Check browser permissions
- Fallback: skip button available

### Level not updating
- Check network tab for API call
- Verify backend endpoint is accessible
- Check user ID is correct
- Verify UpdateUserRequest DTO includes englishLevel

## Future Enhancements

Potential improvements:
- [ ] Add more question variations
- [ ] Implement question randomization
- [ ] Add writing section
- [ ] Save partial progress
- [ ] Allow retaking test
- [ ] Generate PDF certificate
- [ ] Email results to user
- [ ] Admin dashboard for test analytics
- [ ] Multi-language support
- [ ] Accessibility improvements (ARIA labels)

## Support

For issues or questions:
1. Check browser console for errors
2. Verify all dependencies are installed
3. Review this README
4. Check component TypeScript for inline comments
