package com.feedbackbot.service.impl;

import com.feedbackbot.dao.InviteTokenDAO;
import com.feedbackbot.dto.token.InviteTokenCreateRequest;
import com.feedbackbot.dto.token.InviteTokenResponseDto;
import com.feedbackbot.entity.InviteToken;
import com.feedbackbot.mapper.MapperUtils;
import com.feedbackbot.service.InviteTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class InviteTokenServiceImpl implements InviteTokenService {
    private final InviteTokenDAO inviteTokenDAO;
    private final MapperUtils inviteTokenMapper;

    public InviteTokenServiceImpl(InviteTokenDAO inviteTokenDAO, MapperUtils inviteTokenMapper) {
        this.inviteTokenDAO = inviteTokenDAO;
        this.inviteTokenMapper = inviteTokenMapper;
    }

    @Override
    public List<InviteTokenResponseDto> getAll() {
        return inviteTokenDAO.findAll().stream().map(inviteTokenMapper::toInviteTokenResponseDto).toList();
    }

    @Override
    public InviteTokenResponseDto createToken(InviteTokenCreateRequest token) {
        // Set default createdBy if not provided
        if (token.getCreatedBy() == null) {
            token.setCreatedBy("system");
        }
        InviteToken savedToken = inviteTokenDAO.save(inviteTokenMapper.toInviteToken(token));
        log.info("Token successfully saved");
        return inviteTokenMapper.toInviteTokenResponseDto(savedToken);
    }
}
