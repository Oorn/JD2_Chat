package com.andrey.controller.converters;

import com.andrey.controller.responses.ChatFromProfileInfoResponse;
import com.andrey.db_entities.chat_channel.ChatChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatFromProfileInfoResponseConverter implements Converter<ChatChannel, ChatFromProfileInfoResponse> {

    private final ChatFromProfileUserInfoResponseConverter userInfoConverter;

    @Override
    public ChatFromProfileInfoResponse convert(ChatChannel source) {
        ChatFromProfileInfoResponse res = new ChatFromProfileInfoResponse();
        res.setChannelId(source.getId());
        //res.setLastUpdateInfo(source.getLastUpdateInfo());
        res.setLastUpdateDate(source.getLastUpdateDate());
        res.setLastUpdateMessageId(source.getLastUpdateMessageID());
        res.setUsers(
                source.getMembers().stream()
                        .map(userInfoConverter::convert)
                        .collect(Collectors.toList())
        );
        return res;
    }
}
