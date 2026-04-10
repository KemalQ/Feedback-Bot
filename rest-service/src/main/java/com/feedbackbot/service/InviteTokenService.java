package com.feedbackbot.service;

import com.feedbackbot.dto.token.InviteTokenCreateRequest;
import jakarta.validation.Valid;

import java.util.List;

public interface InviteTokenService {
    List<InviteTokenCreateRequest> getAll();

    InviteTokenCreateRequest createToken(@Valid InviteTokenCreateRequest token);
}
