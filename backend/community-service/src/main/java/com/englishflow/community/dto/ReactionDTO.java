package com.englishflow.community.dto;

import com.englishflow.community.entity.Reaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionDTO {
    private Long id;
    private Long userId;
    private Reaction.ReactionType type;
    private Long postId;
    private Long topicId;
}
