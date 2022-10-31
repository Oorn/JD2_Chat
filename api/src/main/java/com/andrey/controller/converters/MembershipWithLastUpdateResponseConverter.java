package com.andrey.controller.converters;

import com.andrey.controller.responses.MembershipWithLastUpdateResponse;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MembershipWithLastUpdateResponseConverter implements Converter<ChatChannelMembership, MembershipWithLastUpdateResponse> {
    @Override
    public MembershipWithLastUpdateResponse convert(ChatChannelMembership source) {
        MembershipWithLastUpdateResponse res = new MembershipWithLastUpdateResponse();
        res.setChannelId(source.getChannelId());
        res.setLastUpdateTimestamp(source.getChannel().getLastUpdateDate());
        if (source.getChannel().getLastUpdateMessageID() != null)
            res.setLastUpdateMessageId(source.getChannel().getLastUpdateMessageID());
        else res.setLastUpdateMessageId(0L);
        res.setChannelName(source.getChannel().getChannelName());
        res.setChannelType(source.getChannel().getChannelType());
        res.setMembershipRole(source.getRole());

        return res;
    }
}
