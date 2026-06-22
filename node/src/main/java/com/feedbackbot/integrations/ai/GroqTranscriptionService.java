package com.feedbackbot.integrations.ai;

public interface GroqTranscriptionService {
    String transcribeVoice(byte[] audioBytes);
}
