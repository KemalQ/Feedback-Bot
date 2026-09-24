package com.feedbackbot.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

    @NotBlank
    @Size(min = 3, max = 64)
    String username,

    @NotBlank
    @Size(min = 12, max = 128)
    String password
){}
