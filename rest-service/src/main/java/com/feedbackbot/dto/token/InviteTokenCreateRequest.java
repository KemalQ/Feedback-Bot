package com.feedbackbot.dto.token;

import lombok.Data;

import java.time.LocalDateTime;

/// getting invite token from admin panel
@Data
public class InviteTokenCreateRequest {

    private String token;

    private String branch;

    private boolean isActive;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private Boolean used;
}
