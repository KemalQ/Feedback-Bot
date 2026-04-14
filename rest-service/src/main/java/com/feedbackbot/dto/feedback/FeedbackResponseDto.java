package com.feedbackbot.dto.feedback;

import com.feedbackbot.enums.UserRole;
import com.feedbackbot.enums.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDto {

//  AppUser
    private String branch;
    private UserRole role;


    private String text;

    private Sentiment sentiment;

    private Integer criticality;

    private String resolution;

    private String googleDocRowId;

    private String trelloCardId;

    private Boolean isProcessed;

    private LocalDateTime createdAt;
}
