package com.feedbackbot.service;

import com.feedbackbot.dto.FeedbackAnalysisResult;

public interface ClaudeAnalysisService {
    FeedbackAnalysisResult analyze(String feedbackText);
}