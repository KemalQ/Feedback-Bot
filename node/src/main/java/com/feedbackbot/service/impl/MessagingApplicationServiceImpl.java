package com.feedbackbot.service.impl;

import com.feedbackbot.entity.VoiceUpdateDto;
import com.feedbackbot.integrations.ai.GroqTranscriptionService;
import com.feedbackbot.listener.MessageValidator;
import com.feedbackbot.service.MainService;
import com.feedbackbot.service.MessagingApplicationService;
import com.feedbackbot.service.ProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;

import static com.feedbackbot.module.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Slf4j
@Service
public class MessagingApplicationServiceImpl implements MessagingApplicationService {

    private final MainService mainService;
    private final MessageValidator messageValidator;
    private final ProducerService producerService;
    private final GroqTranscriptionService groqTranscriptionService;

    public MessagingApplicationServiceImpl(MainService mainService, MessageValidator messageValidator, ProducerService producerService, GroqTranscriptionService groqTranscriptionService){
        this.mainService = mainService;
        this.messageValidator = messageValidator;
        this.producerService = producerService;
        this.groqTranscriptionService = groqTranscriptionService;
    }

    @Override
    public void handleUpdate(Update update) {
        messageValidator.validateUpdate(update);

        log.info("✅ {} message: text={}", TEXT_MESSAGE_UPDATE, update.getMessage().getText());

        mainService.processTextMessage(update);
    }

    @Override
    public void handleCallback(Update update) {
        messageValidator.validateUpdate(update);

        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        //update.getMessage();

        log.info("Callback received: {}", callbackData);
        mainService.processCallback(update, callbackData);
    }

    @Override
    public void handleStartWithToken(Update update, String inviteToken) {
        messageValidator.validateUpdate(update);
        log.info("Start with token {}", inviteToken);
        mainService.processStartWithToken(update, inviteToken);
    }

    @Override
    public void handleVoiceUpdate(VoiceUpdateDto voiceUpdateDto){
        Update update = voiceUpdateDto.getUpdate();
        byte[] audioBytes = voiceUpdateDto.getVoiceBytes();
        Voice voice = update.getMessage().getVoice();

        try {
            log.info("Processing voice: fileId={}, duration={}s",
                    voice.getFileId(), voice.getDuration());

            // 1. Transcripting via Groq Whisper (audio already downloaded by dispatcher)
            String transcribedText = groqTranscriptionService.transcribeVoice(audioBytes);

            // 2. Sending for Sentiment analysis to MainServiceImpl-processVoiceMessage
            log.info("Voice transcribed: {}", transcribedText);

            mainService.processVoiceMessage(update, transcribedText);
        }
        catch (Exception e){
            log.error("❌ Failed to process voice message", e);

            Long chatId = voiceUpdateDto.getUpdate().getMessage().getChatId();
            SendMessage errorMsg = SendMessage.builder()
                    .chatId(chatId)
                    .text("Failed to process voice message")
                    .build();
            producerService.produceAnswer(errorMsg)
            ;
            throw new AmqpRejectAndDontRequeueException("Failed to process voice message", e);
        }
    }
}