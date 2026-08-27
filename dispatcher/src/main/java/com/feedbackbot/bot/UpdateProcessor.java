package com.feedbackbot.bot;

import com.feedbackbot.entity.VoiceUpdateDto;
import com.feedbackbot.utils.MessageUtils;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Voice;

import java.util.UUID;

import static com.feedbackbot.module.RabbitQueue.*;

@Slf4j
@Component
public class UpdateProcessor {

    private final RabbitTemplate rabbitTemplate;
    private final TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final Cache<Long, Boolean> processedUpdateIdsCache;
    private final Cache<String, Boolean> processUpdateStringCache;

    public UpdateProcessor(RabbitTemplate rabbitTemplate, TelegramBot telegramBot, MessageUtils messageUtils, Cache<Long, Boolean> processedUpdateIdsCache, Cache<String, Boolean> processUpdateStringCache) {
        this.rabbitTemplate = rabbitTemplate;
        this.telegramBot = telegramBot;
        this.messageUtils = messageUtils;
        this.processedUpdateIdsCache = processedUpdateIdsCache;
        this.processUpdateStringCache = processUpdateStringCache;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        Integer updateId = update.getUpdateId();
        if (updateId != null && isIdDuplicate(updateId)) {
            log.info("Duplicate update_id={} ignored", updateId);
            return;
        }

        if (update.hasCallbackQuery()){
            processCallBackQuery(update);
        }
        else if (update.hasMessage() && update.getMessage().hasText()) {
            if(isContentDuplicate(update)){
                log.info("Duplicate content ignored, userId={}", update.getMessage().getFrom().getId());
                return;
            }
            distributeMessageByType(update);
        }
        else if(update.hasMessage() && update.getMessage().hasVoice()){/// For audio messages
            distributeMessageByType(update);
        }
        else {
            log.warn("Unsupported message type is:  {}" , update);
        }
    }

    private boolean isIdDuplicate(long updateId){
        Boolean seen = processedUpdateIdsCache.getIfPresent(updateId);
        if(seen != null){
            return true;
        }
        processedUpdateIdsCache.put(updateId, Boolean.TRUE);
        return false;
    }
    private boolean isContentDuplicate(Update update){
        Long userId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText();
        String key = userId + ":" + text;

        Boolean previous = processUpdateStringCache.asMap().putIfAbsent(key, Boolean.TRUE);
        return previous != null;
    }

    private void processCallBackQuery(Update update) {
        String callBackData = update.getCallbackQuery().getData();
        log.info("Callback received: {}", callBackData);

        rabbitTemplate.convertAndSend(
                DIRECT_EXCHANGE,
                TEXT_ROUTE,
                update,
                createCorrelationData());
    }

    private void distributeMessageByType(Update update){
        var message = update.getMessage();

        if (message.hasText()){
            processTextMessage(update);
            log.info("Text message received: {}", update.getMessage().getText());
        }
        else if (message.hasVoice()){ /// For voice messages
            processVoiceMessage(update);
            log.info("Audio message received: {}", update.getMessage().getVoice());//TODO check
        }
        else{
            setUnsupportedMessageTypeView(update);
        }
    }



    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Unsupported message type!");
        setView(sendMessage);
    }
    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processTextMessage(Update update) {
        String text = update.getMessage().getText();

        //message with token
        if (text.startsWith("/start ")){
            String token = text.substring(7).trim();
            log.info("Start with token: {}", token);

            rabbitTemplate.convertAndSend(
                    DIRECT_EXCHANGE,
                    TEXT_ROUTE,
                    update,
                    message -> {
                        message.getMessageProperties().setHeader("invite_token", token);
                        return message;
                    },
                    createCorrelationData()
            );
            return;
        }

        /// Sending to direct queue
        //base text message
        rabbitTemplate.convertAndSend(
                DIRECT_EXCHANGE,
                TEXT_ROUTE,
                update,
                createCorrelationData());
    }

    private void processVoiceMessage(Update update) { /// For voice messages
        // Download audio file from Telegram
        Voice voice = update.getMessage().getVoice();
        String fileId = voice.getFileId();

        log.info("Voice message received: fileId={}, duration={}s, size={}bytes",
                fileId, voice.getDuration(), voice.getFileSize());
        try{
            //Step1 - Get file info
            GetFile getFileRequest = new GetFile(fileId);
            File telegramFile = telegramBot.executeGetFile(getFileRequest);
            String filePath = telegramFile.getFilePath();
            log.info("File path from Telegram: {}", filePath);

            //Step 2 -Download audio file
            byte[] audioBytes = telegramBot.downloadFileBytes(filePath);
            log.info("Download audio: {} bytes", audioBytes.length);

            //Step 3 - Create VoiceUpdateDto
            VoiceUpdateDto voiceUpdateDto = new VoiceUpdateDto(
                    update,
                    audioBytes,
                    voice.getDuration(),
                    voice.getMimeType());

            //Step 4 - sending to queue
            rabbitTemplate.convertAndSend(
                    DIRECT_EXCHANGE,
                    VOICE_ROUTE,
                    voiceUpdateDto,
                    createCorrelationData()
            );
            log.info("Voice DTO sent to queue successfully");
        } catch (Exception e){
            log.error("Failed to process voice message: {}", e.getMessage(), e);
            setView(messageUtils.generateSendMessageWithText(update,
                    "❌ Failed to process voice message, Please try again"));
        }


    }

    private CorrelationData createCorrelationData() {
        return new CorrelationData(UUID.randomUUID().toString());
    }
}