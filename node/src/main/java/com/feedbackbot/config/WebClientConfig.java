package com.feedbackbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestClient;

@Configuration
public class WebClientConfig {

    @Bean
    public RestClient restClient(){
        return RestClient.create();
    }

    @Bean
    public jakarta.validation.Validator validator() {
        return new LocalValidatorFactoryBean();
    }

//    @Bean
//    public ChatMemory chatMemory(){
//        return new InMemoryChatMemory();
//    }
//
//    @Bean
//    public ChatClient chatClient(
//            ChatModel chatModel,
//            ChatMemory chatMemory,
//            @Value("classpath:prompts/chatbot-system-prompt.st") Resource systemPrompt){
//        return ChatClient.builder(chatModel)
//                .defaultSystem(systemPrompt)
//                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
//                .build();
//    }
}
