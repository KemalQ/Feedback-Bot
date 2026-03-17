package com.feedbackbot.dao;

import com.feedbackbot.entity.FeedbackMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackMessageDAO extends JpaRepository<FeedbackMessage, Long> {
    // for React panel-filter by branch, role, criticality
    @Query("""
        SELECT f FROM FeedbackMessage f
        JOIN f.user u
        WHERE (:branch IS NULL OR u.branch = :branch)
          AND (:role   IS NULL OR CAST(u.role AS string) = :role)
          AND (:criticality IS NULL OR f.criticality = :criticality)
        ORDER BY f.createdAt DESC
    """)
    Page<FeedbackMessage> findFiltered(
            @Param("branch") String branch,
            @Param("role") String role,
            @Param("criticality") Integer criticality,
            Pageable pageable
    );

    // критичные необработанные — для Trello-триггера
    List<FeedbackMessage> findByIsProcessedFalseAndCriticalityGreaterThanEqual(Integer criticality);
}
