package com.feedbackbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Slf4j
@Configuration
public class BotConfig {

    @Bean
    public ApplicationRunner registerWebhook(
            @Value("${telegram.bot.token}") String token,
            @Value("${bot.webhook-url}") String webhookUrl
    ) {
        return args -> {
            var client = new OkHttpTelegramClient(token);
            var request = SetWebhook.builder().url(webhookUrl).build();
            client.execute(request);
            log.info("Webhook set: {}", webhookUrl);
        };
    }
}