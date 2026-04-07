package com.englishflow.event.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poll_options")
@Data
@NoArgsConstructor
public class PollOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    @JsonIgnore
    private Poll poll;

    @Column(nullable = false)
    private String text;

    // Store voter IDs to prevent double voting
    @ElementCollection
    @CollectionTable(name = "poll_votes", joinColumns = @JoinColumn(name = "option_id"))
    @Column(name = "user_id")
    private List<Long> voterIds = new ArrayList<>();

    public int getVoteCount() {
        return voterIds.size();
    }
}
