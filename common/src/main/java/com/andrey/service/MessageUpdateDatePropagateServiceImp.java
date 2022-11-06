package com.andrey.service;

import com.andrey.db_entities.chat_channel.ChannelLastUpdateInfo;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.service.cached_user_details.CachedUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MessageUpdateDatePropagateServiceImp implements MessageUpdateDatePropagateService{

    private final CachedUserDetailsService cacheUserService;
    @Override
    public Timestamp updateDateAndPropagate(ChatMessage message) {
        message.updateModificationDate();
        Timestamp newDate = message.getModificationDate();
        message.setLastUpdateDate(newDate);
        ChatChannel channel = message.getChannel();
        channelUpdateLastMessageUpdateDate(channel,newDate, message.getId());
        return newDate;

    }

    @Override
    public Timestamp updateDateAndPropagate(ChatChannel channel) {
        channel.updateModificationDate();
        Timestamp newDate = channel.getModificationDate();
        channelUpdateLastMessageUpdateDate(channel, newDate, 0);
        return newDate;
    }

    @Override
    public List<ChatUser> getRecipientUserList(ChatMessage message) {
        return getRecipientUserStream(message.getChannel()).collect(Collectors.toList());
    }

    private Stream<ChatUser> getRecipientUserStream(ChatChannel channel) {
        return channel.getMembers().stream()
                .filter(ChatChannelMembership::isInteractable)
                .map(ChatChannelMembership::getUser)
                .filter(ChatUser::isInteractable);

    }
    private void channelUpdateLastMessageUpdateDate(ChatChannel channel,Timestamp newDate, long messageID) {
        Timestamp lastUpdateDate = channel.getLastUpdateDate();
        if (lastUpdateDate.after(newDate))
            return;
        if (lastUpdateDate.equals(newDate))
                if (channel.getLastUpdateMessageID() != null)
                    if (channel.getLastUpdateMessageID() > messageID)
                        return;

        //set new last update info, using clone to avoid potentially confusing JPA
        Timestamp finalNewDate = (Timestamp) newDate.clone();
        channel.setLastUpdateDate(finalNewDate);
        channel.setLastUpdateMessageID(messageID);

        //propagate to all users
        getRecipientUserStream(channel)
                .forEach(cu -> userUpdateLastChannelUpdateDate(cu,finalNewDate));
    }
    private void userUpdateLastChannelUpdateDate(ChatUser user, Timestamp newDate) {
        if (newDate.before(user.getLastUpdateChannelDate()))
            return;
        newDate = (Timestamp) newDate.clone();
        user.setLastUpdateChannelDate(newDate);
        cacheUserService.evictUserFromCache(user);

    }
}
