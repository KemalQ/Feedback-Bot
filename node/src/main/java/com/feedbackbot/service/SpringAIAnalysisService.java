package com.feedbackbot.service;

import com.feedbackbot.dto.FeedbackAnalysisResult;

public interface SpringAIAnalysisService {
    FeedbackAnalysisResult analyze(String feedbackText);
    String analyzeAndReturnString(String feedbackText);
}
