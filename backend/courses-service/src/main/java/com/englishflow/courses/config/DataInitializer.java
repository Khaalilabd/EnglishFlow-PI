package com.englishflow.courses.config;

import com.englishflow.courses.entity.Chapter;
import com.englishflow.courses.entity.Course;
import com.englishflow.courses.entity.Lesson;
import com.englishflow.courses.enums.CourseStatus;
import com.englishflow.courses.enums.EnglishLevel;
import com.englishflow.courses.enums.LessonType;
import com.englishflow.courses.repository.ChapterRepository;
import com.englishflow.courses.repository.CourseRepository;
import com.englishflow.courses.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;

    @Override
    public void run(String... args) {
        if (courseRepository.count() == 0) {
            log.info("Initializing sample data...");
            initializeSampleData();
            log.info("Sample data initialized successfully!");
        } else {
            log.info("Data already exists, skipping initialization.");
        }
    }

    private void initializeSampleData() {
        // First, let's create some sample users (tutors and students)
        // Note: In a real microservices setup, these would be in the auth-service
        // For now, we'll just use IDs that should exist in the auth-service
        
        // Tutor IDs: 1, 2, 3
        // Student IDs: 4, 5, 6, 7, 8
        
        // Course 1: English for Beginners (Tutor ID: 1)
        Course beginnerCourse = createCourse(
                "English for Beginners",
                "Start your English learning journey with basic vocabulary, grammar, and conversation skills.",
                EnglishLevel.BEGINNER,
                50,
                LocalDateTime.now().plusDays(7),
                3600,
                1L, // Tutor ID 1 - Sarah Johnson
                "https://images.unsplash.com/photo-1546410531-bb4caa6b424d",
                CourseStatus.PUBLISHED
        );

        Chapter chapter1 = createChapter(
                "Introduction to English",
                "Learn the basics of English alphabet, pronunciation, and greetings",
                Arrays.asList("Master the English alphabet", "Learn basic pronunciation", "Practice common greetings"),
                1,
                120,
                true,
                beginnerCourse
        );

        createLesson("The English Alphabet", "Learn all 26 letters and their sounds", 
                "The English alphabet consists of 26 letters: A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z. Each letter has its own sound and can be combined to form words.",
                "https://example.com/videos/alphabet.mp4", LessonType.VIDEO, 1, 15, true, true, chapter1);
        
        createLesson("Basic Pronunciation", "Master the sounds of English", 
                "English pronunciation can be tricky. Let's start with vowel sounds: A (as in 'cat'), E (as in 'bed'), I (as in 'sit'), O (as in 'hot'), U (as in 'cup'). Practice these sounds daily.",
                "https://example.com/videos/pronunciation.mp4", LessonType.VIDEO, 2, 20, true, true, chapter1);
        
        createLesson("Common Greetings", "Learn how to greet people in English", 
                "Essential greetings: Hello, Hi, Good morning, Good afternoon, Good evening, How are you?, Nice to meet you, How do you do?, What's up? (informal)",
                "https://example.com/videos/greetings.mp4", LessonType.VIDEO, 3, 10, false, true, chapter1);

        Chapter chapter2 = createChapter(
                "Basic Grammar",
                "Understanding simple present tense and basic sentence structure",
                Arrays.asList("Learn present simple tense", "Understand subject-verb agreement", "Form basic sentences"),
                2,
                180,
                true,
                beginnerCourse
        );

        createLesson("Present Simple Tense", "Learn the most basic English tense", 
                "Present Simple is used for habits, facts, and general truths. Form: Subject + Verb (+ s/es for he/she/it). Examples: I work, You work, He works, She works, It works, We work, They work.",
                "https://example.com/videos/present-simple.mp4", LessonType.VIDEO, 1, 25, false, true, chapter2);
        
        createLesson("Sentence Structure", "Build correct English sentences", 
                "Basic English sentence structure: Subject + Verb + Object. Examples: I eat apples, She reads books, They play football. Remember: every sentence needs a subject and a verb.",
                null, LessonType.TEXT, 2, 15, false, true, chapter2);

        // Course 2: Intermediate English (Tutor ID: 2)
        Course intermediateCourse = createCourse(
                "Intermediate English Mastery",
                "Enhance your English skills with advanced grammar, vocabulary, and conversation practice.",
                EnglishLevel.INTERMEDIATE,
                40,
                LocalDateTime.now().plusDays(14),
                4800,
                2L, // Tutor ID 2 - Michael Chen
                "https://images.unsplash.com/photo-1503676260728-1c00da094a0b",
                CourseStatus.PUBLISHED
        );

        Chapter chapter3 = createChapter(
                "Advanced Grammar Concepts",
                "Master complex tenses and conditional sentences",
                Arrays.asList("Learn past perfect tense", "Understand conditionals", "Use modal verbs correctly"),
                1,
                240,
                true,
                intermediateCourse
        );

        createLesson("Past Perfect Tense", "Understanding actions completed before another past action", 
                "Past Perfect: had + past participle. Used for actions completed before another past action. Example: I had finished my homework before I watched TV. Signal words: before, after, already, just, never, ever.",
                "https://example.com/videos/past-perfect.mp4", LessonType.VIDEO, 1, 30, true, true, chapter3);
        
        createLesson("Conditional Sentences", "Master if-clauses and their uses", 
                "Types of conditionals: Zero (If + present, present), First (If + present, will), Second (If + past, would), Third (If + past perfect, would have). Each expresses different levels of possibility.",
                "https://example.com/videos/conditionals.mp4", LessonType.VIDEO, 2, 35, false, true, chapter3);
        
        createLesson("Modal Verbs", "Can, could, should, would, must, might", 
                "Modal verbs express: Ability (can/could), Possibility (may/might), Permission (can/may), Obligation (must/have to), Advice (should/ought to), Requests (could/would).",
                null, LessonType.TEXT, 3, 20, false, true, chapter3);

        Chapter chapter4 = createChapter(
                "Business English",
                "Professional communication and workplace vocabulary",
                Arrays.asList("Learn business vocabulary", "Write professional emails", "Conduct meetings in English"),
                2,
                200,
                true,
                intermediateCourse
        );

        createLesson("Business Vocabulary", "Essential words for the workplace", 
                "Key business terms: meeting, deadline, presentation, report, colleague, client, revenue, profit, budget, strategy, target, achievement, performance, feedback, collaboration.",
                null, LessonType.TEXT, 1, 25, true, true, chapter4);
        
        createLesson("Writing Professional Emails", "Email etiquette and structure", 
                "Email structure: Subject line (clear and specific), Greeting (Dear/Hello), Body (purpose, details, action), Closing (Best regards/Sincerely). Be clear, concise, and professional.",
                "https://example.com/files/email-templates.pdf", LessonType.FILE, 2, 30, false, true, chapter4);

        // Course 3: Advanced English (Tutor ID: 1)
        Course advancedCourse = createCourse(
                "Advanced English Communication",
                "Perfect your English with advanced topics, idioms, and native-level communication skills.",
                EnglishLevel.ADVANCED,
                30,
                LocalDateTime.now().plusDays(21),
                6000,
                1L, // Tutor ID 1 - Sarah Johnson (teaching multiple courses)
                "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8",
                CourseStatus.PUBLISHED
        );

        Chapter chapter5 = createChapter(
                "Idioms and Expressions",
                "Learn common English idioms and colloquial expressions",
                Arrays.asList("Understand native expressions", "Use idioms naturally", "Recognize cultural references"),
                1,
                150,
                true,
                advancedCourse
        );

        createLesson("Common Idioms", "Master everyday English expressions", 
                "Popular idioms: Break the ice (start conversation), Piece of cake (very easy), Hit the nail on the head (exactly right), Cost an arm and a leg (very expensive), Under the weather (feeling sick).",
                "https://example.com/videos/idioms.mp4", LessonType.VIDEO, 1, 40, true, true, chapter5);
        
        createLesson("Phrasal Verbs", "Understanding multi-word verbs", 
                "Phrasal verbs combine verbs with prepositions: look up (search), give up (quit), put off (postpone), take after (resemble), run into (meet unexpectedly), get along with (have good relationship).",
                "https://example.com/videos/phrasal-verbs.mp4", LessonType.MIXED, 2, 35, false, true, chapter5);

        // Course 4: IELTS Preparation (Tutor ID: 3)
        Course ieltsCourse = createCourse(
                "IELTS Exam Preparation",
                "Comprehensive preparation for all four sections of the IELTS exam.",
                EnglishLevel.UPPER_INTERMEDIATE,
                25,
                LocalDateTime.now().plusDays(30),
                7200,
                3L, // Tutor ID 3 - Emma Rodriguez
                "https://images.unsplash.com/photo-1434030216411-0b793f4b4173",
                CourseStatus.DRAFT
        );

        Chapter chapter6 = createChapter(
                "IELTS Listening",
                "Strategies and practice for the listening section",
                Arrays.asList("Understand question types", "Practice note-taking", "Improve listening speed"),
                1,
                180,
                false,
                ieltsCourse
        );

        createLesson("Listening Question Types", "Overview of IELTS listening formats", 
                "IELTS Listening has 4 sections with various question types: multiple choice, matching, labeling, gap-filling, short answers. Each section increases in difficulty.",
                null, LessonType.TEXT, 1, 20, true, false, chapter6);
        
        // Course 5: Conversation Practice (Tutor ID: 2)
        Course conversationCourse = createCourse(
                "English Conversation Mastery",
                "Improve your speaking skills through practical conversation exercises and real-life scenarios.",
                EnglishLevel.INTERMEDIATE,
                35,
                LocalDateTime.now().plusDays(10),
                3000,
                2L, // Tutor ID 2 - Michael Chen
                "https://images.unsplash.com/photo-1573164713714-d95e436ab8d6",
                CourseStatus.PUBLISHED
        );

        Chapter chapter7 = createChapter(
                "Daily Conversations",
                "Practice common everyday conversations",
                Arrays.asList("Order food at restaurants", "Make appointments", "Ask for directions"),
                1,
                120,
                true,
                conversationCourse
        );

        createLesson("At the Restaurant", "Learn to order food and drinks", 
                "Key phrases: I'd like to order..., Could I have..., What do you recommend?, The bill, please, Is service included?, Can we have the menu?, I'm allergic to...",
                "https://example.com/videos/restaurant.mp4", LessonType.VIDEO, 1, 25, true, true, chapter7);
        
        createLesson("Making Appointments", "Schedule meetings and appointments", 
                "Useful expressions: I'd like to make an appointment, Are you available on...?, What time works for you?, I need to reschedule, Can we meet earlier/later?",
                "https://example.com/videos/appointments.mp4", LessonType.VIDEO, 2, 20, false, true, chapter7);

        System.out.println("✅ Sample data initialized successfully!");
        System.out.println("📚 Created 5 courses with the following tutors:");
        System.out.println("   - Course 1 & 3: Tutor ID 1 (Sarah Johnson)");
        System.out.println("   - Course 2 & 5: Tutor ID 2 (Michael Chen)");
        System.out.println("   - Course 4: Tutor ID 3 (Emma Rodriguez)");
        System.out.println("👥 Expected users in auth-service:");
        System.out.println("   - Tutors: IDs 1, 2, 3");
        System.out.println("   - Students: IDs 4, 5, 6, 7, 8");
    }

    private Course createCourse(String title, String description, EnglishLevel level, 
                                Integer maxStudents, LocalDateTime schedule, Integer duration,
                                Long tutorId, String fileUrl, CourseStatus status) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setLevel(level);
        course.setMaxStudents(maxStudents);
        course.setSchedule(schedule);
        course.setDuration(duration);
        course.setTutorId(tutorId);
        course.setFileUrl(fileUrl);
        course.setStatus(status);
        return courseRepository.save(course);
    }

    private Chapter createChapter(String title, String description, java.util.List<String> objectives,
                                  Integer orderIndex, Integer estimatedDuration, Boolean isPublished,
                                  Course course) {
        Chapter chapter = new Chapter();
        chapter.setTitle(title);
        chapter.setDescription(description);
        chapter.setObjectives(objectives);
        chapter.setOrderIndex(orderIndex);
        chapter.setEstimatedDuration(estimatedDuration);
        chapter.setIsPublished(isPublished);
        chapter.setCourse(course);
        return chapterRepository.save(chapter);
    }

    private Lesson createLesson(String title, String description, String content, String contentUrl,
                                LessonType lessonType, Integer orderIndex, Integer duration,
                                Boolean isPreview, Boolean isPublished, Chapter chapter) {
        Lesson lesson = new Lesson();
        lesson.setTitle(title);
        lesson.setDescription(description);
        lesson.setContent(content);
        lesson.setContentUrl(contentUrl);
        lesson.setLessonType(lessonType);
        lesson.setOrderIndex(orderIndex);
        lesson.setDuration(duration);
        lesson.setIsPreview(isPreview);
        lesson.setIsPublished(isPublished);
        lesson.setChapter(chapter);
        return lessonRepository.save(lesson);
    }
}
