package com.feedbackbot.controller;

import com.feedbackbot.dto.feedback.FeedbackFilterRequest;
import com.feedbackbot.dto.feedback.FeedbackResponseDto;
import com.feedbackbot.dto.token.InviteTokenCreateRequest;
import com.feedbackbot.service.FeedbackService;
import com.feedbackbot.service.InviteTokenService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public Page<FeedbackResponseDto> getAll(
            @ModelAttribute FeedbackFilterRequest filter,   // record вместо 5 @RequestParam
            @PageableDefault Pageable pageable) {
        return feedbackService.findAll(filter, pageable);
    }
    @PatchMapping("/{id}/resolve")
    public FeedbackResponseDto resolve(@PathVariable Long id,
                                       @RequestBody String resolution) {
        return feedbackService.markResolved(id, resolution);
    }
}
