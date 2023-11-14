package com.andrey;

import com.andrey.service.channel.ChannelNamingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DummyTests {

    @Test
    public void firstTest()
    {
        var subject = new ChannelNamingServiceImpl();
        var result = subject.generatePrivateChannelName(1,2);
        Assertions.assertEquals("private_chat_1_2", result);
    }
}
