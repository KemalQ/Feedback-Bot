package com.feedbackbot.service;

import com.feedbackbot.entity.AppUser;
import com.feedbackbot.entity.FeedbackMessage;

public interface GoogleSheetsService {
    void appendFeedbackRow(FeedbackMessage message, AppUser appUser);
}
