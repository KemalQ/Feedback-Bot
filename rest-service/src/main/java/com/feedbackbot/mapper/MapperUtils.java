package com.feedbackbot.mapper;

import com.feedbackbot.dto.feedback.FeedbackResponseDto;
import com.feedbackbot.dto.token.InviteTokenCreateRequest;
import com.feedbackbot.entity.FeedbackMessage;
import com.feedbackbot.entity.InviteToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MapperUtils {
    FeedbackResponseDto toFeedbackResponseDto(FeedbackMessage feedback);

    InviteTokenCreateRequest toInviteTokenDto(InviteToken inviteToken);
    InviteToken toInviteToken(InviteTokenCreateRequest inviteTokenDto);

}
