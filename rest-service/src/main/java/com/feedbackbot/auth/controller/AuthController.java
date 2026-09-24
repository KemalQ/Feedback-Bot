package com.feedbackbot.auth.controller;

import com.feedbackbot.auth.service.AuthService;
import com.feedbackbot.auth.service.JwtService;
import com.feedbackbot.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenPairResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        return ResponseEntity.ok(new TokenPairResponse(
                jwtService.generateAccessToken(request.username()),
                authService.issueRefreshToken(request.username())));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenPairResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.rotateRefreshToken(request.refreshToken()));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterAdminRequest request) {
        authService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/bootstrap")
    public ResponseEntity<Void> bootstrap(@Valid @RequestBody RegisterAdminRequest request,
                                          @RequestHeader("X-Bootstrap-Token") String bootstrapToken){
        authService.bootstrapFirstAdmin(request, bootstrapToken);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request,
                                       @RequestHeader(value = "Authorization", required = false) String authHeader,
                                       Authentication authentication) {
        String accessToken = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        authService.logout(request.refreshToken(), accessToken, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}