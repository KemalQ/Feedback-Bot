package com.feedbackbot.service;

import com.feedbackbot.dto.token.InviteTokenCreateRequest;
import com.feedbackbot.dto.token.InviteTokenResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface InviteTokenService {
    /// READ
    List<InviteTokenResponseDto> getAll();

    /// CREATE
    InviteTokenResponseDto createToken(InviteTokenCreateRequest token);
}
