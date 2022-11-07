package com.andrey.controller.converters;

import com.andrey.controller.responses.UserMembershipsWithLastUpdateResponse;
import com.andrey.db_entities.chat_channel.ChannelStatus;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_user.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMembershipsWithLastUpdateResponseConverter implements Converter<ChatUser, UserMembershipsWithLastUpdateResponse> {
    private final MembershipWithLastUpdateResponseConverter membershipConverter;

    @Override
    public UserMembershipsWithLastUpdateResponse convert(ChatUser source) {
        UserMembershipsWithLastUpdateResponse res = new UserMembershipsWithLastUpdateResponse();
        res.setUserId(source.getId());
        res.setChannelResponses(
                source.getChannelMemberships().values().stream()
                        .filter(ChatChannelMembership::isInteractable)
                        .filter(mem -> mem.getChannel().getStatus().equals(ChannelStatus.ACTIVE))
                        .map(membershipConverter::convert)
                        .collect(Collectors.toList())

        );
        return res;

    }
}
