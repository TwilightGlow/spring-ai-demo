package com.example.springaichatdemo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/image_model")
@Profile("siliconflow")
public class ImageModelController {

    private final OpenAiImageModel imageModel;
    private final ChatClient chatClient;

    public ImageModelController(OpenAiImageModel imageModel, ChatClient.Builder chatClientBuilder) {
        this.imageModel = imageModel;
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 文生图，不需要多模态
     */
    @GetMapping(value = "/textToImg", produces = "text/html")
    public String textToImg(@RequestParam(value = "message", defaultValue = "猫吃草") String message) {
        ImageResponse response = imageModel.call(
                new ImagePrompt(message,
                        OpenAiImageOptions.builder()
                                .quality("hd")
                                .N(1)
                                .model("Kwai-Kolors/Kolors")
                                .height(256)
                                .width(256).build()));
        String url = response.getResult().getOutput().getUrl();
        System.out.println("url = " + url);
        return "<img src=\"" + url + "\" alt=\"Image\" style=\"max-width: 100%; height: auto;\"/>" +
                "<p>图片生成成功</p>";
    }

    /**
     * 图生文，需要多模态，支持视觉模态
     */
    @GetMapping(value = "/imgToText")
    public String imgToText() throws IOException {
        Resource resource = new ClassPathResource("/pet.png");
        Message userMessage = UserMessage.builder()
                .media(
                        new Media(detectMimeType(resource), resource)
                )
                .text("这张图片是什么？请描述一下。").build();
        Prompt prompt = new Prompt(
                userMessage,
                OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_4_TURBO.getValue()) // 使用GPT-4-Turbo模型
                        .build()
        );
        return chatClient.prompt(prompt).call().content();
    }

    private MimeType detectMimeType(Resource resource) throws IOException {
        Path filePath = resource.getFile().toPath();
        String contentType = Files.probeContentType(filePath);
        if (contentType != null) {
            return MimeType.valueOf(contentType);
        }
        return MimeTypeUtils.IMAGE_PNG;
    }

}
