package com.feedbackbot.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.feedbackbot.module.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Slf4j
@Service
public class MessagingApplicationService {

    private final MessageValidator messageValidator;

    public MessagingApplicationService(MessageValidator messageValidator){
        this.messageValidator = messageValidator;
    }

    public void handleUpdate(Update update) {
        messageValidator.validateUpdate(update);

        log.info("✅ {} message: text={}", TEXT_MESSAGE_UPDATE, update.getMessage().getText());
        // ***
    }
}
