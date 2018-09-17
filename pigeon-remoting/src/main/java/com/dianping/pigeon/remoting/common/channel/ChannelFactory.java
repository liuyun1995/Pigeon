package com.dianping.pigeon.remoting.common.channel;

//通道工厂
public interface ChannelFactory<C extends Channel> {

    //创建通道
    C createChannel();

}
