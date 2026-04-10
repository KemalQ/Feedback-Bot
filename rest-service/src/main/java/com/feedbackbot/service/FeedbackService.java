package com.feedbackbot.service;

import com.feedbackbot.dto.feedback.FeedbackFilterRequest;
import com.feedbackbot.dto.feedback.FeedbackResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedbackService {
    Page<FeedbackResponseDto> findAll(FeedbackFilterRequest filter, Pageable pageable);// record вместо 5 @RequestParam

    FeedbackResponseDto markResolved(Long id, String resolution);
}
