package com.englishflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorStatusResponse {
    private Boolean enabled;
    private LocalDateTime enabledAt;
    private LocalDateTime lastUsedAt;
    private Integer backupCodesRemaining;
}
