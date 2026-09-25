package com.feedbackbot.auth.service;

import com.feedbackbot.auth.dao.RevokedTokenDAO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenCleanupService {

    private final RevokedTokenDAO revokedTokenDAO;

    @Scheduled(cron = "0 15 3 * * *")
    @Transactional
    public void deleteExpiredRevokedTokens() {
        revokedTokenDAO.deleteExpired(LocalDateTime.now());
    }
}