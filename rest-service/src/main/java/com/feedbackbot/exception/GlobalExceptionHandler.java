package com.feedbackbot.exception;

import com.feedbackbot.auth.exception.AdminAlreadyExistsException;
import com.feedbackbot.auth.exception.InvalidRefreshTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

import static java.util.stream.Collectors.joining;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InviteTokenNotFoundException.class)
    public ResponseEntity<ApiError> handleInviteTokenNotFoundException(
            InviteTokenNotFoundException exception, WebRequest request){
        log.error("Invite Token Not Found Exception: " + exception.getMessage());
        return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException e, WebRequest request){
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(joining(", "));

        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatchException(MethodArgumentTypeMismatchException e, WebRequest request){
        String message = String.format("Invalid value '%s' for parameter '%s'",
                e.getValue(), e.getName());
        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request){
        String message = "Invalid argument: " + e.getMessage();
        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e, WebRequest request){

        log.error("Unhandled exception at {}", request.getDescription(false), e);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
    }

    @ExceptionHandler(FeedbackNotFoundException.class)
    public ResponseEntity<ApiError> handleFeedbackNotFoundException(
            FeedbackNotFoundException exception, WebRequest request){
        log.error("Feedback Not Found: " + exception.getMessage());
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
    }

    @ExceptionHandler(AdminAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleAdminAlreadyExistsException(
            AdminAlreadyExistsException exception, WebRequest request){
        log.error("Admin already exists: " + exception.getMessage());
        return buildError(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshTokenException(
            InvalidRefreshTokenException exception, WebRequest request){
        log.error("Invalid refresh token: " + exception.getMessage());
        return buildError(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String message, WebRequest request){
        ApiError apiError = new ApiError();
        apiError.setStatusCode(status.value());
        apiError.setMessage(message);
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        apiError.setErrorTime(LocalDateTime.now());

        return new ResponseEntity<>(apiError, status);

    }
}
