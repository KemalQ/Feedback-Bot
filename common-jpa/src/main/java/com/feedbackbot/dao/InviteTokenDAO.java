package com.feedbackbot.dao;

import com.feedbackbot.entity.InviteToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InviteTokenDAO extends JpaRepository<InviteToken, Long> {
    Optional<InviteToken> findByTokenAndIsActiveTrue(String token);
}