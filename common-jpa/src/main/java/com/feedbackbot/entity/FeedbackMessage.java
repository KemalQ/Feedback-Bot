package com.feedbackbot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "feedback_message")
public class FeedbackMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "sentiment")
    private Enum sentiment;

    // 1 = min, 5 = critical
    @Column(name = "criticality")
    private Integer criticality;

    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;

    // ID in Google Docs — to find message in google docs
    @Column(name = "google_doc_row_id")
    private String googleDocRowId;

    // ID cards in Trello (only if criticality >= 4)
    @Column(name = "trello_card_id")
    private String trelloCardId;

    @Column(name = "is_processed", nullable = false)
    @Builder.Default
    private Boolean isProcessed = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
