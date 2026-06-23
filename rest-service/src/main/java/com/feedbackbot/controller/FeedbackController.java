package com.feedbackbot.controller;

import com.feedbackbot.dto.PageResponse;
import com.feedbackbot.dto.feedback.FeedbackFilterRequest;
import com.feedbackbot.dto.feedback.FeedbackResponseDto;
import com.feedbackbot.service.FeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("/admin/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/")
    public RedirectView redirectWithTrailingSlash() {
        return new RedirectView("/admin/feedbacks");
    }

    @GetMapping
    public PageResponse<FeedbackResponseDto> getAll(
            @ModelAttribute FeedbackFilterRequest filter,   // record instead of 5 @RequestParam
            @PageableDefault Pageable pageable) {
        log.info("Getting all feedbacks page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<FeedbackResponseDto> page = feedbackService.findAll(filter, pageable);
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDto> getSpecificFeedback(@PathVariable Long id) {
        log.info("Getting feedback for id: {}", id);
        return ResponseEntity.ok(feedbackService.findById(id));
    }
}
