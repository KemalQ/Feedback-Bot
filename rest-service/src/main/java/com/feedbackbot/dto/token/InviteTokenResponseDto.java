package com.feedbackbot.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/// retrieve invite tokens
@AllArgsConstructor
@Data
public class InviteTokenResponseDto {

    private String token;

    private String branch;

    private boolean isActive = true;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private Boolean used = false;
}
