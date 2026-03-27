package com.feedbackbot.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedbackbot.dto.FeedbackAnalysisResult;
import com.feedbackbot.enums.Sentiment;
import com.feedbackbot.service.ClaudeAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ClaudeAnalysisServiceImpl implements ClaudeAnalysisService {
    private String apiKey;

    @Value("${claude.model}")
    private String model;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ClaudeAnalysisServiceImpl(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }


    @Override
    public FeedbackAnalysisResult analyze(String feedbackText) {
        String prompt = buildPrompt(feedbackText);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", 256,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );
        try {
            String response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseResponse(response);

        } catch (Exception e) {
            log.error("❌ Claude API call failed: {}", e.getMessage());
            // fallback — не блокируем пользователя если AI упал
            return FeedbackAnalysisResult.builder()
                    .sentiment(Sentiment.NEUTRAL)
                    .criticality(1)
                    .resolution("Analysis unavailable. Manual review required.")
                    .build();
        }
    }

    private String buildPrompt(String feedbackText) {
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
                """.formatted(feedbackText);
    }

    private FeedbackAnalysisResult parseResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            // Claude возвращает: content[0].text содержит наш JSON
            String jsonText = root
                    .path("content")
                    .get(0)
                    .path("text")
                    .asText();

            JsonNode result = objectMapper.readTree(jsonText);

            Sentiment sentiment = Sentiment.valueOf(
                    result.path("sentiment").asText("NEUTRAL")
            );
            int criticality = result.path("criticality").asInt(1);
            String resolution = result.path("resolution").asText("No suggestion.");

            // защита от выхода за пределы
            criticality = Math.max(1, Math.min(5, criticality));

            log.info("✅ Claude analysis: sentiment={}, criticality={}", sentiment, criticality);

            return FeedbackAnalysisResult.builder()
                    .sentiment(sentiment)
                    .criticality(criticality)
                    .resolution(resolution)
                    .build();

        } catch (Exception e) {
            log.error("❌ Failed to parse Claude response: {}", rawResponse, e);
            return FeedbackAnalysisResult.builder()
                    .sentiment(Sentiment.NEUTRAL)
                    .criticality(1)
                    .resolution("Could not parse analysis.")
                    .build();
        }
    }
}