-- Detailed Lessons Insertion Script
-- This script must be run AFTER insert-courses-complete.sql
-- Contains realistic content for all lesson types

-- Note: Using actual chapter IDs from database
-- Grammar chapters (course 49): 141, 142, 143, 144
-- Business chapters (course 50): 145, 146, 147
-- Pronunciation chapters (course 51): 149, 150, 151, 152
-- Vocabulary chapters (course 52): 153, 154, 155, 156
-- Conversation chapters (course 53): 157, 158, 159, 160

-- ========================================
-- GRAMMAR COURSE LESSONS
-- ========================================

-- Chapter 1: Introduction to English Grammar (chapter_id = 141)
INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
('Welcome to English Grammar', 
 'An introduction to the course and what you will learn about English grammar.',
 'Welcome to English Grammar Fundamentals!

In this comprehensive course, you will learn the essential building blocks of English grammar. Grammar is the foundation of language learning, and mastering it will help you communicate more effectively and confidently.

What you will learn:
• The eight parts of speech and their functions
• How to construct correct sentences
• Present and past tenses
• Articles and determiners
• Common grammar patterns

This course is designed for beginners (A1 level) and requires no prior knowledge of English grammar. Each lesson includes clear explanations, examples, and practice exercises.

Let''s begin your journey to mastering English grammar!',
 '',
 'VIDEO', 0, 15, true, true, 141, NOW(), NOW()),

('Parts of Speech Overview', 
 'Learn about the eight parts of speech in English: nouns, verbs, adjectives, adverbs, pronouns, prepositions, conjunctions, and interjections.',
 'The Eight Parts of Speech

Every word in English belongs to one of eight categories called "parts of speech." Understanding these categories is essential for building correct sentences.

1. NOUNS - Words that name people, places, things, or ideas
   Examples: teacher, London, book, happiness
   
2. PRONOUNS - Words that replace nouns
   Examples: I, you, he, she, it, they, we
   
3. VERBS - Action words or state-of-being words
   Examples: run, eat, is, have, think
   
4. ADJECTIVES - Words that describe nouns
   Examples: beautiful, tall, red, happy
   
5. ADVERBS - Words that describe verbs, adjectives, or other adverbs
   Examples: quickly, very, well, often
   
6. PREPOSITIONS - Words that show relationships between nouns
   Examples: in, on, at, under, between
   
7. CONJUNCTIONS - Words that connect other words or sentences
   Examples: and, but, or, because, although
   
8. INTERJECTIONS - Words that express emotion
   Examples: Oh! Wow! Ouch! Hey!

Practice identifying parts of speech in sentences to improve your grammar skills.',
 '',
 'VIDEO', 1, 25, false, true, 141, NOW(), NOW()),

('Nouns and Pronouns', 
 'Understand nouns (people, places, things) and pronouns (words that replace nouns).',
 'Nouns and Pronouns in Detail

NOUNS
Nouns are words that name people, places, things, or ideas. There are several types:

1. Common Nouns - General names (not capitalized)
   Examples: dog, city, car, teacher
   
2. Proper Nouns - Specific names (always capitalized)
   Examples: London, John, Microsoft, Monday
   
3. Concrete Nouns - Things you can see or touch
   Examples: table, apple, computer
   
4. Abstract Nouns - Ideas or concepts
   Examples: love, freedom, happiness, time
   
5. Countable Nouns - Can be counted (have plural forms)
   Examples: book/books, cat/cats, idea/ideas
   
6. Uncountable Nouns - Cannot be counted (no plural form)
   Examples: water, rice, information, advice

PRONOUNS
Pronouns replace nouns to avoid repetition.

Subject Pronouns: I, you, he, she, it, we, they
Object Pronouns: me, you, him, her, it, us, them
Possessive Pronouns: mine, yours, his, hers, ours, theirs
Possessive Adjectives: my, your, his, her, its, our, their

Example:
"John loves his dog. He walks it every day."
(He = John, it = dog)

Practice using nouns and pronouns correctly in your writing and speaking.',
 '',
 'DOCUMENT', 2, 20, false, true, 141, NOW(), NOW()),

('Verbs and Tenses Introduction', 
 'Introduction to verbs and the concept of tenses in English.',
 'Understanding Verbs and Tenses

WHAT ARE VERBS?
Verbs are the most important part of a sentence. They express actions or states of being.

Action Verbs: run, eat, write, think, speak
State Verbs: be, have, know, like, want

VERB TENSES
English has three main time frames, each with four aspects:

PRESENT TENSES:
• Present Simple: I work
• Present Continuous: I am working
• Present Perfect: I have worked
• Present Perfect Continuous: I have been working

PAST TENSES:
• Past Simple: I worked
• Past Continuous: I was working
• Past Perfect: I had worked
• Past Perfect Continuous: I had been working

FUTURE TENSES:
• Future Simple: I will work
• Future Continuous: I will be working
• Future Perfect: I will have worked
• Future Perfect Continuous: I will have been working

In this course, we will focus on the most common tenses:
- Present Simple and Present Continuous
- Past Simple and Past Continuous

Understanding when to use each tense is key to speaking and writing English correctly.

Example:
"I work in an office." (Present Simple - routine)
"I am working on a project." (Present Continuous - happening now)
"I worked yesterday." (Past Simple - completed action)
"I was working when you called." (Past Continuous - interrupted action)',
 '',
 'VIDEO', 3, 30, false, true, 141, NOW(), NOW());

-- Chapter 2: Present Tenses (chapter_id = 142)
INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
('Present Simple Tense', 
 'Learn how to form and use the present simple tense for habits, facts, and routines.',
 'Present Simple Tense - Complete Guide

WHEN TO USE:
1. Habits and routines
   "I drink coffee every morning."
   
2. Facts and general truths
   "The sun rises in the east."
   "Water boils at 100°C."
   
3. Permanent situations
   "She lives in Paris."
   "He works as a teacher."
   
4. Scheduled events
   "The train leaves at 6 PM."

HOW TO FORM:

Positive:
I/You/We/They + base verb
He/She/It + base verb + s/es

Examples:
"I work in an office."
"She works in a hospital."
"They play football."

Negative:
I/You/We/They + do not (don''t) + base verb
He/She/It + does not (doesn''t) + base verb

Examples:
"I don''t like coffee."
"He doesn''t speak French."

Questions:
Do + I/you/we/they + base verb?
Does + he/she/it + base verb?

Examples:
"Do you like pizza?"
"Does she work here?"

SPELLING RULES FOR THIRD PERSON (he/she/it):
• Most verbs: add -s (work → works, play → plays)
• Verbs ending in -s, -sh, -ch, -x, -o: add -es (watch → watches, go → goes)
• Verbs ending in consonant + y: change y to i and add -es (study → studies)
• Irregular: have → has

TIME EXPRESSIONS:
always, usually, often, sometimes, rarely, never
every day/week/month/year
on Mondays, in the morning, at night

Practice forming present simple sentences with different subjects!',
 '',
 'VIDEO', 0, 30, false, true, 142, NOW(), NOW()),

('Present Simple Practice', 
 'Interactive exercises to practice present simple tense.',
 'Present Simple Practice Exercises

Complete these exercises to master the present simple tense:

EXERCISE 1: Fill in the blanks with the correct form of the verb
1. She _____ (work) in a bank.
2. They _____ (not/like) spicy food.
3. _____ you _____ (speak) English?
4. He _____ (watch) TV every evening.
5. We _____ (not/have) a car.

EXERCISE 2: Correct the mistakes
1. He don''t like coffee. → _____
2. Does they live here? → _____
3. She work in an office. → _____
4. I doesn''t understand. → _____
5. Do she speak French? → _____

EXERCISE 3: Make questions
1. You like pizza. → _____?
2. She works here. → _____?
3. They have a dog. → _____?
4. He speaks Spanish. → _____?
5. You know the answer. → _____?

EXERCISE 4: Write about your daily routine
Use present simple to describe what you do every day. Include:
- What time you wake up
- What you eat for breakfast
- Where you work or study
- What you do in the evening
- What time you go to bed

Example:
"I wake up at 7 AM every day. I eat cereal for breakfast. I work in an office from 9 to 5. In the evening, I watch TV or read a book. I go to bed at 11 PM."

Practice makes perfect! Complete these exercises and check your answers.',
 '',
 'INTERACTIVE', 1, 25, false, true, 142, NOW(), NOW()),

('Present Continuous Tense', 
 'Master the present continuous tense for actions happening now.',
 'Present Continuous Tense - Complete Guide

WHEN TO USE:
1. Actions happening right now
   "I am writing an email." (right now)
   
2. Temporary situations
   "She is living in London this year." (temporary)
   
3. Future arrangements
   "We are meeting tomorrow at 3 PM." (planned future)
   
4. Changing situations
   "The weather is getting colder." (gradual change)
   
5. Annoying habits (with "always")
   "He is always complaining!" (criticism)

HOW TO FORM:

Positive:
Subject + am/is/are + verb-ing

Examples:
"I am working."
"She is studying."
"They are playing."

Negative:
Subject + am not/isn''t/aren''t + verb-ing

Examples:
"I am not working."
"He isn''t studying."
"They aren''t playing."

Questions:
Am/Is/Are + subject + verb-ing?

Examples:
"Are you working?"
"Is she studying?"
"Are they playing?"

SPELLING RULES FOR -ING:
• Most verbs: add -ing (work → working, play → playing)
• Verbs ending in -e: remove e, add -ing (make → making, write → writing)
• One syllable verbs ending in consonant-vowel-consonant: double the last consonant (run → running, sit → sitting)
• Verbs ending in -ie: change ie to y (lie → lying, die → dying)

TIME EXPRESSIONS:
now, right now, at the moment, currently
today, this week, this month, this year
Look! Listen! (to draw attention)

STATIVE VERBS (NOT used in continuous):
know, understand, believe, like, love, hate, want, need, prefer, remember, forget, seem, belong

Wrong: "I am knowing the answer."
Right: "I know the answer."

Practice using present continuous for actions happening now!',
 '',
 'VIDEO', 2, 30, false, true, 142, NOW(), NOW()),

('Present Simple vs Continuous', 
 'Learn when to use present simple versus present continuous.',
 'Present Simple vs Present Continuous - Key Differences

Understanding when to use each tense is crucial for accurate communication.

PRESENT SIMPLE:
✓ Permanent situations
✓ Habits and routines
✓ Facts and general truths
✓ Scheduled events

Examples:
"I live in New York." (permanent)
"She works every day." (routine)
"The Earth orbits the Sun." (fact)
"The train leaves at 6 PM." (schedule)

PRESENT CONTINUOUS:
✓ Actions happening now
✓ Temporary situations
✓ Future arrangements
✓ Changing situations

Examples:
"I am living in a hotel this week." (temporary)
"She is working on a project." (happening now)
"We are meeting tomorrow." (future plan)
"Prices are rising." (change)

COMPARISON EXAMPLES:

1. Permanent vs Temporary:
   "I work in London." (permanent job)
   "I am working in London this month." (temporary assignment)

2. Routine vs Now:
   "He reads books." (general habit)
   "He is reading a book." (right now)

3. General vs Specific:
   "She teaches English." (her profession)
   "She is teaching a class." (at this moment)

4. Always true vs Currently true:
   "Water boils at 100°C." (always)
   "The water is boiling." (right now)

COMMON MISTAKES:

Wrong: "I am understanding English."
Right: "I understand English."
(understand is a stative verb)

Wrong: "She is having a car."
Right: "She has a car."
(have for possession is stative)

Wrong: "What do you do now?"
Right: "What are you doing now?"
(asking about current action)

PRACTICE:
Choose the correct tense:
1. I _____ (work/am working) in an office. [permanent]
2. She _____ (works/is working) on a report now. [current]
3. They _____ (live/are living) in Paris. [permanent]
4. We _____ (have/are having) dinner at 7 PM tonight. [future plan]
5. He _____ (studies/is studying) English every day. [routine]

Master these differences to speak English more naturally!',
 '',
 'DOCUMENT', 3, 20, false, true, 142, NOW(), NOW()),

('Present Tenses Assignment', 
 'Complete exercises using both present tenses.',
 'Present Tenses Assignment

Complete this assignment to demonstrate your understanding of present simple and present continuous tenses.

PART 1: Fill in the blanks (10 points)
Choose present simple or present continuous:

1. Right now, I _____ (sit) in a café and _____ (drink) coffee.
2. My sister _____ (work) as a nurse. She _____ (love) her job.
3. Look! It _____ (rain) outside.
4. We usually _____ (go) to the gym on Mondays, but today we _____ (stay) home.
5. _____ you _____ (understand) the lesson?
6. The children _____ (play) in the garden at the moment.
7. She _____ (not/eat) meat. She _____ (be) vegetarian.
8. What _____ you _____ (do) right now?
9. The sun _____ (rise) in the east and _____ (set) in the west.
10. This week, I _____ (study) for my exams.

PART 2: Correct the mistakes (10 points)
Find and correct the errors:

1. I am knowing the answer.
2. She is having a beautiful house.
3. Do you understanding me?
4. They are live in London.
5. He works on a project now.
6. I am not believing you.
7. Are you want some coffee?
8. She is teach English.
9. We are needing help.
10. He is have lunch now.

PART 3: Translation (10 points)
Translate these sentences to English:

1. Je travaille dans un bureau. (permanent)
2. Je travaille sur un projet maintenant. (current)
3. Elle étudie l''anglais tous les jours. (routine)
4. Ils regardent la télévision en ce moment. (now)
5. Nous habitons à Paris. (permanent)

PART 4: Writing (20 points)
Write two short paragraphs (50-75 words each):

Paragraph 1: Describe your typical day using present simple
Include: your routine, habits, what you usually do

Paragraph 2: Describe what you are doing right now using present continuous
Include: current actions, temporary situations, what''s happening around you

SUBMISSION:
Complete all parts and submit your assignment for review. You will receive feedback within 48 hours.

Total Points: 50
Passing Score: 35/50 (70%)

Good luck!',
 '',
 'ASSIGNMENT', 4, 30, false, true, 142, NOW(), NOW());

-- ========================================
-- BUSINESS ENGLISH LESSONS
-- ========================================

-- Chapter 1: Professional Email Writing (chapter_id = 145)
INSERT INTO lessons (title, description, content, content_url, lesson_type, order_index, duration, is_preview, is_published, chapter_id, created_at, updated_at) VALUES
('Email Structure and Format', 
 'Learn the proper structure of professional business emails.',
 'Professional Email Structure

A well-structured email makes a professional impression and ensures clear communication.

ESSENTIAL COMPONENTS:

1. SUBJECT LINE
   • Clear and specific
   • Summarizes the email purpose
   • Examples:
     ✓ "Meeting Request: Q4 Budget Review"
     ✓ "Follow-up: Project Proposal Discussion"
     ✗ "Hi" or "Question" (too vague)

2. GREETING/SALUTATION
   Formal:
   • Dear Mr./Ms. [Last Name],
   • Dear Sir/Madam, (if name unknown)
   
   Semi-formal:
   • Hello [First Name],
   • Hi [First Name],
   
   Note: Use comma (,) in American English, colon (:) in British English

3. OPENING LINE
   • State your purpose immediately
   • Examples:
     "I am writing to inquire about..."
     "Thank you for your email regarding..."
     "I would like to request..."

4. BODY
   • Keep paragraphs short (2-3 sentences)
   • Use bullet points for lists
   • One main idea per paragraph
   • Be clear and concise

5. CLOSING LINE
   • Summarize or call to action
   • Examples:
     "I look forward to hearing from you."
     "Please let me know if you need any further information."
     "Thank you for your time and consideration."

6. SIGN-OFF
   Formal:
   • Yours sincerely, (if you know the name)
   • Yours faithfully, (if you don''t know the name)
   • Best regards,
   • Kind regards,
   
   Semi-formal:
   • Best,
   • Thanks,
   • Regards,

7. SIGNATURE
   [Your Full Name]
   [Your Position]
   [Company Name]
   [Contact Information]

EXAMPLE EMAIL:

Subject: Meeting Request: Marketing Strategy Discussion

Dear Ms. Johnson,

I hope this email finds you well. I am writing to request a meeting to discuss our Q4 marketing strategy.

I would like to propose the following:
• Date: Next Tuesday, October 15th
• Time: 2:00 PM - 3:00 PM
• Location: Conference Room B

Please let me know if this time works for you, or suggest an alternative that suits your schedule.

I look forward to hearing from you.

Best regards,

John Smith
Marketing Manager
ABC Corporation
john.smith@abc.com
+1 (555) 123-4567

FORMATTING TIPS:
• Use a professional font (Arial, Calibri, Times New Roman)
• Font size: 10-12 pt
• Avoid colors, emojis, or fancy formatting
• Proofread before sending
• Keep it concise (aim for 3-5 short paragraphs)

Practice writing emails with this structure!',
 '',
 'VIDEO', 0, 20, true, true, 145, NOW(), NOW()),

('Formal vs Informal Tone', 
 'Understand when to use formal or informal language in emails.',
 'Email Tone: Formal vs Informal

Choosing the right tone is crucial for effective business communication.

WHEN TO USE FORMAL TONE:

1. First contact with someone
2. Writing to senior management
3. Official requests or complaints
4. Legal or contractual matters
5. External clients or partners
6. Job applications

FORMAL LANGUAGE CHARACTERISTICS:
• Complete sentences
• No contractions (write "I am" not "I''m")
• Polite, respectful language
• Professional vocabulary
• Proper grammar and punctuation

FORMAL PHRASES:
Opening:
• I am writing to...
• I would like to inquire about...
• Thank you for your email dated...
• Further to our conversation...

Requesting:
• I would appreciate it if you could...
• Would it be possible to...
• I would be grateful if you could...
• Could you please...

Closing:
• I look forward to hearing from you.
• Thank you for your time and consideration.
• Please do not hesitate to contact me.
• I remain at your disposal.

WHEN TO USE INFORMAL TONE:

1. Colleagues you know well
2. Internal team communications
3. Follow-up emails
4. Casual updates
5. After establishing a relationship

INFORMAL LANGUAGE CHARACTERISTICS:
• Contractions allowed (I''m, you''re, we''ll)
• Shorter sentences
• Friendly, conversational tone
• Simple vocabulary
• Can be more direct

INFORMAL PHRASES:
Opening:
• Thanks for your email.
• Just wanted to let you know...
• Quick question about...
• Hope you''re doing well.

Requesting:
• Can you...?
• Could you...?
• Would you mind...?
• Let me know if...

Closing:
• Talk soon!
• Thanks!
• Cheers,
• Have a great day!

COMPARISON EXAMPLES:

FORMAL:
"Dear Mr. Brown,

I am writing to request your assistance with the Johnson account. Would it be possible for you to provide the quarterly reports by Friday?

I would greatly appreciate your help with this matter.

Yours sincerely,
Sarah"

INFORMAL:
"Hi Tom,

Hope you''re well! Can you send me the Johnson account reports by Friday? I need them for the meeting.

Thanks!
Sarah"

SEMI-FORMAL (Most Common):
"Hello Tom,

I hope this email finds you well. Could you please send me the Johnson account reports by Friday? I need them for the upcoming meeting.

Thank you for your help.

Best regards,
Sarah"

TONE MISTAKES TO AVOID:

Too Formal (sounds stiff):
"I hereby request that you forward the aforementioned documents at your earliest convenience."

Better:
"Could you please send the documents when you have a chance?"

Too Informal (unprofessional):
"Hey! Send me those docs ASAP!!!"

Better:
"Hi! Could you send me those documents soon? Thanks!"

TIPS FOR CHOOSING THE RIGHT TONE:

1. Consider your relationship with the recipient
2. Think about the company culture
3. Match the tone of previous emails
4. When in doubt, err on the side of formal
5. Adjust your tone as the relationship develops

Practice writing emails in different tones!',
 '',
 'VIDEO', 1, 25, false, true, 145, NOW(), NOW()),

('Common Email Phrases', 
 'Learn useful phrases for different email situations.',
 'Essential Business Email Phrases

Master these phrases for professional email communication.

OPENING PHRASES:

Acknowledging Receipt:
• Thank you for your email.
• I acknowledge receipt of your email dated...
• Thank you for getting in touch.
• I have received your email regarding...

Referring to Previous Contact:
• Further to our conversation...
• Following our meeting yesterday...
• As discussed in our phone call...
• With reference to your email...

Introducing Yourself:
• My name is [Name] and I am...
• I am writing on behalf of...
• I am the [position] at [company]...
• Allow me to introduce myself...

MAKING REQUESTS:

Polite Requests:
• I would appreciate it if you could...
• Would it be possible to...?
• I would be grateful if you could...
• Could you please...?
• Would you mind...?

Urgent Requests:
• I would appreciate your prompt attention to this matter.
• This is quite urgent, so I would appreciate...
• Due to the urgency of this matter...
• I would be grateful for a quick response.

PROVIDING INFORMATION:

• I am writing to inform you that...
• I would like to let you know that...
• Please be advised that...
• I am pleased to inform you that...
• I regret to inform you that...

ASKING FOR INFORMATION:

• I would like to inquire about...
• Could you please provide information on...?
• I am interested in learning more about...
• I would appreciate more details about...
• Could you clarify...?

MAKING SUGGESTIONS:

• I would suggest that...
• May I suggest...?
• Perhaps we could...
• How about...?
• Would it be possible to...?

APOLOGIZING:

• I apologize for...
• Please accept my apologies for...
• I am sorry for any inconvenience caused.
• I regret that...
• Unfortunately, I must apologize for...

ATTACHING DOCUMENTS:

• Please find attached...
• I have attached...
• Attached you will find...
• I am sending you...
• Please see the attached file.

CONFIRMING:

• I can confirm that...
• I would like to confirm...
• This is to confirm...
• I am writing to confirm...
• As confirmed in our meeting...

OFFERING HELP:

• Please let me know if you need any further information.
• If you have any questions, please don''t hesitate to contact me.
• I am happy to provide additional details.
• Please feel free to contact me if you need assistance.
• I remain at your disposal.

CLOSING PHRASES:

Looking Forward:
• I look forward to hearing from you.
• I look forward to your reply.
• I look forward to meeting you.
• I await your response.

Thanking:
• Thank you for your time and consideration.
• Thank you in advance for your help.
• I appreciate your assistance with this matter.
• Thank you for your attention to this matter.

Availability:
• Please let me know if you have any questions.
• Feel free to contact me if you need more information.
• I am available for a call if you would like to discuss further.
• Don''t hesitate to reach out if you need anything.

EXAMPLE EMAIL USING THESE PHRASES:

Subject: Request for Project Update

Dear Ms. Anderson,

Thank you for your email dated March 15th. I am writing to inquire about the status of the website redesign project.

Could you please provide an update on the following:
• Current progress
• Expected completion date
• Any challenges or concerns

I would appreciate it if you could send this information by the end of the week. Please find attached the latest design mockups for your review.

If you have any questions or need any further information, please don''t hesitate to contact me.

I look forward to hearing from you.

Best regards,
Michael Chen

Practice using these phrases in your emails!',
 '',
 'DOCUMENT', 2, 15, false, true, 145, NOW(), NOW()),

('Writing Practice: Request Email', 
 'Practice writing a professional request email.',
 'Email Writing Assignment: Request Email

SCENARIO:
You work for TechCorp Inc. as a Project Coordinator. You need to request a meeting with your manager, Sarah Johnson, to discuss the budget for the upcoming Q4 marketing campaign. You want to meet next week and need to present your budget proposal.

ASSIGNMENT REQUIREMENTS:

Write a professional email that includes:

1. SUBJECT LINE (5 points)
   • Clear and specific
   • Indicates the purpose

2. GREETING (5 points)
   • Appropriate level of formality
   • Correct format

3. OPENING (10 points)
   • State your purpose clearly
   • Provide context if needed

4. BODY (20 points)
   • Request the meeting
   • Suggest specific dates/times
   • Mention what you want to discuss
   • Keep it concise and organized

5. CLOSING (10 points)
   • Appropriate closing phrase
   • Call to action

6. SIGN-OFF (5 points)
   • Professional sign-off
   • Complete signature block

7. TONE AND LANGUAGE (15 points)
   • Appropriate formality
   • Professional vocabulary
   • Polite and respectful

8. GRAMMAR AND SPELLING (15 points)
   • No grammatical errors
   • Correct spelling
   • Proper punctuation

9. FORMAT (10 points)
   • Proper email structure
   • Good paragraph organization
   • Easy to read

10. OVERALL EFFECTIVENESS (5 points)
    • Clear communication
    • Achieves the purpose

TOTAL: 100 points
PASSING SCORE: 70 points

TIPS:
• Use formal or semi-formal tone
• Be specific about dates and times
• Keep it brief (150-200 words)
• Proofread carefully
• Use phrases from the lesson

EXAMPLE STRUCTURE:

Subject: [Your subject line]

Dear [Greeting],

[Opening - state purpose]

[Body - make request with details]

[Closing - call to action]

[Sign-off]
[Your signature]

SUBMISSION:
Write your email and submit it for review. You will receive detailed feedback within 48 hours.

Good luck!',
 '',
 'ASSIGNMENT', 3, 30, false, true, 145, NOW(), NOW());

-- Lessons inserted successfully for Grammar and Business English courses
-- Continue with remaining lessons in the next script file
