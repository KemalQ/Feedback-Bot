package com.feedbackbot.dao;

import com.feedbackbot.entity.FeedbackMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackMessageDAO extends JpaRepository<FeedbackMessage, Long>, JpaSpecificationExecutor<FeedbackMessage> {//,

    // for Trello, criticality >= 4
    List<FeedbackMessage> findByIsProcessedFalseAndCriticalityGreaterThanEqual(Integer criticality);
}
