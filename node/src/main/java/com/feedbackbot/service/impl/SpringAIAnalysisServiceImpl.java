package com.feedbackbot.service.impl;

import com.feedbackbot.dto.FeedbackAnalysisResult;
import com.feedbackbot.enums.Sentiment;
import com.feedbackbot.service.SpringAIAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class SpringAIAnalysisServiceImpl implements SpringAIAnalysisService {
    private final ChatClient chatClient;

    public SpringAIAnalysisServiceImpl(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @Override
    public FeedbackAnalysisResult analyze(String userPrompt) {
        String prompt = buildPrompt(userPrompt);
        try {
            log.info("Sending prompt to analyses to Llama: {}", userPrompt);
            return chatClient.prompt()
                    .system(prompt)
                    .user(userPrompt)
                    .call()
                    .entity(FeedbackAnalysisResult.class); // Returning FeedbackAnalysisResult
        }
        catch (RuntimeException e){
            log.error("❌ AI API call failed: {}", userPrompt, e.getMessage());
            return FeedbackAnalysisResult.builder()
                    .sentiment(Sentiment.NEUTRAL)
                    .criticality(1)
                    .resolution("Analysis unavailable. Manual review required.")
                    .build();
        }
    }

    private String buildPrompt(String userPrompt) {
        return """
                Analyze the following employee feedback from an auto service company.

                Feedback: "%s"

                Respond ONLY with a valid JSON object, no explanation, no markdown:
                {
                  "sentiment": "POSITIVE" or "NEUTRAL" or "NEGATIVE",
                  "criticality": <integer from 1 to 5>,
                  "resolution": "<short actionable suggestion in English, max 100 chars>"
                }

                Criticality scale:
                1 - minor suggestion
                2 - low priority issue
                3 - moderate issue, needs attention
                4 - serious issue, urgent action needed
                5 - critical, immediate response required
                """.formatted(userPrompt);
    }

    @Override
    public String analyzeAndReturnString(String userPrompt) {
        try {
            log.info("Sending prompt to Llama: {}", userPrompt);
            return chatClient.prompt()
                    .user(userPrompt)
                    .call()
                    .content(); // Returning String
        }
        catch (RuntimeException e){
            log.error("Error while sending prompt to Llama: {}", userPrompt, e);
            return "Error: " + e.getMessage();
        }
    }
}
