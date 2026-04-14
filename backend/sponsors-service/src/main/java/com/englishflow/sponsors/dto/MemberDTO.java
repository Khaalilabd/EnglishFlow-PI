package com.englishflow.sponsors.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Integer id;
    private Integer clubId;
    private Long userId;
    private String rank;
    private String userName;
}
