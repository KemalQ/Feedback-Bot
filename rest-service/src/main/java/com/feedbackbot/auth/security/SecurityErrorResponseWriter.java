package com.feedbackbot.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedbackbot.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;

final class SecurityErrorResponseWriter {
    private SecurityErrorResponseWriter() {}

    static void write(HttpServletResponse response, HttpServletRequest request,
                      ObjectMapper objectMapper, HttpStatus status, String message) throws IOException {
        ApiError error = new ApiError();
        error.setStatusCode(status.value());
        error.setMessage(message);
        error.setPath(request.getRequestURI());
        error.setErrorTime(LocalDateTime.now());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), error);
    }
}
