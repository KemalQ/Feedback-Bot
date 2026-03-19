package com.feedbackbot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final UpdateProcessor updateProcessor;

    @PostMapping("/webhook")
    public void handleUpdate(@RequestBody Update update) {
        updateProcessor.processUpdate(update);
    }
}