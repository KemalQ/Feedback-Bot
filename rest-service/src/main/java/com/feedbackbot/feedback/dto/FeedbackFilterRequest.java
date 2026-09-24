package com.feedbackbot.feedback.dto;

import com.feedbackbot.enums.Sentiment;
import com.feedbackbot.enums.UserRole;
import lombok.Data;

@Data
public class FeedbackFilterRequest {
    String branch;
    UserRole role;
    Integer criticality;
    Sentiment sentiment;
}
