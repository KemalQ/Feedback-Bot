package com.feedbackbot.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiError {
    private Integer statusCode;
    private String message;
    private String path;
    private LocalDateTime errorTime;
}
