package com.feedbackbot.dto.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/// retrieve invite tokens
@AllArgsConstructor
@Data
public class InviteTokenResponseDto {

    private String token;

    private String branch;

    @JsonProperty("isActive")
    private boolean isActive;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private Boolean used;
}
