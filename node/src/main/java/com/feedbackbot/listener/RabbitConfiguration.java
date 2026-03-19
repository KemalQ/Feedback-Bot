package com.feedbackbot.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    // for customizing the message converter(used for Instant field in UserEventDTO)
    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

//  or you can use the default message converter
//    @Bean
//    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {// for customizing the message converter
//        return new Jackson2JsonMessageConverter(objectMapper);
//    }

    // for creating ObjectMapper
    // You can delete this bean and import spring-boot-starter-web to pom.xml
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer>
    rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(5);
        factory.setPrefetchCount(10);
        factory.setDefaultRequeueRejected(false);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        return factory;
    }
}
