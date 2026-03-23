package com.feedbackbot.service;

import com.feedbackbot.bot.UpdateProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.feedbackbot.module.RabbitQueue.ANSWER_MESSAGE;

@Slf4j
@Service
public class AnswerConsumer {
    private final UpdateProcessor updateProcessor;


    public AnswerConsumer(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage){
        log.info("Sending answer to chat {}", sendMessage.getChatId());
        updateProcessor.setView(sendMessage);
    }
}







