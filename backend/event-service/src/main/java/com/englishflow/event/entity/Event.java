package com.englishflow.event.entity;

import com.englishflow.event.enums.EventStatus;
import com.englishflow.event.enums.EventType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_event_type", columnList = "type"),
    @Index(name = "idx_event_date", columnList = "eventDate"),
    @Index(name = "idx_event_status", columnList = "status"),
    @Index(name = "idx_event_creator", columnList = "creatorId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;
    
    @Column(nullable = false)
    private LocalDateTime eventDate;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private Integer maxParticipants;
    
    @Column(nullable = false)
    private Integer currentParticipants = 0;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private Long creatorId; // ID of the user who created the event
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String image; // Base64 encoded image
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.PENDING;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Participant> participants = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = EventStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
