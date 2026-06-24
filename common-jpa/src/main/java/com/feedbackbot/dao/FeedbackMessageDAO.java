package com.feedbackbot.dao;

import com.feedbackbot.entity.FeedbackMessage;
import com.feedbackbot.enums.Sentiment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackMessageDAO extends JpaRepository<FeedbackMessage, Long>, JpaSpecificationExecutor<FeedbackMessage> {//,

    @Override
    @EntityGraph(value = "FeedbackMessage.user")
    Page<FeedbackMessage> findAll(Specification<FeedbackMessage> spec, Pageable pageable);

    @Query("SELECT f FROM FeedbackMessage f JOIN f.user u WHERE "+
            "(:branch IS NULL OR u.branch = :branch) AND "+
            "(:role IS NULL OR u.role = :role) AND " +
            "(:criticality IS NULL OR f.criticality = :criticality) AND "+
            "(:sentiment IS NULL OR f.sentiment = :sentiment)")
    List<FeedbackMessage> findByFilters(
            @Param("branch") String branch,
            @Param("role") String role,
            @Param("criticality") Integer criticality,
            @Param("sentiment") Sentiment sentiment
    );

    // for Trello, criticality >= 4
    List<FeedbackMessage> findByIsProcessedFalseAndCriticalityGreaterThanEqual(Integer criticality);
}
