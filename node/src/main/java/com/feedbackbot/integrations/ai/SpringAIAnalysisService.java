package com.feedbackbot.integrations.ai;

import com.feedbackbot.dto.FeedbackAnalysisResult;

public interface SpringAIAnalysisService {
    FeedbackAnalysisResult analyze(String feedbackText);
    String analyzeAndReturnString(String feedbackText);
}
