package com.feedbackbot.dto.feedback;

import com.feedbackbot.enums.Sentiment;
import lombok.Data;

@Data
public class FeedbackFilterRequest {
    String branch;
    String role;
    Integer criticality;
    Sentiment sentiment;
}
