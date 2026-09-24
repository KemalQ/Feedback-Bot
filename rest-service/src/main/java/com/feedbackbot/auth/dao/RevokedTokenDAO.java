package com.feedbackbot.auth.dao;

import com.feedbackbot.auth.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RevokedTokenDAO extends JpaRepository<RevokedToken, Long> {
    boolean existsByJti(String jti);

    @Modifying
    @Query("DELETE FROM RevokedToken r WHERE r.expiresAt < :now")
    void deleteExpired(@Param("now") LocalDateTime now);
}