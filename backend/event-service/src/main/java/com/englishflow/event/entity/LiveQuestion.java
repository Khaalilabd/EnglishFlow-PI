package com.englishflow.event.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "live_questions")
@Data
@NoArgsConstructor
public class LiveQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer eventId;

    @Column(nullable = false)
    private Long authorId;

    private String authorName;

    @Column(nullable = false, length = 500)
    private String text;

    private boolean answered = false;
    private boolean anonymous = false;

    @ElementCollection
    @CollectionTable(name = "question_upvotes", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "user_id")
    private List<Long> upvoterIds = new ArrayList<>();

    public int getUpvoteCount() {
        return upvoterIds.size();
    }

    private LocalDateTime createdAt = LocalDateTime.now();
}
