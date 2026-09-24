package com.feedbackbot.auth.service;

import com.feedbackbot.auth.dao.RevokedTokenDAO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class JwtService {

    private final String issuer;
    private final SecretKey key;
    private final long accessTokenMinutes;
    private final RevokedTokenDAO revokedTokenDAO;

    public JwtService(@Value("${jwt.issuer}") String issuer,
                      @Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-token-minutes:15}") long accessTokenMinutes,
                      RevokedTokenDAO revokedTokenDAO) {
        this.issuer = issuer;
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 32 bytes");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenMinutes = accessTokenMinutes;
        this.revokedTokenDAO = revokedTokenDAO;
    }

    public String generateAccessToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenMinutes, ChronoUnit.MINUTES)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String getUsername(String token) { return parseClaims(token).getSubject(); }
    public String getTokenId(String token) { return parseClaims(token).getId(); }

    public LocalDateTime getExpiration(String token) {
        return LocalDateTime.ofInstant(parseClaims(token).getExpiration().toInstant(), ZoneId.systemDefault());
    }

    public boolean isValid(String token) {
        try { parseClaims(token); return true; }
        catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isValidAndNotRevoked(String token) {
        if (!isValid(token)) return false;
        String jti = getTokenId(token);
        return jti != null && !revokedTokenDAO.existsByJti(jti);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key).requireIssuer(issuer).build()
                .parseSignedClaims(token).getPayload();
    }
}
