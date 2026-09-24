package com.feedbackbot.token.service;

import com.feedbackbot.token.dto.InviteTokenCreateRequest;
import com.feedbackbot.token.dto.InviteTokenResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface InviteTokenService {
    /// READ
    Page<InviteTokenResponseDto> getAll(Pageable pageable);

    /// CREATE
    InviteTokenResponseDto createToken(InviteTokenCreateRequest token);

    void deleteToken(Long id);
}
