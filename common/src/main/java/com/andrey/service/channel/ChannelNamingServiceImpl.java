package com.andrey.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelNamingServiceImpl implements ChannelNamingService{
    @Override
    public String generatePrivateChannelName(long profileId1, long profileId2) {
        if (profileId1 > profileId2) {
            long t = profileId1;
            profileId1 = profileId2;
            profileId2 = t;
        }
        return "private_chat_" + profileId1 + "_" + profileId2;
    }

    @Override
    public String generateProfileChannelName(long profileId1, long profileId2) {
        if (profileId1 > profileId2) {
            long t = profileId1;
            profileId1 = profileId2;
            profileId2 = t;
        }
        return "profile_chat_" + profileId1 + "_" + profileId2;
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
