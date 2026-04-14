package com.feedbackbot.mapper;

import com.feedbackbot.dto.feedback.FeedbackResponseDto;
import com.feedbackbot.dto.token.InviteTokenCreateRequest;
import com.feedbackbot.dto.token.InviteTokenResponseDto;
import com.feedbackbot.entity.FeedbackMessage;
import com.feedbackbot.entity.InviteToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperUtils {
    @Mapping(source = "user.branch", target = "branch")
    @Mapping(source = "user.role", target = "role") // taking only branch and role from user
    FeedbackResponseDto toFeedbackResponseDto(FeedbackMessage feedback);

    InviteTokenCreateRequest toInviteTokenDto(InviteToken inviteToken);

    @Mapping(target = "id", ignore = true)
    InviteToken toInviteToken(InviteTokenCreateRequest inviteTokenDto);

    InviteTokenResponseDto toInviteTokenResponseDto(InviteToken savedToken);
}
