package com.feedbackbot.feedback.service;

import com.feedbackbot.dao.FeedbackMessageDAO;
import com.feedbackbot.dao.specification.FeedbackSpecifications;
import com.feedbackbot.feedback.dto.FeedbackFilterRequest;
import com.feedbackbot.feedback.dto.FeedbackResponseDto;
import com.feedbackbot.entity.FeedbackMessage;
import com.feedbackbot.exception.FeedbackNotFoundException;
import com.feedbackbot.mapper.MapperUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackMessageDAO feedbackMessageDAO;
    private final MapperUtils feedbackMapper;

    public FeedbackServiceImpl(FeedbackMessageDAO feedbackMessageDAO, MapperUtils feedbackMapper) {
        this.feedbackMessageDAO = feedbackMessageDAO;
        this.feedbackMapper = feedbackMapper;
    }

    @Override
    @Transactional(readOnly = true) // read only
    public Page<FeedbackResponseDto> findAll(FeedbackFilterRequest filter,// record
                                             Pageable pageable) {


        Specification<FeedbackMessage> spec = Specification
                .where(FeedbackSpecifications.hasBranch(filter.getBranch()))
                        .and(FeedbackSpecifications.hasRole(filter.getRole()))
                        .and(FeedbackSpecifications.hasCriticality(filter.getCriticality()))
                .and(FeedbackSpecifications.hasSentiment(filter.getSentiment()));

        return feedbackMessageDAO.findAll(spec, pageable).map(feedbackMapper::toFeedbackResponseDto);
    }

    @Override
    @Transactional(readOnly = true)// without it hibernate will not be able to read AppUser fields for FeedbackResponseDto
    public FeedbackResponseDto findById(Long id) {
        return feedbackMessageDAO.findById(id).map(feedbackMapper::toFeedbackResponseDto)
                .orElseThrow(()->new FeedbackNotFoundException("Feedback not found with id: " + id));
    }


    @Override
    public FeedbackResponseDto markResolved(Long id, String resolution) {
        return null;
    }
}
