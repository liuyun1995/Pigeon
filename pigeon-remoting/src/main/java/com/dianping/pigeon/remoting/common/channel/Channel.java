package com.dianping.pigeon.remoting.common.channel;

import com.dianping.pigeon.remoting.common.exception.NetworkException;
import java.net.InetSocketAddress;

//通道接口
public interface Channel {

    //获取连接
    void connect() throws NetworkException;

    //断开连接
    void disConnect();

    //发送请求
    void write(Object message) throws NetworkException;

    //是否可写入
    boolean isWritable();

    //是否可用
    boolean isAvaliable();

    //获取本机地址
    InetSocketAddress getLocalAddress();

    //获取远程地址
    InetSocketAddress getRemoteAddress();

    //获取远程地址字符串
    String getRemoteAddressString();
}
