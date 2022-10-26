package com.andrey.service;

import com.andrey.db_entities.chat_channel.ChannelLastUpdateInfo;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_user.ChatUser;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MessageUpdateDatePropagateServiceImp implements MessageUpdateDatePropagateService{
    @Override
    public Timestamp updateDateAndPropagate(ChatMessage message) {
        Timestamp newDate = message.updateModificationDate();
        ChatChannel channel = message.getChannel();
        channelUpdateLastMessageUpdateDate(channel,newDate, message.getId());
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
        ChannelLastUpdateInfo lastUpdate = channel.getLastUpdateInfo();
        if (lastUpdate.getLastUpdateDate().after(newDate))
            return;
        if ((lastUpdate.getLastUpdateDate().equals(newDate))
                && (lastUpdate.getLastUpdateMessageID() > messageID))
            return;

        //set new last update info, using clone to avoid potentially confusing JPA
        Timestamp finalNewDate = (Timestamp) newDate.clone();
        channel.setLastUpdateInfo(new ChannelLastUpdateInfo(finalNewDate, messageID));

        //propagate to all users
        getRecipientUserStream(channel)
                .forEach(cu -> userUpdateLastChannelUpdateDate(cu,finalNewDate));
    }
    private void userUpdateLastChannelUpdateDate(ChatUser user, Timestamp newDate) {
        if (newDate.before(user.getLastUpdateChannelDate()))
            return;
        newDate = (Timestamp) newDate.clone();
        user.setLastUpdateChannelDate(newDate);
    }
}
