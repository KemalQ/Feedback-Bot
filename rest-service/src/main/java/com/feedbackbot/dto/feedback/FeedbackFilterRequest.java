package com.feedbackbot.dto.feedback;

import lombok.Data;

@Data
public class FeedbackFilterRequest {
    String branch;
    String role;
    Integer criticality;
}
