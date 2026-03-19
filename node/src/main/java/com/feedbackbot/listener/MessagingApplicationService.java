package com.feedbackbot.listener;

import com.feedbackbot.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.feedbackbot.module.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Slf4j
@Service
public class MessagingApplicationService {

    private final MainService mainService;
    private final MessageValidator messageValidator;

    public MessagingApplicationService(MainService mainService, MessageValidator messageValidator){
        this.mainService = mainService;
        this.messageValidator = messageValidator;
    }

    public void handleUpdate(Update update) {
        messageValidator.validateUpdate(update);

        log.info("✅ {} message: text={}", TEXT_MESSAGE_UPDATE, update.getMessage().getText());

        mainService.processTextMessage(update);
    }

    public void handleCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        update.getMessage();

        log.info("🔘 Callback received: {}", callbackData);
        mainService.processCallback(update, callbackData);
    }
}
