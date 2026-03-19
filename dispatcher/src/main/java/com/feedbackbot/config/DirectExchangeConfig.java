package com.feedbackbot.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.feedbackbot.module.RabbitQueue.*;

@Slf4j
@Setter
@Configuration
public class DirectExchangeConfig {

    //EXCHANGE BEAN DECLARATION
    @Bean
    public DirectExchange directExchangeMethod(){
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }


    //QUEUES
    @Bean
    public Queue textQueue(){
        return new Queue(TEXT_MESSAGE_UPDATE, true);
    }

    @Bean
    public Queue answerQueue(){
        return new Queue(ANSWER_MESSAGE, true);
    }


    //BINDINGS
    @Bean
    public Binding ordersBinding(){
        return BindingBuilder.bind(textQueue())
                .to(directExchangeMethod())
                .with(TEXT_ROUTE);
    }

    @Bean
    public Binding answerBinding(){
        return BindingBuilder.bind(answerQueue())
                .to(directExchangeMethod())
                .with(ANSWER_ROUTE);
    }
}
