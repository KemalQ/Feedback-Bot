package com.feedbackbot.service.impl;

import com.feedbackbot.dto.auth.LoginRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminUserService {

    public String login(@Valid LoginRequest request) {
        return "";//TODO implement
    }
}
