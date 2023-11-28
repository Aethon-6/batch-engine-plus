package com.engine.core.execute.channel.factory;


import org.springframework.util.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelFactory {
    public static ConcurrentHashMap<String, ChannelHandler> strategyMap = new ConcurrentHashMap<>();

    public static ChannelHandler getInvokeStrategy(String channelType) {
        return strategyMap.get(channelType);
    }

    public static void register(String channelType, ChannelHandler channelHandler) {
        if (ObjectUtils.isEmpty(channelType) && channelHandler == null) return;
        strategyMap.put(channelType, channelHandler);
    }
}
