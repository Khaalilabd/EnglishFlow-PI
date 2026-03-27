package com.jungle.learning.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "quiz")
@EqualsAndHashCode(exclude = "quiz")
@JsonIgnoreProperties({"quiz"})
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(name = "correct_answer")
    private String correctAnswer;

    @Column(nullable = false)
    private Integer points = 1;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "partial_credit_enabled")
    private Boolean partialCreditEnabled = false;

    public enum QuestionType {
        MCQ, TRUE_FALSE, OPEN
    }
}
