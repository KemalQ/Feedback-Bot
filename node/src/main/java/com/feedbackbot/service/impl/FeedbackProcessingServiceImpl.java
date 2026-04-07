package com.feedbackbot.service.impl;

import com.feedbackbot.dao.FeedbackMessageDAO;
import com.feedbackbot.dto.FullAiResponse;
import com.feedbackbot.entity.AppUser;
import com.feedbackbot.entity.FeedbackMessage;
import com.feedbackbot.integrations.ai.SpringAIAnalysisService;
import com.feedbackbot.integrations.sheets.GoogleSheetsService;
import com.feedbackbot.integrations.trello.TrelloService;
import com.feedbackbot.service.FeedbackProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeedbackProcessingServiceImpl implements FeedbackProcessingService {
    private final SpringAIAnalysisService springAIAnalysisService;
    private final FeedbackMessageDAO feedbackMessageDAO;
    private final GoogleSheetsService googleSheetsService;
    private final TrelloService trelloService;

    public FeedbackProcessingServiceImpl(SpringAIAnalysisService springAIAnalysisService, FeedbackMessageDAO feedbackMessageDAO, GoogleSheetsService googleSheetsService, TrelloService trelloService) {
        this.springAIAnalysisService = springAIAnalysisService;
        this.feedbackMessageDAO = feedbackMessageDAO;
        this.googleSheetsService = googleSheetsService;
        this.trelloService = trelloService;
    }

    @Override
    public String process(String feedbackText, AppUser appUser) {
        FullAiResponse aiResponse = springAIAnalysisService.analyze(feedbackText);

        if (!aiResponse.isRelevant()) {
            log.info("Non-relevant message from user {}, skipping", appUser.getTelegramUserId());
            return "Please send relevant feedback about your work or workplace conditions.";
        }

        FeedbackMessage feedback = FeedbackMessage.builder()
                .user(appUser)
                .text(feedbackText)
                .sentiment(aiResponse.getSentiment())
                .criticality(aiResponse.getCriticality())
                .resolution(aiResponse.getResolution())
                .isProcessed(false)
                .build();   // could use mapper but

        feedbackMessageDAO.save(feedback);

        log.info("Feedback saved: id={}, criticality={}",
                feedback.getId(), feedback.getCriticality());

        googleSheetsService.appendFeedbackRow(feedback, appUser);

        trelloService.createCardIfCritical(feedback, appUser);

        // response to the user
        return buildUserResponse(aiResponse);
    }

    private String buildUserResponse(FullAiResponse analysis) {// Maybe here should be
        // FeedbackAnalysisResult or FeedbackMessage
        String emoji = switch (analysis.getSentiment()){
            case POSITIVE -> "😊";
            case NEUTRAL  -> "📝";
            case NEGATIVE -> "⚠️";
        };

        String urgency = analysis.getCriticality() >= 4
                ? "\n🚨 This issue has been flagged as high priority."
                : "";

        return String.format(
                "%s Your feedback has been received and logged anonymously.\n\n" +
                        "Assessment: %s | Priority: %d/5\n" +
                        "Suggested action: %s%s",
                emoji,
                analysis.getSentiment().toString().toLowerCase(),
                analysis.getCriticality(),
                analysis.getResolution(),
                urgency
        );
    }
}
