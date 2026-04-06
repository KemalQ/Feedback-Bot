package com.feedbackbot.integrations.sheets;

import com.feedbackbot.entity.AppUser;
import com.feedbackbot.entity.FeedbackMessage;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class GoogleSheetsServiceImpl implements GoogleSheetsService {
    private final Sheets sheetsService;

    @Value("${google.sheets.spreadsheet-id}")
    private String spreadsheetId;

    @Value("${google.sheets.range:A:G}")
    private String range;

    public GoogleSheetsServiceImpl(Sheets sheetsService) {
        this.sheetsService = sheetsService;
    }

    @Override
    public void appendFeedbackRow(FeedbackMessage feedback, AppUser user) {
        try {
            String timestamp = feedback.getCreatedAt() != null
                    ? feedback.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    : "N/A";

            List<Object> row = List.of(
                    timestamp,
                    user.getBranch() != null ? user.getBranch() : "N/A",
                    user.getRole() != null ? user.getRole().name() : "N/A",
                    feedback.getSentiment() != null ? feedback.getSentiment().name() : "N/A",
                    feedback.getCriticality() != null ? feedback.getCriticality() : 0,
                    feedback.getResolution() != null ? feedback.getResolution() : "N/A",
                    feedback.getText()
            );

            ValueRange body = new ValueRange().setValues(List.of(row));

            sheetsService.spreadsheets().values()
                    .append(spreadsheetId, range, body)
                    .setValueInputOption("RAW")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();

            log.info("✅ Google Sheets row appended: feedbackId={}, criticality={}",
                    feedback.getId(), feedback.getCriticality());

        } catch (Exception e) {
            log.error("❌ Failed to append row to Google Sheets: {}", e.getMessage());
            // don't throw exception — don't block the main flow
        }
    }
}
