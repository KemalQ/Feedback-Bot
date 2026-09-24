package com.feedbackbot.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterAdminRequest(

    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9._-]{3,64}", message = "must contain 3-64 letters, numbers, '.', '_' or '-'")
    String username,

    @NotBlank
    @Size(min = 12, max = 128)
    String password,

    @NotBlank
    @Size(max = 255)
    String email) {}