package com.feedbackbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


import java.io.InputStream;
import java.net.URL;

@Slf4j
@Component
public class TelegramBot {

    private final TelegramClient telegramClient;
    private final String token;

    public TelegramBot(@Value("${telegram.bot.token}") String token) {
        this.telegramClient = new OkHttpTelegramClient(token);
        this.token = token;
    }

    public void sendAnswerMessage(SendMessage message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message: {}", e.getMessage());
        }
    }

    public File executeGetFile(GetFile getFile){
        try{
            return telegramClient.execute(getFile);
        }
        catch (TelegramApiException e){
            log.error("Error getting file info: {}", e.getMessage());
            throw new RuntimeException("Failed to get file from Telegram", e);
        }
    }

    public byte[] downloadFileBytes(String filePath){
        String downloadUrl = "https://api.telegram.org/file/bot" + token + "/" + filePath;
        try{
            URL url = new URL(downloadUrl);
            try(InputStream stream = url.openStream()){
                return stream.readAllBytes();
            }
        } catch (Exception e){
            log.error("Error downloading file from Telegram: {}", e.getMessage());
            throw new RuntimeException("Failed to download voice file", e);
        }
    }
}