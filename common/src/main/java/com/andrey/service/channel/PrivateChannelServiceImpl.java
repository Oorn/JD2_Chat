package com.andrey.service.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrivateChannelServiceImpl implements PrivateChannelService{
    @Override
    public String generatePrivateChannelName(long profileId1, long profileId2) {
        if (profileId1 > profileId2) {
            long t = profileId1;
            profileId1 = profileId2;
            profileId2 = t;
        }
        return "private_chat_" + profileId1 + "_" + profileId2;
    }
}
