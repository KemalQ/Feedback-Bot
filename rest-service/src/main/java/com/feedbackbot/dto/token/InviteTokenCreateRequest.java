package com.feedbackbot.dto.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/// getting invite token from admin panel
@Data
public class InviteTokenCreateRequest {

    private String token;

    private String branch;

    @JsonProperty("isActive")
    private boolean isActive; /// ! primitive boolean Lombok -> isisActive()

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private Boolean used;

    private String createdBy;
}
