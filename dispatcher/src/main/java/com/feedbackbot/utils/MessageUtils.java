package com.feedbackbot.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {
    public SendMessage generateSendMessageWithText(Update update, String text) {

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text(text).build();
    }
}