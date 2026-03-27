package com.englishflow.courses.config;

import com.englishflow.courses.entity.CourseCategory;
import com.englishflow.courses.repository.CourseCategoryRepository;
import com.englishflow.courses.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final CourseRepository courseRepository;
    private final CourseCategoryRepository categoryRepository;
    
    @Override
    public void run(String... args) {
        // Initialize categories first
        if (categoryRepository.count() == 0) {
            log.info("Initializing course categories...");
            initializeCategories();
            log.info("Course categories initialized successfully!");
        } else {
            log.info("Categories already exist, skipping initialization.");
        }
        
        log.info("Data initialization completed.");
    }

    private void initializeCategories() {
        CourseCategory[] categories = {
            createCategory("General English", "Comprehensive English language learning for all levels", "📚", "#3B82F6", 1),
            createCategory("Business English", "Professional English for workplace communication", "💼", "#10B981", 2),
            createCategory("Grammar", "Master English grammar rules and structures", "📝", "#F59E0B", 3),
            createCategory("Conversation", "Improve speaking and listening skills", "💬", "#8B5CF6", 4),
            createCategory("Writing", "Develop writing skills for various purposes", "✍️", "#EC4899", 5),
            createCategory("Exam Preparation", "Prepare for IELTS, TOEFL, and other exams", "🎯", "#EF4444", 6),
            createCategory("Vocabulary", "Expand your English vocabulary", "📖", "#06B6D4", 7),
            createCategory("Pronunciation", "Perfect your English pronunciation", "🗣️", "#F97316", 8),
            createCategory("Kids English", "Fun English learning for children", "🎨", "#84CC16", 9),
            createCategory("Academic English", "English for academic and research purposes", "🎓", "#6366F1", 10)
        };
        
        for (CourseCategory category : categories) {
            categoryRepository.save(category);
        }
    }

    private CourseCategory createCategory(String name, String description, String icon, String color, int order) {
        CourseCategory category = new CourseCategory();
        category.setName(name);
        category.setDescription(description);
        category.setIcon(icon);
        category.setColor(color);
        category.setActive(true);
        category.setDisplayOrder(order);
        category.setCreatedBy(1L); // System user
        return category;
    }
}
