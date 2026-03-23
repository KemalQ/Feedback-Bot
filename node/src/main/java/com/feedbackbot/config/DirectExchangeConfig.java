package com.feedbackbot.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.feedbackbot.module.RabbitQueue.*;

@Configuration
public class DirectExchangeConfig {
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public Queue answerQueue() {
        return new Queue(ANSWER_MESSAGE, true);
    }

    @Bean
    public Binding answerBinding() {
        return BindingBuilder.bind(answerQueue())
                .to(directExchange())
                .with(ANSWER_ROUTE);
    }
}
