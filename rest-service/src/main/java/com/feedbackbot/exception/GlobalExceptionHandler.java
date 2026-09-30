package com.feedbackbot.exception;

import com.feedbackbot.auth.exception.AdminAlreadyExistsException;
import com.feedbackbot.auth.exception.InvalidRefreshTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import java.time.LocalDateTime;

import static java.util.stream.Collectors.joining;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //------------ domain exceptions
    @ExceptionHandler(InviteTokenNotFoundException.class)
    public ResponseEntity<ApiError> handleInviteTokenNotFoundException(
            InviteTokenNotFoundException exception, WebRequest request){
        log.error("Invite Token Not Found Exception: " + exception.getMessage());
        return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request){
//        String message = "Invalid argument: " + e.getMessage();
//        log.error("Invalid argument: " + e.getMessage());
//        return buildError(HttpStatus.BAD_REQUEST, message, request);
//    } //TODO

    @ExceptionHandler(FeedbackNotFoundException.class)
    public ResponseEntity<ApiError> handleFeedbackNotFoundException(
            FeedbackNotFoundException exception, WebRequest request){
        log.error("Feedback Not Found: " + exception.getMessage());
        return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), request);
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

    //------------ security exceptions
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(
            AuthenticationException exception, WebRequest request) {
        log.error("Authentication failed: " + exception.getMessage());
        return buildError(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                request
        );
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(
            AccessDeniedException exception, WebRequest request){
        log.warn("Access denied: " + exception.getMessage());
        return buildError(HttpStatus.FORBIDDEN, exception.getMessage(), request);
    }

    //------------other exceptions
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatchException(MethodArgumentTypeMismatchException e, WebRequest request){
        String message = String.format("Invalid value '%s' for parameter '%s'",
                e.getValue(), e.getName());
        log.error("Invalid type: " + e.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(Exception e, WebRequest request){
        log.error("Unhandled exception at {}", request.getDescription(false), e);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
    }

    //------------Override method
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(joining(", "));

        log.error("Validation failed: " + message);

        return (ResponseEntity<Object>) (ResponseEntity<?>) buildError(HttpStatus.BAD_REQUEST, message, request);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String message, WebRequest request){
        ApiError apiError = new ApiError();
        apiError.setStatusCode(status.value());
        apiError.setMessage(message);
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        apiError.setErrorTime(LocalDateTime.now());

        return new ResponseEntity<>(apiError, status);
    }

    private ResponseEntity<Object> toObjectResponse(
            HttpStatusCode status, String message, HttpHeaders headers, WebRequest request) {
        ApiError error = new ApiError(status.value(), message,
                request.getDescription(false).replace("uri=", ""), LocalDateTime.now());
        return new ResponseEntity<>(error, headers, status);
    }
}
