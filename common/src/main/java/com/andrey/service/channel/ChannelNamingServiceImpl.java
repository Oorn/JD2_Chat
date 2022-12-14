package com.andrey.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelNamingServiceImpl implements ChannelNamingService{
    @Override
    public String generatePrivateChannelName(long userId1, long userId2) {
        if (userId1 > userId2) {
            long t = userId1;
            userId1 = userId2;
            userId2 = t;
        }
        return "private_chat_" + userId1 + "_" + userId2;
    }

    @Override
    public String generateProfileChannelName(long userId1, long userId2, long profileId1, long profileId2) {
        if (userId1 > userId2) {
            long t = profileId1;
            profileId1 = profileId2;
            profileId2 = t;

            t = userId1;
            userId1 = userId2;
            userId2 = t;
        }
        return "profile_chat_" + userId1 + "_" + userId2 + "_" + profileId1 + "_" + profileId2;
    }

    @Override
    public long[] getMemberIdsFromChannelName(String channelName) {

        String[] nameTokens = channelName.split("_");
        long[] res = new long[2];
        res[0] = Long.parseLong(nameTokens[2]);
        res[1] = Long.parseLong(nameTokens[3]);
        return res;
    }
}
