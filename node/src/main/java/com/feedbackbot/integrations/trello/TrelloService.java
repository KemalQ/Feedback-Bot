package com.feedbackbot.integrations.trello;

import com.feedbackbot.entity.AppUser;
import com.feedbackbot.entity.FeedbackMessage;


public interface TrelloService {
    String createCardIfCritical(FeedbackMessage message, AppUser user);
}
