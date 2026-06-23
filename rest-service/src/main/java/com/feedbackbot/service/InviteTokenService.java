package com.feedbackbot.service;

import com.feedbackbot.dto.token.InviteTokenCreateRequest;
import com.feedbackbot.dto.token.InviteTokenResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface InviteTokenService {
    /// READ
    Page<InviteTokenResponseDto> getAll(Pageable pageable);

    /// CREATE
    InviteTokenResponseDto createToken(InviteTokenCreateRequest token);

    void deleteToken(Long id);
}
