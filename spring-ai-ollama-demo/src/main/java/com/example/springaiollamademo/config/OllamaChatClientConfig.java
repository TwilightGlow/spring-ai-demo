package com.example.springaiollamademo.config;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaChatClientConfig {

    @Resource
    private OllamaChatModel ollamaChatModel;

    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder(ollamaChatModel)
                .build();
    }
}
