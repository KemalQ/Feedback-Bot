package com.feedbackbot.auth.dto;

public record TokenPairResponse(
    String accessToken,
    String refreshToken) {}