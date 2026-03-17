package com.feedbackbot.dao;

import com.feedbackbot.entity.InviteToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InviteTokenDAO extends JpaRepository<InviteToken, Long> {
    Optional<InviteToken> findByTokenAndIsActiveTrue(String token);
}