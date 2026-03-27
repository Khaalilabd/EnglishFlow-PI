package com.englishflow.messaging.dto;

import com.englishflow.messaging.model.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    
    @Size(max = 5000, message = "Message content must not exceed 5000 characters")
    private String content;
    
    @NotNull(message = "Message type is required")
    private Message.MessageType messageType;
    
    @Size(max = 500, message = "File URL must not exceed 500 characters")
    private String fileUrl;
    
    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;
    
    private Long fileSize;
    
    @Size(max = 50, message = "Emoji code must not exceed 50 characters")
    private String emojiCode;
    
    private Integer voiceDuration; // Durée en secondes pour les messages vocaux
}
