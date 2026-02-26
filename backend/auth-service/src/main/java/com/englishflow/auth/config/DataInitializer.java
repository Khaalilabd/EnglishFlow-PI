package com.englishflow.auth.config;

import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Only initialize if no users exist
        if (userRepository.count() > 0) {
            log.info("Users already exist. Skipping initialization.");
            return;
        }

        log.info("Initializing sample users...");

        // Create Admin
        createUser(
            "admin@englishflow.com",
            "password123",
            "Admin",
            "User",
            "+1234567890",
            User.Role.ADMIN,
            null,
            null,
            "System Administrator"
        );

        // Create Academic Office Affair
        createUser(
            "academic@englishflow.com",
            "password123",
            "Academic",
            "Officer",
            "+1234567891",
            User.Role.ACADEMIC_OFFICE_AFFAIR,
            null,
            null,
            "Academic Affairs Officer"
        );

        // Create Tutors
        createUser(
            "john.tutor@englishflow.com",
            "password123",
            "John",
            "Smith",
            "+1234567892",
            User.Role.TUTOR,
            null,
            5,
            "Experienced English teacher specializing in grammar and conversation."
        );

        createUser(
            "sarah.tutor@englishflow.com",
            "password123",
            "Sarah",
            "Johnson",
            "+1234567893",
            User.Role.TUTOR,
            null,
            8,
            "IELTS and TOEFL preparation specialist with 8 years of experience."
        );

        createUser(
            "michael.tutor@englishflow.com",
            "password123",
            "Michael",
            "Brown",
            "+1234567894",
            User.Role.TUTOR,
            null,
            3,
            "Business English and professional communication expert."
        );

        // Create Students
        createUser(
            "alice.student@englishflow.com",
            "password123",
            "Alice",
            "Williams",
            "+1234567895",
            User.Role.STUDENT,
            "A1",
            null,
            "Beginner student eager to learn English."
        );

        createUser(
            "bob.student@englishflow.com",
            "password123",
            "Bob",
            "Davis",
            "+1234567896",
            User.Role.STUDENT,
            "B1",
            null,
            "Intermediate student looking to improve conversation skills."
        );

        createUser(
            "carol.student@englishflow.com",
            "password123",
            "Carol",
            "Miller",
            "+1234567897",
            User.Role.STUDENT,
            "B2",
            null,
            "Upper-intermediate student preparing for IELTS exam."
        );

        createUser(
            "david.student@englishflow.com",
            "password123",
            "David",
            "Wilson",
            "+1234567898",
            User.Role.STUDENT,
            "C1",
            null,
            "Advanced student focusing on business English."
        );

        createUser(
            "emma.student@englishflow.com",
            "password123",
            "Emma",
            "Moore",
            "+1234567899",
            User.Role.STUDENT,
            "A2",
            null,
            "Elementary student working on grammar fundamentals."
        );

        log.info("Sample users created successfully!");
        log.info("Login credentials: email / password123");
    }

    private void createUser(String email, String password, String firstName, String lastName,
                           String phone, User.Role role, String englishLevel, 
                           Integer yearsOfExperience, String bio) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setRole(role);
        user.setEnglishLevel(englishLevel);
        user.setYearsOfExperience(yearsOfExperience);
        user.setBio(bio);
        user.setActive(true);
        user.setRegistrationFeePaid(true);
        user.setProfileCompleted(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Created user: {} ({})", email, role);
    }
}
