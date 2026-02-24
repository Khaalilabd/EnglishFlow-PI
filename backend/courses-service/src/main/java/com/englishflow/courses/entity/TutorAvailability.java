package com.englishflow.courses.entity;

import com.englishflow.courses.enums.DayOfWeek;
import com.englishflow.courses.enums.TutorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tutor_availability")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorAvailability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long tutorId;
    
    @Column(nullable = false)
    private String tutorName;
    
    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "tutor_available_days", joinColumns = @JoinColumn(name = "availability_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day")
    private Set<DayOfWeek> availableDays = new HashSet<>();
    
    @OneToMany(mappedBy = "tutorAvailability", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSlot> timeSlots = new ArrayList<>();
    
    @Column(nullable = false)
    private Integer maxStudentsCapacity;
    
    @Column(nullable = false)
    private Integer currentStudentsCount = 0;
    
    @ElementCollection
    @CollectionTable(name = "tutor_categories", joinColumns = @JoinColumn(name = "availability_id"))
    @Column(name = "category", length = 100)
    private Set<String> categories = new HashSet<>(); // Dynamic category names
    
    @ElementCollection
    @CollectionTable(name = "tutor_levels", joinColumns = @JoinColumn(name = "availability_id"))
    @Column(name = "level", length = 10)
    private Set<String> levels = new HashSet<>(); // A1, A2, B1, B2, C1, C2
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TutorStatus status = TutorStatus.AVAILABLE;
    
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
    
    public Integer getAvailableCapacity() {
        return maxStudentsCapacity - currentStudentsCount;
    }
    
    public Double getCapacityPercentage() {
        if (maxStudentsCapacity == 0) return 0.0;
        return (currentStudentsCount.doubleValue() / maxStudentsCapacity.doubleValue()) * 100;
    }
}
