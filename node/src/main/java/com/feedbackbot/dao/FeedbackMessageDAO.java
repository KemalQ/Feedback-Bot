package com.feedbackbot.dao;

import com.feedbackbot.entity.FeedbackMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackMessageDAO extends JpaRepository<FeedbackMessage, Long> {
}
