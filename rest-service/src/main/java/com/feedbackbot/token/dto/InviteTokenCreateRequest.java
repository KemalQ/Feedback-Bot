package com.feedbackbot.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/// getting invite token from admin panel
@Data
public class InviteTokenCreateRequest {

    @NotBlank
    private String token;

    @NotBlank
    private String branch;

    @JsonProperty("isActive")
    private Boolean isActive; /// ! primitive boolean Lombok -> isisActive()

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private Boolean used;

    private String createdBy;
}
