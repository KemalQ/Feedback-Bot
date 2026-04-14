package com.feedbackbot.controller;

import com.feedbackbot.dto.feedback.FeedbackFilterRequest;
import com.feedbackbot.dto.feedback.FeedbackResponseDto;
import com.feedbackbot.dto.token.InviteTokenCreateRequest;
import com.feedbackbot.service.FeedbackService;
import com.feedbackbot.service.InviteTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public Page<FeedbackResponseDto> getAll(
            @ModelAttribute FeedbackFilterRequest filter,   // record instead of 5 @RequestParam
            @PageableDefault Pageable pageable) {
        log.info("Getting all feedbacks page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return feedbackService.findAll(filter, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> getSpecificFeedback(@PathVariable Long id) {
        log.info("Getting feedback for id: {}", id);
        return ResponseEntity.ok(feedbackService.findById(id));
    }
}
