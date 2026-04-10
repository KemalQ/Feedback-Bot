package com.feedbackbot.controller;

import com.feedbackbot.dto.token.InviteTokenCreateRequest;
import com.feedbackbot.service.InviteTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/tokens/")
public class TokenController {

    private final InviteTokenService inviteTokenService;

    public TokenController(InviteTokenService inviteTokenService) {
        this.inviteTokenService = inviteTokenService;
    }

    @GetMapping("/tokens")
    public ResponseEntity<List<InviteTokenCreateRequest>> getTokens() {
        return ResponseEntity.ok(inviteTokenService.getAll());
    }

    @PostMapping("/tokens")
    public ResponseEntity<InviteTokenCreateRequest> createToken(@Valid @RequestBody InviteTokenCreateRequest token){
        return ResponseEntity.ok(inviteTokenService.createToken(token));
    }




}
