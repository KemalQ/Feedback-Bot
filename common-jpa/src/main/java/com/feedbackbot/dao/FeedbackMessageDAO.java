package com.feedbackbot.dao;

import com.feedbackbot.entity.FeedbackMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackMessageDAO extends JpaRepository<FeedbackMessage, Long>, JpaSpecificationExecutor<FeedbackMessage> {//,

    @Override
    @EntityGraph(value = "FeedbackMessage.user")
    Page<FeedbackMessage> findAll(Specification<FeedbackMessage> spec, Pageable pageable);
    // for Trello, criticality >= 4
    List<FeedbackMessage> findByIsProcessedFalseAndCriticalityGreaterThanEqual(Integer criticality);
}
