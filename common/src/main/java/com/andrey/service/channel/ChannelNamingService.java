package com.andrey.service.channel;

public interface ChannelNamingService {
    String generatePrivateChannelName(long profileId1, long profileId2);

    String generateProfileChannelName(long userId1, long userId2, long profileId1, long profileId2);

    long[] getMemberIdsFromChannelName(String channelName);
}
