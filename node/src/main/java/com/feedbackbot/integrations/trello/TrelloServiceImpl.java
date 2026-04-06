package com.feedbackbot.integrations.trello;

import com.feedbackbot.entity.AppUser;
import com.feedbackbot.entity.FeedbackMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
public class TrelloServiceImpl implements TrelloService{
    private final RestClient restClient;

    @Value("${trello.api.key}")
    private String apiKey;

    @Value("${trello.token}")
    private String token;

    @Value("${trello.list-id}")
    private String listId;


    public TrelloServiceImpl() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.trello.com/1")
                .build();
    }

    @Override
    public void createCardIfCritical(FeedbackMessage feedback, AppUser user) {
        if (feedback.getCriticality() == null || feedback.getCriticality() < 4){
            return;
        }

        try{
            String title = buildTitle(feedback, user);
            String description = buildDescription(feedback, user);
            String labelColor = feedback.getCriticality()>=5 ? "red" : "orange";

            String cardId = createCard(title, description);
            if(cardId == null){
                return;
            }

            addLabel(cardId, labelColor, title);
            log.info("Trello card created. cardId: {}, criticality: {}", cardId, feedback.getCriticality());
        }
        catch (Exception e){
            log.error("Failed to create card: {}", e.getMessage());
            //TODO: send notification to admin
            /// do not block flow
        }


    }


    private String createCard(String title, String description) {
        try{
            var response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/cards")
                            .queryParam("key", apiKey)
                            .queryParam("token", token)
                            .queryParam("idList", listId)
                            .queryParam("name", title)
                            .queryParam("desc", description)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("id")){
                return (String) response.get("id");
            }
        }catch (Exception e){
            log.error("Failed to create trello card request: {}", e.getMessage());
            return null;
        }

        return null;
    }

    private void addLabel(String cardId, String color, String labelName){
        try{
            restClient.post()//key token color name
                    .uri(uriBuilder -> uriBuilder
                            .path("/cards/{cardId}/labels")
                            .queryParam("key", apiKey)
                            .queryParam("token", token)
                            .queryParam("color", color)
                            .queryParam("name", labelName)
                            .build(cardId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toBodilessEntity();
        }catch (Exception e){
            log.error("Failed to add label to trello card: {}", e.getMessage());
        }
    }

    private String buildTitle(FeedbackMessage feedback, AppUser user) {
        String priority = feedback.getCriticality() >= 5 ? "CRITICAL" : "HIGH";
        String branch = user.getBranch() != null ? user.getBranch() : "UNKNOWN";
        String role = user.getRole() != null ? user.getRole().name() : "UNKNOWN";
        String result = String.format("Branch: %s | Role: %s | Priority: %s (%d/5)", branch, role, priority, feedback.getCriticality());
        log.info("Trello card title: {}", result);
        return result;
    }

    private String buildDescription(FeedbackMessage feedback, AppUser user) {
        String timestamp = feedback.getCreatedAt() != null
                ? feedback.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : "N/A";

        String sentiment = feedback.getSentiment() != null
                ? feedback.getSentiment().name() : "N/A";

        return String.format("""
                **Sentiment:** %s
                                **Branch:** %s
                                **Role:** %s
                                **Feedback:**
                                %s
                                **AI Resolution:**
                                %s
                                **Logged:** %s
                                """,
                                sentiment,
                                user.getBranch() != null ? user.getBranch() : "N/A",
                                user.getRole() != null ? user.getRole() : "N/A",
                                feedback.getText(),
                                "N/A",
                                timestamp);
    }
}
