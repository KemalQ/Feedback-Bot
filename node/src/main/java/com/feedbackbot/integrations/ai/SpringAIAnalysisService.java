package com.feedbackbot.integrations.ai;

import com.feedbackbot.dto.FullAiResponse;

public interface SpringAIAnalysisService {
    FullAiResponse analyze(String feedbackText);
}
