package com.dianping.pigeon.remoting.invoker.proxy;

import com.dianping.pigeon.remoting.invoker.config.InvokerConfig;
import java.util.Map;

//服务代理类
public interface ServiceProxy {

    //初始化方法
    void init();

    //获取代理类
    <T> T getProxy(InvokerConfig<T> invokerConfig);

    //获取全部服务调用者
    Map<InvokerConfig<?>, Object> getAllServiceInvokers();

}
