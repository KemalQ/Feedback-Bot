package com.feedbackbot.auth.service;

import com.feedbackbot.auth.dto.RegisterAdminRequest;
import com.feedbackbot.auth.dto.TokenPairResponse;

public interface AuthService {
    void registerAdmin(RegisterAdminRequest request);
    void bootstrapFirstAdmin(RegisterAdminRequest request, String bootstrapToken);
    String issueRefreshToken(String username);
    TokenPairResponse rotateRefreshToken(String rawRefreshToken);
    void logout(String rawRefreshToken, String accessToken, String username);
}