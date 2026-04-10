package com.feedbackbot.dto.feedback;

import com.feedbackbot.enums.UserRole;
import com.feedbackbot.enums.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class FeedbackResponseDto {

//    private AppUser user;
    private Long userId;
    private String userName;
    private String firstName;
    private String lastName;
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
