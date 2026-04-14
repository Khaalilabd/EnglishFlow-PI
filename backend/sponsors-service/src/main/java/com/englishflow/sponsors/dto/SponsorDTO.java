package com.englishflow.sponsors.dto;

import com.englishflow.sponsors.entity.Sponsor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SponsorDTO {
    private Long id;
    
    @NotBlank(message = "Sponsor name is required")
    private String name;
    
    private String description;
    private String logo;
    private String website;
    
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    private String contactPhone;

    private Long userId;
    private String applicantFirstName;
    private String applicantLastName;

    private Integer clubId;
    private String clubName;

    private Sponsor.SponsorLevel level;
    private Sponsor.SponsorStatus status;
    
    @NotNull(message = "Contribution amount is required")
    @Min(value = 0, message = "Contribution amount must be positive")
    private Double contributionAmount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
