package com.feedbackbot.bot;

import com.feedbackbot.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

import static com.feedbackbot.module.RabbitQueue.DIRECT_EXCHANGE;
import static com.feedbackbot.module.RabbitQueue.TEXT_ROUTE;

@Slf4j
@Component
public class UpdateProcessor {

    private final RabbitTemplate rabbitTemplate;
    private final TelegramBot telegramBot;
    private final MessageUtils messageUtils;

    public UpdateProcessor(RabbitTemplate rabbitTemplate, TelegramBot telegramBot, MessageUtils messageUtils) {
        this.rabbitTemplate = rabbitTemplate;
        this.telegramBot = telegramBot;
        this.messageUtils = messageUtils;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            distributeMessageByType(update);

            // TODO -Static answer, change after webhook test
            telegramBot.sendAnswerMessage(messageUtils.generateSendMessageWithText(update, "answer"));
        }
        else {
            log.error("Unsupported message type is:  {}" , update);
        }
    }
    private void distributeMessageByType(Update update){
        var message = update.getMessage();

        if (message.hasText()){
            processTextMessage(update);
            log.info("Text message received: {}", update.getMessage().getText());
        }
        /// In the future feedback messages might be extended
//        else if (message.hasDocument()) {
//            processDocMessage(update);
//        } else if (message.hasPhoto()) {
//            processPhotoMessage(update);
//        }
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
        String chatId = update.getMessage().getChatId().toString();
        
        /// Sending to direct queue
        CorrelationData correlationData = createCorrelationData();
        rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, TEXT_ROUTE,
                update,
                correlationData);
    }


    private CorrelationData createCorrelationData() {
        return new CorrelationData(UUID.randomUUID().toString());
    }
}