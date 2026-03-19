package com.feedbackbot.listener;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.validation.Validator;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MessageValidator {
    private final Validator validator;

    public MessageValidator(Validator validator) {
        this.validator = validator;
    }
    public <T> void validate(T object, String objectType){
        if (object == null){
            log.error("❌Received null {}", objectType);
            throw new IllegalArgumentException(objectType + "cannot be null");
        }

        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()){
            String errors = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            log.error("❌Validation failed for {}: {}", objectType, errors);
            throw new IllegalArgumentException("Invalid " + objectType + ": " + errors);
        }
        log.debug("✅ {} validation passed", objectType);
    }

    public void validateUpdate(Update update){
        validate(update, "Update");
    }
}
