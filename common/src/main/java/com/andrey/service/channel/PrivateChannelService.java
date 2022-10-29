package com.andrey.service.channel;

public interface PrivateChannelService {

    String generatePrivateChannelName(long profileId1, long profileId2);

    long[] getMemberIdsFromChannelName(String channelName);
}
