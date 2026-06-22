package com.feedbackbot.service;

import com.feedbackbot.entity.VoiceUpdateDto;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessagingApplicationService {
    void handleUpdate(Update update);
    void handleCallback(Update update);
    void handleStartWithToken(Update update, String inviteToken);

    void handleVoiceUpdate(VoiceUpdateDto voiceUpdateDto);
}
