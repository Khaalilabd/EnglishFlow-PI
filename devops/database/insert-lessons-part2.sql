-- Detailed Lessons Part 2: Pronunciation, Vocabulary, and Conversation Courses
-- This script continues from insert-lessons-detailed.sql
-- Database: englishflow_courses

-- Using actual chapter IDs from database
-- Pronunciation chapters (course 51): 149, 150, 151, 152
-- Vocabulary chapters (course 52): 153, 154, 155, 156
-- Conversation chapters (course 53): 157, 158, 159, 160

-- ========================================
-- PRONUNCIATION COURSE LESSONS
-- ========================================

-- Chapter 1: English Phonetics Basics (chapter_id = 149)
INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
('Introduction to Phonetics', 
 'Learn what phonetics is and why it matters for pronunciation.',
 'What is Phonetics?

Phonetics is the scientific study of speech sounds. Understanding phonetics will dramatically improve your English pronunciation and help you speak more clearly and confidently.

WHY PHONETICS MATTERS:

1. ACCURATE PRONUNCIATION
   • Learn to produce sounds correctly
   • Reduce your accent
   • Be understood by native speakers

2. LISTENING COMPREHENSION
   • Recognize sounds in fast speech
   • Understand different accents
   • Improve your listening skills

3. SPELLING VS PRONUNCIATION
   English spelling doesn''t always match pronunciation:
   • "though" /θoʊ/
   • "through" /θruː/
   • "tough" /tʌf/
   • "cough" /kɔːf/
   
   All spelled similarly but pronounced differently!

THE INTERNATIONAL PHONETIC ALPHABET (IPA):

The IPA is a system of symbols where each symbol represents ONE sound. This is crucial because:
• English has 26 letters but 44 sounds
• One letter can make different sounds (e.g., "a" in "cat" vs "cake")
• Multiple letters can make one sound (e.g., "sh" in "ship")

ENGLISH SOUND SYSTEM:

Approximately 44 sounds in English:
• 20 vowel sounds (including diphthongs)
• 24 consonant sounds

VOWELS vs CONSONANTS:

Vowels:
• Air flows freely through the mouth
• Voice is always used
• Examples: /iː/ (see), /æ/ (cat), /ʌ/ (cup)

Consonants:
• Air is blocked or restricted
• May be voiced or voiceless
• Examples: /p/ (pen), /b/ (bed), /s/ (see)

VOICED vs VOICELESS SOUNDS:

Voiced: Vocal cords vibrate
• /b/, /d/, /g/, /v/, /z/
• Put your hand on your throat - you feel vibration

Voiceless: No vocal cord vibration
• /p/, /t/, /k/, /f/, /s/
• No vibration when you touch your throat

PRACTICE TIP:
Start by learning the IPA symbols. Use a dictionary with phonetic transcriptions to check pronunciation of new words.

In the next lessons, we will study each sound in detail and practice producing them correctly.',
 '',
 'VIDEO', 0, 20, true, true, 149, NOW(), NOW()),

('The International Phonetic Alphabet', 
 'Learn to read and use the IPA for English pronunciation.',
 'The International Phonetic Alphabet (IPA) - Your Pronunciation Guide

The IPA is your key to perfect English pronunciation. Once you learn it, you can pronounce any English word correctly!

WHY USE THE IPA?

1. ONE SYMBOL = ONE SOUND
   Unlike English spelling, each IPA symbol always represents the same sound.

2. UNIVERSAL SYSTEM
   Used in dictionaries worldwide
   Understood by language learners everywhere

3. PRECISE PRONUNCIATION
   Shows exactly how to pronounce words
   No guessing needed

ENGLISH VOWEL SOUNDS (20 total):

SHORT VOWELS:
/ɪ/ - bit, sit, give
/e/ - bed, said, head
/æ/ - cat, bad, man
/ʌ/ - cup, love, money
/ʊ/ - book, good, put
/ɒ/ - hot, dog, want (British)
/ə/ - about, sofa, banana (schwa - most common sound!)

LONG VOWELS:
/iː/ - see, eat, key
/ɑː/ - car, father, start
/ɔː/ - door, saw, bought
/uː/ - food, blue, through
/ɜː/ - bird, work, learn

DIPHTHONGS (two vowel sounds gliding together):
/eɪ/ - day, make, great
/aɪ/ - my, like, high
/ɔɪ/ - boy, coin, voice
/aʊ/ - now, house, loud
/əʊ/ - go, home, know (British)
/oʊ/ - go, home, know (American)
/ɪə/ - here, ear, beer
/eə/ - hair, care, there
/ʊə/ - tour, pure, sure

ENGLISH CONSONANT SOUNDS (24 total):

STOPS (air completely blocked):
/p/ - pen, happy, stop
/b/ - bed, rabbit, cab
/t/ - tea, better, cat
/d/ - dog, ladder, bad
/k/ - cat, school, back
/g/ - go, bigger, bag

FRICATIVES (air forced through narrow gap):
/f/ - fish, coffee, laugh
/v/ - very, over, love
/θ/ - think, author, bath (voiceless th)
/ð/ - this, mother, bathe (voiced th)
/s/ - see, lesson, bus
/z/ - zoo, easy, has
/ʃ/ - ship, nation, wash
/ʒ/ - measure, vision, beige
/h/ - hot, behind, who

AFFRICATES (stop + fricative):
/tʃ/ - church, teacher, watch
/dʒ/ - judge, magic, age

NASALS (air through nose):
/m/ - man, summer, come
/n/ - no, dinner, sun
/ŋ/ - sing, thinking, long

LIQUIDS:
/l/ - leg, yellow, call
/r/ - red, sorry, car

GLIDES:
/w/ - we, away, quick
/j/ - yes, onion, use

READING IPA TRANSCRIPTIONS:

Word: "cat"
IPA: /kæt/
Breakdown: /k/ + /æ/ + /t/

Word: "through"
IPA: /θruː/
Breakdown: /θ/ + /r/ + /uː/

Word: "beautiful"
IPA: /ˈbjuːtɪfl/
Breakdown: /b/ + /j/ + /uː/ + /t/ + /ɪ/ + /f/ + /l/
(The ˈ mark shows primary stress)

STRESS MARKS:
ˈ = primary stress (before the stressed syllable)
ˌ = secondary stress

Example: "understand"
/ˌʌndəˈstænd/
Secondary stress on "un", primary stress on "stand"

PRACTICE EXERCISES:

1. Read these IPA transcriptions:
   /kæt/ = cat
   /dɒg/ = dog
   /haʊs/ = house
   /ˈtiːtʃə/ = teacher

2. Write these words in IPA:
   - book
   - phone
   - water
   - computer

TIPS FOR LEARNING IPA:

1. Start with sounds you know
2. Practice a few symbols each day
3. Use IPA in your dictionary
4. Write new words in IPA
5. Compare similar sounds

In the next lessons, we will practice each sound group in detail!',
 '',
 'VIDEO', 1, 30, false, true, 149, NOW(), NOW()),

('English Sound System Overview', 
 'Overview of all English sounds: vowels, consonants, and diphthongs.',
 'Complete English Sound System

This comprehensive guide covers all 44 sounds in English with examples and pronunciation tips.

PART 1: VOWEL SOUNDS

Vowels are the core of every syllable. English has more vowel sounds than most languages!

SHORT VOWELS (7 sounds):

1. /ɪ/ - Short I
   Words: bit, sit, give, women
   Tip: Shorter and more relaxed than /iː/
   
2. /e/ - Short E
   Words: bed, said, head, many
   Tip: Mouth slightly open, tongue mid-high
   
3. /æ/ - Short A (cat vowel)
   Words: cat, bad, man, have
   Tip: Mouth wide open, tongue low
   
4. /ʌ/ - Short U (cup vowel)
   Words: cup, love, money, blood
   Tip: Mouth slightly open, tongue mid-low
   
5. /ʊ/ - Short OO (book vowel)
   Words: book, good, put, could
   Tip: Lips slightly rounded
   
6. /ɒ/ - Short O (British)
   Words: hot, dog, want, what
   Tip: Mouth wide open, lips rounded
   
7. /ə/ - Schwa (the most common sound!)
   Words: about, sofa, banana, the
   Tip: Relaxed, neutral sound in unstressed syllables

LONG VOWELS (5 sounds):

1. /iː/ - Long E
   Words: see, eat, key, people
   Tip: Smile, stretch lips, tense tongue
   
2. /ɑː/ - Long A
   Words: car, father, start, heart
   Tip: Mouth wide open, tongue low and back
   
3. /ɔː/ - Long O
   Words: door, saw, bought, thought
   Tip: Lips rounded, tongue back
   
4. /uː/ - Long OO
   Words: food, blue, through, shoe
   Tip: Lips very rounded, tongue high and back
   
5. /ɜː/ - ER sound
   Words: bird, work, learn, her
   Tip: Lips slightly rounded, tongue mid-central

DIPHTHONGS (8 sounds - vowels that glide):

1. /eɪ/ - Long A (day)
   Words: day, make, great, they
   Glides from /e/ to /ɪ/
   
2. /aɪ/ - Long I (my)
   Words: my, like, high, buy
   Glides from /a/ to /ɪ/
   
3. /ɔɪ/ - OY sound
   Words: boy, coin, voice, toy
   Glides from /ɔ/ to /ɪ/
   
4. /aʊ/ - OW sound (now)
   Words: now, house, loud, how
   Glides from /a/ to /ʊ/
   
5. /əʊ/ or /oʊ/ - Long O (go)
   Words: go, home, know, though
   Glides from /ə/ or /o/ to /ʊ/
   
6. /ɪə/ - EAR sound
   Words: here, ear, beer, idea
   Glides from /ɪ/ to /ə/
   
7. /eə/ - AIR sound
   Words: hair, care, there, bear
   Glides from /e/ to /ə/
   
8. /ʊə/ - OOR sound
   Words: tour, pure, sure, poor
   Glides from /ʊ/ to /ə/

PART 2: CONSONANT SOUNDS

STOPS (6 sounds):
Air is completely blocked then released

Voiceless: /p/ /t/ /k/
Voiced: /b/ /d/ /g/

Pairs:
/p/ - /b/: pen - ben, cap - cab
/t/ - /d/: ten - den, bat - bad
/k/ - /g/: came - game, back - bag

FRICATIVES (9 sounds):
Air forced through narrow gap

Voiceless: /f/ /θ/ /s/ /ʃ/ /h/
Voiced: /v/ /ð/ /z/ /ʒ/

Pairs:
/f/ - /v/: fan - van, leaf - leave
/θ/ - /ð/: think - this, bath - bathe
/s/ - /z/: sue - zoo, bus - buzz
/ʃ/ - /ʒ/: sh - measure

AFFRICATES (2 sounds):
Combination of stop + fricative

/tʃ/: church, teacher, watch
/dʒ/: judge, magic, age

NASALS (3 sounds):
Air flows through nose

/m/: man, summer, come
/n/: no, dinner, sun
/ŋ/: sing, thinking, long

LIQUIDS (2 sounds):
/l/: leg, yellow, call
/r/: red, sorry, car

GLIDES (2 sounds):
/w/: we, away, quick
/j/: yes, onion, use

COMMON PRONUNCIATION CHALLENGES:

1. /θ/ and /ð/ (th sounds)
   Not found in many languages
   Practice: think, this, three, the

2. /v/ and /w/
   Different sounds!
   /v/: teeth touch bottom lip (very, have)
   /w/: lips rounded (we, away)

3. /l/ and /r/
   Very different in English
   /l/: tongue touches roof of mouth
   /r/: tongue doesn''t touch anything

4. /ŋ/ (ng sound)
   One sound, not two!
   sing /sɪŋ/ NOT /sɪng/

PRACTICE TIPS:

1. Record yourself
2. Compare with native speakers
3. Practice minimal pairs (words that differ by one sound)
4. Focus on difficult sounds
5. Practice every day

Remember: Perfect pronunciation takes time and practice. Be patient with yourself!',
 '',
 'DOCUMENT', 2, 25, false, true, 149, NOW(), NOW()),

('Pronunciation Practice Session', 
 'Live online session to practice sounds with a tutor.',
 'Live Pronunciation Practice Session

This is a live online session where you will practice English sounds with your tutor and receive personalized feedback.

SESSION STRUCTURE (45 minutes):

PART 1: WARM-UP (5 minutes)
• Introduction and goals
• Quick review of IPA symbols
• Vocal warm-up exercises

PART 2: VOWEL PRACTICE (15 minutes)
• Practice short vs long vowels
• Minimal pairs exercises
• Diphthong practice
• Individual pronunciation check

PART 3: CONSONANT PRACTICE (15 minutes)
• Difficult consonants (/θ/, /ð/, /r/, /l/)
• Voiced vs voiceless pairs
• Consonant clusters
• Individual pronunciation check

PART 4: CONNECTED SPEECH (10 minutes)
• Sentences and phrases
• Natural rhythm and flow
• Common reductions
• Real-life practice

WHAT TO PREPARE:

1. Review IPA symbols
2. Identify your difficult sounds
3. Prepare questions
4. Have a mirror ready (to watch your mouth)
5. Quiet environment with good internet

WHAT YOU WILL PRACTICE:

Minimal Pairs:
• ship - sheep
• bit - beat
• cat - cut
• pen - pan
• think - sink

Difficult Sounds:
• /θ/ - think, three, bath
• /ð/ - this, that, mother
• /r/ - red, very, car
• /l/ - light, yellow, call
• /v/ - very, have, love

Sentences:
• "She sells seashells by the seashore."
• "How much wood would a woodchuck chuck?"
• "The thirty-three thieves thought they thrilled the throne."

FEEDBACK YOU WILL RECEIVE:

• Specific sounds to improve
• Mouth position corrections
• Practice exercises for your needs
• Resources for continued practice

AFTER THE SESSION:

• Recording of the session (if permitted)
• Personalized practice plan
• Follow-up exercises
• Progress tracking

TECHNICAL REQUIREMENTS:

• Stable internet connection
• Microphone and camera
• Quiet environment
• Zoom/Google Meet link (provided before session)

BOOKING:
Sessions are scheduled weekly. Check your course calendar for available times.

This interactive session is crucial for improving your pronunciation. Active participation is key!',
 '',
 'ONLINE', 3, 45, false, true, 149, NOW(), NOW());

-- ========================================
-- VOCABULARY COURSE LESSONS
-- ========================================

-- Chapter 2: Idioms and Expressions (chapter_id = 154)
INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
('What Are Idioms?', 
 'Introduction to idiomatic expressions in English.',
 'Understanding English Idioms

Idioms are one of the most fascinating and challenging aspects of English. Mastering them will make you sound more natural and help you understand native speakers better.

WHAT IS AN IDIOM?

An idiom is a phrase or expression whose meaning cannot be understood from the individual words. The meaning is figurative, not literal.

Example:
"It''s raining cats and dogs"
Literal meaning: Cats and dogs are falling from the sky (impossible!)
Idiomatic meaning: It''s raining very heavily

WHY LEARN IDIOMS?

1. SOUND MORE NATURAL
   Native speakers use idioms constantly in everyday conversation.

2. UNDERSTAND NATIVE SPEAKERS
   Movies, TV shows, books, and conversations are full of idioms.

3. EXPRESS IDEAS CREATIVELY
   Idioms add color and personality to your English.

4. CULTURAL UNDERSTANDING
   Many idioms reflect cultural values and history.

TYPES OF IDIOMS:

1. BODY IDIOMS
   • "Keep your chin up" = Stay positive
   • "Cost an arm and a leg" = Very expensive
   • "Give someone a hand" = Help someone

2. ANIMAL IDIOMS
   • "Let the cat out of the bag" = Reveal a secret
   • "Kill two birds with one stone" = Accomplish two things at once
   • "When pigs fly" = Never/impossible

3. FOOD IDIOMS
   • "Piece of cake" = Very easy
   • "Spill the beans" = Reveal a secret
   • "Butter someone up" = Flatter someone

4. COLOR IDIOMS
   • "Out of the blue" = Unexpectedly
   • "Green with envy" = Very jealous
   • "See red" = Become very angry

5. WEATHER IDIOMS
   • "Under the weather" = Feeling sick
   • "Break the ice" = Make people comfortable
   • "Storm in a teacup" = Big fuss about nothing

COMMON IDIOMS FOR BEGINNERS:

EASY TASKS:
• "Piece of cake" - very easy
• "A walk in the park" - very easy
• "Child''s play" - very easy

DIFFICULT TASKS:
• "Not rocket science" - not very difficult
• "Easier said than done" - difficult to do
• "Uphill battle" - very difficult

UNDERSTANDING:
• "Get the picture" - understand
• "Crystal clear" - very clear
• "Greek to me" - don''t understand at all

SECRETS:
• "Spill the beans" - reveal a secret
• "Let the cat out of the bag" - reveal a secret
• "Keep it under wraps" - keep it secret

MONEY:
• "Cost an arm and a leg" - very expensive
• "Break the bank" - very expensive
• "Dirt cheap" - very inexpensive

HOW TO LEARN IDIOMS:

1. LEARN IN CONTEXT
   Don''t just memorize - understand when to use them.
   
   Example:
   "This exam was a piece of cake!"
   (Use after completing something easy)

2. LEARN IN GROUPS
   Group idioms by theme (body, animals, food, etc.)

3. USE THEM
   Practice using idioms in your speaking and writing.

4. WATCH AND LISTEN
   Pay attention to idioms in movies, TV shows, and conversations.

5. KEEP A NOTEBOOK
   Write down new idioms with examples.

IDIOM MISTAKES TO AVOID:

1. WRONG CONTEXT
   Wrong: "I''m feeling under the weather today!" (when happy)
   Right: "I''m feeling under the weather today." (when sick)

2. MIXING IDIOMS
   Wrong: "Let''s kill two cats with one stone."
   Right: "Let''s kill two birds with one stone."

3. LITERAL TRANSLATION
   Don''t translate idioms from your language word-for-word.

4. OVERUSING
   Don''t use too many idioms in formal writing or professional contexts.

PRACTICE:

Match the idiom to its meaning:
1. "Break a leg" → Good luck
2. "Hit the books" → Study hard
3. "Call it a day" → Stop working
4. "On cloud nine" → Very happy
5. "Bite the bullet" → Do something difficult

In the next lessons, we''ll explore 50+ common idioms in detail with examples and practice exercises!',
 '',
 'VIDEO', 0, 20, false, true, 154, NOW(), NOW()),

('Common Everyday Idioms', 
 'Learn 50 frequently used idioms in everyday English.',
 '50 Essential English Idioms

Master these commonly used idioms to sound more natural in English conversations.

CATEGORY 1: EMOTIONS & FEELINGS

1. "On cloud nine" = Extremely happy
   Example: "She was on cloud nine after getting the job."

2. "Down in the dumps" = Sad, depressed
   Example: "He''s been down in the dumps since his team lost."

3. "Over the moon" = Very happy
   Example: "They were over the moon about the baby news."

4. "Butterflies in my stomach" = Nervous
   Example: "I have butterflies in my stomach before the presentation."

5. "See red" = Become very angry
   Example: "He saw red when someone scratched his car."

CATEGORY 2: DIFFICULTY & EASE

6. "Piece of cake" = Very easy
   Example: "The test was a piece of cake!"

7. "Walk in the park" = Very easy
   Example: "This project is a walk in the park compared to the last one."

8. "Uphill battle" = Very difficult
   Example: "Losing weight is an uphill battle for me."

9. "Back to square one" = Start over
   Example: "The plan failed, so we''re back to square one."

10. "Learn the ropes" = Learn how to do something
    Example: "It takes time to learn the ropes at a new job."

CATEGORY 3: TIME

11. "In the nick of time" = Just in time
    Example: "We arrived at the airport in the nick of time."

12. "Better late than never" = It''s better to do something late than not at all
    Example: "You finally finished the report - better late than never!"

13. "Time flies" = Time passes quickly
    Example: "Time flies when you''re having fun!"

14. "Around the clock" = 24 hours a day
    Example: "The hospital is open around the clock."

15. "Call it a day" = Stop working for the day
    Example: "It''s 6 PM. Let''s call it a day."

CATEGORY 4: COMMUNICATION

16. "Break the ice" = Make people feel comfortable
    Example: "He told a joke to break the ice at the meeting."

17. "Spill the beans" = Reveal a secret
    Example: "Don''t spill the beans about the surprise party!"

18. "Let the cat out of the bag" = Reveal a secret accidentally
    Example: "He let the cat out of the bag about their engagement."

19. "Beat around the bush" = Avoid saying something directly
    Example: "Stop beating around the bush and tell me the truth!"

20. "Get straight to the point" = Say something directly
    Example: "I don''t have much time, so get straight to the point."

CATEGORY 5: MONEY

21. "Cost an arm and a leg" = Very expensive
    Example: "That designer bag costs an arm and a leg!"

22. "Break the bank" = Very expensive
    Example: "This vacation won''t break the bank."

23. "Dirt cheap" = Very inexpensive
    Example: "I bought this shirt for $5 - it was dirt cheap!"

24. "Pay through the nose" = Pay too much
    Example: "We paid through the nose for those concert tickets."

25. "Make ends meet" = Have enough money to live
    Example: "It''s hard to make ends meet with these prices."

CATEGORY 6: SUCCESS & FAILURE

26. "Hit the nail on the head" = Be exactly right
    Example: "You hit the nail on the head with that analysis!"

27. "Miss the boat" = Miss an opportunity
    Example: "I missed the boat on buying that house."

28. "Back to the drawing board" = Start planning again
    Example: "The idea didn''t work, so it''s back to the drawing board."

29. "The ball is in your court" = It''s your turn to act
    Example: "I''ve made my offer. The ball is in your court now."

30. "Throw in the towel" = Give up
    Example: "After trying for hours, he threw in the towel."

CATEGORY 7: UNDERSTANDING

31. "Get the picture" = Understand
    Example: "Do you get the picture now?"

32. "Crystal clear" = Very clear
    Example: "Your instructions were crystal clear."

33. "It''s Greek to me" = I don''t understand at all
    Example: "This math problem is Greek to me!"

34. "Ring a bell" = Sound familiar
    Example: "Does the name John Smith ring a bell?"

35. "Put two and two together" = Understand by connecting facts
    Example: "I put two and two together and realized the truth."

CATEGORY 8: ADVICE & WISDOM

36. "Don''t cry over spilled milk" = Don''t worry about past mistakes
    Example: "You failed the test, but don''t cry over spilled milk."

37. "Actions speak louder than words" = What you do is more important than what you say
    Example: "He says he''ll help, but actions speak louder than words."

38. "The early bird catches the worm" = Success comes to those who start early
    Example: "I always arrive early - the early bird catches the worm!"

39. "Don''t put all your eggs in one basket" = Don''t risk everything on one thing
    Example: "Apply to multiple jobs - don''t put all your eggs in one basket."

40. "When it rains, it pours" = Problems come all at once
    Example: "First my car broke down, then I got sick - when it rains, it pours!"

CATEGORY 9: WORK & EFFORT

41. "Burn the midnight oil" = Work late into the night
    Example: "I''m burning the midnight oil to finish this project."

42. "Go the extra mile" = Make extra effort
    Example: "She always goes the extra mile for her customers."

43. "Pull your weight" = Do your fair share of work
    Example: "Everyone needs to pull their weight on this team."

44. "Cut corners" = Do something poorly to save time/money
    Example: "Don''t cut corners on this project - do it right!"

45. "Get the ball rolling" = Start something
    Example: "Let''s get the ball rolling on this new initiative."

CATEGORY 10: MISCELLANEOUS

46. "Under the weather" = Feeling sick
    Example: "I''m feeling a bit under the weather today."

47. "Once in a blue moon" = Very rarely
    Example: "I only eat fast food once in a blue moon."

48. "The best of both worlds" = All the advantages
    Example: "Working from home gives me the best of both worlds."

49. "Bite off more than you can chew" = Take on too much
    Example: "I bit off more than I could chew with three projects."

50. "It takes two to tango" = Both people are responsible
    Example: "The argument wasn''t just his fault - it takes two to tango."

PRACTICE EXERCISES:

1. Use 5 idioms in sentences about your life
2. Find idioms in a movie or TV show
3. Create dialogues using idioms
4. Teach an idiom to a friend

Remember: Use idioms naturally, don''t force them into every sentence!',
 '',
 'VIDEO', 1, 35, false, true, 154, NOW(), NOW());

-- Continue with more lessons...
-- Due to length constraints, I''ll create the remaining lessons in the next message

-- Lessons inserted successfully for Pronunciation and Vocabulary courses (partial)
-- Continue with remaining lessons in next script
