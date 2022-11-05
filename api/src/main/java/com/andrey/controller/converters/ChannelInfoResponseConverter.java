package com.andrey.controller.converters;

import com.andrey.controller.responses.ChannelInfoResponse;
import com.andrey.controller.responses.ChatFromProfileInfoResponse;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_user.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChannelInfoResponseConverter implements Converter<ChatChannel, ChannelInfoResponse> {

    private final ChannelMemberInfoResponseConverter memberConverter;

    private final ProfileInfoPartialResponseConverter profileConverter;

    private final UserInfoShortResponseConverter userConverter;

    @Override
    public ChannelInfoResponse convert(ChatChannel source) {
        ChannelInfoResponse res = new ChannelInfoResponse();
        res.setChannelId(source.getId());
        res.setChannelType(source.getChannelType());
        res.setMembers(
                source.getMembers().stream()
                        .filter(ChatChannelMembership::isInteractable)
                        .filter(m -> m.getUser().isInteractable())
                        .map(memberConverter::convert)
                        .collect(Collectors.toList())
        );
        switch (res.getChannelType()) {
            case MULTI_USER_CHANNEL:
                res.setChannelOwner(userConverter.convert(source.getOwner()));
                res.setChannelName(source.getChannelName());
                res.setChannelProfiles(null);
                res.setDefaultRole(source.getDefaultRole());
                break;
            case PRIVATE_CHAT_FROM_PROFILE:
                res.setChannelOwner(null);
                res.setChannelName("");
                res.setChannelProfiles(
                        source.getMembers().stream()
                                .map(ChatChannelMembership::getUserProfile)
                                .collect(Collectors.toMap(p -> p.getOwner().getId()
                                        , profileConverter::convert))
                );
                break;
            case PRIVATE_CHAT_BETWEEN_FRIENDS:
                res.setChannelOwner(null);
                res.setChannelName("");
                res.setChannelProfiles(null);
                break;
            default:

        }
        return res;
    }
}
