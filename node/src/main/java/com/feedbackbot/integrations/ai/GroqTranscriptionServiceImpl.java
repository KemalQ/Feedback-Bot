package com.feedbackbot.integrations.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GroqTranscriptionServiceImpl implements GroqTranscriptionService{///
    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public GroqTranscriptionServiceImpl(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    @Override
    public String transcribeVoice(byte[] audioBytes) {
        log.info("Starting transcription, audio size: {} bytes", audioBytes.length);

        Resource audioResource = new ByteArrayResource(audioBytes){

            @Override
            public String getFilename(){
                return "voice.ogg";
            }
        };
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource,
                OpenAiAudioTranscriptionOptions.builder().
                        model("whisper-large-v3")//.language("en")
                        .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.JSON)
                        .build());

        AudioTranscriptionResponse response = transcriptionModel.call(prompt);
        String text = response.getResult().getOutput();

        log.info("Transcription result: {}", text);
        return text;
    }
}
