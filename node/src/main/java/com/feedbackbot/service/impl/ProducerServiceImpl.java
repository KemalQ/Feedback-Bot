package com.feedbackbot.service.impl;

import com.feedbackbot.service.ProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.feedbackbot.module.RabbitQueue.*;

@Slf4j
@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate template;

    public ProducerServiceImpl(RabbitTemplate template) {
        this.template = template;
    }

    @Override
    public void produceAnswer(SendMessage message) {
        log.info("Sending answer to chat {}", message.getChatId());
        template.convertAndSend(DIRECT_EXCHANGE, ANSWER_ROUTE, message);
    }
}
