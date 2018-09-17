package com.dianping.pigeon.remoting.common.pool;

import com.dianping.pigeon.remoting.common.channel.Channel;
import java.util.List;

//通道池接口
public interface ChannelPool<C extends Channel> {

    int getSize();

    boolean isAvaliable();

    C selectChannel() throws ChannelPoolException;

    List<C> getChannels();

    PoolProperties getPoolProperties();

    boolean isClosed();

    void close();

}