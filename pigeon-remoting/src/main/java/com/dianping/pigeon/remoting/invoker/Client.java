package com.dianping.pigeon.remoting.invoker;

import com.dianping.pigeon.remoting.common.channel.Channel;
import com.dianping.pigeon.remoting.common.domain.InvocationRequest;
import com.dianping.pigeon.remoting.common.domain.InvocationResponse;
import com.dianping.pigeon.remoting.common.exception.NetworkException;
import com.dianping.pigeon.remoting.invoker.domain.ConnectInfo;
import com.dianping.pigeon.remoting.invoker.route.region.Region;
import java.util.List;

//客户端接口
public interface Client<C extends Channel> {

    //获取连接信息
    ConnectInfo getConnectInfo();

    //打开连接
    void open();

    //关闭连接
    void close();

    //写入调用结果
    InvocationResponse write(InvocationRequest request) throws NetworkException;

    //处理调用结果
    void processResponse(InvocationResponse response);

    //设置是否活跃
    void setActive(boolean active);

    //是否活跃
    boolean isActive();

    //是否关闭
    boolean isClosed();

    //获取管道
    List<C> getChannels();

    //获取主机名
    String getHost();

    //获取地址
    String getAddress();

    //获取端口
    int getPort();

    //获取网络协议
    String getProtocol();

    //获取区域
    Region getRegion();

    //清空区域
    void clearRegion();

}
