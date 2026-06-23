package com.feedbackbot.controller;

import com.feedbackbot.dto.auth.LoginRequest;
import com.feedbackbot.service.impl.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AdminUserService authService;

    public AuthController(AdminUserService authService) {
        this.authService = authService;
    }

    @GetMapping(value = {"/sing-in", "/login"})
    public ResponseEntity<String> signInPage() {
        return ResponseEntity.ok("Login");
    }

    @PostMapping(value = {"/sing-in", "/login"})
    public ResponseEntity<String> signIn(@Valid @RequestBody LoginRequest request ) {
        return ResponseEntity.ok(authService.login(request));
    }


    @GetMapping(value = {"/sign-up", "/register"})
    public ResponseEntity<String> signUpPage(){
        return ResponseEntity.ok("SignUp");
    }

    @PostMapping(value = {"/sign-up", "/register"})
    public ResponseEntity<String> signUp(@Valid @RequestBody LoginRequest request ) {
        return ResponseEntity.ok(authService.register(request));
    }
}
