package com.feedbackbot.service;

import com.feedbackbot.entity.AppUser;

public interface FeedbackProcessingService {
    String process(String feedbackText, AppUser user);
}
