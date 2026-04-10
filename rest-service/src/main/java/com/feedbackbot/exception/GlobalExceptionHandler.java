package com.feedbackbot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static java.util.stream.Collectors.joining;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InviteTokenNotFoundException.class)
    public ResponseEntity<ApiError> handleInviteTokenNotFoundException(
            InviteTokenNotFoundException exception, WebRequest request){
        ApiError apiError = new ApiError();
        apiError.setStatusCode(HttpStatus.NOT_FOUND.value());
        apiError.setMessage(exception.getMessage());
        apiError.setPath(request.getDescription(false).replace("uri=", ""));
        apiError.setErrorTime(LocalDateTime.now());

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException e, WebRequest request){
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(joining(", "));

        ApiError error = new ApiError();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(message);
        error.setErrorTime(LocalDateTime.now());
        error.setPath(request.getDescription(false).replace("uri= ", ""));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e, WebRequest request){
        ApiError error = new ApiError();
        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage("Internal server error ocured " + e.getMessage());
        error.setErrorTime(LocalDateTime.now());
        error.setPath(request.getDescription(false).replace("uri= ", ""));

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
