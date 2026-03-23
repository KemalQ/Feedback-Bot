package com.feedbackbot.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.feedbackbot.module.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Slf4j
@Service
public class RabbitMessageListener {
    private final MessagingApplicationService messagingApplicationService;

    public RabbitMessageListener(MessagingApplicationService messagingApplicationService) {
        this.messagingApplicationService = messagingApplicationService;
    }

    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeUpdate(Update update,
                              @Header(value = "invite_token", required = false) String inviteToken) {
        try {

            if (update.hasCallbackQuery()){
                messagingApplicationService.handleCallback(update);
                return;
            }
            if (inviteToken != null && !inviteToken.isBlank()){
                messagingApplicationService.handleStartWithToken(update, inviteToken);
                return;
            }

            messagingApplicationService.handleUpdate(update);
        } catch (IllegalArgumentException e) {
            log.error("❌ Failed to process notification: {}", update.getMessage().getText(), e);

            throw new AmqpRejectAndDontRequeueException("Invalid message payload", e);
        }
    }
}
