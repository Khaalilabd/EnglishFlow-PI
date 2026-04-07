package com.englishflow.event.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "hand_raises")
@Data
@NoArgsConstructor
public class HandRaise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer eventId;

    @Column(nullable = false)
    private Long userId;

    private String userName;

    private boolean dismissed = false;

    // Order in queue
    @Column(nullable = false)
    private LocalDateTime raisedAt = LocalDateTime.now();
}
