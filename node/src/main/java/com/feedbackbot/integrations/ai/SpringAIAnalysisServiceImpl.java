package com.feedbackbot.integrations.ai;

import com.feedbackbot.dto.FeedbackAnalysisResult;
import com.feedbackbot.dto.FullAiResponse;
import com.feedbackbot.entity.FeedbackMessage;
import com.feedbackbot.enums.Sentiment;
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
    public FullAiResponse analyze(String userPrompt) {
        String prompt = buildPrompt(userPrompt);
        try {
            log.info("Sending prompt to analyses to Llama: {}", userPrompt);
            return chatClient.prompt()
                    .system(prompt)
                    .user(userPrompt)
                    .call()
                    .entity(FullAiResponse.class); // Returning FullAiResponse
        }
        catch (RuntimeException e){
            log.error("❌ AI API call failed: {}", userPrompt, e);
            return FullAiResponse.builder()
                    .sentiment(Sentiment.NEUTRAL)
                    .criticality(1)
                    .resolution("Analysis unavailable. Manual review required.")
                    .build();
        }
    }

    private String buildPrompt(String userPrompt) {
        return """
            You are analyzing employee feedback from an auto service company (mechanics, painters, electricians, etc.).
            
            Determine if the message is relevant workplace feedback — complaints, suggestions, safety issues, equipment problems, management issues, etc.
            Mark as NOT relevant only: greetings, random numbers, test messages, off-topic chatter.
            When in doubt — mark as relevant.

            Feedback: "%s"

            Respond ONLY with a valid JSON object, no explanation, no markdown, no extra text:
            {
              "isRelevant": true,
              "sentiment": "POSITIVE",
              "criticality": 3,
              "resolution": "short actionable suggestion, max 100 chars"
            }

            Rules:
            - isRelevant: boolean (true or false, no quotes)
            - sentiment: exactly one of POSITIVE, NEUTRAL, NEGATIVE
            - criticality: integer 1-5 (1=minor, 3=moderate, 4=urgent, 5=critical)
            - resolution: string, max 100 chars, English
            """.formatted(userPrompt);
    }
}
