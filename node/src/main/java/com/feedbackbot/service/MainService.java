package com.feedbackbot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void processTextMessage(Update update);
    void processStartWithToken(Update update, String token);

    void processCallback(Update update, String callbackData);

    void processVoiceMessage(Update update, String transcribedText);
//    void processDocMessage(Update update);
//    void processPhotoMessage(Update update);
}