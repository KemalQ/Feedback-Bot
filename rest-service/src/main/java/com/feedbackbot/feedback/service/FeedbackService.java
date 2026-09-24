package com.feedbackbot.feedback.service;

import com.feedbackbot.feedback.dto.FeedbackFilterRequest;
import com.feedbackbot.feedback.dto.FeedbackResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedbackService {
    Page<FeedbackResponseDto> findAll(FeedbackFilterRequest filter, Pageable pageable);// record instead of 5 @RequestParam

    FeedbackResponseDto markResolved(Long id, String resolution);

    FeedbackResponseDto findById(Long id);

}
