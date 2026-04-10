package com.feedbackbot.service.impl;

import com.feedbackbot.dao.FeedbackMessageDAO;
import com.feedbackbot.dao.specification.FeedbackSpecifications;
import com.feedbackbot.dto.feedback.FeedbackFilterRequest;
import com.feedbackbot.dto.feedback.FeedbackResponseDto;
import com.feedbackbot.entity.FeedbackMessage;
import com.feedbackbot.mapper.MapperUtils;
import com.feedbackbot.service.FeedbackService;
import jakarta.transaction.Transactional;
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
    @Transactional // read only
    public Page<FeedbackResponseDto> findAll(FeedbackFilterRequest filter,// record
                                             Pageable pageable) {
        Specification<FeedbackMessage> spec = Specification
                .where(FeedbackSpecifications.hasBranch(filter.getBranch()))
                        .and(FeedbackSpecifications.hasRole(filter.getRole()))
                        .and(FeedbackSpecifications.hasCriticality(filter.getCriticality()));

        return feedbackMessageDAO.findAll(spec, pageable).map(feedbackMapper::toFeedbackResponseDto);
    }

    @Override
    public FeedbackResponseDto markResolved(Long id, String resolution) {
        return null;
    }
}
