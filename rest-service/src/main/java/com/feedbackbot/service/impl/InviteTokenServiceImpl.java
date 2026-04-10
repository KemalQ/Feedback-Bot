package com.feedbackbot.service.impl;

import com.feedbackbot.dao.InviteTokenDAO;
import com.feedbackbot.dto.token.InviteTokenCreateRequest;
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
    public List<InviteTokenCreateRequest> getAll() {
        return inviteTokenDAO.findAll().stream().map(inviteTokenMapper::toInviteTokenDto).toList();
    }

    @Override
    public InviteTokenCreateRequest createToken(InviteTokenCreateRequest token) {
        InviteToken savedToken = inviteTokenDAO.save(inviteTokenMapper.toInviteToken(token));
        log.info("Token successfully saved");
        return inviteTokenMapper.toInviteTokenDto(savedToken);
    }
}
