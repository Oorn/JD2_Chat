package com.andrey.controller.converters;

import com.andrey.controller.responses.PendingInvitesListResponse;
import com.andrey.db_entities.chat_channel_invite.ChatChannelInvite;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PendingInvitesListResponseConverter implements Converter<List<ChatChannelInvite>, PendingInvitesListResponse> {
    private final PendingInviteResponseConverter responseConverter;

    @Override
    public PendingInvitesListResponse convert(List<ChatChannelInvite> source) {
        return PendingInvitesListResponse.builder()
                .invites(source.stream()
                        .map(responseConverter::convert)
                        .collect(Collectors.toList()))
                .build();
    }
}