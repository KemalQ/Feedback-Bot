package com.feedbackbot.controller;

import com.feedbackbot.dto.PageResponse;
import com.feedbackbot.dto.token.InviteTokenCreateRequest;
import com.feedbackbot.dto.token.InviteTokenResponseDto;
import com.feedbackbot.service.InviteTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/admin/tokens")
public class TokenController {

    private final InviteTokenService inviteTokenService;

    public TokenController(InviteTokenService inviteTokenService) {
        this.inviteTokenService = inviteTokenService;
    }

    @GetMapping("/")
    public RedirectView redirectWithTrailingSlash() {
        return new RedirectView("/admin/tokens");
    }

    @GetMapping
    public PageResponse<InviteTokenResponseDto> getTokens(Pageable pageable) {
        log.info("Getting all invite tokens page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<InviteTokenResponseDto> page = inviteTokenService.getAll(pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @PostMapping
    public ResponseEntity<InviteTokenResponseDto> createToken(@Valid @RequestBody InviteTokenCreateRequest token){
        InviteTokenResponseDto savedToken = inviteTokenService.createToken(token);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedToken.getToken())// or savedToken.getId()
                .toUri();

        log.info("Creating new invite token: {}", token);

        return ResponseEntity.created(location).body(savedToken);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInviteToken(@PathVariable Long id){
        inviteTokenService.deleteToken(id);
        log.info("Invite Token deleted! id = {}", id);
        return ResponseEntity.noContent().build();
    }




}
