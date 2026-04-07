package com.feedbackbot.dto;

import com.feedbackbot.enums.Sentiment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullAiResponse {
    private boolean isRelevant;
    private Sentiment sentiment;
    private Integer criticality;
    private String resolution;
}
