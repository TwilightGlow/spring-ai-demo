package com.example.springaichatdemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audio_model")
@Profile("siliconflow")
@RequiredArgsConstructor
public class AudioModelController {

    private final OpenAiAudioTranscriptionModel audioTranscriptionModel;
    private final OpenAiAudioSpeechModel audioSpeechModel;

    /**
     * 音频转文本，需要使用 OpenAI Whisper 模型
     */
    @GetMapping(value = "/audioToText")
    public String audioToText() {
        OpenAiAudioTranscriptionOptions openAiAudioTranscriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .model(OpenAiAudioApi.WhisperModel.WHISPER_1.value) // 使用 OpenAI Whisper 模型进行音频转文本
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .temperature(0f)
                .build();

        Resource audioFile = new ClassPathResource("/青花瓷.mp3");
        AudioTranscriptionPrompt audioTranscriptionPrompt = new AudioTranscriptionPrompt(audioFile, openAiAudioTranscriptionOptions);
        AudioTranscriptionResponse response = audioTranscriptionModel.call(audioTranscriptionPrompt);
        return response.getResult().getOutput();
    }

    /**
     * 文本转语音，生成MP3音频流，需要使用tts-1模型
     */
    @GetMapping(value = "/textToSpeech", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> textToSpeech() {
        // 1. 准备TTS选项和输入文本
        String inputText = "天青色等烟雨，而我在等你。炊烟袅袅升起，隔江千万里。";

        OpenAiAudioSpeechOptions openAiAudioSpeechOptions = OpenAiAudioSpeechOptions.builder()
                .model(OpenAiAudioApi.TtsModel.TTS_1.value)
                .voice(OpenAiAudioApi.SpeechRequest.Voice.NOVA)
                .speed(1.0f)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .build();

        // 2. 创建语音生成请求
        SpeechPrompt speechPrompt = new SpeechPrompt(inputText, openAiAudioSpeechOptions);

        // 3. 调用客户端生成语音
        SpeechResponse response = audioSpeechModel.call(speechPrompt);

        // 4. 获取生成的音频字节
        byte[] audioBytes = response.getResult().getOutput();

        // 5. 创建HTTP响应（MP3音频流）
        ByteArrayResource audioResource = new ByteArrayResource(audioBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.mp3")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(audioResource);
    }
}
