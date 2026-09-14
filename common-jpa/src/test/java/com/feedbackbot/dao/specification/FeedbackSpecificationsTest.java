package com.feedbackbot.dao.specification;

import com.feedbackbot.dao.AppUserDAO;
import com.feedbackbot.dao.FeedbackMessageDAO;
import com.feedbackbot.entity.AppUser;
import com.feedbackbot.entity.FeedbackMessage;
import com.feedbackbot.enums.Sentiment;
import com.feedbackbot.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@EnableJpaRepositories(basePackages = "com.feedbackbot.dao")
@ActiveProfiles("test")
class FeedbackSpecificationsTest {

    @Autowired
    private FeedbackMessageDAO feedbackMessageDAO;

    @Autowired
    private AppUserDAO appUserDAO;

    private AppUser user1;
    private AppUser user2;

    @BeforeEach
    void setUp() {
        feedbackMessageDAO.deleteAll();
        appUserDAO.deleteAll();

        user1 = AppUser.builder()
                .telegramUserId(1L)
                .branch("branch1")
                .role(UserRole.MECHANIC)
                .build();

        user2 = AppUser.builder()
                .telegramUserId(2L)
                .branch("branch2")
                .role(UserRole.MANAGER)
                .build();

        // storing users
        user1 = appUserDAO.save(user1);
        user2 = appUserDAO.save(user2);

        FeedbackMessage message1 = FeedbackMessage.builder()
                .user(user1)
                .text("Test message 1")
                .sentiment(Sentiment.POSITIVE)
                .criticality(3)
                .isProcessed(false)
                .build();

        FeedbackMessage message2 = FeedbackMessage.builder()
                .user(user2)
                .text("Test message 2")
                .sentiment(Sentiment.NEGATIVE)
                .criticality(5)
                .isProcessed(false)
                .build();

        FeedbackMessage message3 = FeedbackMessage.builder()
                .user(user1)
                .text("Test message 3")
                .sentiment(Sentiment.NEUTRAL)
                .criticality(2)
                .isProcessed(false)
                .build();

        feedbackMessageDAO.saveAll(
                List.of(message1, message2, message3)
        );
    }

    @Test
    void hasBranch_shouldFilterByUserBranch() {
        Specification<FeedbackMessage> spec = FeedbackSpecifications.hasBranch("branch1");
        Page<FeedbackMessage> result = feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent())
                .hasSize(2)
                .allMatch(f -> f.getUser().getBranch().equals("branch1"));
    }

    @Test
    void hasBranch_shouldReturnNullWhenBranchIsNull() {
        Specification<FeedbackMessage> spec = FeedbackSpecifications.hasBranch(null);
        Page<FeedbackMessage> result = feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    void hasBranch_shouldReturnEmptyWhenNoMatches() {
        Specification<FeedbackMessage> spec = FeedbackSpecifications.hasBranch("nonexistent");
        Page<FeedbackMessage> result = feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void hasRole_shouldReturnNullWhenRoleIsNull() {
        Specification<FeedbackMessage> spec = FeedbackSpecifications.hasRole(null);
        Page<FeedbackMessage> result = feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    void hasRole_shouldFilterByUserRole() {
        Specification<FeedbackMessage> spec =
                FeedbackSpecifications.hasRole(UserRole.MECHANIC);

        Page<FeedbackMessage> result =
                feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent())
                .hasSize(2)
                .allMatch(f -> f.getUser().getRole() == UserRole.MECHANIC);
    }

    @Test
    void hasCriticality_shouldFilterByCriticality() {
        Specification<FeedbackMessage> spec = FeedbackSpecifications.hasCriticality(5);
        Page<FeedbackMessage> result = feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent())
                .hasSize(1)
                .allMatch(f -> f.getCriticality() == 5);
    }

    @Test
    void hasCriticality_shouldReturnNullWhenCriticalityIsNull() {
        Specification<FeedbackMessage> spec = FeedbackSpecifications.hasCriticality(null);
        Page<FeedbackMessage> result = feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    void hasCriticality_shouldReturnEmptyWhenNoMatches() {
        Specification<FeedbackMessage> spec = FeedbackSpecifications.hasCriticality(10);
        Page<FeedbackMessage> result = feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void hasSentiment_shouldFilterBySentiment() {
        Specification<FeedbackMessage> spec = FeedbackSpecifications.hasSentiment(Sentiment.POSITIVE);
        Page<FeedbackMessage> result = feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent())
                .hasSize(1)
                .allMatch(f -> f.getSentiment() == Sentiment.POSITIVE);
    }

    @Test
    void hasSentiment_shouldReturnNullWhenSentimentIsNull() {
        Specification<FeedbackMessage> spec = FeedbackSpecifications.hasSentiment(null);
        Page<FeedbackMessage> result = feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    void hasSentiment_shouldReturnEmptyWhenNoMatches() {
        Specification<FeedbackMessage> spec = FeedbackSpecifications.hasSentiment(Sentiment.NEGATIVE);
        Page<FeedbackMessage> result = feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent())
                .hasSize(1)
                .allMatch(f -> f.getSentiment() == Sentiment.NEGATIVE);
    }

    @Test
    void combinedSpecifications_shouldWorkTogether() {
        Specification<FeedbackMessage> spec = FeedbackSpecifications.hasBranch("branch1")
                .and(FeedbackSpecifications.hasCriticality(3));

        Page<FeedbackMessage> result = feedbackMessageDAO.findAll(spec, Pageable.unpaged());

        assertThat(result.getContent())
                .hasSize(1)
                .allMatch(f -> f.getUser().getBranch().equals("branch1") && f.getCriticality() == 3);
    }
}
