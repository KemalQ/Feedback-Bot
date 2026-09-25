package com.feedbackbot.auth.service;

import com.feedbackbot.auth.dto.*;
import com.feedbackbot.auth.entity.AdminUser;
import com.feedbackbot.auth.entity.RefreshToken;
import com.feedbackbot.auth.entity.RevokedToken;
import com.feedbackbot.auth.exception.AdminAlreadyExistsException;
import com.feedbackbot.auth.exception.InvalidRefreshTokenException;
import com.feedbackbot.auth.dao.AdminUserDAO;
import com.feedbackbot.auth.dao.RefreshTokenDAO;
import com.feedbackbot.auth.dao.RevokedTokenDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final long REFRESH_TOKEN_DAYS = 30;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AdminUserDAO adminUserDAO;
    private final RefreshTokenDAO refreshTokenDAO;
    private final RevokedTokenDAO revokedTokenDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Value("${app.auth.bootstrap-token:}")
    private String bootstrapToken;

    @Override
    @Transactional
    public void registerAdmin(RegisterAdminRequest request) {
        createAdmin(request);
    }

    @Override
    @Transactional
    public void bootstrapFirstAdmin(RegisterAdminRequest request, String suppliedBootstrapToken) {
        if (!StringUtils.hasText(bootstrapToken) || !constantTimeEquals(bootstrapToken, suppliedBootstrapToken)){
            throw new InvalidRefreshTokenException("Invalid bootstrap token");
        }
        if (adminUserDAO.count() != 0){
            throw new AdminAlreadyExistsException("Initial administrator has already been created");
        }
        createAdmin(request);
    }

    @Override
    @Transactional
    public String issueRefreshToken(String username) {
        AdminUser admin = adminUserDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));

        String rawToken = newRefreshToken();
        refreshTokenDAO.save(RefreshToken.builder()
                .adminUser(admin)
                .tokenHash(hash(rawToken))
                .expiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_DAYS))
                .revoked(false)
                .build());
        return rawToken;
    }



    @Override
    @Transactional
    public TokenPairResponse rotateRefreshToken(String rawRefreshToken) {
        RefreshToken stored = refreshTokenDAO.findByTokenHash(hash(rawRefreshToken))
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not recognized"));

        if (stored.isRevoked()) {
            // A reused rotated token normally indicates token theft: close every session for that user.
            refreshTokenDAO.revokeAllActiveByAdminUserId(stored.getAdminUser().getId());
            throw new InvalidRefreshTokenException("Refresh token reuse detected; all sessions were revoked");
        }
        if (stored.getExpiresAt().isBefore(LocalDateTime.now()) || !stored.getAdminUser().isActive()) {
            throw new InvalidRefreshTokenException("Refresh token expired or revoked");
        }

        stored.setRevoked(true);
        refreshTokenDAO.save(stored);

        String username = stored.getAdminUser().getUsername();
        return new TokenPairResponse(jwtService.generateAccessToken(username), issueRefreshToken(username));
    }

    @Override
    @Transactional
    public void logout(String rawRefreshToken, String accessToken, String username) {
        RefreshToken rt = refreshTokenDAO.findByTokenHash(hash(rawRefreshToken))
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not recognized"));
        if (!rt.getAdminUser().getUsername().equals(username)) {
            throw new InvalidRefreshTokenException("Refresh token does not belong to authenticated user");
        }
        rt.setRevoked(true);
        if (accessToken != null && jwtService.isValidAndNotRevoked(accessToken)) {
            revokedTokenDAO.save(RevokedToken.builder()
                    .adminUser(rt.getAdminUser())
                    .jti(jwtService.getTokenId(accessToken))
                    .expiresAt(jwtService.getExpiration(accessToken))
                    .build());
        }
    }

    private String hash(String raw) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private void ensureAdminDoesNotExist(RegisterAdminRequest request) {
        if (adminUserDAO.findByUsername(request.username()).isPresent() || adminUserDAO.existsByEmail(request.email())) {
            throw new AdminAlreadyExistsException("Username or email already taken: " + request.username());
        }
    }

    private void createAdmin(RegisterAdminRequest request){
        ensureAdminDoesNotExist(request);
        AdminUser admin = AdminUser.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .active(true)
                .build();
        adminUserDAO.save(admin);
        log.info("Admin registered: {}", admin.getUsername());
    }

    private boolean constantTimeEquals(String expectedBootstrapToken, String suppliedBootstrapToken) {
        if (suppliedBootstrapToken == null) return false;
        return MessageDigest.isEqual(expectedBootstrapToken.getBytes(StandardCharsets.UTF_8), suppliedBootstrapToken.getBytes(StandardCharsets.UTF_8));

    }

    private String newRefreshToken() {
        byte[] value = new byte[32];
        SECURE_RANDOM.nextBytes(value);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }
}