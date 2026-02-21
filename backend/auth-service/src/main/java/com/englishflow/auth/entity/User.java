package com.englishflow.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String phone;

    @Column(unique = true)
    private String cin;

    @Column
    private String profilePhoto;

    @Column
    private String dateOfBirth;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String postalCode;

    @Column(length = 500)
    private String bio;

    @Column
    private String englishLevel; // For students: A1, A2, B1, B2, C1, C2

    @Column
    private Integer yearsOfExperience; // For teachers

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean isActive = true;

    @Column(nullable = false)
    private boolean registrationFeePaid = false;
    
    @Column(nullable = false)
    private boolean profileCompleted = false; // For OAuth2 users who need to complete their profile

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Legacy enum for backward compatibility
    public enum Role {
        ADMIN,
        TUTOR,
        STUDENT,
        ACADEMIC_OFFICE_AFFAIR
    }

    // Helper methods for permissions
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isStudent() {
        return role == Role.STUDENT;
    }

    public boolean isTutor() {
        return role == Role.TUTOR;
    }

    public boolean isAcademicStaff() {
        return role == Role.ACADEMIC_OFFICE_AFFAIR;
    }

    public String getRoleName() {
        return role != null ? role.name() : "UNKNOWN";
    }
}
