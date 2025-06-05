package com.example.springaimcpclientdemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider toolCallbackProvider) {
        return builder
                .defaultSystem("你是智能天气助手，请回答与天气相关的问题，所有回答使用中文，用户在询问天气情况时必须要求给出哪个城市和哪一天")
                .defaultTools(toolCallbackProvider)
                .build();
    }
}
